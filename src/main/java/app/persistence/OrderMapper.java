package app.persistence;

import app.entities.Carport;
import app.entities.MaterialLine;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public static List<Object[]> getOrdersByUser(int userId, ConnectionPool connectionPool) throws DatabaseException {
        List<Object[]> orders = new ArrayList<>();
        String sql = """
                SELECT order_id, status, total_price, date
                FROM orders
                WHERE user_id = ?
                ORDER BY order_id DESC
                """;
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("status"),
                        rs.getDouble("total_price"),
                        rs.getDate("date")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrer", e);
        }
        return orders;
    }
    public static List<Object[]> getOrderLines(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        List<Object[]> lines = new ArrayList<>();
        String sql = """
        SELECT m.name, ml.quantity, ml.description, m.unit, ml.unit_price
        FROM orders o
        JOIN carport c ON c.order_id = o.order_id
        JOIN material_line ml ON ml.carport_id = c.carport_id
        JOIN material m ON m.material_id = ml.material_id
        WHERE o.order_id = ?
        """;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lines.add(new Object[]{
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("description"),
                        rs.getString("unit"),
                        rs.getDouble("unit_price")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrelinjer", e);
        }
        return lines;
    }

    public static Carport getCarportByOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = """
        SELECT c.width, c.length, r.roof_covering,
               COALESCE(s.width, 0) AS shed_width,
               COALESCE(s.length, 0) AS shed_length
        FROM carport c
        JOIN roof r ON r.roof_id = c.roof_id
        LEFT JOIN shed s ON s.carport_id = c.carport_id
        WHERE c.order_id = ?
        """;

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int shedWidth = rs.getInt("shed_width");
                int shedLength = rs.getInt("shed_length");
                boolean hasShed = shedWidth > 0 && shedLength > 0;

                return new Carport(
                        rs.getInt("width"),
                        rs.getInt("length"),
                        230,
                        rs.getString("roof_covering"),
                        hasShed,
                        shedWidth,
                        shedLength
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente carport til ordre", e);
        }

        return null;
    }

    public static void sendOffer(int orderId, double price, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status = 'offer', total_price = ? WHERE order_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, price);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke sende tilbud", e);
        }
    }

    public static void saveCarportForOrder(int orderId, Carport carport, List<MaterialLine> materials, ConnectionPool connectionPool) throws DatabaseException {
        String roofSql = """
                INSERT INTO roof (roof_style, roof_covering, pitch, price)
                VALUES (?, ?, ?, 0)
                RETURNING roof_id
                """;
        String carportSql = """
                INSERT INTO carport (order_id, roof_id, width, length)
                VALUES (?, ?, ?, ?)
                RETURNING carport_id
                """;
        String shedSql = """
                INSERT INTO shed (carport_id, width, length)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = connectionPool.getConnection()) {
            try {
                conn.setAutoCommit(false);

                int roofId = insertRoof(conn, roofSql, carport);
                int carportId = insertCarport(conn, carportSql, orderId, roofId, carport);

                if (carport.isHasShed()) {
                    insertShed(conn, shedSql, carportId, carport);
                }

                for (MaterialLine material : materials) {
                    int materialId = findOrCreateMaterial(conn, material);
                    insertMaterialLine(conn, carportId, materialId, material);
                }

                updateOrderPrice(conn, orderId, calculateTotalPrice(materials));
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException(createCarportSaveErrorMessage(e), e);
        }
    }

    public static void acceptOffer(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status = 'approved' WHERE order_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke acceptere tilbud", e);
        }
    }

    public static void rejectOffer(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status = 'rejected' WHERE order_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke afvise tilbud", e);
        }
    }

    public static void payOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET is_paid = true, status = 'completed' WHERE order_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke gennemføre betaling", e);
        }
    }

    public static void cancelOrder(int orderId, int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status = 'rejected' WHERE order_id = ? AND user_id = ? AND status = 'pending'";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke annullere ordre", e);
        }
    }

    public static boolean isOrderOwner(int orderId, int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT 1 FROM orders WHERE order_id = ? AND user_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke verificere ordre-ejerskab", e);
        }
    }

    public static int createOrder(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO orders (user_id, status) VALUES (?, 'pending') RETURNING order_id";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("order_id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette ordre", e);
        }
        throw new DatabaseException("Ordre blev ikke oprettet");
    }

    private static int insertRoof(Connection conn, String sql, Carport carport) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "flat");
            ps.setString(2, carport.getRoofMaterial());
            ps.setNull(3, java.sql.Types.SMALLINT);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("roof_id");
        }
    }

    private static int insertCarport(Connection conn, String sql, int orderId, int roofId, Carport carport) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, roofId);
            ps.setInt(3, carport.getWidth());
            ps.setInt(4, carport.getLength());

            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("carport_id");
        }
    }

    private static void insertShed(Connection conn, String sql, int carportId, Carport carport) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carportId);
            ps.setInt(2, carport.getShedWidth());
            ps.setInt(3, carport.getShedLength());
            ps.executeUpdate();
        }
    }

    private static int findOrCreateMaterial(Connection conn, MaterialLine material) throws SQLException {
        String selectSql = "SELECT material_id FROM material WHERE name = ? AND unit = ?";
        String insertSql = "INSERT INTO material (name, unit, price) VALUES (?, ?, ?) RETURNING material_id";

        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, material.getName());
            ps.setString(2, material.getUnit());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("material_id");
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, material.getName());
            ps.setString(2, material.getUnit());
            ps.setDouble(3, material.getUnitPrice());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("material_id");
        }
    }

    private static void insertMaterialLine(Connection conn, int carportId, int materialId, MaterialLine material) throws SQLException {
        String sql = """
                INSERT INTO material_line (carport_id, material_id, quantity, unit_price, description)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, carportId);
            ps.setInt(2, materialId);
            ps.setInt(3, material.getQuantity());
            ps.setDouble(4, material.getUnitPrice());
            ps.setString(5, material.getDimensions() + " - " + material.getDescription());
            ps.executeUpdate();
        }
    }

    private static void updateOrderPrice(Connection conn, int orderId, double price) throws SQLException {
        String sql = "UPDATE orders SET total_price = ? WHERE order_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, price);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    private static double calculateTotalPrice(List<MaterialLine> materials) {
        double total = 0;

        for (MaterialLine material : materials) {
            total += material.getTotalPrice();
        }

        return total;
    }

    private static String createCarportSaveErrorMessage(SQLException e) {
        String message = e.getMessage();

        if ("23514".equals(e.getSQLState())) {
            if (message.contains("carport_width_check")) {
                return "Carportbredden passer ikke med databasens tilladte mål. Kør update-existing-database.sql og prøv igen.";
            }

            if (message.contains("carport_length_check")) {
                return "Carportlængden passer ikke med databasens tilladte mål. Kør update-existing-database.sql og prøv igen.";
            }

            if (message.contains("shed_width_check")) {
                return "Skurbredden passer ikke med databasens tilladte mål. Kør update-existing-database.sql og prøv igen.";
            }

            if (message.contains("shed_length_check")) {
                return "Skurlængden passer ikke med databasens tilladte mål.";
            }

            if (message.contains("orders_status_check")) {
                return "Ordrestatus passer ikke med databasens tilladte værdier. Kør update-existing-database.sql og prøv igen.";
            }

            return "Et af de valgte mål passer ikke med databasens regler. Tjek bredde, længde og skurmål.";
        }

        if ("23505".equals(e.getSQLState())) {
            return "Der findes allerede carportdata til denne ordre.";
        }

        if ("23503".equals(e.getSQLState())) {
            return "Ordren kunne ikke kobles korrekt sammen med carport, tag eller materialer.";
        }

        return "Carporten kunne ikke gemmes. Tjek databaseforbindelsen og prøv igen.";
    }

}

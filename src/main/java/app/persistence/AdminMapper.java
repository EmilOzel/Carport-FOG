package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminMapper {

    public static List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.user_id, u.email, u.password_hash, u.first_name, u.last_name, u.phone, u.address, u.role, u.zip, z.city " +
                     "FROM users u JOIN zipcode z ON u.zip = z.zip";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente brugere", e);
        }
        return users;
    }

    public static User getUserById(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT u.user_id, u.email, u.password_hash, u.first_name, u.last_name, u.phone, u.address, u.role, u.zip, z.city " +
                     "FROM users u JOIN zipcode z ON u.zip = z.zip WHERE u.user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente bruger", e);
        }
        throw new DatabaseException("Bruger ikke fundet");
    }

    public static void deleteUser(int userId, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke slette bruger", e);
        }
    }

    public static void updateUserRole(int userId, String newRole, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET role = ? WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newRole);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke opdatere brugerrolle", e);
        }
    }

    public static List<Object[]> getAllOrders(ConnectionPool connectionPool) throws DatabaseException {
        List<Object[]> orders = new ArrayList<>();
        String sql = """
                SELECT o.order_id, u.first_name, u.last_name, u.email,
                       o.date, o.status, o.total_price, o.is_paid,
                       COALESCE(SUM(ml.quantity * ml.unit_price), 0) AS cost_price
                FROM orders o
                JOIN users u ON u.user_id = o.user_id
                LEFT JOIN carport c ON c.order_id = o.order_id
                LEFT JOIN material_line ml ON ml.carport_id = c.carport_id
                GROUP BY o.order_id, u.first_name, u.last_name, u.email,
                         o.date, o.status, o.total_price, o.is_paid
                ORDER BY o.order_id DESC
                """;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                orders.add(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getDate("date"),
                        rs.getString("status"),
                        rs.getDouble("total_price"),
                        rs.getBoolean("is_paid"),
                        rs.getDouble("cost_price")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrer", e);
        }
        return orders;
    }

    public static void updateOrderStatus(int orderId, String newStatus, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke opdatere ordrestatus", e);
        }
    }

    public static Map<String, Object> getStatistics(ConnectionPool connectionPool) throws DatabaseException {
        Map<String, Object> stats = new LinkedHashMap<>();
        String sql = """
                SELECT
                    COUNT(*)                                                        AS total_orders,
                    COUNT(*) FILTER (WHERE status = 'pending')                     AS pending,
                    COUNT(*) FILTER (WHERE status = 'approved')                    AS approved,
                    COUNT(*) FILTER (WHERE status = 'completed')                   AS completed,
                    COUNT(*) FILTER (WHERE status = 'rejected')                    AS rejected,
                    COUNT(*) FILTER (WHERE is_paid = true)                         AS paid_orders,
                    COALESCE(SUM(total_price) FILTER (WHERE is_paid = true), 0)    AS total_revenue,
                    COUNT(*) FILTER (WHERE date >= DATE_TRUNC('month', CURRENT_DATE)) AS orders_this_month,
                    COALESCE(
                        SUM(total_price) FILTER (WHERE is_paid = true), 0) -
                    COALESCE((
                        SELECT SUM(ml.quantity * ml.unit_price)
                        FROM orders o2
                        JOIN carport c ON c.order_id = o2.order_id
                        JOIN material_line ml ON ml.carport_id = c.carport_id
                        WHERE o2.is_paid = true
                    ), 0)                                                           AS total_profit
                FROM orders
                """;
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stats.put("totalOrders",      rs.getInt("total_orders"));
                stats.put("pending",          rs.getInt("pending"));
                stats.put("approved",         rs.getInt("approved"));
                stats.put("completed",        rs.getInt("completed"));
                stats.put("rejected",         rs.getInt("rejected"));
                stats.put("paidOrders",       rs.getInt("paid_orders"));
                stats.put("totalRevenue",     rs.getDouble("total_revenue"));
                stats.put("ordersThisMonth",  rs.getInt("orders_this_month"));
                stats.put("totalProfit",      rs.getDouble("total_profit"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente statistik", e);
        }

        String userSql = "SELECT COUNT(*) FROM users WHERE role = 'customer'";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(userSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) stats.put("totalCustomers", rs.getInt(1));
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente brugerantal", e);
        }

        return stats;
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("address"),
                Integer.parseInt(rs.getString("zip")),
                rs.getString("city"),
                rs.getString("role")
        );
    }
}

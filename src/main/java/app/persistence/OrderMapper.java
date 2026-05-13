package app.persistence;

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
                SELECT order_id, status, total_price
                FROM orders
                WHERE customer_id = ?
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
            SELECT m.name, oi.quantity, oi.description, m.unit, m.price
            FROM order_items oi
            JOIN materials m ON oi.material_id = m.material_id
            WHERE oi.order_id = ?
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
                        rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrelinjer", e);
        }
        return lines;
    }

}

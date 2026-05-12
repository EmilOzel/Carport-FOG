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
                SELECT order_id, total_price,
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
                        rs.getDouble("tota_price"),
                        rs.getString("")
                });
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke hente ordrer", e);
        }
        return orders;
    }
}

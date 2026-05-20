package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static void updateUserRole(int userId, String newRole, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newRole);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke opdatere brugerrolle", e);
        }
    }

    private static User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getInt("phone"),
                rs.getString("address"),
                Integer.parseInt(rs.getString("zip")),
                rs.getString("city"),
                rs.getString("role")
        );
    }
}

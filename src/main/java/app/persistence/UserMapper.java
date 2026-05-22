package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserMapper {

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT u.user_id, u.email, u.password_hash, u.first_name, u.last_name, u.phone, u.address, u.role, u.zip, z.city " +
                     "FROM users u JOIN zipcode z ON u.zip = z.zip WHERE u.email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                 User user = mapRow(rs);

                // BCrypt password check
                if (BCrypt.checkpw(password, user.getPassword())) {
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved login", e);
        }
        throw new DatabaseException("Forkert email eller adgangskode");
    }

    public static void createUser(String email, String password, String firstName, String lastName, String address, String zip, int phone, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, address, role, zip,phone) VALUES (?, ?, ?, ?, ?,?, 'customer', ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt());

            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            ps.setString(5, address);
            ps.setString(6, zip);
            ps.setInt(7,phone);
            ps.executeUpdate();

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DatabaseException("Email er allerede i brug");
            }
            throw new DatabaseException("Kunne ikke oprette bruger", e);
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
                rs.getInt("zip"),
                rs.getString("city"),
                rs.getString("role")
        );
    }

    private static boolean zipExists(String zip, Connection connection) throws SQLException {
        String sql = "SELECT zip FROM zipcode WHERE zip = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, zip);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
    //
    //sds asdasda
}

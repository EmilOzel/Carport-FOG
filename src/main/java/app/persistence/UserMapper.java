package app.persistence;

import app.entities.Role;
import app.entities.User;
import app.exceptions.DatabaseException;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    private static User mapUser(ResultSet rs) throws SQLException {

        return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getInt("zipcode"),
                Role.valueOf(rs.getString("role").toUpperCase()),
                rs.getString("city")
        );
    }


    public static User login(String email, String password,
                             ConnectionPool connectionPool)
            throws DatabaseException {

        String sql = "SELECT * FROM customers WHERE email=?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                User user = mapUser(rs);

                if (BCrypt.checkpw(password, user.getPassword())) {
                    return user;
                }
            }

            throw new DatabaseException("Forkert email eller kodeord");

        } catch (SQLException e) {

            throw new DatabaseException("Databasefejl", e);
        }
    }


    public static void createUser(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO customers (email, password, role) VALUES (?, ?, 0, ?)";


        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ps.setString(3,Role.USER.name());
            ps.executeUpdate();

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DatabaseException("Email findes allerede");
            }

            throw new DatabaseException("Databasefejl", e);
        }
    }

    public static User getUserById(int userId, ConnectionPool connectionPool)
            throws DatabaseException {

        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return  mapUser(rs);
            }

            throw new DatabaseException("Bruger ikke fundet");

        } catch (SQLException e) {

            throw new DatabaseException("DB fejl", e);
        }
    }
    //Ssd

}


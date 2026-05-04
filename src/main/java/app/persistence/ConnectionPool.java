package app.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static ConnectionPool instance;
    private static HikariDataSource dataSource;

    private ConnectionPool() {
    }

    public static ConnectionPool getInstance(String user, String password, String url, String db) {
        if (instance == null) {
            dataSource = createHikariConnectionPool(user, password, url, db);
            instance = new ConnectionPool();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public synchronized void close() {
        dataSource.close();
    }

    private static HikariDataSource createHikariConnectionPool(String user, String password, String url, String db) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(String.format(url, db));
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(3);
        config.setPoolName("Postgresql Pool");
        return new HikariDataSource(config);
    }
}

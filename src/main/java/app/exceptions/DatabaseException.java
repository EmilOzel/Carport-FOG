package app.exceptions;

import java.sql.SQLException;

public class DatabaseException extends Exception {
    public DatabaseException(String userMessage) {
        super(userMessage);
    }

    public DatabaseException(String userMessage, String systemMessage) {
        super(userMessage + ": " + systemMessage);
    }

    public DatabaseException(String userMessage, SQLException exception) {
        super(userMessage, exception);
    }
}

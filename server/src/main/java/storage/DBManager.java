package storage;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBManager {
    private static final String PROPS_FILE = "db.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in =
                     DBManager.class.getClassLoader()
                             .getResourceAsStream(PROPS_FILE)) {
            Properties props = new Properties();
            props.load(in);
            url      = props.getProperty("db.url");
            user     = props.getProperty("db.user");
            password = props.getProperty("db.password");
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Cannot load DB properties: " + e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

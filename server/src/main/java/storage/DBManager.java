package storage;

import collection.*;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBManager {
    private static final String PROPS_FILE = "db.properties";
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = DBManager.class.getClassLoader().getResourceAsStream(PROPS_FILE)) {
            Properties props = new Properties();
            props.load(in);

            url = props.getProperty("db.url");
            user = resolve(props.getProperty("db.user"));
            password = resolve(props.getProperty("db.password"));

            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Cannot load DB properties: " + e);
        }
    }

    public static void requestStudyGroup(String query) {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to database successfully");

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    Coordinates coordinates = new Coordinates(
                            rs.getLong("x"),
                            rs.getFloat("y")
                    );

                    Person admin = new Person(
                            rs.getString("admin_name"),
                            rs.getDate("admin_birthday") != null ?
                                    rs.getDate("admin_birthday").toLocalDate().atStartOfDay() : null,
                            rs.getDouble("admin_height"),
                            rs.getString("admin_passport_id")
                    );

                    String ownerUsername = rs.getString("owner_username");

                    StudyGroup group = new StudyGroup(
                            rs.getInt("id"),
                            rs.getString("name"),
                            coordinates,
                            rs.getInt("students_count"),
                            FormOfEducation.valueOf(rs.getString("form_of_education")),
                            Semester.valueOf(rs.getString("semester")),
                            admin,
                            ownerUsername
                    );

                    Collection.getInstance().addElement(group);
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection or query error:");
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    public static void insertStudyGroup(StudyGroup studyGroup) {
        String query = "INSERT INTO study_group (name, x, y, creation_date, students_count, form_of_education, semester, admin_name, admin_birthday, admin_height, admin_passport_id, owner_username) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, studyGroup.getName());
            stmt.setDouble(2, studyGroup.getCoordinates().x());
            stmt.setFloat(3, studyGroup.getCoordinates().y());
            stmt.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate()));
            stmt.setInt(5, studyGroup.getStudentCount());
            stmt.setString(6, studyGroup.getFormOfEducation().toString());
            stmt.setString(7, studyGroup.getSemester().toString());
            stmt.setString(8, studyGroup.getGroupAdmin().name());
            stmt.setDate(9, studyGroup.getGroupAdmin().birthday() != null ? Date.valueOf(studyGroup.getGroupAdmin().birthday().toLocalDate()) : null);
            stmt.setDouble(10, studyGroup.getGroupAdmin().height());
            stmt.setString(11, studyGroup.getGroupAdmin().getBirthdayString());
            stmt.setString(12, studyGroup.getOwner());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting study group into database: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    public static Boolean isCorrectUser(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password);
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking password for user: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            return false;
        }
    }


    public static void insertAuthentication(Authentication auth) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, auth.name());
            stmt.setString(2, auth.password());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting authentication into database: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }


    private static String resolve(String value) {
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            String envVar = value.substring(2, value.length() - 1);
            String envValue = System.getenv(envVar);
            if (envValue == null) {
                throw new RuntimeException("Environment variable " + envVar + " not set");
            }
            return envValue;
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

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

    /**
     * Reads study groups from DB and populates the collection.
     */
    public static void requestStudyGroup(String query) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Connected to database successfully");
            while (rs.next()) {
                Coordinates coordinates = new Coordinates(
                        rs.getLong("x") == 0 ? null : rs.getLong("x"),
                        rs.getFloat("y") == 0 ? null : rs.getFloat("y")
                );
                Person admin = new Person(
                        rs.getString("admin_name"),
                        rs.getDate("admin_birthday") != null ?
                                rs.getDate("admin_birthday").toLocalDate().atStartOfDay() : null,
                        rs.getDouble("admin_height") == 0 ? null : rs.getDouble("admin_height"),
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
        } catch (SQLException e) {
            System.out.println("Connection or query error: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    public static boolean queryById(int id, String username, String query) {
        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            Logging.log(Logging.makeMessage("SQL error during work " + e.getMessage(), e.getStackTrace()));
            return false;
        }
    }

    public static boolean queryByOwner(String username, String query) {
        try (Connection connection = DBManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Logging.log(Logging.makeMessage("SQL error during work " + e.getMessage(), e.getStackTrace()));
            return false;
        }
    }

    /**
     * Inserts a new StudyGroup into the database.
     */
    public static void insertStudyGroup(StudyGroup studyGroup) {
        String sql = "INSERT INTO study_group (name, x, y, creation_date, students_count, form_of_education, " +
                "semester, admin_name, admin_birthday, admin_height, admin_passport_id, owner_username) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            prepareStudyGroupStatement(studyGroup, stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting study group: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Checks if a user exists by username.
     */
    public static boolean addUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            return false;
        }
    }

    /**
     * Validates username/password pair.
     */
    public static Boolean isCorrectUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password").equals(password);
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error checking credentials: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            return false;
        }
    }

    /**
     * Updates an existing study group by ID.
     */
    public static Boolean updateStudyGroup(StudyGroup studyGroup, String username) {
        String sql = "UPDATE study_group SET name = ?, x = ?, y = ?, creation_date = ?, students_count = ?, " +
                "form_of_education = ?, semester = ?, admin_name = ?, admin_birthday = ?, admin_height = ?, " +
                "admin_passport_id = ?, owner_username = ? WHERE id = ? AND owner_username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            prepareStudyGroupStatement(studyGroup, stmt);
            stmt.setInt(13, studyGroup.getId());
            stmt.setString(14, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating study group: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            return false;
        }
    }

    /**
     * Helper to set StudyGroup parameters on PreparedStatement.
     */
    private static void prepareStudyGroupStatement(StudyGroup studyGroup, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, studyGroup.getName());
        stmt.setObject(2, studyGroup.getCoordinates().x(), Types.BIGINT);
        stmt.setObject(3, studyGroup.getCoordinates().y(), Types.FLOAT);
        stmt.setTimestamp(4, Timestamp.valueOf(studyGroup.getCreationDate()));
        stmt.setInt(5, studyGroup.getStudentCount());
        stmt.setString(6, studyGroup.getFormOfEducation().toString());
        stmt.setString(7, studyGroup.getSemester().toString());
        stmt.setString(8, studyGroup.getGroupAdmin().name());
        stmt.setDate(9, studyGroup.getGroupAdmin().birthday() != null ? Date.valueOf(studyGroup.getGroupAdmin().birthday().toLocalDate()) : null);
        stmt.setObject(10, studyGroup.getGroupAdmin().height(), Types.DOUBLE);
        stmt.setString(11, studyGroup.getGroupAdmin().passportID());
        stmt.setString(12, studyGroup.getOwner());
    }

    private static String resolve(String value) {
        if (value != null && value.startsWith("${") && value.endsWith("}")) {
            String envVar = value.substring(2, value.length() - 1);
            String envValue = System.getenv(envVar);
            if (envValue == null) throw new RuntimeException("Missing env var: " + envVar);
            return envValue;
        }
        return value;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

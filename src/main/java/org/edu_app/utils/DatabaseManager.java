package org.edu_app.utils;
import org.edu_app.Main;
import org.edu_app.model.dto.SubmissionCreateDTO;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseManager {
    // Database connection properties
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/edu_app";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234";


    private Connection connection;

    public Connection GetConnection() {
        return this.connection;
    }

    public DatabaseManager() {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Establishes a connection to the database
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to PostgreSQL database successfully!");
        } catch (SQLException e) {
            System.err.println("Connection to PostgreSQL database failed!");
            e.printStackTrace();
        }
    }

    /**
     * Closes the database connection
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully!");
            } catch (SQLException e) {
                System.err.println("Failed to close database connection!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Executes an SQL script file
     *
     * @param scriptPath Path to the SQL script file
     * @return true if execution was successful, false otherwise
     */
    public boolean executeScriptFile(String scriptPath) {
        try {
            // Read SQL script file
            List<String> sqlStatements = readSqlFile(scriptPath);

            // Execute each SQL statement
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute(sql);
                        System.out.println("Successfully executed: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    }
                }
            }

            System.out.println("Script execution completed successfully!");
            return true;

        } catch (IOException e) {
            System.err.println("Error reading SQL script file: " + scriptPath);
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("Error executing SQL statement!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reads an SQL file and splits it into individual SQL statements
     *
     * @param filePath Path to the SQL file
     * @return List of SQL statements
     * @throws IOException If file cannot be read
     */
    private List<String> readSqlFile(String filePath) throws IOException {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }

                currentStatement.append(line).append(" ");

                // Check if the statement is complete (ends with semicolon)
                if (line.trim().endsWith(";")) {
                    sqlStatements.add(currentStatement.toString());
                    currentStatement = new StringBuilder();
                }
            }

            // Add the last statement if it doesn't end with semicolon
            if (currentStatement.length() > 0) {
                sqlStatements.add(currentStatement.toString());
            }
        }

        return sqlStatements;
    }


    /**
     * Inserts a submission into the database
     *
     * @param submission The submission data to insert
     * @return true if insertion was successful, false otherwise
     */
    public void insertSubmission(SubmissionCreateDTO submission) {
        String sql = "INSERT INTO submissions (submitted_at, student_comment, student_id, assignment_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Assuming your DTO has corresponding getter methods
            // Use current timestamp if not provided
            pstmt.setTimestamp(1, submission.getSubmittedAt() != null ?
                    Timestamp.valueOf(submission.getSubmittedAt()) :
                    new Timestamp(System.currentTimeMillis()));
            pstmt.setString(2, submission.getStudentComment());
            pstmt.setLong(3, submission.getStudentId());
            pstmt.setLong(4, submission.getAssignmentId());

            int rowsAffected = pstmt.executeUpdate();

            // Check if insertion was successful
            if (rowsAffected > 0) {
                Main.getLogger().info("Submission inserted successfully!");

                // If you need the generated submission ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long submissionId = generatedKeys.getLong(1);
                        Main.getLogger().info("Generated submission ID: " + submissionId);

                        // If your submission has files or other related data that needs to be inserted,
                        // you can use the submissionId here to insert them
                    }
                }

            }

        } catch (SQLException e) {
            Main.getLogger().error("Error inserting submission: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a UserDetailsService that validates against the database
     *
     * @return A UserDetailsService implementation that checks the database
     */
    public UserDetailsService createUserDetailsService() {
        return username -> {
            String sql = "SELECT * FROM users WHERE email = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // User exists, get their details
                        String email = rs.getString("email");
                        String password = rs.getString("password");
                        String role = rs.getString("role");

                        // Return user with appropriate configuration for the password encoder
                        // Since we're using our custom PepperedPasswordEncoder, we don't need
                        // to add any prefix - the encoder will handle the pepper
                        return User
                                .withUsername(email)
                                .password(password)
                                .roles(role)
                                .build();
                    }
                }
            } catch (SQLException e) {
                Main.getLogger().error("Error finding user: " + e.getMessage());
                e.printStackTrace();
            }

            throw new UsernameNotFoundException("User not found: " + username);
        };
    }
}
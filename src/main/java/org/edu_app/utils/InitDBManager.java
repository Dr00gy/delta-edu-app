package org.edu_app.utils;
import org.edu_app.Main;
import org.edu_app.model.dto.SubmissionCreateDTO;
import org.edu_app.model.dto.UserDTO;
import org.edu_app.model.entity.Role;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class InitDBManager {
    // Use Spring to inject values from the application.yaml
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.hikari.username}")
    private String dbUser;

    @Value("${spring.datasource.hikari.password}")
    private String dbPassword;

    private Connection connection;

    public Connection GetConnection() {
        return this.connection;
    }

    public InitDBManager() {
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
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
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
     * Creates a UserDetailsService that validates against the database
     *
     * @return A UserDetailsService implementation that checks the database
     */
    public UserDetailsService createUserDetailsService() {
        return username -> {
            String sql = "SELECT * FROM users WHERE email = ?";

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String email = rs.getString("email");
                        String password = rs.getString("password");
                        String role = rs.getString("role");

                        return User.withUsername(email)
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


    /**
     * Fetches user details from the database for loading assets based on authenticated user
     *
     * @param email The email of the user.
     * @return UserDTO containing user details.
     */
    public UserDTO getUserDetails(String email) {
        String sql = "SELECT id, email, first_name, last_name, role FROM users WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id");
                    String userEmail = rs.getString("email");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String roleName = rs.getString("role");

                    Role role = Role.valueOf(roleName.toUpperCase());
                    return new UserDTO(id, userEmail, firstName, lastName, role);
                }
            }
        } catch (SQLException e) {
            Main.getLogger().error("Error fetching user details: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
package com.cms.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/course_management_system";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";
    private static Connection connection = null;

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver explicitly (good practice for JDBC console apps)
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found. Add it to your dependencies.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed! Check output console.");
            e.printStackTrace();
        }
        return connection;
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Test connection failed: " + e.getMessage());
            return false;
        }
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}

package com.cms.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String url;
    private static String user;
    private static String password;

    static {
        // Tải cấu hình từ database.properties hoặc biến môi trường làm dự phòng
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            Properties prop = new Properties();
            if (input == null) {
                // Thử đọc từ biến môi trường
                url = System.getenv("DB_URL");
                user = System.getenv("DB_USER");
                password = System.getenv("DB_PASSWORD");
            } else {
                prop.load(input);
                url = prop.getProperty("db.url");
                user = prop.getProperty("db.username");
                password = prop.getProperty("db.password");
            }

            // Load driver explicitly (good practice for JDBC console apps)
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Không thể khởi tạo cấu hình cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    /**
     * Trả về kết nối mới mỗi khi được gọi để đảm bảo an toàn đa luồng
     * và tương thích hoàn toàn với cơ chế tự đóng của try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Test connection failed: " + e.getMessage());
            return false;
        }
    }

}

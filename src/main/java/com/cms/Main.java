package com.cms;

import com.cms.config.DatabaseConnection;
import com.cms.presentation.MenuController;

public class Main {
    public static void main(String[] args) {
        System.out.println("Đang khởi động hệ thống...");

        // Kiểm tra kết nối CSDL
        if (DatabaseConnection.testConnection()) {
            System.out.println("Kết nối cơ sở dữ liệu PostgreSQL thành công!");
            MenuController controller = new MenuController();
            controller.start();
        } else {
            System.err.println("Lỗi: Không thể kết nối tới cơ sở dữ liệu PostgreSQL!");
            System.err.println("Vui lòng kiểm tra lại dịch vụ cơ sở dữ liệu và cấu hình kết nối.");
        }

        // Close resources before shutdown
        DatabaseConnection.closeConnection();
    }
}

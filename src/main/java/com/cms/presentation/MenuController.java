package com.cms.presentation;

import com.cms.model.Role;
import com.cms.model.Student;
import com.cms.service.CourseService;
import com.cms.service.EnrollmentService;
import com.cms.service.StudentService;
import com.cms.util.InputValidator;

import java.util.Scanner;

public class MenuController {
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final Scanner scanner;
    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final AdminController adminController;
    private final StudentController studentController;
    private int failedAttempts = 0;

    public MenuController() {
        this.scanner = new Scanner(System.in);
        this.courseService = new CourseService();
        this.studentService = new StudentService();
        this.enrollmentService = new EnrollmentService();
        this.adminController = new AdminController(scanner, courseService, studentService, enrollmentService);
        this.studentController = new StudentController(scanner, courseService, studentService, enrollmentService);
    }

    public void start() {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("  HỆ THỐNG QUẢN LÝ KHÓA HỌC VÀ HỌC VIÊN");
            System.out.println("=============================================");
            System.out.println("[1] Đăng nhập hệ thống");
            System.out.println("[2] Thoát chương trình");
            System.out.println("---------------------------------------------");

            int choice = InputValidator.readInt(scanner, "Nhập lựa chọn của bạn (1-2): ", false);
            switch (choice) {
                case 1 -> login();
                case 2 -> {
                    System.out.println("Cảm ơn bạn đã sử dụng hệ thống! Tạm biệt.");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn lại (1-2).");
            }
        }
    }

    private void login() {
        System.out.println("\n--- ĐĂNG NHẬP HỆ THỐNG ---");
        String email = InputValidator.readString(scanner, "Nhập tài khoản (Email): ", false);
        String password = InputValidator.readString(scanner, "Nhập mật khẩu: ", false);

        Student student = studentService.login(email, password);
        if (student != null) {
            failedAttempts = 0;
            if (student.getRole() == Role.ADMIN) {
                System.out.println("\nĐăng nhập thành công! Chào mừng Administrator: " + student.getName());
                adminController.adminMenu(student);
            } else if (student.getRole() == Role.STUDENT) {
                System.out.println("\nĐăng nhập thành công! Chào mừng học viên: " + student.getName());
                studentController.studentMenu(student);
            } else {
                System.out.println("Lỗi: Quyền truy cập không hợp lệ!");
            }
        } else {
            failedAttempts++;
            int remaining = MAX_LOGIN_ATTEMPTS - failedAttempts;
            System.out.println("Lỗi: Tài khoản hoặc mật khẩu không chính xác!");
            if (remaining > 0) {
                System.out.println("Cảnh báo: Bạn còn " + remaining + " lần thử trước khi bị tạm khóa đăng nhập.");
            } else {
                System.out.println("\n[CẢNH BÁO AN NINH] Bạn đã đăng nhập sai quá " + MAX_LOGIN_ATTEMPTS + " lần liên tiếp!");
                System.out.println("Hệ thống tạm khóa tính năng đăng nhập. Vui lòng thử lại sau.");
                failedAttempts = 0;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

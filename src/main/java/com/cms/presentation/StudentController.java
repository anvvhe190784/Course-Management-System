package com.cms.presentation;

import com.cms.model.Course;
import com.cms.model.Enrollment;
import com.cms.model.Status;
import com.cms.model.Student;
import com.cms.service.CourseService;
import com.cms.service.EnrollmentService;
import com.cms.service.StudentService;
import com.cms.util.ConsoleTable;
import com.cms.util.InputValidator;
import com.cms.util.PaginationHelper;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class StudentController {
    private final Scanner scanner;
    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    public StudentController(Scanner scanner, CourseService courseService, StudentService studentService, EnrollmentService enrollmentService) {
        this.scanner = scanner;
        this.courseService = courseService;
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    public void studentMenu(Student student) {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("            MENU CHÍNH - HỌC VIÊN");
            System.out.println("=============================================");
            System.out.println("[1] Xem danh sách khóa học đang có (Phân trang & Gợi ý)");
            System.out.println("[2] Tìm kiếm khóa học theo tên");
            System.out.println("[3] Đăng ký khóa học");
            System.out.println("[4] Xem khóa học đã đăng ký (Có phân trang)");
            System.out.println("[5] Sắp xếp khóa học đã đăng ký");
            System.out.println("[6] Hủy đăng ký khóa học");
            System.out.println("[7] Đổi mật khẩu tài khoản");
            System.out.println("[8] Đăng xuất");
            System.out.println("---------------------------------------------");

            int choice = InputValidator.readInt(scanner, "Nhập lựa chọn của bạn (1-8): ", false);
            switch (choice) {
                case 1 -> viewAvailableCourses(student);
                case 2 -> searchCoursesForStudent();
                case 3 -> registerCourse(student);
                case 4 -> viewRegisteredCourses(student);
                case 5 -> sortRegisteredCourses(student);
                case 6 -> cancelRegistration(student);
                case 7 -> changeStudentPassword(student);
                case 8 -> {
                    System.out.println("Đã đăng xuất tài khoản Học viên.");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn lại (1-8).");
            }
        }
    }

    private void viewAvailableCourses(Student student) {
        System.out.println("\n--- DANH SÁCH KHÓA HỌC ĐANG CÓ ---");
        List<Course> allCourses = courseService.getAllCourses();

        // Bonus: Hiển thị các khóa học được thuật toán gợi ý riêng cho học viên
        List<Course> recommendations = enrollmentService.getRecommendedCourses(student.getId(), 3, allCourses);
        if (!recommendations.isEmpty()) {
            System.out.println("\n★ KHÓA HỌC GỢI Ý DÀNH RIÊNG CHO BẠN (Dựa trên lịch sử học tập & độ phổ biến) ★");
            ConsoleTable.printCourseTable(recommendations);
            System.out.println("--------------------------------------------------------------------------------");
        }

        PaginationHelper.paginate(scanner, "TẤT CẢ KHÓA HỌC", allCourses, PaginationHelper.DEFAULT_PAGE_SIZE, ConsoleTable::printCourseTable);
    }

    private void searchCoursesForStudent() {
        System.out.println("\n--- TÌM KIẾM KHÓA HỌC ---");
        String name = InputValidator.readString(scanner, "Nhập tên khóa học muốn tìm: ", false);
        List<Course> results = courseService.searchCoursesByName(name);
        System.out.println("\nKết quả tìm kiếm:");
        ConsoleTable.printCourseTable(results);
    }

    private void registerCourse(Student student) {
        System.out.println("\n--- ĐĂNG KÝ HỌC ---");
        List<Course> allCourses = courseService.getAllCourses();
        ConsoleTable.printCourseTable(allCourses);

        int courseId = InputValidator.readInt(scanner, "Nhập ID khóa học muốn đăng ký: ", true);
        try {
            if (enrollmentService.registerCourse(student.getId(), courseId)) {
                System.out.println("Đăng ký khóa học thành công! Yêu cầu của bạn đang chờ duyệt.");
            } else {
                System.out.println("Đăng ký khóa học thất bại!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }

    private void viewRegisteredCourses(Student student) {
        System.out.println("\n--- KHÓA HỌC BẠN ĐÃ ĐĂNG KÝ ---");
        List<Enrollment> list = enrollmentService.getEnrollmentsByStudent(student.getId());
        PaginationHelper.paginate(scanner, "KHÓA HỌC ĐÃ ĐĂNG KÝ", list, PaginationHelper.DEFAULT_PAGE_SIZE, this::printRegisteredCoursesTable);
    }

    private void printRegisteredCoursesTable(List<Enrollment> list) {
        ConsoleTable table = new ConsoleTable();
        table.addHeader("Mã KH (ID)");
        table.addHeader("Tên khóa học");
        table.addHeader("Thời lượng");
        table.addHeader("Giảng viên");
        table.addHeader("Ngày đăng ký");
        table.addHeader("Trạng thái");

        for (Enrollment e : list) {
            table.addRow(Arrays.asList(
                    String.valueOf(e.getCourseId()),
                    e.getCourse() != null ? e.getCourse().getName() : "N/A",
                    e.getCourse() != null ? e.getCourse().getDuration() + " giờ" : "N/A",
                    e.getCourse() != null ? e.getCourse().getInstructor() : "N/A",
                    e.getRegisteredAt() != null ? e.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                    e.getStatus() != null ? e.getStatus().getDisplayName() : "N/A"
            ));
        }
        table.print();
    }

    private void sortRegisteredCourses(Student student) {
        System.out.println("\n--- SẮP XẾP KHÓA HỌC ĐÃ ĐĂNG KÝ ---");
        System.out.println("[1] Sắp xếp theo Tên khóa học");
        System.out.println("[2] Sắp xếp theo Ngày đăng ký");
        int critChoice = InputValidator.readInt(scanner, "Chọn tiêu chí (1-2): ", false);
        String criteria = critChoice == 2 ? "date" : "name";

        System.out.println("[1] Tăng dần (Ascending)");
        System.out.println("[2] Giảm dần (Descending)");
        int orderChoice = InputValidator.readInt(scanner, "Chọn chiều (1-2): ", false);
        boolean ascending = orderChoice == 1;

        List<Enrollment> sorted = enrollmentService.sortRegisteredCourses(student.getId(), criteria, ascending);
        System.out.println("\nDanh sách khóa học đã đăng ký sau khi sắp xếp:");
        printRegisteredCoursesTable(sorted);
    }

    private void cancelRegistration(Student student) {
        System.out.println("\n--- HỦY ĐĂNG KÝ KHÓA HỌC ---");
        List<Enrollment> list = enrollmentService.getEnrollmentsByStudent(student.getId());
        printRegisteredCoursesTable(list);
        if (list.isEmpty()) {
            return;
        }

        int courseId = InputValidator.readInt(scanner, "Nhập ID khóa học muốn hủy đăng ký: ", true);
        boolean confirm = InputValidator.confirm(scanner, "Bạn chắc chắn muốn hủy đăng ký khóa học này (Y/N)?: ");
        if (confirm) {
            try {
                if (enrollmentService.cancelRegistration(student.getId(), courseId)) {
                    System.out.println("Hủy đăng ký khóa học thành công!");
                } else {
                    System.out.println("Hủy đăng ký thất bại!");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    private void changeStudentPassword(Student student) {
        System.out.println("\n--- ĐỔI MẬT KHẨU TÀI KHOẢN HỌC VIÊN ---");
        String oldPassword = InputValidator.readString(scanner, "Nhập mật khẩu cũ: ", false);
        String verification = InputValidator.readString(scanner, "Nhập Email hoặc Số điện thoại để xác thực: ", false);
        String newPassword = InputValidator.readString(scanner, "Nhập mật khẩu mới: ", false);

        if (newPassword.length() < 6) {
            System.out.println("Lỗi: Mật khẩu mới quá ngắn! Yêu cầu ít nhất 6 ký tự.");
            return;
        }

        try {
            if (studentService.changePassword(student.getId(), oldPassword, verification, newPassword)) {
                System.out.println("Đổi mật khẩu thành công!");
            } else {
                System.out.println("Đổi mật khẩu thất bại!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
}


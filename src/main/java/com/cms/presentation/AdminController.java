package com.cms.presentation;

import com.cms.model.*;
import com.cms.service.CourseService;
import com.cms.service.EnrollmentService;
import com.cms.service.StudentService;
import com.cms.util.ConsoleTable;
import com.cms.util.InputValidator;
import com.cms.util.PaginationHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdminController {
    private final Scanner scanner;
    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    public AdminController(Scanner scanner, CourseService courseService, StudentService studentService, EnrollmentService enrollmentService) {
        this.scanner = scanner;
        this.courseService = courseService;
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    public void adminMenu(Student admin) {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("              MENU CHÍNH - ADMIN");
            System.out.println("=============================================");
            System.out.println("[1] Quản lý Danh mục Khóa học");
            System.out.println("[2] Quản lý Thông tin Học viên");
            System.out.println("[3] Quản lý Đăng ký Khóa học");
            System.out.println("[4] Báo cáo Thống kê");
            System.out.println("[5] Đăng xuất");
            System.out.println("---------------------------------------------");

            int choice = InputValidator.readInt(scanner, "Nhập lựa chọn của bạn (1-5): ", false);
            switch (choice) {
                case 1 -> manageCourses();
                case 2 -> manageStudents();
                case 3 -> manageRegistrations();
                case 4 -> showStatistics();
                case 5 -> {
                    System.out.println("Đã đăng xuất tài khoản Admin.");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn lại (1-5).");
            }
        }
    }

    // ==========================================
    // QUẢN LÝ KHÓA HỌC
    // ==========================================

    private void manageCourses() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ DANH MỤC KHÓA HỌC (ADMIN) ---");
            System.out.println("[1] Hiển thị danh sách khóa học (Có phân trang)");
            System.out.println("[2] Thêm mới khóa học");
            System.out.println("[3] Chỉnh sửa thông tin khóa học");
            System.out.println("[4] Xóa khóa học theo ID");
            System.out.println("[5] Tìm kiếm khóa học theo tên");
            System.out.println("[6] Sắp xếp khóa học");
            System.out.println("[7] Quay về menu chính Admin");
            System.out.println("-----------------------------------------");

            int choice = InputValidator.readInt(scanner, "Chọn chức năng (1-7): ", false);
            switch (choice) {
                case 1 -> displayAllCoursesPaginated();
                case 2 -> addCourse();
                case 3 -> editCourse();
                case 4 -> deleteCourse();
                case 5 -> searchCourses();
                case 6 -> sortCourses();
                case 7 -> { return; }
                default -> System.out.println("Lựa chọn không hợp lệ! Vui lòng nhập từ 1 đến 7.");
            }
        }
    }

    public void displayAllCoursesPaginated() {
        List<Course> courses = courseService.getAllCourses();
        PaginationHelper.paginate(scanner, "DANH SÁCH KHÓA HỌC", courses, PaginationHelper.DEFAULT_PAGE_SIZE, this::printCourseTable);
    }

    public void printCourseTable(List<Course> courses) {
        ConsoleTable.printCourseTable(courses);
    }

    private void addCourse() {
        System.out.println("\n--- THÊM MỚI KHÓA HỌC ---");
        String name = InputValidator.readString(scanner, "Nhập tên khóa học: ", false);
        int duration = InputValidator.readInt(scanner, "Nhập thời lượng (giờ): ", true);
        String instructor = InputValidator.readString(scanner, "Nhập tên giảng viên: ", false);

        if (courseService.addCourse(name, duration, instructor)) {
            System.out.println("Thêm khóa học mới thành công!");
        } else {
            System.out.println("Thêm khóa học thất bại!");
        }
    }

    private void editCourse() {
        System.out.println("\n--- CHỈNH SỬA THÔNG TIN KHÓA HỌC ---");
        int id = InputValidator.readInt(scanner, "Nhập ID khóa học cần sửa: ", true);
        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("Lỗi: ID khóa học không tồn tại, vui lòng kiểm tra lại!");
            return;
        }

        while (true) {
            System.out.println("\nThông tin hiện tại của khóa học:");
            System.out.println("ID: " + course.getId() + " | Tên: " + course.getName() + " | Thời lượng: " + course.getDuration() + "h | Giảng viên: " + course.getInstructor());
            System.out.println("[1] Sửa tên khóa học");
            System.out.println("[2] Sửa thời lượng");
            System.out.println("[3] Sửa giảng viên");
            System.out.println("[4] Lưu và quay lại");
            System.out.println("[5] Hủy bỏ thay đổi và quay lại");
            int subChoice = InputValidator.readInt(scanner, "Nhập lựa chọn (1-5): ", false);

            switch (subChoice) {
                case 1 -> {
                    String newName = InputValidator.readString(scanner, "Nhập tên mới: ", false);
                    course.setName(newName);
                }
                case 2 -> {
                    int newDuration = InputValidator.readInt(scanner, "Nhập thời lượng mới (giờ): ", true);
                    course.setDuration(newDuration);
                }
                case 3 -> {
                    String newInstructor = InputValidator.readString(scanner, "Nhập giảng viên mới: ", false);
                    course.setInstructor(newInstructor);
                }
                case 4 -> {
                    if (courseService.updateCourse(course)) {
                        System.out.println("Cập nhật thông tin khóa học thành công!");
                    } else {
                        System.out.println("Cập nhật thông tin khóa học thất bại!");
                    }
                    return;
                }
                case 5 -> {
                    System.out.println("Đã hủy bỏ chỉnh sửa.");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void deleteCourse() {
        System.out.println("\n--- XÓA KHÓA HỌC ---");
        int id = InputValidator.readInt(scanner, "Nhập ID khóa học cần xóa: ", true);
        Course course = courseService.getCourseById(id);
        if (course == null) {
            System.out.println("Lỗi: ID khóa học không tồn tại!");
            return;
        }

        System.out.println("Cảnh báo: Xóa khóa học sẽ đồng thời xóa toàn bộ đăng ký liên quan!");
        boolean confirm = InputValidator.confirm(scanner, "Bạn có chắc chắn muốn xóa khóa học '" + course.getName() + "' (Y/N)?: ");
        if (confirm) {
            if (courseService.deleteCourse(id)) {
                System.out.println("Xóa khóa học thành công!");
            } else {
                System.out.println("Xóa khóa học thất bại!");
            }
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
    }

    private void searchCourses() {
        System.out.println("\n--- TÌM KIẾM KHÓA HỌC ---");
        String name = InputValidator.readString(scanner, "Nhập tên khóa học cần tìm: ", false);
        List<Course> results = courseService.searchCoursesByName(name);
        System.out.println("\nKết quả tìm kiếm cho '" + name + "':");
        printCourseTable(results);
    }

    private void sortCourses() {
        System.out.println("\n--- SẮP XẾP KHÓA HỌC ---");
        System.out.println("[1] Sắp xếp theo ID");
        System.out.println("[2] Sắp xếp theo Tên khóa học");
        int critChoice = InputValidator.readInt(scanner, "Chọn tiêu chí (1-2): ", false);
        String criteria = critChoice == 2 ? "name" : "id";

        System.out.println("[1] Tăng dần (Ascending)");
        System.out.println("[2] Giảm dần (Descending)");
        int orderChoice = InputValidator.readInt(scanner, "Chọn chiều sắp xếp (1-2): ", false);
        boolean ascending = orderChoice == 1;

        List<Course> sorted = courseService.sortCourses(criteria, ascending);
        System.out.println("\nDanh sách sau khi sắp xếp:");
        printCourseTable(sorted);
    }

    // ==========================================
    // QUẢN LÝ HỌC VIÊN
    // ==========================================

    private void manageStudents() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ THÔNG TIN HỌC VIÊN (ADMIN) ---");
            System.out.println("[1] Hiển thị danh sách học viên (Có phân trang)");
            System.out.println("[2] Thêm mới học viên");
            System.out.println("[3] Chỉnh sửa thông tin học viên");
            System.out.println("[4] Xóa học viên theo ID");
            System.out.println("[5] Tìm kiếm học viên");
            System.out.println("[6] Sắp xếp danh sách học viên");
            System.out.println("[7] Quay về menu chính Admin");
            System.out.println("-----------------------------------------");

            int choice = InputValidator.readInt(scanner, "Chọn chức năng (1-7): ", false);
            switch (choice) {
                case 1 -> displayAllStudentsPaginated();
                case 2 -> addStudent();
                case 3 -> editStudent();
                case 4 -> deleteStudent();
                case 5 -> searchStudents();
                case 6 -> sortStudents();
                case 7 -> { return; }
                default -> System.out.println("Lựa chọn không hợp lệ! Vui lòng nhập từ 1 đến 7.");
            }
        }
    }

    public void displayAllStudentsPaginated() {
        List<Student> students = studentService.getAllStudents();
        PaginationHelper.paginate(scanner, "DANH SÁCH HỌC VIÊN", students, PaginationHelper.DEFAULT_PAGE_SIZE, this::printStudentTable);
    }

    public void printStudentTable(List<Student> students) {
        ConsoleTable table = new ConsoleTable();
        table.addHeader("Mã HV (ID)");
        table.addHeader("Họ tên");
        table.addHeader("Ngày sinh");
        table.addHeader("Email");
        table.addHeader("Giới tính");
        table.addHeader("Số điện thoại");
        table.addHeader("Ngày tạo");

        for (Student s : students) {
            table.addRow(Arrays.asList(
                    String.valueOf(s.getId()),
                    s.getName(),
                    s.getDob() != null ? s.getDob().toString() : "N/A",
                    s.getEmail(),
                    s.isSex() ? "Nam" : "Nữ",
                    s.getPhone() != null ? s.getPhone() : "N/A",
                    s.getCreateAt() != null ? s.getCreateAt().toString() : "N/A"
            ));
        }
        table.print();
    }

    private void addStudent() {
        System.out.println("\n--- THÊM MỚI HỌC VIÊN ---");
        String name = InputValidator.readString(scanner, "Nhập họ tên học viên: ", false);
        LocalDate dob = InputValidator.readLocalDate(scanner, "Nhập ngày sinh (yyyy-MM-dd): ", false);

        String email;
        while (true) {
            email = InputValidator.readEmail(scanner, "Nhập email (Unique): ", false);
            if (studentService.isEmailExists(email)) {
                System.out.println("Lỗi: Email đã được sử dụng bởi một tài khoản khác! Vui lòng nhập lại.");
            } else {
                break;
            }
        }

        boolean sex = InputValidator.readSex(scanner, "Nhập giới tính (1: Nam, 0: Nữ): ");
        String phone = InputValidator.readPhone(scanner, "Nhập số điện thoại: ", true);
        String password = InputValidator.readString(scanner, "Nhập mật khẩu: ", false);

        if (studentService.addStudent(name, dob, email, sex, phone, password)) {
            System.out.println("Thêm học viên mới thành công!");
        } else {
            System.out.println("Thêm học viên thất bại!");
        }
    }

    private void editStudent() {
        System.out.println("\n--- CHỈNH SỬA THÔNG TIN HỌC VIÊN ---");
        int id = InputValidator.readInt(scanner, "Nhập ID học viên cần sửa: ", true);
        Student student = studentService.getStudentById(id);
        if (student == null || student.getRole() == Role.ADMIN) {
            System.out.println("Lỗi: ID học viên không tồn tại!");
            return;
        }

        while (true) {
            System.out.println("\nThông tin hiện tại của học viên:");
            System.out.println("ID: " + student.getId() + " | Tên: " + student.getName() + " | Email: " + student.getEmail() + " | SĐT: " + student.getPhone());
            System.out.println("[1] Sửa họ tên");
            System.out.println("[2] Sửa ngày sinh");
            System.out.println("[3] Sửa email");
            System.out.println("[4] Sửa giới tính");
            System.out.println("[5] Sửa số điện thoại");
            System.out.println("[6] Lưu và quay lại");
            System.out.println("[7] Hủy bỏ thay đổi");
            int subChoice = InputValidator.readInt(scanner, "Nhập lựa chọn (1-7): ", false);

            switch (subChoice) {
                case 1 -> {
                    String newName = InputValidator.readString(scanner, "Nhập họ tên mới: ", false);
                    student.setName(newName);
                }
                case 2 -> {
                    LocalDate newDob = InputValidator.readLocalDate(scanner, "Nhập ngày sinh mới (yyyy-MM-dd): ", false);
                    student.setDob(newDob);
                }
                case 3 -> {
                    String newEmail = InputValidator.readEmail(scanner, "Nhập email mới: ", false);
                    if (!newEmail.equalsIgnoreCase(student.getEmail()) && studentService.isEmailExists(newEmail)) {
                        System.out.println("Lỗi: Email đã được sử dụng!");
                    } else {
                        student.setEmail(newEmail);
                    }
                }
                case 4 -> {
                    boolean newSex = InputValidator.readSex(scanner, "Chọn giới tính (1: Nam, 0: Nữ): ");
                    student.setSex(newSex);
                }
                case 5 -> {
                    String newPhone = InputValidator.readPhone(scanner, "Nhập số điện thoại mới: ", true);
                    student.setPhone(newPhone);
                }
                case 6 -> {
                    if (studentService.updateStudent(student)) {
                        System.out.println("Cập nhật học viên thành công!");
                    } else {
                        System.out.println("Cập nhật học viên thất bại!");
                    }
                    return;
                }
                case 7 -> {
                    System.out.println("Đã hủy bỏ chỉnh sửa.");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void deleteStudent() {
        System.out.println("\n--- XÓA HỌC VIÊN ---");
        int id = InputValidator.readInt(scanner, "Nhập ID học viên cần xóa: ", true);
        Student student = studentService.getStudentById(id);
        if (student == null || student.getRole() == Role.ADMIN) {
            System.out.println("Lỗi: ID học viên không tồn tại!");
            return;
        }

        System.out.println("Cảnh báo: Xóa học viên sẽ đồng thời xóa toàn bộ đăng ký liên quan!");
        boolean confirm = InputValidator.confirm(scanner, "Bạn có chắc chắn muốn xóa học viên '" + student.getName() + "' (Y/N)?: ");
        if (confirm) {
            if (studentService.deleteStudent(id)) {
                System.out.println("Xóa học viên thành công!");
            } else {
                System.out.println("Xóa học viên thất bại!");
            }
        } else {
            System.out.println("Đã hủy thao tác xóa.");
        }
    }

    private void searchStudents() {
        System.out.println("\n--- TÌM KIẾM HỌC VIÊN ---");
        String query = InputValidator.readString(scanner, "Nhập ID, Họ tên hoặc Email cần tìm: ", false);
        List<Student> results = studentService.searchStudents(query);
        System.out.println("\nKết quả tìm kiếm cho '" + query + "':");
        printStudentTable(results);
    }

    private void sortStudents() {
        System.out.println("\n--- SẮP XẾP DANH SÁCH HỌC VIÊN ---");
        System.out.println("[1] Sắp xếp theo ID");
        System.out.println("[2] Sắp xếp theo Họ tên");
        int critChoice = InputValidator.readInt(scanner, "Chọn tiêu chí (1-2): ", false);
        String criteria = critChoice == 2 ? "name" : "id";

        System.out.println("[1] Tăng dần (Ascending)");
        System.out.println("[2] Giảm dần (Descending)");
        int orderChoice = InputValidator.readInt(scanner, "Chọn chiều sắp xếp (1-2): ", false);
        boolean ascending = orderChoice == 1;

        List<Student> sorted = studentService.sortStudents(criteria, ascending);
        System.out.println("\nDanh sách học viên sau khi sắp xếp:");
        printStudentTable(sorted);
    }

    // ==========================================
    // QUẢN LÝ ĐĂNG KÝ KHÓA HỌC
    // ==========================================

    private void manageRegistrations() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ ĐĂNG KÝ KHÓA HỌC (ADMIN) ---");
            System.out.println("[1] Xem danh sách sinh viên đăng ký theo khóa học");
            System.out.println("[2] Duyệt đăng ký khóa học của sinh viên");
            System.out.println("[3] Xóa sinh viên khỏi khóa học");
            System.out.println("[4] Quay về menu chính Admin");
            System.out.println("-----------------------------------------");

            int choice = InputValidator.readInt(scanner, "Chọn chức năng (1-4): ", false);
            switch (choice) {
                case 1 -> showRegistrationsByCourse();
                case 2 -> reviewEnrollmentRequest();
                case 3 -> removeStudentFromCourse();
                case 4 -> { return; }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showRegistrationsByCourse() {
        displayAllCoursesPaginated();
        int courseId = InputValidator.readInt(scanner, "Nhập ID khóa học cần xem danh sách học viên: ", true);
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            System.out.println("Lỗi: ID khóa học không tồn tại!");
            return;
        }

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        System.out.println("\nDANH SÁCH ĐĂNG KÝ CHO KHÓA HỌC: " + course.getName().toUpperCase());

        ConsoleTable table = new ConsoleTable();
        table.addHeader("Mã ĐK (ID)");
        table.addHeader("Mã HV (Student ID)");
        table.addHeader("Họ tên");
        table.addHeader("Email");
        table.addHeader("Ngày đăng ký");
        table.addHeader("Trạng thái");

        for (Enrollment e : enrollments) {
            table.addRow(Arrays.asList(
                    String.valueOf(e.getId()),
                    String.valueOf(e.getStudentId()),
                    e.getStudent() != null ? e.getStudent().getName() : "N/A",
                    e.getStudent() != null ? e.getStudent().getEmail() : "N/A",
                    e.getRegisteredAt() != null ? e.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A",
                    e.getStatus() != null ? e.getStatus().getDisplayName() : "N/A"
            ));
        }
        table.print();
    }

    private void reviewEnrollmentRequest() {
        List<Enrollment> all = enrollmentService.getAllEnrollments();
        List<Enrollment> waiting = all.stream()
                .filter(e -> e.getStatus() == Status.WAITING)
                .toList();

        if (waiting.isEmpty()) {
            System.out.println("\nKhông có yêu cầu đăng ký nào đang chờ duyệt.");
            return;
        }

        System.out.println("\nDanh sách yêu cầu đăng ký đang chờ duyệt:");
        ConsoleTable table = new ConsoleTable();
        table.addHeader("Mã ĐK (ID)");
        table.addHeader("Tên học viên");
        table.addHeader("Tên khóa học");
        table.addHeader("Ngày đăng ký");

        for (Enrollment e : waiting) {
            table.addRow(Arrays.asList(
                    String.valueOf(e.getId()),
                    e.getStudent() != null ? e.getStudent().getName() : "N/A",
                    e.getCourse() != null ? e.getCourse().getName() : "N/A",
                    e.getRegisteredAt() != null ? e.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A"
            ));
        }
        table.print();

        int id = InputValidator.readInt(scanner, "Nhập Mã đăng ký (ID) cần xử lý: ", true);
        Enrollment target = enrollmentService.getEnrollmentById(id);
        if (target == null) {
            System.out.println("Lỗi: Mã đăng ký không tồn tại!");
            return;
        }

        String studentName = target.getStudent() != null ? target.getStudent().getName() : "ID " + target.getStudentId();
        String courseName = target.getCourse() != null ? target.getCourse().getName() : "ID " + target.getCourseId();

        System.out.println("Bạn muốn duyệt hay từ chối đăng ký của học viên '" + studentName + "' vào khóa học '" + courseName + "'?");
        System.out.println("[1] Đồng ý duyệt (CONFIRM)");
        System.out.println("[2] Từ chối duyệt (DENIED)");
        System.out.println("[3] Hủy bỏ thao tác");
        int decision = InputValidator.readInt(scanner, "Nhập lựa chọn (1-3): ", false);

        Status newStatus = null;
        if (decision == 1) {
            newStatus = Status.CONFIRM;
        } else if (decision == 2) {
            newStatus = Status.DENIED;
        }

        if (newStatus != null) {
            try {
                if (enrollmentService.reviewEnrollment(id, newStatus)) {
                    System.out.println("Xử lý phê duyệt thành công!");
                } else {
                    System.out.println("Phê duyệt thất bại!");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } else {
            System.out.println("Đã hủy bỏ thao tác duyệt.");
        }
    }

    private void removeStudentFromCourse() {
        int id = InputValidator.readInt(scanner, "Nhập Mã đăng ký (ID) để xóa sinh viên khỏi khóa học: ", true);
        Enrollment target = enrollmentService.getEnrollmentById(id);
        if (target == null) {
            System.out.println("Lỗi: Mã đăng ký không tồn tại!");
            return;
        }

        String studentName = target.getStudent() != null ? target.getStudent().getName() : "Học viên ID " + target.getStudentId();
        String courseName = target.getCourse() != null ? target.getCourse().getName() : "Khóa học ID " + target.getCourseId();

        boolean confirm = InputValidator.confirm(scanner, "Bạn có chắc chắn muốn xóa học viên '" + studentName + "' khỏi khóa '" + courseName + "' (Y/N)?: ");
        if (confirm) {
            if (enrollmentService.removeStudentFromCourse(id)) {
                System.out.println("Xóa học viên khỏi khóa học thành công!");
            } else {
                System.out.println("Xóa thất bại!");
            }
        }
    }

    // ==========================================
    // BÁO CÁO THỐNG KÊ
    // ==========================================

    private void showStatistics() {
        while (true) {
            System.out.println("\n--- BÁO CÁO THỐNG KÊ (ADMIN) ---");
            System.out.println("[1] Thống kê tổng số lượng khóa học và tổng số học viên");
            System.out.println("[2] Thống kê tổng số học viên theo từng khóa học");
            System.out.println("[3] Thống kê Top 5 khóa học đông học viên nhất");
            System.out.println("[4] Liệt kê các khóa học có trên 10 học viên");
            System.out.println("[5] Quay về menu chính Admin");
            System.out.println("-----------------------------------------");

            int choice = InputValidator.readInt(scanner, "Nhập lựa chọn (1-5): ", false);
            switch (choice) {
                case 1 -> {
                    long totalCourses = enrollmentService.countTotalCourses();
                    long totalStudents = enrollmentService.countTotalStudents();
                    System.out.println("\nBÁO CÁO TỔNG QUAN:");
                    System.out.println("+---------------------------------------+-------+");
                    System.out.println("| Chỉ tiêu                              | Giá trị |");
                    System.out.println("+---------------------------------------+-------+");
                    System.out.printf("| Tổng số lượng khóa học                | %5d |\n", totalCourses);
                    System.out.printf("| Tổng số lượng học viên                | %5d |\n", totalStudents);
                    System.out.println("+---------------------------------------+-------+");
                }
                case 2 -> {
                    Map<String, Long> counts = enrollmentService.getStudentCountPerCourse();
                    System.out.println("\nTHỐNG KÊ HỌC VIÊN THEO TỪNG KHÓA HỌC (ĐÃ DUYỆT):");
                    ConsoleTable table = new ConsoleTable();
                    table.addHeader("Tên khóa học");
                    table.addHeader("Số học viên");
                    counts.forEach((cName, count) -> table.addRow(Arrays.asList(cName, String.valueOf(count))));
                    table.print();
                }
                case 3 -> {
                    List<Map.Entry<String, Long>> top5 = enrollmentService.getTop5PopularCourses();
                    System.out.println("\nTOP 5 KHÓA HỌC ĐÔNG HỌC VIÊN NHẤT (ĐÃ DUYỆT):");
                    ConsoleTable table = new ConsoleTable();
                    table.addHeader("Hạng");
                    table.addHeader("Tên khóa học");
                    table.addHeader("Số học viên");
                    int rank = 1;
                    for (Map.Entry<String, Long> entry : top5) {
                        table.addRow(Arrays.asList(String.valueOf(rank++), entry.getKey(), String.valueOf(entry.getValue())));
                    }
                    table.print();
                }
                case 4 -> {
                    List<Map.Entry<String, Long>> over10 = enrollmentService.getCoursesWithMoreThan10Students();
                    System.out.println("\nKHÓA HỌC CÓ TRÊN 10 HỌC VIÊN:");
                    if (over10.isEmpty()) {
                        System.out.println("Không có khóa học nào có trên 10 học viên.");
                    } else {
                        ConsoleTable table = new ConsoleTable();
                        table.addHeader("Tên khóa học");
                        table.addHeader("Số học viên");
                        for (Map.Entry<String, Long> entry : over10) {
                            table.addRow(Arrays.asList(entry.getKey(), String.valueOf(entry.getValue())));
                        }
                        table.print();
                    }
                }
                case 5 -> { return; }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }
}


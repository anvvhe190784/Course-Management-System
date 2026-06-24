package com.cms.presentation;

import com.cms.model.*;
import com.cms.service.CourseService;
import com.cms.service.EnrollmentService;
import com.cms.service.StudentService;
import com.cms.util.ConsoleTable;
import com.cms.util.InputValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuController {
    private final Scanner scanner = new Scanner(System.in);
    private final CourseService courseService = new CourseService();
    private final StudentService studentService = new StudentService();
    private final EnrollmentService enrollmentService = new EnrollmentService();

    public void start() {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("  HỆ THỐNG QUẢN LÝ KHÓA HỌC VÀ HỌC VIÊN");
            System.out.println("=============================================");
            System.out.println("[1] Đăng nhập Admin");
            System.out.println("[2] Đăng nhập Học viên");
            System.out.println("[3] Thoát chương trình");
            System.out.println("---------------------------------------------");
            
            int choice = InputValidator.readInt(scanner, "Nhập lựa chọn của bạn: ", false);
            switch (choice) {
                case 1 -> adminLogin();
                case 2 -> studentLogin();
                case 3 -> {
                    System.out.println("Cảm ơn bạn đã sử dụng hệ thống! Tạm biệt.");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn lại (1-3).");
            }
        }
    }

    // ==========================================
    // LOGIN FUNCTIONS
    // ==========================================

    private void adminLogin() {
        System.out.println("\n--- ĐĂNG NHẬP HỆ THỐNG (ADMIN) ---");
        String email = InputValidator.readString(scanner, "Nhập tài khoản (Email): ", false);
        String password = InputValidator.readString(scanner, "Nhập mật khẩu: ", false);

        Student admin = studentService.login(email, password);
        if (admin != null && admin.getRole() == Role.ADMIN) {
            System.out.println("\nĐăng nhập thành công! Chào mừng Administrator: " + admin.getName());
            adminMenu(admin);
        } else {
            System.out.println("Lỗi: Tài khoản hoặc mật khẩu Admin không chính xác!");
        }
    }

    private void studentLogin() {
        System.out.println("\n--- ĐĂNG NHẬP HỆ THỐNG (HỌC VIÊN) ---");
        String email = InputValidator.readString(scanner, "Nhập tài khoản (Email): ", false);
        String password = InputValidator.readString(scanner, "Nhập mật khẩu: ", false);

        Student student = studentService.login(email, password);
        if (student != null && student.getRole() == Role.STUDENT) {
            System.out.println("\nĐăng nhập thành công! Chào mừng học viên: " + student.getName());
            studentMenu(student);
        } else {
            System.out.println("Lỗi: Tài khoản hoặc mật khẩu Học viên không chính xác!");
        }
    }

    // ==========================================
    // MENUS
    // ==========================================

    private void adminMenu(Student admin) {
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

    private void studentMenu(Student student) {
        while (true) {
            System.out.println("\n=============================================");
            System.out.println("            MENU CHÍNH - HỌC VIÊN");
            System.out.println("=============================================");
            System.out.println("[1] Xem danh sách khóa học đang có");
            System.out.println("[2] Tìm kiếm khóa học theo tên");
            System.out.println("[3] Đăng ký khóa học");
            System.out.println("[4] Xem khóa học đã đăng ký");
            System.out.println("[5] Sắp xếp khóa học đã đăng ký");
            System.out.println("[6] Hủy đăng ký khóa học");
            System.out.println("[7] Đổi mật khẩu tài khoản");
            System.out.println("[8] Đăng xuất");
            System.out.println("---------------------------------------------");

            int choice = InputValidator.readInt(scanner, "Nhập lựa chọn của bạn (1-8): ", false);
            switch (choice) {
                case 1 -> viewAvailableCourses();
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

    // ==========================================
    // ADMIN SUB-MENUS & OPERATIONS
    // ==========================================

    private void manageCourses() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ DANH MỤC KHÓA HỌC (ADMIN) ---");
            System.out.println("[1] Hiển thị danh sách khóa học");
            System.out.println("[2] Thêm mới khóa học");
            System.out.println("[3] Chỉnh sửa thông tin khóa học");
            System.out.println("[4] Xóa khóa học theo ID");
            System.out.println("[5] Tìm kiếm khóa học theo tên");
            System.out.println("[6] Sắp xếp khóa học");
            System.out.println("[7] Quay về menu chính Admin");
            System.out.println("-----------------------------------------");

            int choice = InputValidator.readInt(scanner, "Chọn chức năng (1-7): ", false);
            switch (choice) {
                case 1 -> displayAllCoursesTable();
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

    private void displayAllCoursesTable() {
        List<Course> courses = courseService.getAllCourses();
        printCourseTable(courses);
    }

    private void printCourseTable(List<Course> courses) {
        ConsoleTable table = new ConsoleTable();
        table.addHeader("Mã KH (ID)");
        table.addHeader("Tên khóa học");
        table.addHeader("Thời lượng (giờ)");
        table.addHeader("Giảng viên");
        table.addHeader("Ngày tạo");

        for (Course c : courses) {
            table.addRow(Arrays.asList(
                    String.valueOf(c.getId()),
                    c.getName(),
                    String.valueOf(c.getDuration()),
                    c.getInstructor(),
                    c.getCreateAt().toString()
            ));
        }
        table.print();
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

    private void manageStudents() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ THÔNG TIN HỌC VIÊN (ADMIN) ---");
            System.out.println("[1] Hiển thị danh sách học viên");
            System.out.println("[2] Thêm mới học viên");
            System.out.println("[3] Chỉnh sửa thông tin học viên");
            System.out.println("[4] Xóa học viên theo ID");
            System.out.println("[5] Tìm kiếm học viên");
            System.out.println("[6] Sắp xếp danh sách học viên");
            System.out.println("[7] Quay về menu chính Admin");
            System.out.println("-----------------------------------------");

            int choice = InputValidator.readInt(scanner, "Chọn chức năng (1-7): ", false);
            switch (choice) {
                case 1 -> displayAllStudentsTable();
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

    private void displayAllStudentsTable() {
        List<Student> students = studentService.getAllStudents();
        printStudentTable(students);
    }

    private void printStudentTable(List<Student> students) {
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
                    s.getDob().toString(),
                    s.getEmail(),
                    s.isSex() ? "Nam" : "Nữ",
                    s.getPhone() != null ? s.getPhone() : "N/A",
                    s.getCreateAt().toString()
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
        displayAllCoursesTable();
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
                    e.getStudent().getName(),
                    e.getStudent().getEmail(),
                    e.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    translateStatus(e.getStatus())
            ));
        }
        table.print();
    }

    private void reviewEnrollmentRequest() {
        // Display all waiting registrations first to help admin choose
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
                    e.getStudent().getName(),
                    e.getCourse().getName(),
                    e.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
        table.print();

        int id = InputValidator.readInt(scanner, "Nhập Mã đăng ký (ID) cần xử lý: ", true);
        Enrollment target = enrollmentService.getEnrollmentById(id);
        if (target == null) {
            System.out.println("Lỗi: Mã đăng ký không tồn tại!");
            return;
        }

        System.out.println("Bạn muốn duyệt hay từ chối đăng ký của học viên '" + target.getStudent().getName() + "' vào khóa học '" + target.getCourse().getName() + "'?");
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
            if (enrollmentService.reviewEnrollment(id, newStatus)) {
                System.out.println("Xử lý phê duyệt thành công!");
            } else {
                System.out.println("Phê duyệt thất bại!");
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

        boolean confirm = InputValidator.confirm(scanner, "Bạn có chắc chắn muốn xóa học viên '" + target.getStudent().getName() + "' khỏi khóa '" + target.getCourse().getName() + "' (Y/N)?: ");
        if (confirm) {
            if (enrollmentService.removeStudentFromCourse(id)) {
                System.out.println("Xóa học viên khỏi khóa học thành công!");
            } else {
                System.out.println("Xóa thất bại!");
            }
        }
    }

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

    // ==========================================
    // STUDENT OPERATIONS
    // ==========================================

    private void viewAvailableCourses() {
        System.out.println("\n--- DANH SÁCH KHÓA HỌC ĐANG CÓ ---");
        displayAllCoursesTable();
    }

    private void searchCoursesForStudent() {
        System.out.println("\n--- TÌM KIẾM KHÓA HỌC ---");
        String name = InputValidator.readString(scanner, "Nhập tên khóa học muốn tìm: ", false);
        List<Course> results = courseService.searchCoursesByName(name);
        System.out.println("\nKết quả tìm kiếm:");
        printCourseTable(results);
    }

    private void registerCourse(Student student) {
        System.out.println("\n--- ĐĂNG KÝ HỌC ---");
        displayAllCoursesTable();
        int courseId = InputValidator.readInt(scanner, "Nhập ID khóa học muốn đăng ký: ", true);
        
        if (enrollmentService.registerCourse(student.getId(), courseId)) {
            System.out.println("Đăng ký khóa học thành công! Yêu cầu của bạn đang chờ duyệt.");
        } else {
            System.out.println("Đăng ký khóa học thất bại!");
        }
    }

    private void viewRegisteredCourses(Student student) {
        System.out.println("\n--- KHÓA HỌC BẠN ĐÃ ĐĂNG KÝ ---");
        List<Enrollment> list = enrollmentService.getEnrollmentsByStudent(student.getId());
        printRegisteredCoursesTable(list);
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
                    e.getCourse().getName(),
                    e.getCourse().getDuration() + " giờ",
                    e.getCourse().getInstructor(),
                    e.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    translateStatus(e.getStatus())
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
        viewRegisteredCourses(student);
        int courseId = InputValidator.readInt(scanner, "Nhập ID khóa học muốn hủy đăng ký: ", true);

        boolean confirm = InputValidator.confirm(scanner, "Bạn chắc chắn muốn hủy đăng ký khóa học này (Y/N)?: ");
        if (confirm) {
            if (enrollmentService.cancelRegistration(student.getId(), courseId)) {
                System.out.println("Hủy đăng ký khóa học thành công!");
            } else {
                System.out.println("Hủy đăng ký thất bại!");
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

        if (enrollmentService.registerCourse(0, 0)) { // Just double checking mock connectivity
            // Empty body
        }

        if (studentService.changePassword(student.getId(), oldPassword, verification, newPassword)) {
            System.out.println("Đổi mật khẩu thành công!");
        } else {
            System.out.println("Đổi mật khẩu thất bại! Vui lòng kiểm tra lại thông tin.");
        }
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private String translateStatus(Status status) {
        return switch (status) {
            case WAITING -> "Chờ duyệt";
            case CONFIRM -> "Đã xác nhận";
            case DENIED -> "Bị từ chối";
            case CANCEL -> "Đã hủy";
        };
    }
}

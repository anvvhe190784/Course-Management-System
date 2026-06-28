package com.cms.service;

import com.cms.dao.CourseDAO;
import com.cms.dao.EnrollmentDAO;
import com.cms.dao.impl.CourseDAOImpl;
import com.cms.dao.impl.EnrollmentDAOImpl;
import com.cms.model.Course;
import com.cms.model.Enrollment;
import com.cms.model.Status;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnrollmentService {
    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;
    private final StudentService studentService;

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.courseDAO = new CourseDAOImpl();
        this.studentService = new StudentService();
    }

    public EnrollmentService(EnrollmentDAO enrollmentDAO, CourseDAO courseDAO, StudentService studentService) {
        this.enrollmentDAO = enrollmentDAO;
        this.courseDAO = courseDAO;
        this.studentService = studentService;
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentDAO.getAll();
    }

    public Enrollment getEnrollmentById(int id) {
        return enrollmentDAO.getById(id);
    }

    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        return enrollmentDAO.getByCourseId(courseId);
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        return enrollmentDAO.getByStudentId(studentId);
    }

    /**
     * Register student for a course.
     * Validates if course exists and checks for duplicates.
     */
    public boolean registerCourse(int studentId, int courseId) {
        // Validate course existence
        Course course = courseDAO.getById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Mã khóa học không tồn tại!");
        }

        // Validate duplicates
        List<Enrollment> studentEnrollments = enrollmentDAO.getByStudentId(studentId);
        boolean alreadyEnrolled = studentEnrollments.stream()
                .anyMatch(e -> e.getCourseId() == courseId && e.getStatus() != Status.CANCEL);

        if (alreadyEnrolled) {
            throw new IllegalArgumentException("Bạn đã đăng ký khóa học này rồi!");
        }

        Enrollment enrollment = new Enrollment(studentId, courseId, Status.WAITING);
        return enrollmentDAO.insert(enrollment);
    }

    /**
     * Sort student's registered courses using Stream API.
     * Criteria can be: "name" (course name) or "date" (registration date).
     */
    public List<Enrollment> sortRegisteredCourses(int studentId, String criteria, boolean ascending) {
        List<Enrollment> enrollments = enrollmentDAO.getByStudentId(studentId);

        Comparator<Enrollment> comparator = switch (criteria.trim().toLowerCase()) {
            case "name" -> Comparator.comparing(e -> e.getCourse().getName());
            default -> Comparator.comparing(Enrollment::getRegisteredAt);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return enrollments.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Cancel registration by student.
     * Only permitted if status is WAITING.
     */
    public boolean cancelRegistration(int studentId, int courseId) {
        List<Enrollment> enrollments = enrollmentDAO.getByStudentId(studentId);
        Enrollment target = enrollments.stream()
                .filter(e -> e.getCourseId() == courseId)
                .findFirst()
                .orElse(null);

        if (target == null) {
            throw new IllegalArgumentException("Bạn chưa đăng ký khóa học này!");
        }

        if (target.getStatus() != Status.WAITING) {
            throw new IllegalArgumentException("Khóa học đã được duyệt hoặc từ chối, không thể hủy đăng ký!");
        }

        // Perform cancellation (we can update status to CANCEL or delete row.
        // According to DB check constraint, CANCEL is allowed, but the code says
        // delete.
        // Let's delete row as in the DAO definition, or update status.
        // In DB, deleting is clean and matches the DAO method deleteByStudentAndCourse)
        return enrollmentDAO.deleteByStudentAndCourse(studentId, courseId);
    }

    /**
     * Approve or deny enrollment request (Admin).
     */
    public boolean reviewEnrollment(int enrollmentId, Status newStatus) {
        Enrollment enrollment = enrollmentDAO.getById(enrollmentId);
        if (enrollment == null) {
            throw new IllegalArgumentException("Mã đăng ký không tồn tại!");
        }
        if (enrollment.getStatus() != Status.WAITING) {
            throw new IllegalStateException("Đăng ký này đã được xử lý trước đó!");
        }
        return enrollmentDAO.updateStatus(enrollmentId, newStatus);
    }

    /**
     * Remove student from course (Admin).
     */
    public boolean removeStudentFromCourse(int enrollmentId) {
        return enrollmentDAO.delete(enrollmentId);
    }

    // ==========================================
    // STATISTICS METHODS USING JAVA STREAM API
    // ==========================================

    /**
     * Thống kê tổng số lượng khóa học
     */
    public long countTotalCourses() {
        return courseDAO.getAll().stream().count();
    }

    /**
     * Thống kê tổng số học viên (role STUDENT)
     */
    public long countTotalStudents() {
        return studentService.getAllStudents().stream().count();
    }

    /**
     * Thống kê tổng số học viên (đã CONFIRM) theo từng khóa.
     * Trả về Map<Tên khóa học, Số học viên>
     */
    public Map<String, Long> getStudentCountPerCourse() {
        List<Enrollment> allEnrollments = enrollmentDAO.getAll();

        // Group by course name and count CONFIRM enrollments
        Map<String, Long> counts = allEnrollments.stream()
                .filter(e -> e.getStatus() == Status.CONFIRM)
                .collect(Collectors.groupingBy(
                        e -> e.getCourse().getName(),
                        Collectors.counting()));

        // Ensure courses with 0 students are also present in the result
        List<Course> allCourses = courseDAO.getAll();
        for (Course c : allCourses) {
            counts.putIfAbsent(c.getName(), 0L);
        }

        return counts;
    }

    /**
     * Thống kê top 5 khóa học đông sinh viên nhất (được CONFIRM)
     */
    public List<Map.Entry<String, Long>> getTop5PopularCourses() {
        Map<String, Long> counts = getStudentCountPerCourse();

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Liệt kê các khóa học có trên 10 học viên (được CONFIRM)
     */
    public List<Map.Entry<String, Long>> getCoursesWithMoreThan10Students() {
        Map<String, Long> counts = getStudentCountPerCourse();

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 10)
                .collect(Collectors.toList());
    }
}

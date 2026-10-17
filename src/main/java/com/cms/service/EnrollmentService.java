package com.cms.service;

import com.cms.dao.CourseDAO;
import com.cms.dao.EnrollmentDAO;
import com.cms.dao.impl.CourseDAOImpl;
import com.cms.dao.impl.EnrollmentDAOImpl;
import com.cms.model.Course;
import com.cms.model.Enrollment;
import com.cms.model.Role;
import com.cms.model.Status;
import com.cms.model.Student;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        if (studentService != null) {
            Student student = studentService.getStudentById(studentId);
            if (student != null && student.getRole() != Role.STUDENT) {
                throw new IllegalArgumentException("Tài khoản Quản trị viên không thể đăng ký khóa học!");
            }
        }

        // Validate course existence
        Course course = courseDAO.getById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Mã khóa học không tồn tại!");
        }

        // Validate duplicates and handle re-registration
        List<Enrollment> studentEnrollments = enrollmentDAO.getByStudentId(studentId);
        Enrollment existing = studentEnrollments.stream()
                .filter(e -> e.getCourseId() == courseId)
                .findFirst()
                .orElse(null);

        if (existing != null) {
            if (existing.getStatus() == Status.WAITING) {
                throw new IllegalArgumentException("Khóa học này đang chờ duyệt, bạn không thể đăng ký lại!");
            }
            if (existing.getStatus() == Status.CONFIRM) {
                throw new IllegalArgumentException("Bạn đã tham gia khóa học này rồi!");
            }
            // If previous enrollment was DENIED or CANCEL, allow re-registration by resetting to WAITING
            return enrollmentDAO.updateStatus(existing.getId(), Status.WAITING);
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

        return enrollmentDAO.updateStatus(target.getId(), Status.CANCEL);
    }

    /**
     * Approve or deny enrollment request (Admin).
     */
    public boolean reviewEnrollment(int enrollmentId, Status newStatus) {
        if (newStatus != Status.CONFIRM && newStatus != Status.DENIED) {
            throw new IllegalArgumentException("Trạng thái phê duyệt chỉ chấp nhận CONFIRM hoặc DENIED!");
        }
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

    /**
     * Thuật toán gợi ý khóa học cho học viên (Bonus feature):
     * 1. Lọc ra danh sách khóa học mà học viên CHƯA đăng ký (hoặc đã CANCEL).
     * 2. Ưu tiên giảng viên mà học viên đã học và được CONFIRM (+5 điểm).
     * 3. Ưu tiên thời lượng tương đồng (chênh lệch <= 15h) với thói quen học (+2 điểm).
     * 4. Ưu tiên khóa học có độ phổ biến cao trên toàn hệ thống (+1.5 điểm / học viên CONFIRM).
     */
    public List<Course> getRecommendedCourses(int studentId, int limit) {
        return getRecommendedCourses(studentId, limit, null);
    }

    public List<Course> getRecommendedCourses(int studentId, int limit, List<Course> preloadedCourses) {
        List<Enrollment> myEnrollments = enrollmentDAO.getByStudentId(studentId);
        Set<Integer> enrolledCourseIds = myEnrollments.stream()
                .filter(e -> e.getStatus() != Status.CANCEL)
                .map(Enrollment::getCourseId)
                .collect(Collectors.toSet());

        List<Course> allCourses = preloadedCourses != null ? preloadedCourses : courseDAO.getAll();
        List<Course> candidates = allCourses.stream()
                .filter(c -> !enrolledCourseIds.contains(c.getId()))
                .toList();

        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // Tập hợp giảng viên học viên từng học và đã CONFIRM
        Set<String> favoredInstructors = myEnrollments.stream()
                .filter(e -> e.getStatus() == Status.CONFIRM && e.getCourse() != null)
                .map(e -> e.getCourse().getInstructor().trim().toLowerCase())
                .collect(Collectors.toSet());

        // Thời lượng trung bình các khóa học viên đã được CONFIRM
        double avgDuration = myEnrollments.stream()
                .filter(e -> e.getStatus() == Status.CONFIRM && e.getCourse() != null)
                .mapToInt(e -> e.getCourse().getDuration())
                .average()
                .orElse(45.0);

        // Bản đồ độ phổ biến của từng khóa học trên toàn hệ thống
        Map<Integer, Long> popularCourseCounts = enrollmentDAO.getAll().stream()
                .filter(e -> e.getStatus() == Status.CONFIRM)
                .collect(Collectors.groupingBy(Enrollment::getCourseId, Collectors.counting()));

        // Sắp xếp các khóa học đề xuất theo điểm số giảm dần
        return candidates.stream()
                .sorted((c1, c2) -> {
                    double score1 = computeCourseScore(c1, favoredInstructors, avgDuration, popularCourseCounts);
                    double score2 = computeCourseScore(c2, favoredInstructors, avgDuration, popularCourseCounts);
                    return Double.compare(score2, score1);
                })
                .limit(limit > 0 ? limit : 3)
                .collect(Collectors.toList());
    }

    private double computeCourseScore(Course course, Set<String> favoredInstructors, double avgDuration, Map<Integer, Long> popularity) {
        double score = 0.0;
        // Trùng giảng viên yêu thích
        if (course.getInstructor() != null && favoredInstructors.contains(course.getInstructor().trim().toLowerCase())) {
            score += 5.0;
        }
        // Thời lượng tương đồng (+-15 giờ)
        double diff = Math.abs(course.getDuration() - avgDuration);
        if (diff <= 15.0) {
            score += 2.0;
        }
        // Độ phổ biến của khóa học
        long count = popularity.getOrDefault(course.getId(), 0L);
        score += count * 1.5;
        return score;
    }
}

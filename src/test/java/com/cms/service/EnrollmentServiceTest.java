package com.cms.service;

import com.cms.dao.CourseDAO;
import com.cms.dao.EnrollmentDAO;
import com.cms.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentDAO enrollmentDAO;

    @Mock
    private CourseDAO courseDAO;

    @Mock
    private StudentService studentService;

    private EnrollmentService enrollmentService;

    private Course course1;
    private Course course2;
    private Course course3;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(enrollmentDAO, courseDAO, studentService);

        course1 = new Course(10, "Java Core", 40, "Nguyen Van A", LocalDate.now());
        course2 = new Course(20, "Spring Cloud", 45, "Nguyen Van A", LocalDate.now());
        course3 = new Course(30, "ReactJS Master", 30, "Tran Thi B", LocalDate.now());
    }

    @Test
    void testRegisterCourse_Success() {
        when(courseDAO.getById(10)).thenReturn(course1);
        when(enrollmentDAO.getByStudentId(1)).thenReturn(Collections.emptyList());
        when(enrollmentDAO.insert(any(Enrollment.class))).thenReturn(true);

        boolean result = enrollmentService.registerCourse(1, 10);

        assertTrue(result);
        verify(enrollmentDAO, times(1)).insert(any(Enrollment.class));
    }

    @Test
    void testRegisterCourse_CourseNotFound_ThrowsException() {
        when(courseDAO.getById(99)).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.registerCourse(1, 99)
        );

        assertTrue(ex.getMessage().contains("không tồn tại"));
    }

    @Test
    void testRegisterCourse_AdminRole_ThrowsException() {
        when(courseDAO.getById(10)).thenReturn(course1);
        when(studentService.getStudentById(99)).thenReturn(
                new Student(99, "Admin", LocalDate.now(), "admin@cms.local", true, "0123456789", Role.ADMIN, "hash", LocalDate.now())
        );

        assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.registerCourse(99, 10)
        );
    }

    @Test
    void testRegisterCourse_AlreadyWaiting_ThrowsException() {
        when(courseDAO.getById(10)).thenReturn(course1);
        Enrollment waiting = new Enrollment(1, 1, 10, LocalDateTime.now(), Status.WAITING);
        when(enrollmentDAO.getByStudentId(1)).thenReturn(List.of(waiting));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.registerCourse(1, 10)
        );

        assertTrue(ex.getMessage().contains("đang chờ duyệt"));
    }

    @Test
    void testRegisterCourse_AlreadyConfirmed_ThrowsException() {
        when(courseDAO.getById(10)).thenReturn(course1);
        Enrollment confirmed = new Enrollment(1, 1, 10, LocalDateTime.now(), Status.CONFIRM);
        when(enrollmentDAO.getByStudentId(1)).thenReturn(List.of(confirmed));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.registerCourse(1, 10)
        );

        assertTrue(ex.getMessage().contains("đã tham gia"));
    }

    @Test
    void testRegisterCourse_ReEnrollAfterDenied_UpdatesStatusToWaiting() {
        when(courseDAO.getById(10)).thenReturn(course1);
        Enrollment denied = new Enrollment(5, 1, 10, LocalDateTime.now(), Status.DENIED);
        when(enrollmentDAO.getByStudentId(1)).thenReturn(List.of(denied));
        when(enrollmentDAO.updateStatus(5, Status.WAITING)).thenReturn(true);

        boolean result = enrollmentService.registerCourse(1, 10);

        assertTrue(result);
        verify(enrollmentDAO, times(1)).updateStatus(5, Status.WAITING);
        verify(enrollmentDAO, never()).insert(any(Enrollment.class));
    }

    @Test
    void testCancelRegistration_SuccessWhenWaiting() {
        Enrollment waiting = new Enrollment(1, 1, 10, LocalDateTime.now(), Status.WAITING);
        when(enrollmentDAO.getByStudentId(1)).thenReturn(List.of(waiting));
        when(enrollmentDAO.updateStatus(1, Status.CANCEL)).thenReturn(true);

        boolean result = enrollmentService.cancelRegistration(1, 10);

        assertTrue(result);
        verify(enrollmentDAO, times(1)).updateStatus(1, Status.CANCEL);
    }

    @Test
    void testCancelRegistration_FailsWhenConfirmed() {
        Enrollment confirmed = new Enrollment(1, 1, 10, LocalDateTime.now(), Status.CONFIRM);
        when(enrollmentDAO.getByStudentId(1)).thenReturn(List.of(confirmed));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.cancelRegistration(1, 10)
        );

        assertTrue(ex.getMessage().contains("không thể hủy"));
    }

    @Test
    void testReviewEnrollment_Success() {
        Enrollment waiting = new Enrollment(1, 1, 10, LocalDateTime.now(), Status.WAITING);
        when(enrollmentDAO.getById(1)).thenReturn(waiting);
        when(enrollmentDAO.updateStatus(1, Status.CONFIRM)).thenReturn(true);

        boolean result = enrollmentService.reviewEnrollment(1, Status.CONFIRM);

        assertTrue(result);
        verify(enrollmentDAO, times(1)).updateStatus(1, Status.CONFIRM);
    }

    @Test
    void testReviewEnrollment_InvalidTargetStatus_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.reviewEnrollment(1, Status.WAITING)
        );

        assertThrows(IllegalArgumentException.class, () ->
                enrollmentService.reviewEnrollment(1, Status.CANCEL)
        );
    }

    @Test
    void testReviewEnrollment_AlreadyProcessed_ThrowsException() {
        Enrollment confirmed = new Enrollment(1, 1, 10, LocalDateTime.now(), Status.CONFIRM);
        when(enrollmentDAO.getById(1)).thenReturn(confirmed);

        assertThrows(IllegalStateException.class, () ->
                enrollmentService.reviewEnrollment(1, Status.DENIED)
        );
    }

    @Test
    void testGetRecommendedCourses_PrioritizesSameInstructorAndPopularity() {
        // Student already confirmed course1 (Instructor: "Nguyen Van A", Duration: 40)
        Enrollment e1 = new Enrollment(1, 1, 10, LocalDateTime.now(), Status.CONFIRM);
        e1.setCourse(course1);
        when(enrollmentDAO.getByStudentId(1)).thenReturn(List.of(e1));

        // Available candidate courses: course2 (Instructor "Nguyen Van A") vs course3 ("Tran Thi B")
        when(courseDAO.getAll()).thenReturn(Arrays.asList(course1, course2, course3));
        when(enrollmentDAO.getAll()).thenReturn(Collections.emptyList());

        List<Course> recommendations = enrollmentService.getRecommendedCourses(1, 2);

        // Course 2 has same instructor "Nguyen Van A" -> higher score -> should rank 1st
        assertFalse(recommendations.isEmpty());
        assertEquals("Spring Cloud", recommendations.get(0).getName());
    }
}


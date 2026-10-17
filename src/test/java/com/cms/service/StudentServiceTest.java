package com.cms.service;

import com.cms.dao.StudentDAO;
import com.cms.model.Role;
import com.cms.model.Student;
import com.cms.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentDAO studentDAO;

    private StudentService studentService;

    private Student admin;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentDAO);

        String hashedPw = SecurityUtil.sha256("password123");

        admin = new Student(1, "Admin User", LocalDate.of(1990, 1, 1), "admin@cms.local", true, "0123456789", Role.ADMIN, hashedPw, LocalDate.now());
        student1 = new Student(2, "Nguyen Van An", LocalDate.of(2000, 5, 10), "an@gmail.com", true, "0912345678", Role.STUDENT, hashedPw, LocalDate.now());
        student2 = new Student(3, "Bui Thi Cuc", LocalDate.of(2001, 8, 20), "cuc@gmail.com", false, "0987654321", Role.STUDENT, hashedPw, LocalDate.now());
    }

    @Test
    void testGetAllStudents_ExcludesAdminRole() {
        when(studentDAO.getAll()).thenReturn(Arrays.asList(admin, student1, student2));

        List<Student> students = studentService.getAllStudents();

        assertEquals(2, students.size());
        assertTrue(students.stream().allMatch(s -> s.getRole() == Role.STUDENT));
    }

    @Test
    void testLogin_Success() {
        when(studentDAO.getByEmail("an@gmail.com")).thenReturn(student1);

        Student result = studentService.login("an@gmail.com", "password123");

        assertNotNull(result);
        assertEquals("Nguyen Van An", result.getName());
    }

    @Test
    void testLogin_WrongPassword() {
        when(studentDAO.getByEmail("an@gmail.com")).thenReturn(student1);

        Student result = studentService.login("an@gmail.com", "wrongpass");

        assertNull(result);
    }

    @Test
    void testLogin_UserNotFound() {
        when(studentDAO.getByEmail("unknown@gmail.com")).thenReturn(null);

        Student result = studentService.login("unknown@gmail.com", "password123");

        assertNull(result);
    }

    @Test
    void testLogin_NullParameters() {
        assertNull(studentService.login(null, "pass"));
        assertNull(studentService.login("email", null));
        assertNull(studentService.login(null, null));
    }

    @Test
    void testChangePassword_Success() {
        when(studentDAO.getById(2)).thenReturn(student1);
        when(studentDAO.update(any(Student.class))).thenReturn(true);

        boolean result = studentService.changePassword(2, "password123", "an@gmail.com", "newSecret123");

        assertTrue(result);
        assertTrue(SecurityUtil.verifyPassword("newSecret123", student1.getPassword()));
        assertTrue(student1.getPassword().contains("$"));
        verify(studentDAO, times(1)).update(student1);
    }

    @Test
    void testChangePassword_WrongOldPassword_ThrowsException() {
        when(studentDAO.getById(2)).thenReturn(student1);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                studentService.changePassword(2, "wrongold", "an@gmail.com", "newSecret123")
        );

        assertTrue(ex.getMessage().contains("Mật khẩu cũ không chính xác"));
    }

    @Test
    void testChangePassword_MismatchedVerification_ThrowsException() {
        when(studentDAO.getById(2)).thenReturn(student1);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                studentService.changePassword(2, "password123", "wrongemail@gmail.com", "newSecret123")
        );

        assertTrue(ex.getMessage().contains("Thông tin xác thực"));
    }

    @Test
    void testDeleteStudent_AdminAccount_ReturnsFalse() {
        when(studentDAO.getById(1)).thenReturn(admin);

        assertFalse(studentService.deleteStudent(1));
        verify(studentDAO, never()).delete(1);
    }

    @Test
    void testUpdateStudent_AdminAccount_ReturnsFalse() {
        assertFalse(studentService.updateStudent(admin));
        verify(studentDAO, never()).update(any());
    }

    @Test
    void testSearchStudents_ByNameAndEmail() {
        when(studentDAO.getAll()).thenReturn(Arrays.asList(admin, student1, student2));

        List<Student> resultsByName = studentService.searchStudents("cuc");
        assertEquals(1, resultsByName.size());
        assertEquals("Bui Thi Cuc", resultsByName.get(0).getName());

        List<Student> resultsByEmail = studentService.searchStudents("an@gmail.com");
        assertEquals(1, resultsByEmail.size());
        assertEquals("Nguyen Van An", resultsByEmail.get(0).getName());
    }

    @Test
    void testSortStudents_ByNameAscending() {
        when(studentDAO.getAll()).thenReturn(Arrays.asList(admin, student1, student2));

        List<Student> sorted = studentService.sortStudents("name", true);

        assertEquals("Bui Thi Cuc", sorted.get(0).getName());
        assertEquals("Nguyen Van An", sorted.get(1).getName());
    }
}


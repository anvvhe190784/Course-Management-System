package com.cms.service;

import com.cms.dao.StudentDAO;
import com.cms.dao.impl.StudentDAOImpl;
import com.cms.model.Role;
import com.cms.model.Student;
import com.cms.util.SecurityUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public List<Student> getAllStudents() {
        // Exclude ADMIN role from general student lists for management purposes
        return studentDAO.getAll().stream()
                .filter(s -> s.getRole() == Role.STUDENT)
                .collect(Collectors.toList());
    }

    public Student getStudentById(int id) {
        return studentDAO.getById(id);
    }

    public boolean addStudent(String name, LocalDate dob, String email, boolean sex, String phone, String password) {
        // Automatically hash password in SHA-256 before inserting
        String hashedPassword = SecurityUtil.sha256(password);
        Student student = new Student(name, dob, email, sex, phone, Role.STUDENT, hashedPassword);
        return studentDAO.insert(student);
    }

    public boolean updateStudent(Student student) {
        // If password is changed, it should already be hashed or we hash it here if it's plain text.
        // In our app, editing password will go through a specific change password logic, or we hash it.
        // Let's assume the password is saved as is, so if the user updates password in edit, it should be hashed.
        return studentDAO.update(student);
    }

    public boolean deleteStudent(int id) {
        return studentDAO.delete(id);
    }

    /**
     * Authenticate user login (both Admin and Student).
     * Compares email/username and SHA-256 hashed password.
     */
    public Student login(String usernameOrEmail, String password) {
        Student user = studentDAO.getByEmail(usernameOrEmail);
        if (user != null) {
            String hashedInput = SecurityUtil.sha256(password);
            if (user.getPassword().equals(hashedInput)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Change student password with verification of old password and email/phone.
     */
    public boolean changePassword(int studentId, String oldPassword, String verificationInfo, String newPassword) {
        Student student = studentDAO.getById(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Học viên không tồn tại!");
        }

        // Verify old password
        String hashedOld = SecurityUtil.sha256(oldPassword);
        if (!student.getPassword().equals(hashedOld)) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác!");
        }

        // Verify email or phone (verificationInfo can match either)
        boolean verified = (student.getEmail().equalsIgnoreCase(verificationInfo) || 
                            (student.getPhone() != null && student.getPhone().equals(verificationInfo)));
        
        if (!verified) {
            throw new IllegalArgumentException("Thông tin xác thực (Email hoặc Số điện thoại) không khớp!");
        }

        // Hash new password and update
        student.setPassword(SecurityUtil.sha256(newPassword));
        return studentDAO.update(student);
    }

    /**
     * Search students by name, email, or ID using Java Stream API.
     */
    public List<Student> searchStudents(String query) {
        List<Student> allStudents = getAllStudents();
        String searchStr = query.trim().toLowerCase();
        
        return allStudents.stream()
                .filter(s -> s.getName().toLowerCase().contains(searchStr) ||
                             s.getEmail().toLowerCase().contains(searchStr) ||
                             String.valueOf(s.getId()).equals(searchStr))
                .collect(Collectors.toList());
    }

    /**
     * Sort students using Java Stream API.
     * Criteria can be: "id" or "name".
     */
    public List<Student> sortStudents(String criteria, boolean ascending) {
        List<Student> allStudents = getAllStudents();
        Comparator<Student> comparator = switch (criteria.trim().toLowerCase()) {
            case "name" -> Comparator.comparing(Student::getName);
            default -> Comparator.comparing(Student::getId);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return allStudents.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public boolean isEmailExists(String email) {
        return studentDAO.getByEmail(email) != null;
    }
}

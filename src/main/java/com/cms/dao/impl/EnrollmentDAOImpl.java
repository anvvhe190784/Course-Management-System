package com.cms.dao.impl;

import com.cms.config.DatabaseConnection;
import com.cms.dao.EnrollmentDAO;
import com.cms.model.Course;
import com.cms.model.Enrollment;
import com.cms.model.Role;
import com.cms.model.Status;
import com.cms.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    @Override
    public List<Enrollment> getAll() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.id, e.student_id, e.course_id, e.registered_at, e.status, " +
                     "s.name as student_name, s.dob as student_dob, s.email as student_email, s.sex as student_sex, s.phone as student_phone, s.role as student_role, s.create_at as student_create_at, " +
                     "c.name as course_name, c.duration as course_duration, c.instructor as course_instructor, c.create_at as course_create_at " +
                     "FROM enrollment e " +
                     "JOIN student s ON e.student_id = s.id " +
                     "JOIN course c ON e.course_id = c.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapFullRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách đăng ký: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Enrollment getById(int id) {
        String sql = "SELECT e.id, e.student_id, e.course_id, e.registered_at, e.status, " +
                     "s.name as student_name, s.dob as student_dob, s.email as student_email, s.sex as student_sex, s.phone as student_phone, s.role as student_role, s.create_at as student_create_at, " +
                     "c.name as course_name, c.duration as course_duration, c.instructor as course_instructor, c.create_at as course_create_at " +
                     "FROM enrollment e " +
                     "JOIN student s ON e.student_id = s.id " +
                     "JOIN course c ON e.course_id = c.id " +
                     "WHERE e.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapFullRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm đăng ký theo ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Enrollment> getByStudentId(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.id, e.student_id, e.course_id, e.registered_at, e.status, " +
                     "c.name as course_name, c.duration as course_duration, c.instructor as course_instructor, c.create_at as course_create_at " +
                     "FROM enrollment e " +
                     "JOIN course c ON e.course_id = c.id " +
                     "WHERE e.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getTimestamp("registered_at").toLocalDateTime(),
                            Status.valueOf(rs.getString("status"))
                    );
                    Date courseCreateDate = rs.getDate("course_create_at");
                    Course c = new Course(
                            rs.getInt("course_id"),
                            rs.getString("course_name"),
                            rs.getInt("course_duration"),
                            rs.getString("course_instructor"),
                            courseCreateDate != null ? courseCreateDate.toLocalDate() : null
                    );
                    e.setCourse(c);
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải đăng ký theo ID học viên: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Enrollment> getByCourseId(int courseId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT e.id, e.student_id, e.course_id, e.registered_at, e.status, " +
                     "s.name as student_name, s.dob as student_dob, s.email as student_email, s.sex as student_sex, s.phone as student_phone, s.role as student_role, s.create_at as student_create_at " +
                     "FROM enrollment e " +
                     "JOIN student s ON e.student_id = s.id " +
                     "WHERE e.course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getTimestamp("registered_at").toLocalDateTime(),
                            Status.valueOf(rs.getString("status"))
                    );
                    Date dobDate = rs.getDate("student_dob");
                    Date createDate = rs.getDate("student_create_at");
                    Student s = new Student(
                            rs.getInt("student_id"),
                            rs.getString("student_name"),
                            dobDate != null ? dobDate.toLocalDate() : null,
                            rs.getString("student_email"),
                            rs.getBoolean("student_sex"),
                            rs.getString("student_phone"),
                            Role.valueOf(rs.getString("student_role")),
                            null, // Do not load password for lists
                            createDate != null ? createDate.toLocalDate() : null
                    );
                    e.setStudent(s);
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải đăng ký theo ID khóa học: " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean insert(Enrollment enrollment) {
        String sql = "INSERT INTO enrollment (student_id, course_id, registered_at, status) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setString(3, enrollment.getStatus().name());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm đăng ký mới: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateStatus(int id, Status status) {
        String sql = status == Status.WAITING
                ? "UPDATE enrollment SET status = ?, registered_at = CURRENT_TIMESTAMP WHERE id = ?"
                : "UPDATE enrollment SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái đăng ký: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM enrollment WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa đăng ký: " + e.getMessage());
            return false;
        }
    }

    private Enrollment mapFullRow(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment(
                rs.getInt("id"),
                rs.getInt("student_id"),
                rs.getInt("course_id"),
                rs.getTimestamp("registered_at").toLocalDateTime(),
                Status.valueOf(rs.getString("status"))
        );

        Date studentDob = rs.getDate("student_dob");
        Date studentCreateAt = rs.getDate("student_create_at");
        Student s = new Student(
                rs.getInt("student_id"),
                rs.getString("student_name"),
                studentDob != null ? studentDob.toLocalDate() : null,
                rs.getString("student_email"),
                rs.getBoolean("student_sex"),
                rs.getString("student_phone"),
                Role.valueOf(rs.getString("student_role")),
                null, // Password is not selected and should never leak in enrollment lists
                studentCreateAt != null ? studentCreateAt.toLocalDate() : null
        );

        Date courseCreateAt = rs.getDate("course_create_at");
        Course c = new Course(
                rs.getInt("course_id"),
                rs.getString("course_name"),
                rs.getInt("course_duration"),
                rs.getString("course_instructor"),
                courseCreateAt != null ? courseCreateAt.toLocalDate() : null
        );

        e.setStudent(s);
        e.setCourse(c);
        return e;
    }
}

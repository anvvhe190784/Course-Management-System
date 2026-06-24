package com.cms.dao.impl;

import com.cms.config.DatabaseConnection;
import com.cms.dao.StudentDAO;
import com.cms.model.Role;
import com.cms.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, create_at FROM student";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Student student = mapRow(rs);
                students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách học viên: " + e.getMessage());
        }
        return students;
    }

    @Override
    public Student getById(int id) {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, create_at FROM student WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm học viên theo ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Student getByEmail(String email) {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, create_at FROM student WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm học viên theo Email: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insert(Student student) {
        String sql = "INSERT INTO student (name, dob, email, sex, phone, role, password, create_at) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setDate(2, Date.valueOf(student.getDob()));
            stmt.setString(3, student.getEmail());
            stmt.setBoolean(4, student.isSex());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getRole().name());
            stmt.setString(7, student.getPassword());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm học viên mới: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Student student) {
        String sql = "UPDATE student SET name = ?, dob = ?, email = ?, sex = ?, phone = ?, role = ?, password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setDate(2, Date.valueOf(student.getDob()));
            stmt.setString(3, student.getEmail());
            stmt.setBoolean(4, student.isSex());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getRole().name());
            stmt.setString(7, student.getPassword());
            stmt.setInt(8, student.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thông tin học viên: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa học viên: " + e.getMessage());
            return false;
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Date dobDate = rs.getDate("dob");
        Date createDate = rs.getDate("create_at");
        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                dobDate != null ? dobDate.toLocalDate() : null,
                rs.getString("email"),
                rs.getBoolean("sex"),
                rs.getString("phone"),
                Role.valueOf(rs.getString("role")),
                rs.getString("password"),
                createDate != null ? createDate.toLocalDate() : null
        );
    }
}

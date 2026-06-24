package com.cms.dao.impl;

import com.cms.config.DatabaseConnection;
import com.cms.dao.CourseDAO;
import com.cms.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public List<Course> getAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, name, duration, instructor, create_at FROM course";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("duration"),
                        rs.getString("instructor"),
                        rs.getDate("create_at").toLocalDate()
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải danh sách khóa học: " + e.getMessage());
        }
        return courses;
    }

    @Override
    public Course getById(int id) {
        String sql = "SELECT id, name, duration, instructor, create_at FROM course WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("duration"),
                            rs.getString("instructor"),
                            rs.getDate("create_at").toLocalDate()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm khóa học theo ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insert(Course course) {
        String sql = "INSERT INTO course (name, duration, instructor, create_at) VALUES (?, ?, ?, CURRENT_DATE)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getDuration());
            stmt.setString(3, course.getInstructor());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm khóa học mới: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Course course) {
        String sql = "UPDATE course SET name = ?, duration = ?, instructor = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getDuration());
            stmt.setString(3, course.getInstructor());
            stmt.setInt(4, course.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật khóa học: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM course WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa khóa học: " + e.getMessage());
            return false;
        }
    }
}

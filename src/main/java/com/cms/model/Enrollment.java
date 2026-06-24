package com.cms.model;

import java.time.LocalDateTime;

public class Enrollment {
    private int id;
    private int studentId;
    private int courseId;
    private LocalDateTime registeredAt;
    private Status status;

    // Associated objects for join queries
    private Student student;
    private Course course;

    public Enrollment() {
    }

    public Enrollment(int studentId, int courseId, Status status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
    }

    public Enrollment(int id, int studentId, int courseId, LocalDateTime registeredAt, Status status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.registeredAt = registeredAt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", registeredAt=" + registeredAt +
                ", status=" + status +
                '}';
    }
}

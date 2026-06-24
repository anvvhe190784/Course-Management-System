package com.cms.dao;

import com.cms.model.Enrollment;
import com.cms.model.Status;
import java.util.List;

public interface EnrollmentDAO {
    List<Enrollment> getAll();
    Enrollment getById(int id);
    List<Enrollment> getByStudentId(int studentId);
    List<Enrollment> getByCourseId(int courseId);
    boolean insert(Enrollment enrollment);
    boolean updateStatus(int id, Status status);
    boolean delete(int id);
    boolean deleteByStudentAndCourse(int studentId, int courseId);
}

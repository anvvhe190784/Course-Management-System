package com.cms.dao;

import com.cms.model.Course;
import java.util.List;

public interface CourseDAO {
    List<Course> getAll();
    Course getById(int id);
    boolean insert(Course course);
    boolean update(Course course);
    boolean delete(int id);
}

package com.cms.dao;

import com.cms.model.Student;
import java.util.List;

public interface StudentDAO {
    List<Student> getAll();
    Student getById(int id);
    Student getByEmail(String email);
    boolean insert(Student student);
    boolean update(Student student);
    boolean delete(int id);
}

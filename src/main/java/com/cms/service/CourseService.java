package com.cms.service;

import com.cms.dao.CourseDAO;
import com.cms.dao.impl.CourseDAOImpl;
import com.cms.model.Course;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CourseService {
    private final CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAOImpl();
    }

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAll();
    }

    public Course getCourseById(int id) {
        return courseDAO.getById(id);
    }

    public boolean addCourse(String name, int duration, String instructor) {
        Course course = new Course(name, duration, instructor);
        return courseDAO.insert(course);
    }

    public boolean updateCourse(Course course) {
        return courseDAO.update(course);
    }

    public boolean deleteCourse(int id) {
        return courseDAO.delete(id);
    }

    /**
     * Search courses by name using Java Stream API.
     * Uses fuzzy case-insensitive matching.
     */
    public List<Course> searchCoursesByName(String name) {
        List<Course> allCourses = courseDAO.getAll();
        String searchStr = name.trim().toLowerCase();
        return allCourses.stream()
                .filter(course -> course.getName().toLowerCase().contains(searchStr))
                .collect(Collectors.toList());
    }

    /**
     * Sort courses using Java Stream API.
     * Criteria can be: "id" or "name".
     */
    public List<Course> sortCourses(String criteria, boolean ascending) {
        List<Course> allCourses = courseDAO.getAll();
        Comparator<Course> comparator = switch (criteria.trim().toLowerCase()) {
            case "name" -> Comparator.comparing(Course::getName);
            default -> Comparator.comparing(Course::getId);
        };

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return allCourses.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}

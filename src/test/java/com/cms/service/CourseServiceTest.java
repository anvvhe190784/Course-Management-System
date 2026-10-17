package com.cms.service;

import com.cms.dao.CourseDAO;
import com.cms.model.Course;
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
public class CourseServiceTest {

    @Mock
    private CourseDAO courseDAO;

    private CourseService courseService;

    private List<Course> mockCourses;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseDAO);
        mockCourses = Arrays.asList(
                new Course(1, "Java Core Programming", 45, "Nguyen Van A", LocalDate.now()),
                new Course(2, "Spring Boot Microservices", 60, "Tran Thi B", LocalDate.now()),
                new Course(3, "PostgreSQL for Developers", 30, "Nguyen Van A", LocalDate.now()),
                new Course(4, "Algorithms & Data Structures", 50, "Le Van C", LocalDate.now())
        );
    }

    @Test
    void testGetAllCourses() {
        when(courseDAO.getAll()).thenReturn(mockCourses);

        List<Course> result = courseService.getAllCourses();

        assertEquals(4, result.size());
        verify(courseDAO, times(1)).getAll();
    }

    @Test
    void testGetCourseById() {
        when(courseDAO.getById(1)).thenReturn(mockCourses.get(0));

        Course course = courseService.getCourseById(1);

        assertNotNull(course);
        assertEquals("Java Core Programming", course.getName());
    }

    @Test
    void testAddCourse() {
        when(courseDAO.insert(any(Course.class))).thenReturn(true);

        boolean result = courseService.addCourse("Docker Fundamentals", 20, "Tran Thi B");

        assertTrue(result);
        verify(courseDAO, times(1)).insert(any(Course.class));
    }

    @Test
    void testSearchCoursesByName_FuzzyAndCaseInsensitive() {
        when(courseDAO.getAll()).thenReturn(mockCourses);

        List<Course> results = courseService.searchCoursesByName("java");

        assertEquals(1, results.size());
        assertEquals("Java Core Programming", results.get(0).getName());
    }

    @Test
    void testSortCourses_ByNameAscending() {
        when(courseDAO.getAll()).thenReturn(mockCourses);

        List<Course> sorted = courseService.sortCourses("name", true);

        assertEquals("Algorithms & Data Structures", sorted.get(0).getName());
        assertEquals("Spring Boot Microservices", sorted.get(sorted.size() - 1).getName());
    }

    @Test
    void testSortCourses_ByNameDescending() {
        when(courseDAO.getAll()).thenReturn(mockCourses);

        List<Course> sorted = courseService.sortCourses("name", false);

        assertEquals("Spring Boot Microservices", sorted.get(0).getName());
        assertEquals("Algorithms & Data Structures", sorted.get(sorted.size() - 1).getName());
    }

    @Test
    void testSortCourses_ByIdDescending() {
        when(courseDAO.getAll()).thenReturn(mockCourses);

        List<Course> sorted = courseService.sortCourses("id", false);

        assertEquals(4, sorted.get(0).getId());
        assertEquals(1, sorted.get(sorted.size() - 1).getId());
    }
}


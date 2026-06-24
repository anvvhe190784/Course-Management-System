package com.cms.model;

import java.time.LocalDate;

public class Student {
    private int id;
    private String name;
    private LocalDate dob;
    private String email;
    private boolean sex; // true = Nam, false = Nữ
    private String phone;
    private Role role;
    private String password;
    private LocalDate createAt;

    public Student() {
    }

    public Student(String name, LocalDate dob, String email, boolean sex, String phone, Role role, String password) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.sex = sex;
        this.phone = phone;
        this.role = role;
        this.password = password;
    }

    public Student(int id, String name, LocalDate dob, String email, boolean sex, String phone, Role role, String password, LocalDate createAt) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.sex = sex;
        this.phone = phone;
        this.role = role;
        this.password = password;
        this.createAt = createAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                ", sex=" + (sex ? "Nam" : "Nữ") +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", createAt=" + createAt +
                '}';
    }
}

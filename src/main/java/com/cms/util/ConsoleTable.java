package com.cms.util;

import com.cms.model.Course;
import java.util.ArrayList;
import java.util.List;

public class ConsoleTable {
    private final List<String> headers = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    public ConsoleTable() {
    }

    public ConsoleTable(String... headers) {
        if (headers != null) {
            for (String h : headers) {
                this.headers.add(h);
            }
        }
    }

    public void addHeader(String header) {
        headers.add(header);
    }

    public void addRow(List<String> row) {
        rows.add(row);
    }

    public void addRow(String... row) {
        if (row != null) {
            rows.add(List.of(row));
        }
    }

    public static void printCourseTable(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            System.out.println("Không có dữ liệu khóa học nào để hiển thị.");
            return;
        }
        ConsoleTable table = new ConsoleTable("ID", "Tên khóa học", "Thời lượng (Giờ)", "Giảng viên", "Ngày tạo");
        for (Course c : courses) {
            table.addRow(
                    String.valueOf(c.getId()),
                    c.getName(),
                    String.valueOf(c.getDuration()),
                    c.getInstructor(),
                    c.getCreateAt() != null ? c.getCreateAt().toString() : "N/A"
            );
        }
        table.print();
    }

    public void print() {
        if (headers.isEmpty() && rows.isEmpty()) {
            System.out.println("Danh sách trống.");
            return;
        }

        int numCols = headers.isEmpty() ? rows.get(0).size() : headers.size();
        int[] colWidths = new int[numCols];

        // Header widths
        for (int i = 0; i < headers.size(); i++) {
            colWidths[i] = Math.max(colWidths[i], getLength(headers.get(i)));
        }

        // Row widths
        for (List<String> row : rows) {
            for (int i = 0; i < Math.min(row.size(), numCols); i++) {
                String val = row.get(i);
                if (val != null) {
                    colWidths[i] = Math.max(colWidths[i], getLength(val));
                }
            }
        }

        // Create horizontal divider line
        StringBuilder divider = new StringBuilder("+");
        for (int w : colWidths) {
            divider.append("-".repeat(w + 2)).append("+");
        }
        String dividerStr = divider.toString();

        System.out.println(dividerStr);

        // Print header
        if (!headers.isEmpty()) {
            StringBuilder sb = new StringBuilder("|");
            for (int i = 0; i < numCols; i++) {
                sb.append(" ").append(padRight(headers.get(i), colWidths[i])).append(" |");
            }
            System.out.println(sb);
            System.out.println(dividerStr);
        }

        // Print rows
        for (List<String> row : rows) {
            StringBuilder sb = new StringBuilder("|");
            for (int i = 0; i < numCols; i++) {
                String cell = i < row.size() ? row.get(i) : "";
                if (cell == null) cell = "";
                sb.append(" ").append(padRight(cell, colWidths[i])).append(" |");
            }
            System.out.println(sb);
        }
        System.out.println(dividerStr);
    }

    // Handles double-byte / unicode character display lengths roughly if needed
    // But standard padRight formatting handles normal strings well. 
    private int getLength(String s) {
        if (s == null) return 0;
        // In Vietnamese, some characters take standard width, but length() works fine in most modern terminals.
        return s.length();
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        int len = getLength(s);
        if (len >= n) {
            return s;
        }
        return s + " ".repeat(n - len);
    }
}

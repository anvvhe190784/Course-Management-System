package com.cms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private InputValidator() {
        // Private constructor
    }

    public static String readString(Scanner scanner, String prompt, boolean nullable) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                if (nullable) {
                    return null;
                } else {
                    System.out.println("Lỗi: Dữ liệu không được để trống. Vui lòng nhập lại!");
                    continue;
                }
            }
            return input;
        }
    }

    public static int readInt(Scanner scanner, String prompt, boolean positiveOnly) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Lỗi: Dữ liệu không được để trống!");
                continue;
            }
            try {
                int val = Integer.parseInt(input);
                if (positiveOnly && val <= 0) {
                    System.out.println("Lỗi: Vui lòng nhập số nguyên dương (> 0)!");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Định dạng số không hợp lệ! Vui lòng nhập lại số nguyên.");
            }
        }
    }

    public static double readDouble(Scanner scanner, String prompt, boolean positiveOnly) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Lỗi: Dữ liệu không được để trống!");
                continue;
            }
            try {
                double val = Double.parseDouble(input);
                if (positiveOnly && val <= 0) {
                    System.out.println("Lỗi: Vui lòng nhập số thực dương (> 0)!");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Định dạng số không hợp lệ! Vui lòng nhập lại số thực.");
            }
        }
    }

    public static LocalDate readLocalDate(Scanner scanner, String prompt, boolean nullable) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                if (nullable) {
                    return null;
                } else {
                    System.out.println("Lỗi: Ngày sinh không được để trống!");
                    continue;
                }
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Lỗi: Định dạng ngày không hợp lệ (yêu cầu yyyy-MM-dd, ví dụ: 2000-01-30)!");
            }
        }
    }

    public static String readEmail(Scanner scanner, String prompt, boolean nullable) {
        while (true) {
            String input = readString(scanner, prompt, nullable);
            if (input == null) return null;
            if (EMAIL_PATTERN.matcher(input).matches()) {
                return input;
            } else {
                System.out.println("Lỗi: Email không hợp lệ (ví dụ: student@gmail.com)!");
            }
        }
    }

    public static String readPhone(Scanner scanner, String prompt, boolean nullable) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                if (nullable) {
                    return null;
                } else {
                    System.out.println("Lỗi: Số điện thoại không được để trống!");
                    continue;
                }
            }
            if (PHONE_PATTERN.matcher(input).matches()) {
                return input;
            } else {
                System.out.println("Lỗi: Số điện thoại không hợp lệ (chỉ nhập số, độ dài từ 10-15 ký tự)!");
            }
        }
    }

    public static boolean readSex(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                System.out.println("Lỗi: Giới tính không được để trống!");
                continue;
            }
            if (input.equals("1") || input.equals("nam") || input.equals("m") || input.equals("y")) {
                return true;
            } else if (input.equals("0") || input.equals("nữ") || input.equals("nu") || input.equals("f") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Lỗi: Lựa chọn giới tính không hợp lệ (Nhập 1 cho Nam hoặc 0 cho Nữ)!");
            }
        }
    }

    public static boolean confirm(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes") || input.equals("có") || input.equals("co")) {
                return true;
            } else if (input.equals("n") || input.equals("no") || input.equals("không") || input.equals("khong")) {
                return false;
            } else {
                System.out.println("Lỗi: Vui lòng nhập Y/Yes hoặc N/No!");
            }
        }
    }
}

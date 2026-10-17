package com.cms.util;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class PaginationHelper {
    public static final int DEFAULT_PAGE_SIZE = 5;

    private PaginationHelper() {
        // Utility class
    }

    /**
     * Hiển thị danh sách dữ liệu với cơ chế phân trang trên Console.
     * Cho phép chuyển trang linh hoạt (Next, Prev, Số trang, Thoát).
     */
    public static <T> void paginate(Scanner scanner, String title, List<T> items, int pageSize, Consumer<List<T>> pageRenderer) {
        if (items == null || items.isEmpty()) {
            System.out.println("Danh sách trống.");
            return;
        }

        int actualPageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        int totalPages = (int) Math.ceil((double) items.size() / actualPageSize);
        int currentPage = 1;

        if (totalPages == 1) {
            pageRenderer.accept(items);
            return;
        }

        while (true) {
            int fromIndex = (currentPage - 1) * actualPageSize;
            int toIndex = Math.min(fromIndex + actualPageSize, items.size());
            List<T> pageItems = items.subList(fromIndex, toIndex);

            System.out.println("\n--- " + title + " (Trang " + currentPage + "/" + totalPages + " - Tổng số: " + items.size() + " mục) ---");
            pageRenderer.accept(pageItems);

            System.out.println("\n[N] Trang tiếp (Next) | [P] Trang trước (Prev) | [1-" + totalPages + "] Số trang | [Q] Thoát xem danh sách");
            System.out.print("Nhập lựa chọn của bạn: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("q") || input.equals("thoat") || input.equals("quit") || input.isEmpty()) {
                break;
            } else if (input.equals("n") || input.equals("next")) {
                if (currentPage < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Bạn đang ở trang cuối cùng!");
                }
            } else if (input.equals("p") || input.equals("prev")) {
                if (currentPage > 1) {
                    currentPage--;
                } else {
                    System.out.println("Bạn đang ở trang đầu tiên!");
                }
            } else {
                try {
                    int page = Integer.parseInt(input);
                    if (page >= 1 && page <= totalPages) {
                        currentPage = page;
                    } else {
                        System.out.println("Lỗi: Số trang không hợp lệ (Phải từ 1 đến " + totalPages + ")!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        }
    }
}


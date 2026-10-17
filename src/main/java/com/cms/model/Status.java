package com.cms.model;

public enum Status {
    WAITING("Chờ duyệt"),
    DENIED("Bị từ chối"),
    CANCEL("Đã hủy"),
    CONFIRM("Đã xác nhận");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

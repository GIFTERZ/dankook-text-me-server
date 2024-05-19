package gifterz.textme.domain.entity;

import java.util.Locale;

public enum StatusType {
    ACTIVATE(0, "ACTIVATE"),
    DELETED(1, "DELETED"),
    DEACTIVATE(2, "DEACTIVATE"),
    PENDING(3, "PENDING");

    final int number;
    final String status;

    StatusType(int number, String status) {
        this.number = number;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getNumber() {
        return number;
    }

    public static StatusType fromStatus(String inputStatus) {
        return StatusType.valueOf(inputStatus.toUpperCase(Locale.ENGLISH));
    }

}

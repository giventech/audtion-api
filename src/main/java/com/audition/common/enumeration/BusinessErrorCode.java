package com.audition.common.enumeration;

public enum BusinessErrorCode {
    RESOURCE_NOT_FOUND("E001", "Resource Not Found"),
    INVALID_REQUEST("E002", "Invalid Request"),
    DUPLICATE_ENTRY("E003", "Duplicate Entry"),
    UNAUTHORIZED_ACCESS("E004", "Unauthorized Access"),
    FORBIDDEN_ACTION("E005", "Forbidden Action");

    private final String code;
    private final String message;

    BusinessErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

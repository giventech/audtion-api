package com.audition.common.exception;

@SuppressWarnings("PMD")
public class BusinessException extends RuntimeException {

    private String businessErrorCode;
    private String message;


    public BusinessException(final String message, final String businessErrorCode, final Throwable cause) {
        super(message, cause);
        this.businessErrorCode = businessErrorCode;
        this.message = message;


    }

    public String getBusinessErrorCode() {
        return businessErrorCode;
    }

    public String getMessage() {
        return message;
    }
}
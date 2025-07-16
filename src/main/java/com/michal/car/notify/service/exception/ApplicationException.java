package com.michal.car.notify.service.exception;

/**
 * @author Michal Remis
 */
public class ApplicationException extends RuntimeException {

    private ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ApplicationException of(String message, Throwable cause) {
        return new ApplicationException(message, cause);
    }
}
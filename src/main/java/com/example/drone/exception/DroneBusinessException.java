package com.example.drone.exception;

public class DroneBusinessException extends RuntimeException {

    private final int errorCode;

    public DroneBusinessException(String message) {
        super(message);
        this.errorCode = 400;
    }

    public DroneBusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public DroneBusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

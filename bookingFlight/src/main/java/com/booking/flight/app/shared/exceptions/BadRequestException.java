package com.booking.flight.app.shared.exceptions;


public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
    }
}

package com.booking.flight.app.model.exceptions;


public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
    }
}

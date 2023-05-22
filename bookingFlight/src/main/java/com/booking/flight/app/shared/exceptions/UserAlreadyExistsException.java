package com.booking.flight.app.shared.exceptions;


public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("User already exists.");
    }

    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}

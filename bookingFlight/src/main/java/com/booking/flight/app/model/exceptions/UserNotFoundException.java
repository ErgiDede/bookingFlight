package com.booking.flight.app.model.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }

}

package com.booking.flight.app.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterForm {

    private String firstName;
    private String lastName;

    @JsonProperty("email")
    private String username;

    @Size(min = 8, message = "Password is too short. It needs to be at least 8 characters.")
    private String password;
}
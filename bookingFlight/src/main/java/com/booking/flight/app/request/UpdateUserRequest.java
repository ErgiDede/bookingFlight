package com.booking.flight.app.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private Long id;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String username;
    @Size(min = 8, message = "Password is too short. It needs to be at least 8 characters.")
    private String password;
    private String address;
    private String phoneNumber;
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }
    @JsonProperty
    public void setPassword(final String password) {
        this.password = password;
    }
}

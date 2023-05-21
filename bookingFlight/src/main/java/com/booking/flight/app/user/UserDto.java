package com.booking.flight.app.user;


import com.booking.flight.app.model.enums.Role;
import com.booking.flight.app.model.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;

    @JsonProperty("email")
    private String username;

    @JsonIgnore
    @Size(min = 8, message = "Password is too short. It needs to be at least 8 characters.")
    private String password;

    private Role role;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;

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

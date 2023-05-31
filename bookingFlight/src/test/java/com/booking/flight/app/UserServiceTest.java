package com.booking.flight.app;

import com.booking.flight.app.entity.UserEntity;
import com.booking.flight.app.repository.FlightRepository;
import com.booking.flight.app.repository.UserRepo;
import com.booking.flight.app.request.CreateUserRequest;
import com.booking.flight.app.response.UserResponse;
import com.booking.flight.app.service.UserService;
import com.booking.flight.app.shared.enums.UserStatus;
import com.booking.flight.app.shared.exceptions.BadRequestException;
import com.booking.flight.app.shared.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;



    @Test
    void createUser_UsernameExists_ThrowsUserAlreadyExistsException() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("existinguser");
        request.setPassword("testpassword");
        // Set other properties...

        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        // Set other properties...

        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(existingUser));

        // Act and Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));

        verify(userRepo, never()).save(any(UserEntity.class));
    }

    @Test
    void createUser_InvalidRequest_ThrowsBadRequestException() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        // Set other properties...

        // Act and Assert
        assertThrows(BadRequestException.class, () -> userService.createUser(request));

        verify(userRepo, never()).save(any(UserEntity.class));
    }
}

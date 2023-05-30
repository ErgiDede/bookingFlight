package com.booking.flight.app.service;


import com.booking.flight.app.entity.BookingFlight;
import com.booking.flight.app.entity.FlightEntity;
import com.booking.flight.app.repository.FlightRepository;
import com.booking.flight.app.repository.UserRepo;
import com.booking.flight.app.shared.enums.Role;
import com.booking.flight.app.shared.enums.UserStatus;
import com.booking.flight.app.shared.exceptions.BadRequestException;
import com.booking.flight.app.shared.exceptions.UserAlreadyExistsException;
import com.booking.flight.app.shared.exceptions.UserNotFoundException;
import com.booking.flight.app.shared.utils.ModelMapperUtils;
import com.booking.flight.app.request.CreateUserRequest;
import com.booking.flight.app.request.UpdateUserRequest;
import com.booking.flight.app.entity.UserEntity;
import com.booking.flight.app.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final FlightRepository flightRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails findByActiveUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsernameAndStatus(username, UserStatus.ACTIVE).orElseThrow(UserNotFoundException::new);
    }

    public UserResponse getByEmail(String email) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmail(email).
                orElseThrow(UserNotFoundException::new);
        return ModelMapperUtils.map(user, UserResponse.class);
    }


    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
        UserEntity currentUser = userRepo.findById(updateUserRequest.getId()).orElseThrow(UserNotFoundException::new);

        UserEntity updatedUser = ModelMapperUtils.map(updateUserRequest, UserEntity.class);
        if (updateUserRequest.getPassword() == null) {
            updatedUser.setPassword(currentUser.getPassword());
        } else {
            updatedUser.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }
        updatedUser.setUpdatedAt(Instant.now());
        updatedUser.setCreatedAt(currentUser.getCreatedAt());
        updatedUser.setStatus(currentUser.getStatus());
        updatedUser.setRole(currentUser.getRole());
        updatedUser = userRepo.save(updatedUser);
        return ModelMapperUtils.map(updatedUser, UserResponse.class);
    }

    public List<UserResponse> getAllUsers() {
        return ModelMapperUtils.mapAll(userRepo.findAll(), UserResponse.class);
    }


    public void deleteUser(long id) {
        userRepo.deleteById(id);
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {

        if (checkIfUsernameExists(createUserRequest)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        createUserRequest.setStatus(UserStatus.ACTIVE);
        createUserRequest.setCreatedAt(Instant.now());
        createUserRequest.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        createUserRequest.setRole(Role.TRAVELLER);

        if (!validateNewUser(createUserRequest)) {
            throw new BadRequestException("All fields are required.");
        }

        UserEntity newUser = userRepo.save(ModelMapperUtils.map(createUserRequest, UserEntity.class));
        return ModelMapperUtils.map(newUser, UserResponse.class);
    }

    public boolean validateNewUser(CreateUserRequest createUserRequest) {
        return (createUserRequest.getFirstName() != null && createUserRequest.getLastName() != null && createUserRequest.getUsername() != null && createUserRequest.getRole() != null && createUserRequest.getPassword() != null);
    }

    public boolean checkIfUsernameExists(CreateUserRequest createUserRequest) {
        Optional<UserEntity> existingUser = userRepo.findByUsername(createUserRequest.getUsername());
        return existingUser.isPresent();
    }

    public UserResponse getLoggedInUser() {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userEntity == null) throw new BadRequestException("Session Expired.");
        return ModelMapperUtils.map(userEntity, UserResponse.class);
    }

    public List<UserResponse> getTravellersByFlight(Long flightId) {
        FlightEntity flightEntity = flightRepository.findById(flightId).orElse(null); // Replace "flightRepository" with your actual repository

        Set<UserEntity> users = new HashSet<>();
        if (flightEntity != null) {
            List<BookingFlight> bookingFlights = flightEntity.getBookingFlights();
            for (BookingFlight bookingFlight : bookingFlights) {
                UserEntity userEntity = bookingFlight.getBookingEntity().getUserEntity();
                users.add(userEntity);
            }
        }

        return ModelMapperUtils.mapAll(users, UserResponse.class);
    }

}

package com.booking.flight.app.user;


import com.booking.flight.app.flight.BookingFlight;
import com.booking.flight.app.flight.FlightEntity;
import com.booking.flight.app.flight.FlightRepository;
import com.booking.flight.app.shared.enums.Role;
import com.booking.flight.app.shared.enums.UserStatus;
import com.booking.flight.app.shared.exceptions.BadRequestException;
import com.booking.flight.app.shared.exceptions.UserAlreadyExistsException;
import com.booking.flight.app.shared.exceptions.UserNotFoundException;
import com.booking.flight.app.shared.utils.ModelMapperUtils;
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
        return userRepo.findByUsernameAndStatus(username, UserStatus.ACTIVE).
                orElseThrow(UserNotFoundException::new);
    }

    public UserDto getByEmail(String email) throws UsernameNotFoundException {
        UserEntity user = userRepo.findByEmail(email).
                orElseThrow(UserNotFoundException::new);
        UserDto userDto = ModelMapperUtils.map(user, UserDto.class);
        return userDto;
    }

    public UserDto updateUser(UserDto userDto) {
        if(!validateUser(userDto)) {
            throw new BadRequestException("All fields are required.");
        }
        UserEntity currentUser = userRepo.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);

        UserEntity updatedUser = ModelMapperUtils.map(userDto, UserEntity.class);
        if (userDto.getPassword() == null){
        updatedUser.setPassword(currentUser.getPassword());
        }else {
            updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        updatedUser.setUpdatedAt(Instant.now());
        updatedUser.setCreatedAt(currentUser.getCreatedAt());
        updatedUser.setStatus(currentUser.getStatus());
        updatedUser.setRole(currentUser.getRole());
        updatedUser = userRepo.save(updatedUser);
        return ModelMapperUtils.map(updatedUser, UserDto.class);
    }

    public UserDto getUserById(long id) {
        return ModelMapperUtils.map(userRepo.findById(id).orElseThrow(UserNotFoundException::new), UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        return ModelMapperUtils.mapAll(userRepo.findAll(), UserDto.class);
    }

    public UserDto deactivateUser(long id) {
        UserEntity userEntity = userRepo.findById(id).orElseThrow(UserNotFoundException::new);
        userEntity.setStatus(UserStatus.INACTIVE);
        userEntity.setUpdatedAt(Instant.now());
        return ModelMapperUtils.map(userRepo.save(userEntity), UserDto.class);
    }

    public void deleteUser(long id) {

        userRepo.deleteById(id);
    }

    public boolean checkUserPermissionToGetUserInformation(String email) {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userEntity.getRole().equals(Role.ADMIN) || userEntity.getEmail().equalsIgnoreCase(email);
    }

    public UserDto createUser(UserDto userDto) {

        if(checkIfUsernameExists(userDto)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        userDto.setStatus(UserStatus.ACTIVE);
        userDto.setCreatedAt(Instant.now());
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setRole(Role.TRAVELLER);

        if(!validateNewUser(userDto)) {
            throw new BadRequestException("All fields are required.");
        }

        UserEntity newUser = userRepo.save(ModelMapperUtils.map(userDto, UserEntity.class));
        return ModelMapperUtils.map(newUser, UserDto.class);
    }

    public boolean validateUser(UserDto userDto) {
        return (userDto.getFirstName() != null && userDto.getLastName() != null
                && userDto.getUsername() != null && userDto.getRole() != null);
    }

    public boolean validateNewUser(UserDto userDto) {
        return (userDto.getFirstName() != null && userDto.getLastName() != null
                && userDto.getUsername() != null && userDto.getRole() != null
                && userDto.getPassword() != null);
    }

    public boolean checkIfUsernameExists(UserDto userDto) {
        Optional<UserEntity> existingUser = userRepo.findByUsername(userDto.getUsername());
        return existingUser.isPresent();
    }

    public UserDto getLoggedInUser() {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userEntity == null)
            throw new BadRequestException("Session Expired.");
        return ModelMapperUtils.map(userEntity, UserDto.class);
    }

    public List<UserResponse> getUsersByFlight(Long flightId) {
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

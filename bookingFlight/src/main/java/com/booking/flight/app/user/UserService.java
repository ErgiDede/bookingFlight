package com.booking.flight.app.user;


import com.booking.flight.app.model.enums.Role;
import com.booking.flight.app.model.enums.UserStatus;
import com.booking.flight.app.model.exceptions.BadRequestException;
import com.booking.flight.app.model.exceptions.UserAlreadyExistsException;
import com.booking.flight.app.model.exceptions.UserNotFoundException;
import com.booking.flight.app.model.utils.ModelMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserDetails findByActiveUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsernameAndStatus(username, UserStatus.ACTIVE).
                orElseThrow(UserNotFoundException::new);
    }

    public UserDto updateUser(UserDto userDto) {
        if(!validateUser(userDto)) {
            throw new BadRequestException("All fields are required.");
        }
        User currentUser = userRepo.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);
        if(checkIfUsernameExists(userDto)) {
            throw new UserAlreadyExistsException("User with enail: " + userDto.getUsername() + " already exists.");
        }
        User updatedUser = ModelMapperUtils.map(userDto, User.class);
        updatedUser.setPassword(currentUser.getPassword());
        updatedUser.setUpdatedAt(Instant.now());
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
        User user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);
        user.setStatus(UserStatus.INACTIVE);
        user.setUpdatedAt(Instant.now());
        return ModelMapperUtils.map(userRepo.save(user), UserDto.class);
    }

    public void deleteUser(long id) {
        User user = userRepo.findById(id).orElseThrow(UserNotFoundException::new);
        user.setStatus(UserStatus.INACTIVE);
        userRepo.delete(user);
    }

    public boolean checkUserPermissionToGetUserInformation(long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getRole().equals(Role.ADMIN) || user.getId() == id;
    }

    public UserDto createUser(UserDto userDto) {
        if(!validateNewUser(userDto)) {
            throw new BadRequestException("All fields are required.");
        }
        if(checkIfUsernameExists(userDto)) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        userDto.setStatus(UserStatus.ACTIVE);
        userDto.setCreatedAt(Instant.now());
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User newUser = userRepo.save(ModelMapperUtils.map(userDto, User.class));
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
        Optional<User> existingUser = userRepo.findByUsername(userDto.getUsername());
        return existingUser.isPresent();
    }

    public UserDto getLoggedInUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user == null)
            throw new BadRequestException("Session Expired.");
        return ModelMapperUtils.map(user, UserDto.class);
    }

}

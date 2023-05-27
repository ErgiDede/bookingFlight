package com.booking.flight.app.loadData;


import com.booking.flight.app.shared.enums.Role;
import com.booking.flight.app.shared.enums.UserStatus;
import com.booking.flight.app.user.UserEntity;
import com.booking.flight.app.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Component
public class LoadData implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepo.findByUsernameIn(List.of("admin", "traveller1", "traveller2")).isEmpty()) {
            userRepo.save(
                    UserEntity.builder().username("admin").password(passwordEncoder.encode("password")).role(Role.ADMIN)
                            .status(UserStatus.ACTIVE)
                            .firstName("first").lastName("last").createdAt(Instant.now()).build()
            );
            userRepo.save(
                    UserEntity.builder().username("traveller1").password(passwordEncoder.encode("password")).role(Role.TRAVELLER)
                            .status(UserStatus.ACTIVE)
                            .firstName("traveller1").lastName("traveller1").createdAt(Instant.now()).build()
            );
            userRepo.save(
                    UserEntity.builder().username("traveller2").password(passwordEncoder.encode("password")).role(Role.TRAVELLER)
                            .status(UserStatus.INACTIVE)
                            .firstName("traveller2").lastName("traveller2").createdAt(Instant.now()).build()
            );
        }
    }
}

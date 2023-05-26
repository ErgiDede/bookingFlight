package com.booking.flight.app.loadData;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoadData implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

    }

    /*private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepo.save(
                User.builder().username("admin").password(passwordEncoder.encode("password")).role(Role.ADMIN)
                        .status(UserStatus.ACTIVE)
                        .firstName("first").lastName("last").createdAt(Instant.now()).build()
        );
        userRepo.save(
                User.builder().username("traveller1").password(passwordEncoder.encode("password")).role(Role.TRAVELLER)
                        .status(UserStatus.ACTIVE)
                        .firstName("employ1").lastName("lastname1").createdAt(Instant.now()).build()
        );
        userRepo.save(
                User.builder().username("traveller2").password(passwordEncoder.encode("password")).role(Role.TRAVELLER)
                        .status(UserStatus.INACTIVE)
                        .firstName("employ2").lastName("lastname2").createdAt(Instant.now()).build()
        );
    }*/
}

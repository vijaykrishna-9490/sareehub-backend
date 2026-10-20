package com.sareehub.config;

import com.sareehub.entity.RoleName;
import com.sareehub.entity.User;
import com.sareehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "vijayadmin@gmail.com";

        if (userRepository.existsByEmail(email)) {
            return;
        }

        Set<RoleName> roles = new HashSet<>();
        roles.add(RoleName.ROLE_ADMIN);

        User admin = User.builder()
                .fullName("SareeHub Admin")
                .email(email)
                .password(passwordEncoder.encode("Kakivijay@9490"))
                .phone("9490591462")
                .roles(roles)
                .build();

        userRepository.save(admin);

        System.out.println("=================================");
        System.out.println("NEW ADMIN CREATED");
        System.out.println("Email: " + email);
        System.out.println("Password: Kakivjay@9490");
        System.out.println("=================================");
    }
}

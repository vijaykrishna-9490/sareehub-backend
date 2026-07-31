package com.sareehub.service;

import com.sareehub.dto.*;
import com.sareehub.entity.RoleName;
import com.sareehub.entity.User;
import com.sareehub.exception.ApiException;
import com.sareehub.repository.UserRepository;
import com.sareehub.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        Set<RoleName> roles = new HashSet<>();
        roles.add(RoleName.ROLE_CUSTOMER);

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .roles(roles)
                .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        if (user.isBlocked()) {
            throw new ApiException("Your account has been blocked", HttpStatus.FORBIDDEN);
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        List<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toList());
        String token = jwtUtil.generateToken(user.getEmail(), roles);

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}

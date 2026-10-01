package com.turfreservation.service;

import com.turfreservation.dto.AuthResponse;
import com.turfreservation.dto.LoginRequest;
import com.turfreservation.dto.RegisterRequest;
import com.turfreservation.entity.User;
import com.turfreservation.exception.BadRequestException;
import com.turfreservation.exception.UnauthorizedException;
import com.turfreservation.repository.UserRepository;
import com.turfreservation.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and Confirm Password do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getRole().name());

        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.getRole().equals(request.getRole())) {
            throw new UnauthorizedException("Role mismatch. This account is registered as " + user.getRole());
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
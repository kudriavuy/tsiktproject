package com.classroom.service;

import com.classroom.model.User;
import com.classroom.repository.UserRepository;
import com.classroom.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public Map<String, Object> register(String name, String email, String password) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.existsByEmail(email)) {
            response.put("success", false);
            response.put("message", "Email already in use");
            return response;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        response.put("success", true);
        response.put("token", token);
        response.put("user", user);
        return response;
    }

    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPasswordHash())) {
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return response;
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail());

        response.put("success", true);
        response.put("token", token);
        response.put("user", user);
        return response;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}


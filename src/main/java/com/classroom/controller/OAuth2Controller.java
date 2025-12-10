package com.classroom.controller;

import com.classroom.model.User;
import com.classroom.repository.UserRepository;
import com.classroom.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OAuth2Controller {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/authorize-url")
    public ResponseEntity<Map<String, String>> getAuthorizeUrl() {
        Map<String, String> response = new HashMap<>();
        response.put("url", "http://localhost:8080/oauth2/authorization/google");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getOAuth2User(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = null;
        String email = null;
        String name = null;

        // Check authentication type
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oauth2User) {
            // OAuth2 authorization
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            String picture = oauth2User.getAttribute("picture");

            if (email == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Email not found in Google account");
                return ResponseEntity.badRequest().body(error);
            }

            // Check if user exists
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                user = existingUser.get();
                // Update profile photo from Google if it exists and user doesn't have photo yet
                if (picture != null && (user.getProfilePhotoUrl() == null || user.getProfilePhotoUrl().isEmpty())) {
                    user.setProfilePhotoUrl(picture);
                    user = userRepository.save(user);
                }
            } else {
                // Create new user
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : email);
                user.setPasswordHash(""); // OAuth users don't have password
                if (picture != null) {
                    user.setProfilePhotoUrl(picture);
                }
                user = userRepository.save(user);
            }
        } else if (principal instanceof User) {
            // Regular JWT authorization
            user = (User) principal;
            email = user.getEmail();
            name = user.getName();
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Unknown authentication type");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}


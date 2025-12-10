package com.classroom.controller;

import com.classroom.model.User;
import com.classroom.repository.UserRepository;
import com.classroom.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth/oauth2/success")
@RequiredArgsConstructor
public class OAuth2SuccessController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<String> handleOAuth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("<html><body><h1>Authorization error</h1></body></html>");
        }

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        if (email == null) {
            return ResponseEntity.badRequest()
                    .body("<html><body><h1>Email not found in Google account</h1></body></html>");
        }

        // Check if user exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

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

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Return HTML page with token
        String html = String.format(
                "<html><head><title>Successful Authorization</title></head>" +
                        "<body style='font-family: Arial; text-align: center; padding: 50px;'>" +
                        "<h1>Successfully authorized!</h1>" +
                        "<p>Your token:</p>" +
                        "<textarea id='token' style='width: 600px; height: 100px; font-family: monospace;'>%s</textarea>" +
                        "<br><br>" +
                        "<button onclick='copyToken()' style='padding: 10px 20px; font-size: 16px;'>Copy token</button>" +
                        "<br><br>" +
                        "<p>Copy the token and paste it into the application</p>" +
                        "<script>" +
                        "function copyToken() {" +
                        "  document.getElementById('token').select();" +
                        "  document.execCommand('copy');" +
                        "  alert('Token copied!');" +
                        "}" +
                        "</script>" +
                        "</body></html>",
                token
        );

        return ResponseEntity.ok(html);
    }
}


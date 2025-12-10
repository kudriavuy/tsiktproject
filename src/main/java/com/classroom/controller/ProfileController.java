package com.classroom.controller;

import com.classroom.model.User;
import com.classroom.service.ProfileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<User> getProfile(@PathVariable Long userId) {
        return profileService.getUserProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable Long userId,
            @RequestBody UpdateProfileRequest request,
            Authentication authentication) {

        // Check that user is updating their own profile
        if (!isOwnerOrAdmin(userId, authentication)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "No access to this profile");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        try {
            User user = profileService.updateProfile(
                    userId,
                    request.getName(),
                    request.getBio(),
                    request.getPhone()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/{userId}/profile/photo")
    public ResponseEntity<Map<String, Object>> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        // Check that user is updating their own profile
        if (!isOwnerOrAdmin(userId, authentication)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "No access to this profile");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        if (file.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "File is empty");
            return ResponseEntity.badRequest().body(error);
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "File must be an image");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            User user = profileService.updateProfilePhoto(userId, file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", user);
            response.put("photoUrl", user.getProfilePhotoUrl());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "File upload error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{userId}/profile/photo")
    public ResponseEntity<Map<String, Object>> deleteProfilePhoto(
            @PathVariable Long userId,
            Authentication authentication) {

        // Check that user is updating their own profile
        if (!isOwnerOrAdmin(userId, authentication)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "No access to this profile");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        try {
            profileService.deleteProfilePhoto(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile photo deleted");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/uploads/profiles/{filename:.+}")
    public ResponseEntity<Resource> getProfilePhoto(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/profiles/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (filename.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.toLowerCase().endsWith(".gif")) {
                    contentType = "image/gif";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean isOwnerOrAdmin(Long userId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getUserId().equals(userId);
        }
        return false;
    }

    @Data
    static class UpdateProfileRequest {
        private String name;
        private String bio;
        private String phone;
    }
}


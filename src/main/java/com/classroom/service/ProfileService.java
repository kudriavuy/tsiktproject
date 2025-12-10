package com.classroom.service;

import com.classroom.model.User;
import com.classroom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private static final String UPLOAD_DIR = "uploads/profiles/";

    public Optional<User> getUserProfile(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public User updateProfile(Long userId, String name, String bio, String phone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        if (phone != null) {
            user.setPhone(phone);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateProfilePhoto(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Delete old photo if it exists and is not from Google
        if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().startsWith("http")) {
            try {
                Path oldFilePath = Paths.get(user.getProfilePhotoUrl());
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                }
            } catch (Exception e) {
                // Ignore errors when deleting old file
            }
        }

        // Update photo URL
        user.setProfilePhotoUrl("/uploads/profiles/" + filename);

        return userRepository.save(user);
    }

    @Transactional
    public void deleteProfilePhoto(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().startsWith("http")) {
            try {
                Path filePath = Paths.get(user.getProfilePhotoUrl());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (Exception e) {
                // Ignore file deletion errors
            }
        }

        user.setProfilePhotoUrl(null);
        userRepository.save(user);
    }
}


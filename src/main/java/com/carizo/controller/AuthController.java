package com.carizo.controller;

import com.carizo.dto.AuthRequest;
import com.carizo.dto.AuthResponse;
import com.carizo.model.User;
import com.carizo.service.UserService;
import com.carizo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String UPLOAD_DIR = "uploads/profile-images/";

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    // 1. Get profile
    @GetMapping("/profile")
    public User getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userService.getUserByEmail(email);
    }

    // 2. Update profile with optional image upload and token refresh if email changed
    @PutMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("user") User updatedUser,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        String email = userDetails.getUsername();
        User existingUser = userService.getUserByEmail(email);

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());

        // Handle image upload if present
        if (image != null && !image.isEmpty()) {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(imagePath, image.getBytes());
            existingUser.setProfileImageUrl("/" + UPLOAD_DIR + fileName);
        }

        User updated = userService.updateUser(existingUser);

        // Generate new token if email changed
        String newToken = null;
        if (!email.equals(updated.getEmail())) {
            newToken = jwtUtil.generateToken(updated.getEmail(), updated.getRole());
        }

        Map<String, Object> response = new HashMap<>();
        response.put("user", updated);
        if (newToken != null) {
            response.put("token", newToken);
        }

        return ResponseEntity.ok(response);
    }

    // 3. Register with optional image upload
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public User register(
            @RequestPart("user") User user,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(imagePath, image.getBytes());

            imageUrl = "/" + UPLOAD_DIR + fileName;
        }

        return userService.registerUser(user, imageUrl);
    }

    // 4. Login endpoint returning JWT token, role, username, and profile image url
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(), authRequest.getPassword()
                )
        );

        User user = userService.getUserByEmail(authRequest.getEmail());

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getRole(), user.getUsername(), user.getProfileImageUrl(), user.getId() );
    }
}

package com.steve.auth_service.controller;



import com.steve.auth_service.dto.AuthResponse;
import com.steve.auth_service.dto.LoginRequest;
import com.steve.auth_service.dto.RegisterRequest;
import com.steve.auth_service.model.User;
import com.steve.auth_service.repository.UserRepository;
import com.steve.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final String UPLOAD_DIR = "C:/Users/User/Desktop/banking-system/uploads/";

    @PostMapping("/register")
    public  ResponseEntity<?> register(@ModelAttribute RegisterRequest request) throws IOException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Ensure upload directory exists
        File uploadDirFile = new File(UPLOAD_DIR);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        // Save file
        String fileName = System.currentTimeMillis() + "_" + request.getProfilePicture().getOriginalFilename();
        File file = new File(uploadDirFile, fileName);
        request.getProfilePicture().transferTo(file);

        // Instead of saving full C:/... path, just store relative name
        String relativePath = "/uploads/" + fileName;

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureUrl(relativePath) // ✅ store relative URL
                .role("USER")
                .build();

        userRepository.save(user);



        return ResponseEntity.ok("User registered successfully. Please log in.");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
}
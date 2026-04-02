package com.steve.auth_service.controller;



import com.steve.auth_service.dto.AuthResponse;
import com.steve.auth_service.dto.LoginRequest;
import com.steve.auth_service.dto.RegisterRequest;
import com.steve.auth_service.kafka.AuditEventProducer;
import com.steve.auth_service.model.User;
import com.steve.auth_service.repository.UserRepository;
import com.steve.auth_service.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditEventProducer auditEventProducer;

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute RegisterRequest request) throws IOException {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }

        String relativePath = saveProfilePicture(request.getProfilePicture());

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureUrl(relativePath)
                .role("USER")
                .build();

        userRepository.save(user);
        auditEventProducer.sendRegistrationAudit(user.getEmail());
        log.info("User registered: {}", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully. Please log in."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail());
                    auditEventProducer.sendLoginAudit(user.getEmail(), true);
                    log.info("Login success: {}", user.getEmail());
                    return ResponseEntity.ok(AuthResponse.builder()
                            .token(token)
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .profilePictureUrl(user.getProfilePictureUrl())
                            .build());
                })
                .orElseGet(() -> {
                    auditEventProducer.sendLoginAudit(request.getEmail(), false);
                    log.warn("Login failed for: {}", request.getEmail());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(null);
                });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String saveProfilePicture(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        file.transferTo(new File(dir, fileName));
        return "/uploads/" + fileName;
    }

    // ── Exception Handlers ────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleError(Exception e) {
        log.error("Auth error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}

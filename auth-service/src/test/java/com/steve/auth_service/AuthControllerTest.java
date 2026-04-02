package com.steve.auth_service;

import com.steve.auth_service.security.JwtUtil;
import com.steve.auth_service.dto.LoginRequest;
import com.steve.auth_service.dto.RegisterRequest;
import com.steve.auth_service.model.User;
import com.steve.auth_service.kafka.AuditEventProducer;
import com.steve.auth_service.repository.UserRepository;
import com.steve.auth_service.controller.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuditEventProducer auditEventProducer;

    @InjectMocks private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    void login_returnsToken_whenCredentialsValid() {
        User user = buildUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("test@example.com")).thenReturn("mock-jwt-token");

        LoginRequest request = new LoginRequest("test@example.com", "password123");
        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(auditEventProducer).sendLoginAudit("test@example.com", true);
    }

    @Test
    void login_returns401_whenPasswordWrong() {
        User user = buildUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginRequest request = new LoginRequest("test@example.com", "wrongpass");
        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(auditEventProducer).sendLoginAudit("test@example.com", false);
    }

    @Test
    void login_returns401_whenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("nobody@example.com", "pass");
        ResponseEntity<?> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── JwtUtil ───────────────────────────────────────────────────────────────

    @Test
    void jwtUtil_generateAndValidate() {
        JwtUtil util = new JwtUtil("u8F3nD9sK2qL1vR5bX7zW6pT4mQ0yA8e", 3600000L);
        String token = util.generateToken("steve@bank.com");

        assertThat(token).isNotBlank();
        assertThat(util.validateToken(token)).isTrue();
        assertThat(util.extractEmail(token)).isEqualTo("steve@bank.com");
    }

    @Test
    void jwtUtil_invalidToken_returnsFalse() {
        JwtUtil util = new JwtUtil("u8F3nD9sK2qL1vR5bX7zW6pT4mQ0yA8e", 3600000L);
        assertThat(util.validateToken("not.a.real.token")).isFalse();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User buildUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashed-password")
                .fullName("Test User")
                .role("USER")
                .build();
    }
}

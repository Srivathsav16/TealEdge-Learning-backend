package com.tealedge.backend.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.tealedge.backend.dto.AuthResponse;
import com.tealedge.backend.dto.GoogleLoginRequest;
import com.tealedge.backend.dto.LoginRequest;
import com.tealedge.backend.dto.SignupRequest;
import com.tealedge.backend.dto.UserDTO;
import com.tealedge.backend.exception.BadRequestException;
import com.tealedge.backend.exception.UnauthorizedException;
import com.tealedge.backend.model.User;
import com.tealedge.backend.repository.UserRepository;
import com.tealedge.backend.security.GoogleTokenVerifier;
import com.tealedge.backend.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Authenticate a user with Google Token")
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleAuth(@RequestBody GoogleLoginRequest request) {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(request.getToken());
        if (payload == null) {
            throw new UnauthorizedException("Invalid Google Token");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        Optional<User> userOpt = userRepository.findByEmail(email);
        User user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            if (request.getRole() == null || request.getRole().isEmpty()) {
                throw new BadRequestException("Role is required for new users");
            }
            user = new User(name, email, "", request.getRole());
            user = userRepository.save(user);
        }

        String customToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(new AuthResponse(customToken, userDTO));
    }

    @Operation(summary = "Manual Sign Up")
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists.");
        }
        if (request.getRole() == null || request.getRole().isEmpty()) {
            throw new BadRequestException("Role is required for new users");
        }
        User user = new User(request.getName(), request.getEmail(), request.getPassword(), request.getRole());
        user = userRepository.save(user);

        String customToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(new AuthResponse(customToken, userDTO));
    }

    @Operation(summary = "Manual Login")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // Using plain password comparison for simplicity/testing since Spring Security is not configured for bcrypt
        if (user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String customToken = jwtUtil.generateToken(user.getEmail(), user.getRole());
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(new AuthResponse(customToken, userDTO));
    }
}

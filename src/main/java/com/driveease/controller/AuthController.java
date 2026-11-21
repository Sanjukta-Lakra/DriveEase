package com.driveease.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import com.driveease.dto.DtoMapper;
import com.driveease.dto.LoginRequest;
import com.driveease.dto.LoginResponse;
import com.driveease.dto.UserRequest;
import com.driveease.dto.UserResponse;
import com.driveease.entity.User;
import com.driveease.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final UserService userService;
    private final DtoMapper dtoMapper;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(UserService userService,
                          DtoMapper dtoMapper,
                          AuthenticationManager authenticationManager) {

        this.userService = userService;
        this.dtoMapper = dtoMapper;
        this.authenticationManager = authenticationManager;
    }


    // Register customer
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());

        user.setDrivingLicenseNumber(
                request.getDrivingLicenseNumber()
        );

        User savedUser = userService.registerUser(user);

        UserResponse response =
                dtoMapper.toUserResponse(savedUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        User user = userService.getUserByEmail(
                authentication.getName()
        );

        String role = user.getRole().name();

        LoginResponse response = new LoginResponse(
                user.getId(),
                "Login successful",
                user.getEmail(),
                role
        );

        return ResponseEntity.ok(response);
    }


    // Get logged-in user details
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> getCurrentUser(
            Authentication authentication) {

        User user = userService.getUserByEmail(
                authentication.getName()
        );

        String role = user.getRole().name();

        LoginResponse response = new LoginResponse(
                user.getId(),
                "Authenticated",
                user.getEmail(),
                role
        );

        return ResponseEntity.ok(response);
    }


    // Logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request) {

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }
}

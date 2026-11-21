package com.driveease.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.driveease.dto.DtoMapper;
import com.driveease.dto.UserProfileRequest;
import com.driveease.dto.UserResponse;
import com.driveease.entity.User;
import com.driveease.service.UserService;

@RestController
@RequestMapping("/api/customers/profile")
public class CustomerProfileController {

    private final UserService userService;
    private final DtoMapper dtoMapper;

    public CustomerProfileController(UserService userService,
                                     DtoMapper dtoMapper) {
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    // Check that the logged-in user is a customer
    private User getLoggedInCustomer(Authentication authentication) {

        if (authentication == null ||
            authentication.getAuthorities().stream()
                .noneMatch(authority ->
                    authority.getAuthority().equals("ROLE_CUSTOMER"))) {

            throw new AccessDeniedException(
                    "Only customers can access this profile.");
        }

        return userService.getUserByEmail(authentication.getName());
    }

    // Get customer profile
    @GetMapping
    public ResponseEntity<UserResponse> getProfile(
            Authentication authentication) {

        User customer = getLoggedInCustomer(authentication);

        return ResponseEntity.ok(
                dtoMapper.toUserResponse(customer));
    }

    // Update customer profile
    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UserProfileRequest request,
            Authentication authentication) {

        User customer = getLoggedInCustomer(authentication);

        User updatedCustomer = userService.updateCustomerProfile(
                customer.getId(),
                request.getName(),
                request.getPhone(),
                request.getDrivingLicenseNumber()
        );

        return ResponseEntity.ok(
                dtoMapper.toUserResponse(updatedCustomer));
    }
}

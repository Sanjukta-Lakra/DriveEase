package com.driveease.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.driveease.dto.DtoMapper;
import com.driveease.dto.UserResponse;
import com.driveease.entity.User;
import com.driveease.enums.UserRole;
import com.driveease.service.UserService;

@RestController
@RequestMapping("/api/admin/customers")
public class CustomerController {

    private final UserService userService;
    private final DtoMapper dtoMapper;

    public CustomerController(UserService userService,
                              DtoMapper dtoMapper) {
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    // Get all registered customers
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllCustomers() {

        List<UserResponse> customers = userService.getAllUsers()
                .stream()
                .filter(user -> user.getRole() == UserRole.CUSTOMER)
                .map(dtoMapper::toUserResponse)
                .toList();

        return ResponseEntity.ok(customers);
    }

    // Get all users including admins
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(dtoMapper::toUserResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // Change user role
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam UserRole role,
            Authentication authentication) {

        User existingUser = userService.getUserById(id);

        if (existingUser.getEmail().equalsIgnoreCase(
                authentication.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot change your own role");
        }

        User updatedUser = userService.updateUserRole(id, role);

        return ResponseEntity.ok(
                dtoMapper.toUserResponse(updatedUser));
    }

    // Activate or deactivate user account
    @PutMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            Authentication authentication) {

        User existingUser = userService.getUserById(id);

        if (existingUser.getEmail().equalsIgnoreCase(
                authentication.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot change your own account status");
        }

        User updatedUser = userService.updateUserStatus(id, active);

        return ResponseEntity.ok(
                dtoMapper.toUserResponse(updatedUser));
    }
    
 // Move customer to Trash
    @PatchMapping("/{id}/trash")
    public ResponseEntity<String> moveCustomerToTrash(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userService.getUserById(id);

        if (user.getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot move your own account to Trash");
        }

        userService.moveUserToTrash(id);

        return ResponseEntity.ok("Customer moved to Trash successfully");
    }

    // Get trashed customers
    @GetMapping("/trash")
    public ResponseEntity<List<UserResponse>> getTrashedCustomers() {

        List<UserResponse> users = userService.getTrashedUsers()
                .stream()
                .map(dtoMapper::toUserResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // Restore customer
    @PatchMapping("/{id}/restore")
    public ResponseEntity<String> restoreCustomer(
            @PathVariable Long id) {

        userService.restoreUser(id);

        return ResponseEntity.ok("Customer restored successfully");
    }

    // Permanently delete customer
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<String> permanentlyDeleteCustomer(
            @PathVariable Long id) {

        userService.permanentlyDeleteUser(id);

        return ResponseEntity.ok("Customer permanently deleted");
    }
}

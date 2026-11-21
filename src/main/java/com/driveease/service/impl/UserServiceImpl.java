package com.driveease.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.driveease.entity.User;
import com.driveease.enums.UserRole;
import com.driveease.exception.DuplicateResourceException;
import com.driveease.exception.ResourceNotFoundException;
import com.driveease.repository.UserRepository;
import com.driveease.repository.BookingRepository;
import com.driveease.repository.ReviewRepository;
import com.driveease.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            BookingRepository bookingRepository,
            ReviewRepository reviewRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {

        user.setEmail(user.getEmail().trim().toLowerCase());

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException(
                    "Email is already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user.setDeleted(false);

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findByDeletedFalse();
    }

    @Override
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email.trim().toLowerCase())
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email));
    }

    @Override
    public User updateUserRole(Long id, UserRole role) {

        User user = getUserById(id);

        user.setRole(role);

        return userRepository.save(user);
    }

    @Override
    public User updateUserStatus(Long id, boolean active) {

        User user = getUserById(id);

        user.setActive(active);

        return userRepository.save(user);
    }

    @Override
    public User updateCustomerProfile(
            Long id,
            String name,
            String phone,
            String drivingLicenseNumber) {

        User user = getUserById(id);

        user.setName(name.trim());
        user.setPhone(phone.trim());
        user.setDrivingLicenseNumber(drivingLicenseNumber.trim());

        return userRepository.save(user);
    }

    // Move customer to Trash
    @Override
    public void moveUserToTrash(Long id) {

        User user = getUserById(id);

        if (user.getRole() != UserRole.CUSTOMER) {
            throw new IllegalArgumentException(
                    "Admin accounts cannot be moved to Trash");
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    // Get trashed customers
    @Override
    public List<User> getTrashedUsers() {
        return userRepository.findByDeletedTrue()
                .stream()
                .filter(user -> user.getRole() == UserRole.CUSTOMER)
                .toList();
    }

    // Restore customer
    @Override
    public void restoreUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + id));

        if (!user.isDeleted()) {
            throw new IllegalArgumentException(
                    "User is not in Trash");
        }

        if (user.getRole() != UserRole.CUSTOMER) {
            throw new IllegalArgumentException(
                    "Admin accounts cannot be restored through customer Trash");
        }

        user.setDeleted(false);
        userRepository.save(user);
    }

    // Permanently delete customer
    @Override
    @Transactional
    public void permanentlyDeleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + id));

        if (!user.isDeleted()) {
            throw new IllegalArgumentException(
                    "Move the customer to Trash before permanent deletion");
        }

        if (user.getRole() != UserRole.CUSTOMER) {
            throw new IllegalArgumentException(
                    "Admin accounts cannot be permanently deleted");
        }

        if (bookingRepository.existsByUserId(id)) {
            throw new IllegalStateException(
                    "Cannot permanently delete customer because booking history exists");
        }

        if (reviewRepository.existsByUserId(id)) {
            throw new IllegalStateException(
                    "Cannot permanently delete customer because review history exists");
        }

        userRepository.delete(user);
    }
}

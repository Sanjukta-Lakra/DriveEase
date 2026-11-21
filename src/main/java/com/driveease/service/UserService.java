package com.driveease.service;

import java.util.List;

import com.driveease.entity.User;
import com.driveease.enums.UserRole;

public interface UserService {

    User registerUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    User getUserByEmail(String email);

    User updateUserRole(Long id, UserRole role);

    User updateUserStatus(Long id, boolean active);
    
    User updateCustomerProfile(Long id, String name, String phone, String drivingLicenseNumber);
    
    void moveUserToTrash(Long id);

    List<User> getTrashedUsers();

    void restoreUser(Long id);

    void permanentlyDeleteUser(Long id);
}

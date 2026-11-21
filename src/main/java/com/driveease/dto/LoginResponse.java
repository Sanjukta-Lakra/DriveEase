package com.driveease.dto;

public class LoginResponse {

    private Long id;
    private String message;
    private String email;
    private String role;

    public LoginResponse() {

    }

    public LoginResponse(Long id, String message, String email, String role) {
        this.id = id;
        this.message = message;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

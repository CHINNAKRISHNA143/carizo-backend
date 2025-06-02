package com.carizo.dto;

public class AuthResponse {
    private String token;
    private String role;
    private String username;
    private String profileImageUrl;
    private Long userId;  // new field

    public AuthResponse(String token, String role, String username, String profileImageUrl, Long userId) {
        this.token = token;
        this.role = role;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.userId = userId;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
    
}

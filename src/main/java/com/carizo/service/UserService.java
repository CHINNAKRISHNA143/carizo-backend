package com.carizo.service;

import com.carizo.model.User;
import com.carizo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user, String profileImageUrl) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            user.setProfileImageUrl(profileImageUrl);
        }

        return userRepository.save(user);
    }

    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
 // ✅ Add this method for updating user profile
    public User updateUser(User user) {
        return userRepository.save(user); // Will update based on ID
    }

}

package com.carizo.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carizo.model.PasswordResetToken;
import com.carizo.model.User;
import com.carizo.repository.PasswordResetTokenRepository;
import com.carizo.repository.UserRepository;

@Service
public class PasswordResetService {
	
	@Value("${app.frontend.url}")
	private String frontendUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public void sendResetLink(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Don't reveal whether the email exists
        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        // Delete old token if it exists
        tokenRepository.findByUser(user)
                .ifPresent(tokenRepository::delete);

        // Generate random token
        String token = UUID.randomUUID().toString();

        // Expiry = 30 minutes
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken =
                new PasswordResetToken(token, expiry, user);

        tokenRepository.save(resetToken);

        String resetLink =
                frontendUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            tokenRepository.delete(resetToken);

            throw new RuntimeException("Reset token has expired");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}
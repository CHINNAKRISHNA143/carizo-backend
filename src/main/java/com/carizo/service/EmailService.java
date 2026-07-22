package com.carizo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Reset Your Carizo Password");

        message.setText(
                "Hello,\n\n"
              + "Click the link below to reset your password:\n\n"
              + resetLink
              + "\n\nThis link expires in 30 minutes.\n\n"
              + "If you didn't request this, you can safely ignore this email."
        );

        mailSender.send(message);
    }
}
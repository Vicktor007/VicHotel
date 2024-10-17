package com.vic.VicHotel.service.impl;

import com.vic.VicHotel.dto.Response;
import com.vic.VicHotel.entity.Token;
import com.vic.VicHotel.entity.User;
import com.vic.VicHotel.exception.MyException;
import com.vic.VicHotel.repository.TokenRepository;
import com.vic.VicHotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;

@Service
public class ForgottenPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${aws.frontend.url}")
    private String frontendUrl;

//    @Value("${frontend.url}")
//    private String frontendUrl;


    public Response forgotPassword(String email) {
        Response response = new Response();
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new MyException("User does not exist"));

            // Delete existing token if it exists
            tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

            // Create Reset Token
            String resetToken = generateResetToken() + user.getId();

            // Hash token before saving to DB
            String hashedToken = passwordEncoder.encode(resetToken);

            // Save Hashed Token to DB
            Token token = new Token();
            token.setUser(user);
            token.setToken(hashedToken);
            token.setCreatedAt(new Date());
            token.setExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // Thirty minutes
            tokenRepository.save(token);

            // Construct Reset Url with Token ID and Raw Token
            String resetUrl = frontendUrl + "/reset-password/" + token.getId() + "/" + resetToken;

            // Reset Email
            String message = String.format(
                    "<h2>Hello %s</h2><p>Please use the url below to reset your password</p><p>This reset link is valid for only 30 minutes.</p><a href=%s clicktracking=off>%s</a><p>Regards...</p><p>Vic Royal Suites Team</p>",
                    user.getUsername(), resetUrl, resetUrl);
            String subject = "Password Reset Request";

            emailService.sendMail(user.getEmail(), subject, message);
            response.setStatusCode(200);
        } catch (MyException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred while sending Mail  " + e.getMessage());

        }
        return response;
    }




    private String generateResetToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }



}


package com.vic.VicHotel.service.impl;

import com.vic.VicHotel.dto.Response;
import com.vic.VicHotel.entity.Token;
import com.vic.VicHotel.entity.User;
import com.vic.VicHotel.exception.MyException;
import com.vic.VicHotel.repository.TokenRepository;
import com.vic.VicHotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ResetPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Response resetPassword(String resetToken, Long tokenId, String newPassword) {
        Response response = new Response();
        try{

            // Find token by ID in database
            Token token = tokenRepository.findByIdAndExpiresAtAfter(tokenId, new Date())
                    .orElseThrow(() -> new MyException("Invalid or expired token"));

            // Verify the token
            if (!passwordEncoder.matches(resetToken, token.getToken())) {
                throw new MyException("Invalid or expired token");
            }

            // Find user
            User user = token.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Delete token after successful password reset
            tokenRepository.delete(token);
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

}



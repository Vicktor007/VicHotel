package com.vic.VicHotel.controller;


import com.vic.VicHotel.dto.LoginRequest;
import com.vic.VicHotel.dto.Response;
import com.vic.VicHotel.entity.User;
import com.vic.VicHotel.service.impl.ForgottenPasswordService;
import com.vic.VicHotel.service.impl.ResetPasswordService;
import com.vic.VicHotel.service.interfac.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ForgottenPasswordService forgottenPasswordService;

    @Autowired
    ResetPasswordService resetPasswordService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody User user) {
        Response response = userService.register(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest) {
        Response response = userService.login(loginRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<Response>  forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
       Response response = forgottenPasswordService.forgotPassword(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/reset-password/{tokenId}/{resetToken}")
    public ResponseEntity<Response> resetPassword(@PathVariable Long tokenId, @PathVariable String resetToken, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        Response response = resetPasswordService.resetPassword(resetToken, tokenId, newPassword);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}

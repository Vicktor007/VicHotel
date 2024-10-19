package com.vic.VicHotel.service.impl;

import com.vic.VicHotel.dto.LoginRequest;
import com.vic.VicHotel.dto.Response;
import com.vic.VicHotel.dto.UserDto;
import com.vic.VicHotel.entity.User;
import com.vic.VicHotel.exception.MyException;
import com.vic.VicHotel.repository.UserRepository;
import com.vic.VicHotel.service.interfac.IUserService;
import com.vic.VicHotel.utils.JWTUtils;
import com.vic.VicHotel.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService  implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Override
    public Response register(User user) {
        Response response = new Response();
        try {
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new MyException(user.getEmail() + "Already Exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDto userDTO = Utils.mapUserEntityToUserDTO(savedUser);

            String userEmail = user.getEmail(); // user's email
            String adminEmail = "vicktord007@gmail.com"; // Administrator's email
            String adminSubject = "New User Registration";
            String adminText = "A new User with email " + userEmail + " has just registered an account";

            // Check if the user is an admin
            if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {

                emailService.sendMail(adminEmail, adminSubject, adminText);
            }
            response.setStatusCode(200);
            response.setUser(userDTO);
        } catch (MyException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During USer Registration " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response login(LoginRequest loginRequest) {
        Response response = new Response();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new MyException("user Not found"));

            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setMessage("successful");

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error Occurred During USer Login " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllUsers() {

        Response response = new Response();
        try {
            List<User> userList = userRepository.findAll();
            List<UserDto> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUserList(userDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {


        Response response = new Response();


        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new MyException("User Not Found"));
            UserDto userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteUser(String userId) {


        Response response = new Response();

        try {
           User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new MyException("User Not Found"));
            userRepository.deleteById(Long.valueOf(userId));
            String userEmail = user.getEmail(); // user's email
            String adminEmail = "vicktord007@gmail.com"; // Administrator's email
            String adminSubject = "User Account Deleted";
            String adminText = "User with email " + userEmail + " has deleted their account";

            // Check if the user is an admin
            if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {

                emailService.sendMail(adminEmail, adminSubject, adminText);
            }
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all user " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getUserById(String userId) {

        Response response = new Response();

        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new MyException("User Not Found"));
            UserDto userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getMyInfo(String email) {

        Response response = new Response();

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new MyException("User Not Found"));
            UserDto userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setUser(userDTO);

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }
}

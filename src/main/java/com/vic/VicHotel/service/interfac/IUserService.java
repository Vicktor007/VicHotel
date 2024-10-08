package com.vic.VicHotel.service.interfac;

import com.vic.VicHotel.dto.LoginRequest;
import com.vic.VicHotel.dto.Response;
import com.vic.VicHotel.entity.User;

public interface IUserService {

    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);


}

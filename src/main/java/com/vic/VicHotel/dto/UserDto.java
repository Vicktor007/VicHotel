package com.vic.VicHotel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vic.VicHotel.entity.Booking;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private String PhoneNumber;
    private String role;
    private List<Booking> bookings = new ArrayList<>();
}

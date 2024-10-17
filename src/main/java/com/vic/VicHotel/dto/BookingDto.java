package com.vic.VicHotel.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {

    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numOfAdults;
    private int numOfChildren;
    private int totalNumberOfGuests;
    private String bookingConfirmationCode;
    private String notificationMode;
    private UserDto user;
    private RoomDto room;
}

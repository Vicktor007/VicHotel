package com.vic.VicHotel.service.interfac;

import com.vic.VicHotel.dto.Response;
import com.vic.VicHotel.entity.Booking;

public interface IBookingService {
    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);
}

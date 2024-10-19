package com.vic.VicHotel.service.impl;

import com.vic.VicHotel.dto.BookingDto;
import com.vic.VicHotel.dto.Response;
import com.vic.VicHotel.entity.Booking;
import com.vic.VicHotel.entity.Room;
import com.vic.VicHotel.entity.User;
import com.vic.VicHotel.exception.MyException;
import com.vic.VicHotel.repository.BookingRepository;
import com.vic.VicHotel.repository.RoomRepository;
import com.vic.VicHotel.repository.UserRepository;
import com.vic.VicHotel.service.interfac.IBookingService;
import com.vic.VicHotel.service.interfac.IRoomService;
import com.vic.VicHotel.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Value("${aws.frontend.url}")
    private String frontendUrl;


    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check in date must come after check out date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new MyException("Room Not Found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new MyException("User Not Found"));

            List<Booking> existingBookings = room.getBookings();

            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new MyException("Room not Available for selected date range");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);

            String userEmail = user.getEmail();
            String confirmationUrl = frontendUrl +  "/bookingDetails/" + bookingConfirmationCode;
            String subject = "Vic Royal Room Booking Confirmation";
            String smsText = "Your booking is confirmed with Vic Royal Suites. Your confirmation code is: " + bookingConfirmationCode + ". " + " " + " View your booking here: " + confirmationUrl;

            String text = "Your booking is confirmed with Vic Royal Suites. Your confirmation code is: <a href=\"" + confirmationUrl + "\">" + bookingConfirmationCode + "</a>";
            if (bookingRequest.getNotificationMode().equals("email")) {
                //Email message service
                emailService.sendMail(userEmail, subject, text);
            } else {
                // sms text service
                smsService.sendSms(user.getPhoneNumber(), smsText);
            }

            String adminEmail = "vicktord007@gmail.com"; // Administrator's email
            String adminSubject = "Room Reservation Notification";
            String adminText = "User with email " + userEmail + " has booked a Reservation with us. The confirmation code is<a href=\"" + confirmationUrl + "\">" + bookingConfirmationCode + "</a>" ;
            emailService.sendMail(adminEmail, adminSubject, adminText);



            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new MyException("Booking Not Found"));
            BookingDto bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Finding a booking: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response getAllBookings() {

        Response response = new Response();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDto> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting all bookings: " + e.getMessage());

        }
        return response;
    }

    public Response cancelBooking(Long bookingId) {
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new MyException("Booking Does Not Exist"));
            User user = booking.getUser(); // Get the user associated with the booking
            bookingRepository.deleteById(bookingId);

            String userEmail = user.getEmail(); // Get the user's email
            String email = "vic-Royal@gmail.com";
            String subject = "Vic Royal Cancelled Reservation";
            String text = "Your reservation is cancelled with Vic Royal Suites. You can contact us here if it was an error: " + email;

            // Email message service
            emailService.sendMail(userEmail, subject, text);

            // Check if the user is an admin
            if (!user.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                String adminEmail = "vicktord007@gmail.com"; // Administrator's email
                String adminSubject = "Cancelled Reservation Notification";
                String adminText = "User with email " + userEmail + " has cancelled a Reservation.";
                emailService.sendMail(adminEmail, adminSubject, adminText);
            }

            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (MyException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling a booking: " + e.getMessage());

        }
        return response;
    }


//    @Override
//    public Response cancelBooking(Long bookingId) {
//        Response response = new Response();
//
//        try {
//            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new MyException("Booking Does Not Exist"));
//            User user = booking.getUser(); // Get the user associated with the booking
//            bookingRepository.deleteById(bookingId);
//
//            String userEmail = user.getEmail(); // Get the user's email
//            String email = "vic-Royal@gmail.com";
//            String subject = "Vic Royal Cancelled Reservation";
//            String text = "Your reservation is cancelled with Vic Royal Suites. You can contact us here if it was an error: " + email;
//
//            // Email message service
//            emailService.sendMail(userEmail, subject, text);
//
//            response.setStatusCode(200);
//            response.setMessage("successful");
//
//        } catch (MyException e) {
//            response.setStatusCode(404);
//            response.setMessage(e.getMessage());
//
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setMessage("Error Cancelling a booking: " + e.getMessage());
//
//        }
//        return response;
//    }


//    public Response cancelBooking(Long bookingId) {
//        Response response = new Response();
//
//        try {
//            bookingRepository.findById(bookingId).orElseThrow(() -> new MyException("Booking Does Not Exist"));
//            bookingRepository.deleteById(bookingId);
//            User user = booking.g
//
//            String email = "vic-Royal@gmail.com";
//            String subject = "Vic Royal Cancelled Reservation";
//            String text = "Your reservation is cancelled  with Vic Royal Suites. You can contact Us here if it was an error : " + email;
//
//
//
//                //Email message service
//                emailService.sendMail(user.getEmail(), subject, text);
//
//            response.setStatusCode(200);
//            response.setMessage("successful");
//
//        } catch (MyException e) {
//            response.setStatusCode(404);
//            response.setMessage(e.getMessage());
//
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setMessage("Error Cancelling a booking: " + e.getMessage());
//
//        }
//        return response;
//    }

    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}

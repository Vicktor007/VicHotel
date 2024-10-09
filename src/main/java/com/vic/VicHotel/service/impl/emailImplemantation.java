//package com.vic.VicHotel.service.impl;
//
//public class emailImplemantation {
//
//    To modify your Spring Boot code to send a confirmation code via email, you'll need to integrate an email service. Here's how you can do it:
//
//            1. **Add Dependencies**: Add the necessary dependencies for sending emails. If you're using Maven, add the following to your `pom.xml`:
//
//            ```xml
//            <dependency>
//    <groupId>org.springframework.boot</groupId>
//    <artifactId>spring-boot-starter-mail</artifactId>
//</dependency>
//            ```
//
//            2. **Configure Email Properties**: Add your email configuration to `application.properties` or `application.yml`:
//
//            ```properties
//    spring.mail.host=smtp.example.com
//    spring.mail.port=587
//    spring.mail.username=your-email@example.com
//    spring.mail.password=your-email-password
//    spring.mail.properties.mail.smtp.auth=true
//    spring.mail.properties.mail.smtp.starttls.enable=true
//            ```
//
//            3. **Create Email Service**: Create a service to handle email sending.
//
//```java
//package com.vic.VicHotel.service.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//    @Service
//    public class EmailService {
//
//        @Autowired
//        private JavaMailSender mailSender;
//
//        public void sendEmail(String to, String subject, String text) {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(text);
//            mailSender.send(message);
//        }
//    }
//```
//
//        4. **Modify BookingService**: Inject the `EmailService` into your `BookingService` and send the email after generating the confirmation code.
//
//```java
//    @Service
//    public class BookingService implements IBookingService {
//
//        @Autowired
//        private BookingRepository bookingRepository;
//
//        @Autowired
//        private IRoomService roomService;
//
//        @Autowired
//        private RoomRepository roomRepository;
//
//        @Autowired
//        private UserRepository userRepository;
//
//        @Autowired
//        private EmailService emailService; // Add this line
//
//        @Override
//        public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
//            Response response = new Response();
//
//            try {
//                if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
//                    throw new IllegalArgumentException("Check in date must come after check out date");
//                }
//                Room room = roomRepository.findById(roomId).orElseThrow(() -> new MyException("Room Not Found"));
//                User user = userRepository.findById(userId).orElseThrow(() -> new MyException("User Not Found"));
//
//                List<Booking> existingBookings = room.getBookings();
//
//                if (!roomIsAvailable(bookingRequest, existingBookings)) {
//                    throw new MyException("Room not Available for selected date range");
//                }
//
//                bookingRequest.setRoom(room);
//                bookingRequest.setUser(user);
//                String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
//                bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
//                bookingRepository.save(bookingRequest);
//
//                // Send confirmation email
//                String subject = "Booking Confirmation";
//                String text = "Your booking is confirmed. Your confirmation code is: " + bookingConfirmationCode;
//                emailService.sendEmail(user.getEmail(), subject, text);
//
//                response.setStatusCode(200);
//                response.setMessage("successful");
//                response.setBookingConfirmationCode(bookingConfirmationCode);
//
//            } catch (MyException e) {
//                response.setStatusCode(404);
//                response.setMessage(e.getMessage());
//
//            } catch (Exception e) {
//                response.setStatusCode(500);
//                response.setMessage("Error Saving a booking: " + e.getMessage());
//
//            }
//            return response;
//        }
//
//        // Other methods remain unchanged
//    }
//```
//
//    This will send an email with the confirmation code to the user's email address after a successful booking. Let me know if you need any further assistance!
//
//
//
//}

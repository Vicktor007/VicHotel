package com.vic.VicHotel.service.impl;

public class smsimplementation {
//
//    Yes, you can definitely use SMS instead of email to send the verification code. To do this, you'll need to integrate an SMS service like Twilio. Here's how you can modify your implementation:
//
//            1. **Add Dependencies**: Add the necessary dependencies for sending SMS. If you're using Maven, add the following to your `pom.xml`:
//
//            ```xml
//            <dependency>
//    <groupId>com.twilio.sdk</groupId>
//    <artifactId>twilio</artifactId>
//    <version>8.27.0</version>
//</dependency>
//            ```
//
//            2. **Configure Twilio Properties**: Add your Twilio configuration to `application.properties` or `application.yml`:
//
//            ```properties
//    twilio.accountSid=your_account_sid
//    twilio.authToken=your_auth_token
//    twilio.phoneNumber=your_twilio_phone_number
//```
//
//        3. **Create SMS Service**: Create a service to handle SMS sending.
//
//```java
//package com.vic.VicHotel.service.impl;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
//    @Service
//    public class SmsService {
//
//        @Value("${twilio.accountSid}")
//        private String accountSid;
//
//        @Value("${twilio.authToken}")
//        private String authToken;
//
//        @Value("${twilio.phoneNumber}")
//        private String fromPhoneNumber;
//
//        @PostConstruct
//        public void init() {
//            Twilio.init(accountSid, authToken);
//        }
//
//        public void sendSms(String to, String text) {
//            Message.creator(new PhoneNumber(to), new PhoneNumber(fromPhoneNumber), text).create();
//        }
//    }
//```
//
//        4. **Modify BookingService**: Inject the `SmsService` into your `BookingService` and send the SMS after generating the confirmation code.
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
//        private SmsService smsService; // Add this line
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
//                // Send confirmation SMS
//                String text = "Your booking is confirmed. Your confirmation code is: " + bookingConfirmationCode;
//                smsService.sendSms(user.getPhoneNumber(), text);
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
//    This will send an SMS with the confirmation code to the user's phone number after a successful booking. Let me know if you need any further assistance!
//
//    Source: Conversation with Copilot, 10/11/2024
//            (1) github.com. https://github.com/centennial-web/pethappy/tree/68ab263249c5d365b17bf27adafa44b37ec81b47/server%2Fsrc%2Fmain%2Fjava%2Fca%2Fpethappy%2Fserver%2Fservices%2FSmsService.java.
}

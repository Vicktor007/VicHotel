package com.vic.VicHotel.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "check in date is required")
    private LocalDate checkInDate;

    @NotNull(message = "check out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Number of adults must not be less than 1")
    private int numOfAdults;

    @Min(value = 0, message = "Number of adults must not be less than 0" )
    private int noOfChildren;


    private int totalNumberOfGuests;
    private String bookingConfirmationCode;
    private String notificationMode;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void calculateTotalNumberOfGuests(){
        this.totalNumberOfGuests = this.numOfAdults + this.noOfChildren;
    }

    public void setNumOfAdults(@Min(value = 1, message = "Number of adults must not be less than 1") int numOfAdults) {
        this.numOfAdults = numOfAdults;
        calculateTotalNumberOfGuests();
    }

    public void setNoOfChildren(@Min(value = 0, message = "Number of children must not be less than 0") int noOfChildren) {
        this.noOfChildren = noOfChildren;
        calculateTotalNumberOfGuests();
    }

    public void setNotificationMode(String mode) {
        if (mode.equals("email") || mode.equals("sms")) {
            this.notificationMode = mode;
        } else {
            throw new IllegalArgumentException("Invalid notification mode");
        }
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingConfirmationCode='" + bookingConfirmationCode + '\'' +
                ", id=" + id +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numOfAdults=" + numOfAdults +
                ", noOfChildren=" + noOfChildren +
                ", totalNumberOfGuests=" + totalNumberOfGuests +
                ", notificationMode='" + notificationMode + '\'' +

                '}';
    }

}



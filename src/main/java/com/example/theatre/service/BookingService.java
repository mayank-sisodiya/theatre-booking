package com.example.theatre.service;

import com.example.theatre.dao.BookingRespository;
import com.example.theatre.dao.ShowRespository;
import com.example.theatre.dto.Seat;
import com.example.theatre.entity.Booking;
import com.example.theatre.entity.Show;
import com.example.theatre.entity.User;
import com.example.theatre.enums.BookingStatus;
import com.example.theatre.enums.ShowType;
import com.example.theatre.exceptions.BookingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingService {

    @Autowired
    private BookingRespository bookingRespository;

    @Autowired
    private ShowRespository showRespository;

    @Autowired
    private PaymentService paymentService;

    @Transactional
    Booking bookSeats(User user, Show show, List<Seat> seats) {
        log.info("Received booking request for user: {}, show: {} and sheats: {}",user,show,seats);

        Double totalPrices = seats.stream().mapToDouble(seat -> seat.getSeatType().getPrice()).sum();
        Booking booking = Booking.builder()
                .bookingId(generateBookingId(show))
                .price(totalPrices)
                .user(user)
                .seats(seats)
                .show(show)
                .status(BookingStatus.FAILURE)
                .build();

        if(Duration.between(LocalDateTime.now(), LocalDateTime.of(show.getDate(),show.getShowType().getTime())).toMinutes() < 15){
            log.info("seats cannot be booked prior 15 min of show");
            notifyUserWithStatus(booking, user);
            throw new BookingException("seats cannot be booked prior 15 min of show");

        }

        if(!show.getAvailableSeats().containsAll(seats)) {
            log.info("Seats are already booked");
            notifyUserWithStatus(booking, user);
            throw new BookingException("Seats already booked");
        }

        List<Seat> availableSeats = show.getAvailableSeats();
        synchronized (availableSeats) {
            availableSeats.removeAll(seats);
            show.setAvailableSeats(availableSeats);
            showRespository.save(show);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(()->paymentService.payment(totalPrices));
        Boolean result = false;
        try {
            result = future.get(3, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Payment failed, timeout exception");
            notifyUserWithStatus(booking, user);
            throw new BookingException("Payement failed due to payment timeout");
        }

        if(!result) {
            log.error("Payment failed, rolling back available seats");
            notifyUserWithStatus(booking, user);
            throw new BookingException("Payement failed");

        }

        log.info("Payment successful");
        booking.setStatus(BookingStatus.SUCCESS);
        notifyUserWithStatus(booking, user);

        bookingRespository.save(booking);
        return booking;
    }

    private String generateBookingId(Show show) {
        StringBuilder bookingId = new StringBuilder();
        bookingId.append("RM");
        bookingId.append(show.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yy")));
        bookingId.append(show.getShowType().getValue());
        bookingId.append(bookingRespository.countBookingByShow(show));
        return bookingId.toString();
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void notifyUserWithStatus(Booking booking, User user) {
        booking.addPropertyChangeListener(user);
        booking.notifyUser("Transcation was a "+ booking.getStatus().toString());
    }
}

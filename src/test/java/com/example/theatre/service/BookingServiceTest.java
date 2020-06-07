package com.example.theatre.service;

import com.example.theatre.dao.BookingRespository;
import com.example.theatre.dao.ShowRespository;
import com.example.theatre.dao.UserRepository;
import com.example.theatre.dto.Seat;
import com.example.theatre.entity.Booking;
import com.example.theatre.entity.Show;
import com.example.theatre.entity.User;
import com.example.theatre.enums.ShowType;
import com.example.theatre.exceptions.BookingException;
import lombok.extern.slf4j.Slf4j;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Slf4j
public class BookingServiceTest {
    @Rule
    public MockitoRule mockitoRule= MockitoJUnit.rule();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShowRespository showRespository;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRespository bookingRespository;
    @Mock
    private PaymentService paymentService;

    User userOne;
    User userTwo;
    Show show;
    List<Seat> seatsOne = new ArrayList<>(), seatsTwo = new ArrayList<>();
    Seat seatA, seatB, seatC, seatD, seatE;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userOne = new User(1L);
        userTwo = new User(2L);
        show = new Show(1L, LocalDate.of(2020, 10,15), ShowType.MORNING);
        seatA = new Seat(1,'A');
        seatB = new Seat(2, 'B');
        seatC = new Seat(1, 'C');
        seatD = new Seat(5, 'D');
        seatE = new Seat(9, 'F');

        seatsOne.add(seatA); seatsOne.add(seatB); seatsOne.add(seatC);
        seatsTwo.add(seatD); seatsTwo.add(seatC); seatsTwo.add(seatE);

        userRepository.save(userOne);
        userRepository.save(userTwo);
        showRespository.save(show);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBookTickets() {
        //when(passwordEncoder.encode("1")).thenReturn("a");
        try {
            when(paymentService.payment(anyDouble())).thenReturn(true);
        }catch (InterruptedException e) {
            log.warn("Interuption error");
        }
        Booking booking = bookingService.bookSeats(userOne,show, seatsOne);
        double expectedPrice = booking.getPrice();
        assertEquals(750.0, expectedPrice, 0.0001);
    }

    @Test
    public void paymentSuccessSeatsAvailabityTest() {
        try {
            when(paymentService.payment(anyDouble())).thenReturn(true);
        }catch (InterruptedException e) {
            log.warn("Interuption error");
        }
        int availableSeatsBeforeTest = show.getAvailableSeats().size();
        Booking booking = bookingService.bookSeats(userOne,show, seatsOne);
        int availableSeatsAfterTest = showRespository.findById(show.getShowId()).get().getAvailableSeats().size();
        assertEquals(availableSeatsBeforeTest, availableSeatsAfterTest + 3);
    }

    @Test(expected = BookingException.class)
    public void paymentFailureSeatsAvailabityTest() {
        bookingService.setPaymentService(paymentService);
        try {
            when(paymentService.payment(anyDouble())).thenReturn(false);
        }catch (InterruptedException e) {
            log.warn("Interuption error");
        }
        int availableSeatsBeforeTest = show.getAvailableSeats().size();
        Booking booking = bookingService.bookSeats(userOne,show, seatsOne);
        int availableSeatsAfterTest = showRespository.findById(show.getShowId()).get().getAvailableSeats().size();
        assertEquals(availableSeatsBeforeTest, availableSeatsAfterTest );
    }

    @Test
    public void concurencyTest() {
        try {
            when(paymentService.payment(anyDouble())).thenReturn(true);
        }catch (InterruptedException e) {
            log.warn("Interuption error");
        }
        final ArrayList<Thread> threads = new ArrayList<>();
        //defult value s false
        final ArrayList<Boolean> booleans = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User(Long.valueOf(i+5));
            userRepository.save(user);
            Thread thread = new Thread(() -> {
                //bookingSerice.bookSeats will throw exception when not able to book and hence boolean[i] will remain false
                Booking booking = bookingService.bookSeats(user, show, seatsOne);
                booleans.add(true);
            });
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long count = booleans.stream().filter(Boolean::booleanValue).count();
        assertEquals(1,count);
    }

}
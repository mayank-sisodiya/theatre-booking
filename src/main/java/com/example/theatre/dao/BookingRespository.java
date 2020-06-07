package com.example.theatre.dao;

import com.example.theatre.entity.Booking;
import com.example.theatre.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRespository extends JpaRepository<Booking, Long> {
    Integer countBookingByShow(Show show);
}

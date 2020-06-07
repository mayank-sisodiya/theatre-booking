package com.example.theatre.utils;

import com.example.theatre.dto.Seat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SeatGenerator {
    public static List<Seat> getInitialSeats() {
        List<Seat> seats = new ArrayList<>();
        IntStream.range('A', 'Y')
                .mapToObj(col -> IntStream.range(1, 9)
                        .mapToObj(row -> new Seat(row, (char) col))
                        .collect(Collectors.toList()))
                .forEach(seats::addAll);
        return seats;
    }
}

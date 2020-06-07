package com.example.theatre.entity;

import com.example.theatre.dto.Seat;
import com.example.theatre.enums.ShowType;
import com.example.theatre.utils.SeatGenerator;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showId;

    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private ShowType showType;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Seat> availableSeats = new ArrayList<>();

    @OneToMany(mappedBy = "show", fetch = FetchType.LAZY)
    private List<Booking> booking;

    public Show(Long id, LocalDate date,ShowType showType) {
        this.showId = id;
        this.date = date;
        this.showType = showType;
        this.availableSeats = SeatGenerator.getInitialSeats();
    }
}


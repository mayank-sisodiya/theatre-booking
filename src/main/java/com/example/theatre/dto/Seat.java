package com.example.theatre.dto;

import com.example.theatre.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Data
public class Seat implements Serializable {
    Integer row;
    Character column;
    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    public Seat(Integer row, Character column) {
        this.row = row;
        this.column = column;
        if(row>7)
            this.seatType = SeatType.PLATIMUN;
        else if(row>4)
            this.seatType = SeatType.GOLD;
        else
            this.seatType = SeatType.SILVER;
    }

    @Override
    public String toString() {
        return row.toString() + column;
    }
}

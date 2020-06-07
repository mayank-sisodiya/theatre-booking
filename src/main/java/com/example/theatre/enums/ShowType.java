package com.example.theatre.enums;

import java.time.LocalDateTime;
import java.time.LocalTime;

public enum ShowType {
    MORNING("MOR",LocalTime.of(10,0)), EVENING("EVE",LocalTime.of(15,0)), MATINEE("MAT",LocalTime.of(16,0));
    private final String value;
    private final LocalTime time;

    ShowType(String value, LocalTime time) {
        this.value = value;
        this.time = time;
    }

    public String getValue() {
        return value;
    }
    public LocalTime getTime() { return time; }
}

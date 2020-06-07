package com.example.theatre.enums;

public enum SeatType {
    SILVER("SILVER", 250.0), GOLD("GOLD", 500.0), PLATIMUN("PLATINUM", 750.0);

    private final String value;
    private final Double price;

    SeatType(String value, Double price) {
        this.value = value;
        this.price = price;
    }

    public String getValue() {
        return value;
    }
    public Double getPrice() { return price; }
}

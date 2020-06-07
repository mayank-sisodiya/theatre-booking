package com.example.theatre.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {
    public Boolean payment(Double price) throws InterruptedException {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            log.warn("Interuption error");
        }
        return true;
    }
}

package com.example.theatre.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

@Entity
@Table(name = "user_table")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class User implements PropertyChangeListener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Booking> bookings;

    public User(Long id) {
        this.userId = id;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        log.info("Folloing booking event received: {}",propertyChangeEvent.getNewValue());
    }
}

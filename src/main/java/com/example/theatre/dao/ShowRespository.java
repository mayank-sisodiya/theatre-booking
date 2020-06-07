package com.example.theatre.dao;

import com.example.theatre.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRespository extends JpaRepository<Show, Long> {
}

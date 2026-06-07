package com.ineedtoilet.api.repository;

import com.ineedtoilet.api.entity.Toilet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToiletRepository extends JpaRepository<Toilet, Long> {
}
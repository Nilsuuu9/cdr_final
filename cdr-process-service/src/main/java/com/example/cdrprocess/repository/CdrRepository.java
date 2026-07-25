package com.example.cdrprocess.repository;

import com.example.cdrprocess.entity.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CdrRepository extends JpaRepository<Cdr, Long> {

    boolean existsByEventId(String eventId);
}

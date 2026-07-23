package com.example.cdrreport.repository;

import com.example.cdrreport.entity.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdrRepository extends JpaRepository<Cdr, Long> {
    List<Cdr> findByANumber(String aNumber);
}

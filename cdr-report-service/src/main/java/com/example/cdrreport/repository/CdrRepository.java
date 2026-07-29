package com.example.cdrreport.repository;

import com.example.cdrreport.entity.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CdrRepository extends JpaRepository<Cdr, Long> {
    @Query("select c from Cdr c where c.aNumber = :aNumber")
    List<Cdr> findByANumber(@Param("aNumber") String aNumber);
}

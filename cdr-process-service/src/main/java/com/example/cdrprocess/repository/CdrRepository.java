package com.example.cdrprocess.repository;

import com.example.cdrprocess.entity.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CdrRepository extends JpaRepository<Cdr, Long> {

    boolean existsByEventId(String eventId);
}
//Spring Data JPA sayesinde temel MySQL işlemleri otomatik gelir:save(),findAll(),findById(),delete(),Bu metot özel olarak eklenmiştir:boolean existsByEventId(String eventId); Service bu metodu kullanarak aynı Kafka mesajının tekrar kaydedilip kaydedilmediğini kontrol eder.

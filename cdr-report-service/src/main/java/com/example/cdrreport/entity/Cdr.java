package com.example.cdrreport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "cdrs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cdr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private String eventId;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "a_number")
    private String aNumber;
    @Column(name = "b_number")
    private String bNumber;
    @Column(name = "setup_duration")
    private Long setupDuration;
    @Column(name = "conversation_duration")
    private Long conversationDuration;
    private String direction;
    private String result;
    @Column(name = "charge_amount")
    private BigDecimal chargeAmount;
}

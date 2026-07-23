package com.example.cdrprocess.entity;

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

    @Column(name = "event_id", unique = true, length = 36)
    private String eventId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(length = 15)
    private String imsi;

    @Column(length = 14)
    private String imei;

    @Column(name = "cell_id")
    private Integer cellId;

    @Column(name = "lac_id")
    private Integer lacId;

    @Column(name = "a_number", nullable = false)
    private String aNumber;

    @Column(name = "b_number", nullable = false)
    private String bNumber;

    @Column(name = "setup_duration")
    private Long setupDuration;

    @Column(name = "conversation_duration")
    private Long conversationDuration;

    private String direction;
    private String result;

    @Column(name = "charge_amount", precision = 10, scale = 2)
    private BigDecimal chargeAmount;

    public Cdr(String eventId, LocalDateTime startTime, LocalDateTime endTime, String imsi, String imei,
               Integer cellId, Integer lacId, String aNumber, String bNumber, Long setupDuration,
               Long conversationDuration, String direction, String result, BigDecimal chargeAmount) {
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imsi = imsi;
        this.imei = imei;
        this.cellId = cellId;
        this.lacId = lacId;
        this.aNumber = aNumber;
        this.bNumber = bNumber;
        this.setupDuration = setupDuration;
        this.conversationDuration = conversationDuration;
        this.direction = direction;
        this.result = result;
        this.chargeAmount = chargeAmount;
    }
}

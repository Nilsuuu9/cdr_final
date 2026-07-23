package com.example.cdrreport.service;

import com.example.cdrreport.entity.Cdr;
import com.example.cdrreport.repository.CdrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //servis içi transactionlar okuma amaçlıdır.
public class CdrQueryService {
    private final CdrRepository cdrRepository;

    public CdrQueryService(CdrRepository cdrRepository) {
        this.cdrRepository = cdrRepository;
    }

    public List<Cdr> getAll() {
        return cdrRepository.findAll();
    } //tüm kayıtları getirir

    public List<Cdr> getByCallerNumber(String callerNumber) {
        return cdrRepository.findByANumber(callerNumber);
    }  //arayan numaraya göre filtreleme
}

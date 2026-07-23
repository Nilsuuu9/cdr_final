package com.example.cdrprocess.exception;

public class InvalidCdrMessageException extends RuntimeException {

    public InvalidCdrMessageException(String message) {
        super(message);
    }
}

//özel hata sınıfıdır 'throw new InvalidCdrMessageException(...)' Bu mesaj Kafka’dan geldi ama CDR olarak geçerli değil.
//Böylece listener, geçersiz JSON hatasıyla veritabanı/altyapı hatasını birbirinden ayırabilir.

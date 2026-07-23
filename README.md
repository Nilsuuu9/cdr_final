# CDR System - Faz 2

Bu proje sahte CDR (Call Detail Record) verisi ureten, Kafka ile isleyen ve raporlayan mikroservis yapisidir.

## Mimari

```text
cdr-generator
  Her saniye rastgele CDR JSON'u uretir.
          |
          | kafka:9092 / cdr-raw-topic
          v
Kafka
          |
          v
cdr-process-service
  Mesaji dogrular, ucret hesaplar ve MySQL'e yazar.
          |
          v
MySQL / cdr_system / cdrs
          |
          v
cdr-report-service
  Sadece CDR kayitlarini okur ve GET endpoint'lerini sunar.
          |
          v
http://localhost:8082

Adminer
  MySQL verisini tarayicidan kontrol etmek icin kullanilir.
  http://localhost:8081
```

Servis sorumluluklari:

- `cdr-generator`: CDR uretir; MySQL'e veya REST endpoint'ine yazmaz.
- `cdr-process-service`: Kafka'dan okur ve MySQL'e yazar; REST endpoint'i yoktur.
- `cdr-report-service`: MySQL'den okur; Kafka dinlemez ve CDR yazmaz.
- `kafka-init`: `cdr-raw-topic` topic'ini bir kez olusturur, sonra `Exited (0)` durumunda durur.

## Sistemi Baslatma

Docker Desktop acikken proje kok dizininde su komutu calistir:

```powershell
docker compose up -d --build
```

Bu tek komut MySQL, Kafka, Adminer, topic olusturucu, Python generator, process service ve report service'i baslatir.

Durumu kontrol etmek icin:

```powershell
docker compose ps
```

Canli process service loglari:

```powershell
docker compose logs -f cdr-process-service
```

Sistemi kapatmak icin:

```powershell
docker compose down
```

`down -v` kullanma; MySQL ve Kafka volume verilerini de siler.

## Endpoint Ornekleri

Report service adresi:

```text
http://localhost:8082
```

### Tum CDR Kayitlari

```http
GET /api/cdrs
```

```powershell
Invoke-RestMethod http://localhost:8082/api/cdrs
```

Ornek cevap:

```json
[
  {
    "id": 858,
    "eventId": "071d44df-5ba6-437a-bee7-c4f6ce643e1a",
    "startTime": "2026-07-22T08:29:18",
    "endTime": "2026-07-22T08:32:44",
    "aNumber": "5551112233",
    "bNumber": "5554445566",
    "setupDuration": 4,
    "conversationDuration": 206,
    "direction": "MO",
    "result": "ANSWERED",
    "chargeAmount": 10.30
  }
]
```

### Arayan Numaraya Gore CDR Sorgulama

```http
GET /api/cdrs/by-caller/{phoneNumber}
```

```powershell
Invoke-RestMethod http://localhost:8082/api/cdrs/by-caller/5551112233
```

Faz 2'de manuel CDR olusturan `POST` endpoint'i yoktur. Yeni CDR'lar yalnizca Python generator tarafindan Kafka'ya gonderilir.

## Adminer ile MySQL Kontrolu

Tarayicidan ac:

```text
http://localhost:8081
```

Giris bilgileri:

```text
System: MySQL
Server: mysql
Username: root
Password: .env dosyasindaki MYSQL_PASSWORD degeri
Database: cdr_system
```

`cdrs` tablosunda process service'in yazdigi CDR kayitlarini gorebilirsin.

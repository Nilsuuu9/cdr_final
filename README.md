# CDR System - Phase 2

This project is a microservice system that generates sample CDR (Call Detail Record) data, processes it with Kafka, and provides reporting endpoints.

## Architecture

```text
cdr-generator
  Generates a random CDR JSON message every second.
          |
          | kafka:9092 / cdr-raw-topic
          v
Kafka
          |
          v
cdr-process-service
  Validates messages, calculates charges, and writes records to MySQL.
          |
          v
MySQL / cdr_system / cdrs
          |
          v
cdr-report-service
  Reads CDR records and exposes GET endpoints.
          |
          v
http://localhost:8082

Adminer
  Used to inspect MySQL data in a browser.
  http://localhost:8081
```

Service responsibilities:

- `cdr-generator`: Generates CDR messages; it does not write to MySQL or a REST endpoint.
- `cdr-process-service`: Reads from Kafka and writes to MySQL; it has no REST endpoint.
- `cdr-report-service`: Reads from MySQL; it does not consume Kafka messages or write CDR records.
- `kafka-init`: Creates the `cdr-raw-topic` topic once and then exits with status `Exited (0)`.

## Starting the System

With Docker Desktop running, execute the following command from the project root:

```powershell
docker compose up -d --build
```

This command starts MySQL, Kafka, Adminer, the topic initializer, the Python generator, the process service, and the report service.

To check the service status:

```powershell
docker compose ps
```

Live process service logs:

```powershell
docker compose logs -f cdr-process-service
```

To stop the system:

```powershell
docker compose down
```

Do not use `down -v`; it also deletes the MySQL and Kafka volume data.

## Endpoint Examples

Report service URL:

```text
http://localhost:8082
```

### All CDR Records

```http
GET /api/cdrs
```

```powershell
Invoke-RestMethod http://localhost:8082/api/cdrs
```

Example response:

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

### Query CDR Records by Caller Number

```http
GET /api/cdrs/by-caller/{phoneNumber}
```

```powershell
Invoke-RestMethod http://localhost:8082/api/cdrs/by-caller/5551112233
```

Phase 2 does not provide a `POST` endpoint for manually creating CDR records. New CDRs are sent to Kafka only by the Python generator.

## Inspecting MySQL with Adminer

Open the following URL in a browser:

```text
http://localhost:8081
```

Login details:

```text
System: MySQL
Server: mysql
Username: root
Password: The `MYSQL_PASSWORD` value from the `.env` file
Database: cdr_system
```

The CDR records written by the process service are available in the `cdrs` table.

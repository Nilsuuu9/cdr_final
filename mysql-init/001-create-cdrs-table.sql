CREATE TABLE IF NOT EXISTS cdrs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id VARCHAR(36) UNIQUE,
    start_time DATETIME(6) NOT NULL,
    end_time DATETIME(6) NOT NULL,
    imsi VARCHAR(15),
    imei VARCHAR(14),
    cell_id INT,
    lac_id INT,
    a_number VARCHAR(255) NOT NULL,
    b_number VARCHAR(255) NOT NULL,
    setup_duration BIGINT,
    conversation_duration BIGINT,
    direction VARCHAR(255),
    result VARCHAR(255),
    charge_amount DECIMAL(10, 2),
    PRIMARY KEY (id)
);

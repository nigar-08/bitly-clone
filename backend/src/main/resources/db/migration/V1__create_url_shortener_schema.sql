CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(254) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE url_mappings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    original_url VARCHAR(2048) NOT NULL,
    short_url VARCHAR(12) NOT NULL,
    click_count INT NOT NULL DEFAULT 0,
    created_date DATETIME(6) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_url_mappings_short_url UNIQUE (short_url),
    CONSTRAINT fk_url_mappings_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_url_mapping_user_created ON url_mappings (user_id, created_date);

CREATE TABLE click_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    click_date DATETIME(6) NOT NULL,
    url_mapping_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_click_events_mapping FOREIGN KEY (url_mapping_id) REFERENCES url_mappings (id)
);

CREATE INDEX idx_click_mapping_date ON click_events (url_mapping_id, click_date);

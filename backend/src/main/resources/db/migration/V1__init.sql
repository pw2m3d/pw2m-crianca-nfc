CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(160) NOT NULL,
    email VARCHAR(190) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE children (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    public_token CHAR(36) NOT NULL,
    full_name VARCHAR(160) NOT NULL,
    birth_date DATE NULL,
    blood_type VARCHAR(5) NULL,
    photo_url VARCHAR(500) NULL,
    address_line VARCHAR(255) NULL,
    city VARCHAR(120) NULL,
    state CHAR(2) NULL,
    public_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_children_public_token (public_token),
    KEY idx_children_owner_id (owner_id),
    CONSTRAINT fk_children_owner
        FOREIGN KEY (owner_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE medical_info (
    id BIGINT NOT NULL AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    allergies TEXT NULL,
    conditions_text TEXT NULL,
    medications TEXT NULL,
    health_plan VARCHAR(160) NULL,
    health_plan_number VARCHAR(120) NULL,
    emergency_notes TEXT NULL,
    updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_medical_child (child_id),
    CONSTRAINT fk_medical_child
        FOREIGN KEY (child_id) REFERENCES children(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE emergency_contacts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    name VARCHAR(160) NOT NULL,
    relation_label VARCHAR(80) NOT NULL,
    phone VARCHAR(40) NOT NULL,
    whatsapp VARCHAR(40) NULL,
    priority INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    KEY idx_contacts_child (child_id),
    CONSTRAINT fk_contacts_child
        FOREIGN KEY (child_id) REFERENCES children(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE nfc_devices (
    id BIGINT NOT NULL AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    device_name VARCHAR(120) NOT NULL,
    device_token CHAR(36) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_nfc_device_token (device_token),
    KEY idx_nfc_child (child_id),
    CONSTRAINT fk_nfc_child
        FOREIGN KEY (child_id) REFERENCES children(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE access_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    child_id BIGINT NOT NULL,
    device_id BIGINT NULL,
    accessed_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_logs_child_date (child_id, accessed_at),
    CONSTRAINT fk_logs_child
        FOREIGN KEY (child_id) REFERENCES children(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_logs_device
        FOREIGN KEY (device_id) REFERENCES nfc_devices(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

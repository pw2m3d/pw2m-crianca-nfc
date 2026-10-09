-- ============================================================
-- Pessoas protegidas
-- Mantemos a tabela "children" por compatibilidade,
-- mas agora ela poderá representar criança, idoso, adulto etc.
-- ============================================================

ALTER TABLE children
    ADD COLUMN person_type VARCHAR(30)
    NOT NULL DEFAULT 'CHILD'
    AFTER full_name;


-- ============================================================
-- NFC / PRODUTOS
-- ============================================================

-- O dispositivo não precisa mais estar ligado a uma pessoa
-- no momento da fabricação.

ALTER TABLE nfc_devices
    MODIFY child_id BIGINT NULL;


-- Código permanente.
-- VARCHAR(50) permite futuramente códigos como:
-- PW2M-7K4M-92QD
-- e continua aceitando UUIDs antigos.

ALTER TABLE nfc_devices
    MODIFY device_token VARCHAR(50) NOT NULL;


-- Cliente proprietário do produto.

ALTER TABLE nfc_devices
    ADD COLUMN owner_id BIGINT NULL
    AFTER id;


-- PIN secreto de ativação armazenado em hash BCrypt.
--
-- NULL é permitido porque os dispositivos antigos já existentes
-- não possuíam PIN de ativação.

ALTER TABLE nfc_devices
    ADD COLUMN activation_code_hash VARCHAR(255) NULL
    AFTER device_token;


-- Estado comercial/operacional do produto.

ALTER TABLE nfc_devices
    ADD COLUMN status VARCHAR(30)
    NOT NULL DEFAULT 'ACTIVE'
    AFTER activation_code_hash;


-- Quando foi produzido / cadastrado no estoque PW2M.

ALTER TABLE nfc_devices
    ADD COLUMN manufactured_at TIMESTAMP(6)
    NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
    AFTER status;


-- Quando o comprador reivindicou o dispositivo.

ALTER TABLE nfc_devices
    ADD COLUMN claimed_at TIMESTAMP(6) NULL
    AFTER manufactured_at;


-- ============================================================
-- MIGRAR OS NFCs QUE JÁ EXISTEM
-- ============================================================

-- Os dispositivos atuais já estão ligados a crianças.
-- Portanto o proprietário será o mesmo responsável da criança.

UPDATE nfc_devices d
INNER JOIN children c
        ON c.id = d.child_id
SET
    d.owner_id = c.owner_id,

    d.status =
        CASE
            WHEN d.active = TRUE
                THEN 'ACTIVE'
            ELSE 'SUSPENDED'
        END,

    d.manufactured_at = d.created_at;


-- ============================================================
-- ÍNDICES
-- ============================================================

ALTER TABLE nfc_devices
    ADD KEY idx_nfc_owner (owner_id);

ALTER TABLE nfc_devices
    ADD KEY idx_nfc_status (status);


-- ============================================================
-- FOREIGN KEY DO CLIENTE
-- ============================================================

ALTER TABLE nfc_devices
    ADD CONSTRAINT fk_nfc_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(id)
        ON DELETE SET NULL;


-- ============================================================
-- ALTERAR RELAÇÃO NFC -> PESSOA
--
-- Hoje está ON DELETE CASCADE.
--
-- Não queremos mais apagar fisicamente um chaveiro comprado
-- quando uma pessoa for excluída da conta.
-- ============================================================

ALTER TABLE nfc_devices
    DROP FOREIGN KEY fk_nfc_child;


ALTER TABLE nfc_devices
    ADD CONSTRAINT fk_nfc_child
        FOREIGN KEY (child_id)
        REFERENCES children(id)
        ON DELETE SET NULL;
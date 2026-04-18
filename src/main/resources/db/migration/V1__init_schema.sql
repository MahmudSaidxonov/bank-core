CREATE TABLE users
(
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) UNIQUE NOT NULL,
    email           VARCHAR(100) UNIQUE NOT NULL,
    password_hash   VARCHAR(225) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    account_number  VARCHAR(20) UNIQUE NOT NULL,
    balance         NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    currency        VARCHAR(3) DEFAULT 'UZS',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
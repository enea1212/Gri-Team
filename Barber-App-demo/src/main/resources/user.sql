CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
    );

ALTER TABLE users
    ADD COLUMN enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN verification_token VARCHAR(255);

CREATE TABLE IF NOT EXISTS anunturi (
                          id SERIAL PRIMARY KEY,
                          titlu VARCHAR(255) NOT NULL,
                          descriere TEXT NOT NULL,
                          pret NUMERIC(10, 2) NOT NULL,
                          poza VARCHAR(255) NOT NULL,
                          user_id BIGINT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_user FOREIGN KEY (user_id)
                              REFERENCES users(id)
                              ON DELETE CASCADE
);
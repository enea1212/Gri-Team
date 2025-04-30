CREATE TABLE anunturi (
                          id BIGSERIAL PRIMARY KEY,
                          titlu VARCHAR(255) NOT NULL,
                          descriere TEXT NOT NULL,
                          pret DECIMAL(10,2) NOT NULL,
                          locatie VARCHAR(255) NOT NULL,
                          poza VARCHAR(255),
                          user_id BIGINT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id)
);
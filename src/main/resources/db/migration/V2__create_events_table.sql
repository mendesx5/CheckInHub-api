CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY ,
    title VARCHAR(150) NOT NULL ,
    description TEXT ,
    date_time TIMESTAMP NOT NULL ,
    location VARCHAR(255) NOT NULL ,
    capacity INT NOT NULL ,
    status VARCHAR(30) NOT NULL ,
    organizer_id BIGINT NOT NULL ,

    CONSTRAINT fk_events_organizer
        FOREIGN KEY (organizer_id)
        REFERENCES users (id)
        ON DELETE RESTRICT
);
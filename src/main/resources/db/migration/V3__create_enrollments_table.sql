CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY ,
    event_id BIGINT NOT NULL ,
    participant_id BIGINT NOT NULL ,
    qr_code_token VARCHAR(255) NOT NULL UNIQUE ,
    status VARCHAR(30) NOT NULL ,
    date_enrollment TIMESTAMP NOT NULL ,

    CONSTRAINT fk_enrollments_event
        FOREIGN KEY (event_id)
        REFERENCES events (id)
        ON DELETE RESTRICT ,

    CONSTRAINT fk_enrollments_participant
        FOREIGN KEY (participant_id)
        REFERENCES users (id)
        ON DELETE RESTRICT ,

    CONSTRAINT uk_enrollment_event_user
        UNIQUE (event_id, participant_id)
);
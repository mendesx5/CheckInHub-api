CREATE TABLE check_ins (
    id BIGSERIAL PRIMARY KEY ,
    enrollment_id BIGINT NOT NULL UNIQUE ,
    check_in_date_time TIMESTAMP NOT NULL ,
    validated_by_id BIGINT ,

    CONSTRAINT fk_checkin_enrollment
         FOREIGN KEY (enrollment_id)
         REFERENCES enrollments (id)
         ON DELETE CASCADE ,

    CONSTRAINT fk_checkin_validated_by
         FOREIGN KEY (validated_by_id)
         REFERENCES users (id)
         ON DELETE SET NULL
);
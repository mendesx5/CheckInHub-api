ALTER TABLE events
ADD CONSTRAINT chk_events_capacity_positive
CHECK ( capacity > 0 );

ALTER TABLE users
ADD CONSTRAINT chk_users_role
CHECK (role IN ('ORGANIZER', 'PARTICIPANT'));

ALTER TABLE events
ADD CONSTRAINT chk_events_status
CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED'));

ALTER TABLE enrollments
ADD CONSTRAINT chk_enrollments_status
CHECK (status IN ('CONFIRMED', 'CANCELED'));

CREATE INDEX idx_events_organizer_id
ON events (organizer_id);

CREATE INDEX idx_enrollments_event_id
ON enrollments (event_id);

CREATE INDEX idx_enrollments_participant_id
ON enrollments (participant_id);
package com.mendes.check_in_hub.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventIdAndParticipantId(long eventId, long participantId);

    long countByEventIdAndStatus(long eventId, EnrollmentStatus status);

}

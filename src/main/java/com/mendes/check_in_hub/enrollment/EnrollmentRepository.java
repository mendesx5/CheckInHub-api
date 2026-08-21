package com.mendes.check_in_hub.enrollment;

import com.mendes.check_in_hub.checkin.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByEventIdAndParticipantId(long eventId, long participantId);

    long countByEventIdAndStatus(long eventId, EnrollmentStatus status);

    Optional<Enrollment> findByQrCodeToken(String qrCodeToken);

    List<Enrollment> findByParticipantId(Long participantId);

    List<Enrollment> findByEventId(Long eventId);

    List<CheckIn> findByEnrollmentId(Long enrollmentId);

}

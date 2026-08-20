package com.mendes.check_in_hub.checkin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    boolean existsByEnrollmentId(long enrollmentId);

    List<CheckIn> findByEnrollmentEventId(Long eventId);

}

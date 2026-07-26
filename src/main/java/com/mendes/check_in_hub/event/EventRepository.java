package com.mendes.check_in_hub.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizerId(Long OrganizerId);

    List<Event> findByStatus (EventStatus status);

}

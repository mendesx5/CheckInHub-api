package com.mendes.check_in_hub.enrollment;

import com.mendes.check_in_hub.event.Event;
import com.mendes.check_in_hub.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_enrollment_event_user",
                    columnNames = {"event_id", "participant_id"}
            )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;

    @Column(name = "qr_code_token", nullable = false, unique = true)
    private String qrCodeToken;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(name = "date_enrollment")
    private LocalDateTime dateEnrollment;

}

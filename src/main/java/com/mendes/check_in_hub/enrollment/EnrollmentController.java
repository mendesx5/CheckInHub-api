package com.mendes.check_in_hub.enrollment;

import com.mendes.check_in_hub.enrollment.DTO.EnrollmentRequest;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment (@Valid @RequestBody EnrollmentRequest enrollmentRequest) {
        EnrollmentResponse enrollmentResponse = enrollmentService.createEnrollment(enrollmentRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(enrollmentResponse.id())
                .toUri();
        return ResponseEntity.created(location).body(enrollmentResponse);
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> findByEnrollmentId (@PathVariable Long enrollmentId) {
        EnrollmentResponse enrollmentResponse = enrollmentService.findByEnrollmentId(enrollmentId);
        return ResponseEntity.ok(enrollmentResponse);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> findAllEnrollments () {
        List<EnrollmentResponse> enrollmentResponseList = enrollmentService.findAllEnrollments();
        return ResponseEntity.ok(enrollmentResponseList);
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> deleteEnrollment (@PathVariable Long enrollmentId) {
        enrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.ok().build();
    }

}

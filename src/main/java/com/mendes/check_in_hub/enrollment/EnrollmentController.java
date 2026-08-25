package com.mendes.check_in_hub.enrollment;

import com.google.zxing.WriterException;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentRequest;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentResponse;
import com.mendes.check_in_hub.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Tag(name = "Enrollments", description = "Enrollment management endpoints")
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "Create enrollment", description = "Create a new enrollment")
    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            @Valid @RequestBody EnrollmentRequest request,
            Authentication authentication
    ) {
        User participant = (User) authentication.getPrincipal();

        EnrollmentResponse response = enrollmentService.createEnrollment(request, participant);

        return ResponseEntity.ok(response);
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

    @GetMapping("/me")
    public ResponseEntity<List<EnrollmentResponse>> findMyEnrollments (Authentication authentication) {
        User participant = (User) authentication.getPrincipal();

        List<EnrollmentResponse> response = enrollmentService.findMyEnrollments(participant);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EnrollmentResponse>> findEnrollmentsByEvent (
            @PathVariable Long eventId,
            Authentication authentication
    ) {
        User organizer = (User) authentication.getPrincipal();

        List<EnrollmentResponse> response = enrollmentService.findEnrollmentsByEvent(eventId, organizer);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment (
            @PathVariable Long enrollmentId,
            Authentication authentication
    ) {
        User participant = (User) authentication.getPrincipal();

        enrollmentService.cancelEnrollment(enrollmentId, participant);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{enrollmentId}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getEnrollmentQRCode (
            @PathVariable Long enrollmentId,
            Authentication authentication
    ) throws IOException, WriterException {
        User participant = (User) authentication.getPrincipal();

        byte[] qrCode = enrollmentService.generateEnrollmentQrCode(enrollmentId, participant);

        return ResponseEntity.ok(qrCode);
    }

}

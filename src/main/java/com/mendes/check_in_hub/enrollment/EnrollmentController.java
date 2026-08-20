package com.mendes.check_in_hub.enrollment;

import com.mendes.check_in_hub.enrollment.DTO.EnrollmentRequest;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentResponse;
import com.mendes.check_in_hub.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enrollment(
            @Valid @RequestBody EnrollmentRequest request,
            Authentication authentication
    ) {
        User participant = (User) authentication.getPrincipal();

        EnrollmentResponse response = enrollmentService.createEnrollment(request, participant);

        return ResponseEntity.ok(response);
    }

}

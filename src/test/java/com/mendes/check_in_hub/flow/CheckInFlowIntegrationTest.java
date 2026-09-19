package com.mendes.check_in_hub.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mendes.check_in_hub.auth.DTO.LoginRequest;
import com.mendes.check_in_hub.checkin.DTO.CheckInRequest;
import com.mendes.check_in_hub.config.TestcontainersConfig;
import com.mendes.check_in_hub.enrollment.DTO.EnrollmentRequest;
import com.mendes.check_in_hub.event.DTO.EventRequest;
import com.mendes.check_in_hub.user.DTO.UserRequest;
import com.mendes.check_in_hub.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfig.class)
@ActiveProfiles("test")
class CheckInFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullFlow_createEventEnrollAndCheckIn() throws Exception {
        long unique = System.currentTimeMillis();
        String organizerEmail = "organizer" + unique + "@test.com";
        String participantEmail = "participant" + unique + "@test.com";
        String password = "123456";

        // 1. Register organizer and participant
        registerUser("Organizer", organizerEmail, password, UserRole.ORGANIZER);
        registerUser("Participant", participantEmail, password, UserRole.PARTICIPANT);

        // 2. Login both and grab their JWTs
        String organizerToken = login(organizerEmail, password);
        String participantToken = login(participantEmail, password);

        // 3. Organizer creates the event (DRAFT)
        EventRequest eventRequest = new EventRequest(
                "Java Meetup",
                "Encontro de backend Java",
                LocalDateTime.now().plusDays(10),
                "Natal/RN",
                50
        );

        MvcResult createEventResult = mockMvc.perform(post("/events")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long eventId = objectMapper
                .readTree(createEventResult.getResponse().getContentAsString())
                .get("id").asLong();

        // 4. Organizer publishes the event
        mockMvc.perform(put("/events/publish/{eventId}", eventId)
                        .header("Authorization", "Bearer " + organizerToken))
                .andExpect(status().isOk());

        // 5. Participant enrolls and receives a QR code token
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(eventId);

        MvcResult enrollResult = mockMvc.perform(post("/enrollments")
                        .header("Authorization", "Bearer " + participantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enrollmentRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String qrCodeToken = objectMapper
                .readTree(enrollResult.getResponse().getContentAsString())
                .get("qrCodeToken").asText();

        assertNotNull(qrCodeToken);

        // 6. Organizer validates the QR code at the door
        CheckInRequest checkInRequest = new CheckInRequest(qrCodeToken);

        mockMvc.perform(post("/check-in")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest)))
                .andExpect(status().isOk());

        // 7. Scanning the same QR code twice must fail (business rule)
        mockMvc.perform(post("/check-in")
                        .header("Authorization", "Bearer " + organizerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest)))
                .andExpect(status().isBadRequest());
    }

    private void registerUser(String name, String email, String password, UserRole role) throws Exception {
        UserRequest request = new UserRequest(name, email, password, role);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest(email, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }
}

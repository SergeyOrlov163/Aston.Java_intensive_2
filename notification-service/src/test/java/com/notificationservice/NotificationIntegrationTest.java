package com.notificationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notificationservice.controller.NotificationController;
import com.notificationservice.dto.NotificationRequestDto;
import com.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private com.notificationservice.service.NotificationService notificationService;

    @Test
    void sendNotification_viaApi_callsService() throws Exception {
        NotificationRequestDto requestDto = new NotificationRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setOperation("CREATE");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                        .andExpect(status().isOk());

        verify(notificationService).sendNotification("test@example.com", "CREATE");
    }
}

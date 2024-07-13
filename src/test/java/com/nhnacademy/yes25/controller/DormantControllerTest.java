package com.nhnacademy.yes25.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.yes25.application.service.DormantService;
import com.nhnacademy.yes25.presentation.dto.request.CreateAuthNumberRequest;
import com.nhnacademy.yes25.presentation.dto.request.SubmitAuthNumberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.nhnacademy.yes25.presentation.controller.DormantController;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DormantControllerTest {

    @Mock
    private DormantService dormantService;

    @InjectMocks
    private DormantController dormantController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dormantController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createAuthNumber_ShouldReturnNoContent() throws Exception {
        CreateAuthNumberRequest request = new CreateAuthNumberRequest("test@example.com");

        doNothing().when(dormantService).createAuthNumberByEmail(any(CreateAuthNumberRequest.class));

        mockMvc.perform(post("/auth/dormant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(dormantService, times(1)).createAuthNumberByEmail(any(CreateAuthNumberRequest.class));
    }

    @Test
    void submitAuthNumber_ShouldReturnTrue() throws Exception {
        SubmitAuthNumberRequest request = new SubmitAuthNumberRequest("test@example.com", "123456");

        when(dormantService.updateUserStateByAuthNumber(any(SubmitAuthNumberRequest.class))).thenReturn(true);

        mockMvc.perform(post("/auth/dormant/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(dormantService, times(1)).updateUserStateByAuthNumber(any(SubmitAuthNumberRequest.class));
    }

    @Test
    void submitAuthNumber_ShouldReturnFalse() throws Exception {
        SubmitAuthNumberRequest request = new SubmitAuthNumberRequest("test@example.com", "123456");

        when(dormantService.updateUserStateByAuthNumber(any(SubmitAuthNumberRequest.class))).thenReturn(false);

        mockMvc.perform(post("/auth/dormant/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(dormantService, times(1)).updateUserStateByAuthNumber(any(SubmitAuthNumberRequest.class));
    }
}
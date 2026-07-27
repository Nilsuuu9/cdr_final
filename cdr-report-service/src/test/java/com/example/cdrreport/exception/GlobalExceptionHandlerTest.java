package com.example.cdrreport.exception;

import com.example.cdrreport.controller.CdrReportController;
import com.example.cdrreport.mapper.CdrReportMapper;
import com.example.cdrreport.service.CdrQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CdrReportController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CdrQueryService cdrQueryService;

    @MockitoBean
    private CdrReportMapper cdrReportMapper;

    @Test
    void getByCaller_shouldReturnBadRequestForBlankPhoneNumber() throws Exception {
        mockMvc.perform(get("/api/cdrs/by-caller/%20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid request"))
                .andExpect(jsonPath("$.path").value("/api/cdrs/by-caller/ "));
    }

    @Test
    void getAllCdrs_shouldReturnInternalServerErrorForDatabaseFailure() throws Exception {
        given(cdrQueryService.getAll())
                .willThrow(new DataAccessResourceFailureException("Database unavailable"));

        mockMvc.perform(get("/api/cdrs"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Database error"))
                .andExpect(jsonPath("$.message")
                        .value("The request could not be completed because of a database error."));
    }

    @Test
    void getAllCdrs_shouldReturnInternalServerErrorForUnexpectedFailure() throws Exception {
        given(cdrQueryService.getAll())
                .willThrow(new IllegalStateException("Unexpected failure"));

        mockMvc.perform(get("/api/cdrs"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal server error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."));
    }
}

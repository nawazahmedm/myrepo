import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hutch.fsp.exi.transformer.controller.TransformationController;
import com.hutch.fsp.exi.transformer.db.entity.SecurityEvent;
import com.hutch.fsp.exi.transformer.service.ISecurityEventService;
import com.hutch.fsp.exi.transformer.service.impl.AsyncTransformServiceImpl;
import com.hutch.fsp.exi.transformer.service.impl.TransformServiceImpl;
import com.hutch.fsp.exi.transformer.service.impl.TransformerServiceRequestValidatorImpl;
import com.hutch.transformer.TransformerRequest;
import com.hutch.transformer.response.TransformerResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TransformationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TransformServiceImpl transformServiceImpl;

    @Mock
    private AsyncTransformServiceImpl asyncTransformServiceImpl;

    @Mock
    private ISecurityEventService securityEventService;

    @Mock
    private TransformerServiceRequestValidatorImpl validator;

    @Mock
    private Logger logger;

    @InjectMocks
    private TransformationController transformationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transformationController).build();
    }

    @Test
    void testTransformData_Success() throws Exception {
        TransformerRequest request = new TransformerRequest();
        request.setData("Valid Data");

        SecurityEvent securityEvent = new SecurityEvent();
        when(securityEventService.saveSecEvent(any())).thenReturn(securityEvent);
        when(validator.validate(any())).thenReturn(Collections.emptyList());

        TransformerResponse response = new TransformerResponse();
        when(transformServiceImpl.transformAndSaveToDB(any())).thenReturn(response);

        mockMvc.perform(post("/hutch/transform")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(securityEventService, times(1)).saveSecEvent(any());
        verify(transformServiceImpl, times(1)).transformAndSaveToDB(any());
    }

    @Test
    void testTransformData_ValidationError() throws Exception {
        TransformerRequest request = new TransformerRequest();
        request.setData("Invalid Data");

        SecurityEvent securityEvent = new SecurityEvent();
        when(securityEventService.saveSecEvent(any())).thenReturn(securityEvent);
        when(validator.validate(any())).thenReturn(List.of("Missing required field"));

        mockMvc.perform(post("/hutch/transform")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isExpectationFailed());

        verify(securityEventService, times(1)).saveSecEvent(any());
        verify(transformServiceImpl, never()).transformAndSaveToDB(any());
    }

    @Test
    void testTransformData_AsyncProcessing() throws Exception {
        TransformerRequest request = new TransformerRequest();
        request.setData("Async Data");

        SecurityEvent securityEvent = new SecurityEvent();
        when(securityEventService.saveSecEvent(any())).thenReturn(securityEvent);
        when(validator.validate(any())).thenReturn(Collections.emptyList());

        when(request.getTransformerServiceRequest().getRequestMetadata().getRequestPurpose())
                .thenReturn("ShelfwithCollateral");

        mockMvc.perform(post("/hutch/transform")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(asyncTransformServiceImpl, times(1)).transformAndSaveToDB(any());
        verify(transformServiceImpl, never()).transformAndSaveToDB(any());
    }

    @Test
    void testLoggerCoverage() throws Exception {
        TransformerRequest request = new TransformerRequest();
        request.setData("Log Test Data");

        SecurityEvent securityEvent = new SecurityEvent();
        when(securityEventService.saveSecEvent(any())).thenReturn(securityEvent);
        when(validator.validate(any())).thenReturn(Collections.emptyList());

        TransformerResponse response = new TransformerResponse();
        when(transformServiceImpl.transformAndSaveToDB(any())).thenReturn(response);

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        mockMvc.perform(post("/hutch/transform")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(logger, atLeastOnce()).info(logCaptor.capture(), any());
        String logMessage = logCaptor.getValue();
        assert logMessage.contains("Request received to transform:");
    }
}

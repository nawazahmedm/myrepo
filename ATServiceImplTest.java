package com.allah.fsp.transformer.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.allah.fsp.transformer.exception.FSPEXITransformerException;
import com.allah.fsp.transformer.service.DBService;
import com.allah.fsp.transformer.service.JsonToXmlMapper;
import com.allah.fsp.transformer.util.XMLSchemaElementValidator;
import com.allah.fsp.transformer.config.AppConfig;
import com.allah.fsp.transformer.model.TransformerRequest;

@ExtendWith(MockitoExtension.class)
class ATServiceImplTest {

    @InjectMocks
    private AsyncTransformServiceImpl asyncTransformService;

    @Mock
    private JsonToXmlMapper jsonToXmlMapper;

    @Mock
    private DBService dbService;

    @Mock
    private XMLSchemaElementValidator xmlSchemaElementValidator;

    @Mock
    private AppConfig appConfig;

    private TransformerRequest mockRequest;
    private Path drnJsonPath;
    private Path drnXmlPath;
    
    @BeforeEach
    void setUp() throws Exception {
        mockRequest = mock(TransformerRequest.class);
        drnJsonPath = Paths.get("src/test/resources/sample.json");
        drnXmlPath = Paths.get("src/test/resources/sample.xml");

        // Create sample JSON and XML files
        File jsonFile = drnJsonPath.toFile();
        if (!jsonFile.exists()) {
            jsonFile.createNewFile();
        }
        
        File xmlFile = drnXmlPath.toFile();
        if (!xmlFile.exists()) {
            xmlFile.createNewFile();
        }
    }

    @Test
    void testTransformAndSaveToDB_Success() throws Exception {
        // Mocking JSON to XML transformation
        when(jsonToXmlMapper.transformJsonToXml(any(), any(), any(), any()))
                .thenReturn(new ByteArrayInputStream("<xml>sample</xml>".getBytes()));

        // Mock schema validation (no errors)
        when(xmlSchemaElementValidator.validateXMLFileAgainstSchema(anyString(), anyString()))
                .thenReturn(List.of());

        // Mock AppConfig for Queue
        when(appConfig.getQueue()).thenReturn(mock(AppConfig.Queue.class));
        when(appConfig.getQueue().getOrchestratorInQueue()).thenReturn("testQueue");

        // Mock S3 Upload
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "sample.xml", "text/xml", new FileInputStream(drnXmlPath.toFile()));

        // Execute the method
        assertDoesNotThrow(() -> asyncTransformService.transformAndSaveToDB(mockRequest));

        // Verify method interactions
        verify(jsonToXmlMapper, times(1)).transformJsonToXml(any(), any(), any(), any());
        verify(xmlSchemaElementValidator, times(1)).validateXMLFileAgainstSchema(anyString(), anyString());
        verify(dbService, times(1)).updateStatusForTransformation(mockRequest);
    }

    @Test
    void testTransformAndSaveToDB_TransformationFailure() throws Exception {
        when(jsonToXmlMapper.transformJsonToXml(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Transformation failed"));

        Exception exception = assertThrows(FSPEXITransformerException.class, 
                () -> asyncTransformService.transformAndSaveToDB(mockRequest));

        assertTrue(exception.getMessage().contains("Transformation failed"));
    }
}

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.javalearnings.fsp.exd.splitter.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.MDC;
import org.springframework.messaging.MessageHeaders;

import java.util.*;

class EventProcessorServiceImplTest {

    @Mock
    private XmlReaderUtility xmlReaderUtility;

    @Mock
    private IRoutingService routingService;

    @Mock
    private EventRoutingConfiguration eventRoutingConfiguration;

    @InjectMocks
    private EventProcessorServiceImpl eventProcessorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessEvent_ValidEvent() {
        // Arrange
        String message = "<sampleXml>...</sampleXml>";
        MessageHeaders headers = new MessageHeaders(new HashMap<>());

        String messageIdentifier = "12345";
        String eventName = "PublicationEvent";
        String eventType = "PublicationType";
        String documentType = "DocumentType";
        String documentFormatType = "PDF";

        List<String> eventNameList = Collections.singletonList(eventName);

        EventConfig mockEventConfig = new EventConfig();
        mockEventConfig.setEventName(eventName);
        mockEventConfig.setEventType(eventType);

        when(xmlReaderUtility.xpathEvaluator(eq(message), eq(headers), anyString()))
                .thenAnswer(invocation -> {
                    String xpath = invocation.getArgument(2, String.class);
                    if (XpathExpressionConstants.CSP_EVENT_META_DATA_EVENT_NAME_EXPR.equals(xpath)) {
                        return eventNameList;
                    } else if (XpathExpressionConstants.CSP_EVENT_PAYLOAD_EVENT_IDENTIFIER_ORCH_MSG_IDENTIFIER.equals(xpath)) {
                        return Collections.singletonList(messageIdentifier);
                    } else if (XpathExpressionConstants.CSP_EVENT_PAYLOAD_DOC_TYPE_EXPR.equals(xpath)) {
                        return Collections.singletonList(documentType);
                    } else if (XpathExpressionConstants.CSP_EVENT_PAYLOAD_PUBLICATION_TYPE_EXPR.equals(xpath)) {
                        return Collections.singletonList(eventType);
                    } else if (XpathExpressionConstants.CSP_EVENT_PAYLOAD_PUBLICATION_DOC_FORMAT_TYPE_EXPR.equals(xpath)) {
                        return Collections.singletonList(documentFormatType);
                    }
                    return null;
                });

        when(eventRoutingConfiguration.getEventConfigs())
                .thenReturn(Collections.singletonList(mockEventConfig));

        // Act
        eventProcessorService.processEvent(message, headers);

        // Assert
        verify(routingService, times(1)).routeMessage(
                eq(message),
                eq(headers),
                eq(mockEventConfig),
                eq(documentType),
                eq(documentFormatType),
                eq(messageIdentifier)
        );

        assertNull(MDC.get("MessageIdentifier")); // Ensure MDC is cleared after execution
    }

    @Test
    void testProcessEvent_InvalidEvent() {
        // Arrange
        String message = "<sampleXml>...</sampleXml>";
        MessageHeaders headers = new MessageHeaders(new HashMap<>());

        String messageIdentifier = "12345";
        String eventName = "UnknownEvent";
        String eventType = "UnknownType";

        List<String> eventNameList = Collections.singletonList(eventName);

        when(xmlReaderUtility.xpathEvaluator(eq(message), eq(headers), anyString()))
                .thenAnswer(invocation -> {
                    String xpath = invocation.getArgument(2, String.class);
                    if (XpathExpressionConstants.CSP_EVENT_META_DATA_EVENT_NAME_EXPR.equals(xpath)) {
                        return eventNameList;
                    } else if (XpathExpressionConstants.CSP_EVENT_PAYLOAD_EVENT_IDENTIFIER_ORCH_MSG_IDENTIFIER.equals(xpath)) {
                        return Collections.singletonList(messageIdentifier);
                    } else if (XpathExpressionConstants.CSP_EVENT_PAYLOAD_DOC_TYPE_EXPR.equals(xpath)) {
                        return Collections.singletonList(eventType);
                    }
                    return null;
                });

        when(eventRoutingConfiguration.getEventConfigs())
                .thenReturn(Collections.emptyList()); // No matching event config

        // Act
        eventProcessorService.processEvent(message, headers);

        // Assert
        verify(routingService, never()).routeMessage(any(), any(), any(), any(), any(), any());
        assertNull(MDC.get("MessageIdentifier")); // Ensure MDC is cleared after execution
    }
}

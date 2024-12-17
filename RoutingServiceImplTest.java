import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.javalearnings.fsp.exd.splitter.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageHeaders;

import java.util.*;

class RoutingServiceImplTest {

    @Mock
    private IMessagingPortAdapter orchestratorMessageAdapterImpl;

    @Mock
    private IMessagingPortAdapter secManagerMessageAdapterImpl;

    @Mock
    private IOrchestratorRestAdapter orchestratorRestAdapter;

    @Mock
    private EventRoutingConfiguration eventRoutingConfiguration;

    @InjectMocks
    private RoutingServiceImpl routingService;

    private EventConfig eventConfig;
    private MessageHeaders headers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        headers = new MessageHeaders(new HashMap<>());
        eventConfig = new EventConfig();
    }

    @Test
    void testRouteMessage_RoutingType_ProductTypes() {
        // Arrange
        eventConfig.setRoutingType("PRODUCT_TYPES");
        eventConfig.setEnabledTargets(Arrays.asList("Target1"));

        Target target = new Target();
        target.setName("Target1");
        target.setProductTypes(Collections.singletonList("SomeProduct"));

        when(eventRoutingConfiguration.getTarget()).thenReturn(Collections.singletonList(target));

        // Act
        routingService.routeMessage("sampleMessage", headers, eventConfig, "docType", "docFormatType", "msgId");

        // Assert
        // Since PRODUCT_TYPES case is TODO, just verify that no exception is thrown
        verify(eventRoutingConfiguration, times(1)).getTarget();
    }

    @Test
    void testRouteMessage_RoutingType_IsAvailableInFSP() {
        // Arrange
        eventConfig.setRoutingType("IS AVAILABLE IN FSP");
        eventConfig.setEnabledTargets(Arrays.asList("Target1"));

        Target target = new Target();
        target.setName("Target1");
        target.setIsAvailableInFSP(true);

        when(eventRoutingConfiguration.getTarget()).thenReturn(Collections.singletonList(target));

        // Act
        routingService.routeMessage("sampleMessage", headers, eventConfig, "docType", "docFormatType", "msgId");

        // Assert
        verify(orchestratorRestAdapter, times(1))
                .connectOrchestratorAndPublishMessage("sampleMessage", headers, true, "msgId");
    }

    @Test
    void testRouteMessage_RoutingType_DisclosureFiles_Valid() {
        // Arrange
        eventConfig.setRoutingType("DISCLOSURE FILES");
        eventConfig.setEnabledTargets(Arrays.asList("Target1"));

        Target target = new Target();
        target.setName("Target1");
        target.setDisclosureFiles(Collections.singletonList("docType"));

        when(eventRoutingConfiguration.getTarget()).thenReturn(Collections.singletonList(target));

        // Act
        routingService.routeMessage("sampleMessage", headers, eventConfig, "docType", "zip", "msgId");

        // Assert
        verify(orchestratorMessageAdapterImpl, times(1))
                .sendMessage("sampleMessage", headers, true);
    }

    @Test
    void testRouteMessage_DisclosureFiles_NoMatch() {
        // Arrange
        eventConfig.setRoutingType("DISCLOSURE FILES");
        eventConfig.setEnabledTargets(Arrays.asList("Target1"));

        Target target = new Target();
        target.setName("Target1");
        target.setDisclosureFiles(Collections.singletonList("differentDocType"));

        when(eventRoutingConfiguration.getTarget()).thenReturn(Collections.singletonList(target));

        // Act
        routingService.routeMessage("sampleMessage", headers, eventConfig, "docType", "zip", "msgId");

        // Assert
        verify(orchestratorMessageAdapterImpl, never()).sendMessage(any(), any(), anyBoolean());
    }

    @Test
    void testRouteMessage_DefaultRoutingType() {
        // Arrange
        eventConfig.setRoutingType("UNKNOWN_TYPE");
        eventConfig.setEnabledTargets(Arrays.asList("Target1"));

        Target target = new Target();
        target.setName("Target1");

        when(eventRoutingConfiguration.getTarget()).thenReturn(Collections.singletonList(target));

        // Act
        routingService.routeMessage("sampleMessage", headers, eventConfig, "docType", "docFormatType", "msgId");

        // Assert
        // Since default case is TODO, just verify no other method is called
        verify(orchestratorRestAdapter, never()).connectOrchestratorAndPublishMessage(any(), any(), anyBoolean(), any());
        verify(orchestratorMessageAdapterImpl, never()).sendMessage(any(), any(), anyBoolean());
    }
}

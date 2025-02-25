import static org.mockito.Mockito.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MessageConsumerTest {

    @InjectMocks
    private MessageConsumer messageConsumer;

    @Mock
    private MessageService messageService; // Mock service that inserts into DB

    @Mock
    private JmsTemplate jmsTemplate; // Mock JMS Template for sending messages

    private Message testMessage;

    @BeforeEach
    void setUp() throws JMSException {
        MockitoAnnotations.openMocks(this);

        // Create a mock JMS message
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText("Test message");
        message.setJMSMessageID("12345");
        message.setJMSRedelivered(true); // Simulating a redelivery scenario
        testMessage = message;
    }

    @Test
    void testMessageRedeliveryOnDatabaseFailure() throws Exception {
        // Simulate DB failure on the first two attempts
        doThrow(new RuntimeException("Database error"))
                .doThrow(new RuntimeException("Database error"))
                .doNothing() // Succeed on the third attempt
                .when(messageService).saveMessage(anyString());

        // Call the consumer method
        messageConsumer.receiveMessage(testMessage);

        // Verify that the service was called three times (retries happened)
        verify(messageService, times(3)).saveMessage(anyString());

        // Ensure the message was NOT sent to DLQ
        verify(jmsTemplate, never()).convertAndSend(eq("DLQ.myQueue"), anyString());
    }

    @Test
    void testMessageGoesToDLQAfterMaxRetries() throws Exception {
        // Simulate DB failure in all retry attempts
        doThrow(new RuntimeException("Database error")).when(messageService).saveMessage(anyString());

        // Call the consumer method
        messageConsumer.receiveMessage(testMessage);

        // Verify the message was retried three times before sending to DLQ
        verify(messageService, times(3)).saveMessage(anyString());

        // Ensure the message is sent to DLQ after max retries
        verify(jmsTemplate, times(1)).convertAndSend(eq("DLQ.myQueue"), anyString());
    }
}

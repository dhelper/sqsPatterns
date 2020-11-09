package common.sqs.helpers;

import com.amazon.sqs.javamessaging.acknowledge.Acknowledger;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.sqs.contracts.AttributesNames;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import common.sqs.helpers.testClasses.MessageOfTypeA;
import common.sqs.helpers.testClasses.MessageOfTypeB;
import common.sqs.helpers.testClasses.SqsMessageListenerForTests;

import javax.jms.JMSException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SqsMessageListenerBaseTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private Acknowledger fakeAcknowledger;

    @InjectMocks
    private SqsMessageListenerForTests target;

    @Test
    void onMessage_signedUpForMessagesOfSpecificType_ignoreOtherMessagesCallAch() throws JMSException, JsonProcessingException {
        AtomicBoolean wasCalled = new AtomicBoolean(false);

        var messageB = new MessageOfTypeB("some string");
        var messageString = objectMapper.writeValueAsString(messageB);

        Message message = new Message()
                .withBody(messageString)
                .withMessageAttributes(Map.of(
                        AttributesNames.MessageClass, new MessageAttributeValue().withDataType("String").withStringValue(MessageOfTypeB.class.getName())))
                .withAttributes(Map.of("ApproximateReceiveCount", "1"))
                .withMessageId("msg-1");

        target.registerMessage(MessageOfTypeA.class, messageOfTypeA -> wasCalled.set(true));
        target.onMessage(new SQSTextMessage(fakeAcknowledger, "", message));

        assertAll(
                () -> assertFalse(wasCalled.get()),
                () -> verify(fakeAcknowledger).acknowledge(any())
        );
    }

    @Test
    void onMessage_signedUpForMessagesOfSpecificType_CallMessageHandler() throws JMSException, JsonProcessingException {
        AtomicReference<MessageOfTypeA> actual = new AtomicReference<>();

        var messageA = new MessageOfTypeA(1, 2);
        var messageString = objectMapper.writeValueAsString(messageA);

        Message message = new Message()
                .withBody(messageString)
                .withMessageAttributes(Map.of(
                        AttributesNames.MessageClass, new MessageAttributeValue().withDataType("String").withStringValue(MessageOfTypeA.class.getName())))
                .withAttributes(Map.of("ApproximateReceiveCount", "1"))
                .withMessageId("msg-1");

        target.registerMessage(MessageOfTypeA.class, actual::set);
        target.onMessage(new SQSTextMessage(fakeAcknowledger, "", message));

        assertAll(
                () -> assertEquals(messageA, actual.get()),
                () -> verify(fakeAcknowledger).acknowledge(any())
        );
    }

    @Test
    void onMessage_gotEmptyMessage_returnWithoutAch() throws JMSException {
        Message message = new Message()
                .withAttributes(Map.of("ApproximateReceiveCount", "1"))
                .withMessageId("msg-1");

        target.onMessage(new SQSTextMessage(fakeAcknowledger, "", message));

        verify(fakeAcknowledger, never()).acknowledge(any());
    }

    @Test
    void onMessage_gotMessageWithoutMessageClass_returnAfterAch() throws JMSException {
        Message message = new Message()
                .withBody("{\"text\":\"blah blah\"}")

                .withAttributes(Map.of("ApproximateReceiveCount", "1"))
                .withMessageId("msg-1");

        target.onMessage(new SQSTextMessage(fakeAcknowledger, "", message));

        verify(fakeAcknowledger, never()).acknowledge(any());
    }

    @Test
    void onMessage_gotTextMessageWithMessageType_returnAfterAch() throws JMSException {
        Message message = new Message()
                .withBody("{\"text\":\"blah blah\"}")
                .withMessageAttributes(Map.of(
                        AttributesNames.MessageClass, new MessageAttributeValue().withDataType("String").withStringValue(MessageOfTypeA.class.getName())))
                .withAttributes(Map.of(
                        "ApproximateReceiveCount", "1"))
                .withMessageId("msg-1");

        target.onMessage(new SQSTextMessage(fakeAcknowledger, "", message));

        verify(fakeAcknowledger).acknowledge(any());
    }

}

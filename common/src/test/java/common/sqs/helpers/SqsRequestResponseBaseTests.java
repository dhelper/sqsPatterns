package common.sqs.helpers;

import com.amazon.sqs.javamessaging.acknowledge.Acknowledger;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.sqs.contracts.AttributesNames;
import common.sqs.helpers.testClasses.MessageOfTypeA;
import common.sqs.helpers.testClasses.MessageOfTypeB;
import common.sqs.helpers.testClasses.SqsRequestResponseForTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jms.JMSException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SqsRequestResponseBaseTests {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AmazonSQS fakeAmazonSQS;

    @Mock
    private Acknowledger fakeAcknowledger;

    @InjectMocks
    private SqsRequestResponseForTests target;

    @Captor
    private ArgumentCaptor<SendMessageRequest> sendMessageRequestCaptor;

    @Test
    void handleMessage_receiveRegisteredMessage_sendResponseMessage() throws JsonProcessingException, JMSException {
        var messageA = new MessageOfTypeA(1, 2);
        var messageString = objectMapper.writeValueAsString(messageA);

        Message message = new Message()
                .withBody(messageString)
                .withMessageAttributes(Map.of(
                        AttributesNames.MessageClass, new MessageAttributeValue().withDataType("String").withStringValue(MessageOfTypeA.class.getName())))
                .withAttributes(Map.of("ApproximateReceiveCount", "1"))
                .withMessageId("msg-1");

        target.registerMessage(MessageOfTypeA.class,
                msg -> new MessageOfTypeB(msg.getIntValue1() + " " + msg.getIntValue2()));

        target.onMessage(new SQSTextMessage(fakeAcknowledger, "", message));

        verify(fakeAmazonSQS).sendMessage(sendMessageRequestCaptor.capture());
        final SendMessageRequest request = sendMessageRequestCaptor.getValue();

        assertEquals(MessageOfTypeB.class.getName(),
                request.getMessageAttributes().get(AttributesNames.MessageClass).getStringValue());

        var actual = objectMapper.readValue(request.getMessageBody(), MessageOfTypeB.class);
        var expected = new MessageOfTypeB("1 2");

        assertEquals(expected, actual);
    }
}

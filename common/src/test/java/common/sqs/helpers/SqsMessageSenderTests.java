package common.sqs.helpers;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.sqs.contracts.AttributesNames;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import common.sqs.helpers.testClasses.MessageOfTypeB;
import common.sqs.helpers.testClasses.SqsMessageSenderForTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
public class SqsMessageSenderTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AmazonSQS fakeAmazonSQS;

    @InjectMocks
    private SqsMessageSenderForTests target;

    @Captor
    private ArgumentCaptor<SendMessageRequest> sendMessageRequestCaptor;

    @Test
    void sendMessage_addMessageTypeInMessageProperty() throws JsonProcessingException {
        target.sendMessage(new MessageOfTypeB("message"));

        verify(fakeAmazonSQS).sendMessage(sendMessageRequestCaptor.capture());
        final SendMessageRequest request = sendMessageRequestCaptor.getValue();

        assertEquals(MessageOfTypeB.class.getName(), request.getMessageAttributes().get(AttributesNames.MessageClass).getStringValue());
    }
}

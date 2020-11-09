package senderServiceAcceptance;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import messages.MyOtherMessage;
import messages.MySimpleMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

public class QueueSteps {
    private final AmazonSQS amazonSQS;

    public QueueSteps(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    @Then("a new message should be queued with title {string} and content {string}")
    public void aNewMessageShouldBeQueuedWithTitleAndContent(String title, String content) {
        var expected = new MySimpleMessage(title, content);

        var actual = receiveMessage(ScenarioContext.queueUrl, 1, MySimpleMessage.class);

        assertEquals(List.of(expected), actual);
    }


    @Then("the following counter messages should be queued:")
    public void theFollowingCounterMessagesShouldBeQueued(List<MyOtherMessage> expected) {
        var actual = receiveMessage(ScenarioContext.queueUrl, expected.size(), MyOtherMessage.class);

        var sorted = actual.stream().sorted(Comparator.comparingInt(MyOtherMessage::getNumber)).collect(toList());

        assertEquals(expected, sorted);
    }

    private <T> List<T> receiveMessage(String queueUrl, int count, Class<T> msgType) {
        int maxIterations = 1000;
        List<String> result = new ArrayList<>();
        int iteration = 0;
        do {
            ReceiveMessageResult receiveMessageResult = amazonSQS.receiveMessage(queueUrl);

            var messagesText = receiveMessageResult.getMessages()
                    .stream()
                    .map(Message::getBody).collect(toList());

            result.addAll(messagesText);

            iteration++;
        } while (result.size() < count && iteration <= maxIterations);

        assertNotEquals(0, result.size());


        ObjectMapper objectMapper = new ObjectMapper();


        return result.stream().map(t -> {
            try {
                return objectMapper.readValue(t, msgType);
            } catch (JsonProcessingException e) {
                fail("Failed to receive message of type: " + msgType.getSimpleName());
                return null;
            }
        }).collect(toList());
    }
}

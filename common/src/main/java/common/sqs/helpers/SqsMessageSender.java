package common.sqs.helpers;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.sqs.contracts.AttributesNames;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqsMessageSender {
    private final AmazonSQS sqsClient;
    private final String queueName;

    public SqsMessageSender(AmazonSQS sqsClient, String queueName) {
        this.sqsClient = sqsClient;
        this.queueName = queueName;
    }

    protected <T> void sendMessage(T message) throws JsonProcessingException {

        var queueUrl = sqsClient.getQueueUrl(queueName);

        var sendMessageRequest = createSendMessageRequest(message, queueUrl);

        var sendMessageResult = sqsClient.sendMessage(sendMessageRequest);

        log.info("sent message {}\nresult:{}", message, sendMessageResult);
    }

    private <T> SendMessageRequest createSendMessageRequest(T message, com.amazonaws.services.sqs.model.GetQueueUrlResult queueUrl) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(message);
        var sendMessageRequest = new SendMessageRequest(queueUrl.getQueueUrl(), requestJson);

        sendMessageRequest.addMessageAttributesEntry(AttributesNames.MessageClass, createStringAttributeValue(message.getClass().getName()));

        return sendMessageRequest;
    }

    private MessageAttributeValue createStringAttributeValue(String value) {
        return new MessageAttributeValue().withDataType("String").withStringValue(value);
    }
}

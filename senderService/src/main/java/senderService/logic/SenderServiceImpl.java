package senderService.logic;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.sqs.helpers.SqsMessageSender;
import lombok.extern.slf4j.Slf4j;
import messages.MyOtherMessage;
import messages.MySimpleMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SenderServiceImpl extends SqsMessageSender implements SenderService {
    public SenderServiceImpl(AmazonSQS sqsClient,
                             @Value("${sqs.sender.queue.name}")String queueName) {
        super(sqsClient, queueName);
    }

    @Override
    public void send(String title, String content) {
        var message = new MySimpleMessage(title, content);

        try {
            sendMessage(message);
        } catch (JsonProcessingException e) {
            log.warn("Error while sending message:", e);
        }
    }

    @Override
    public void send(String title, int content) {
        var message = new MyOtherMessage(title, content);

        try {
            sendMessage(message);
        } catch (JsonProcessingException e) {
            log.warn("Error while sending message:", e);
        }
    }
}

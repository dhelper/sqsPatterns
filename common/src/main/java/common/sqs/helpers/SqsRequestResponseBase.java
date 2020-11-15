package common.sqs.helpers;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.sqs.contracts.AttributesNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public abstract class SqsRequestResponseBase extends SqsMessageSender implements MessageListener {
    private final Map<String, Function<Object, Object>> typeToAction = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SqsRequestResponseBase(AmazonSQS sqsClient,
                                  @Value("${sqs.response.queue.name}") String responseQueueName) {
        super(sqsClient, responseQueueName);
    }

    protected <T, R> void registerMessage(Class<? super T> messageType, Function<? super T, ? super R> messageHandler) {
        typeToAction.put(messageType.getName(), o -> messageHandler.apply((T) o));
    }

    @Override
    public void onMessage(Message message) {
        try {
            var textMessage = (TextMessage) message;

            if (StringUtils.isNullOrEmpty(textMessage.getText())) {
                log.warn("Received empty message {}", message.getJMSMessageID());
                return;
            }

            final String messageClass = message.getStringProperty(AttributesNames.MessageClass);
            if (StringUtils.isNullOrEmpty(messageClass)) {
                log.warn("Received message without message class {}:\n{},", message.getJMSMessageID(), textMessage.getText());
                return;
            }

            log.info("found message of type:{}", messageClass);

            handleMessage(textMessage.getText(), messageClass);

        } catch (Exception exc) {
            log.error("Error while processing message:", exc);
        }
    }

    private void handleMessage(String text, String messageClass) throws ClassNotFoundException, JsonProcessingException {
        if (typeToAction.containsKey(messageClass)) {

            final Class<?> aClass = Class.forName(messageClass);

            var message = objectMapper.readValue(text, aClass);

            final Function<Object, Object> consumer = typeToAction.get(messageClass);

            var response = consumer.apply(message);

            sendMessage(response);
        }
    }
}

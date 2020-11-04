package common.sqs.helpers;

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
import java.util.function.Consumer;

@Slf4j
public class SqsMessageListenerBase implements MessageListener {
    private final Map<String, Consumer<Object>> typeToAction = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${sqs.listener.queue.name}")
    String listenerQueueName;

    @SuppressWarnings("unchecked")
    protected <T> void registerMessage(Class<? super T> messageType, Consumer<? super T> messageHandler) {
        typeToAction.put(messageType.getName(), o -> messageHandler.accept((T) o));
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

            message.acknowledge();
        } catch (Exception exc) {
            log.error("Error while processing message:", exc);
        }
    }

    private void handleMessage(String text, String messageClass) throws ClassNotFoundException, JsonProcessingException {
        if (typeToAction.containsKey(messageClass)) {

            final Class<?> aClass = Class.forName(messageClass);

            var message = objectMapper.readValue(text, aClass);

            final Consumer<Object> consumer = typeToAction.get(messageClass);

            consumer.accept(message);
        }
    }
}

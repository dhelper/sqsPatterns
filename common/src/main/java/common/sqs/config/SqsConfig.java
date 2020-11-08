package common.sqs.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import common.sqs.helpers.SqsMessageListenerBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@EnableJms
@Import({
        SqsConnectionFactoryConfig.class,
        SqsClientConfig.class
})
public class SqsConfig {
    @Value("${sqs.listener.queue.name}")
    private String listenerQueueName;

    @Value("${sqs.max.concurrent.consumers:10}")
    private int maxConcurrentConsumers;

    private final SqsMessageListenerBase messageListener;
    private final SQSConnectionFactory connectionFactory;

    public SqsConfig(SqsMessageListenerBase messageListener, SQSConnectionFactory sqsConnectionFactory) {
        this.messageListener = messageListener;
        this.connectionFactory = sqsConnectionFactory;
    }

    @Bean
    public DefaultMessageListenerContainer jmsListenerContainer() {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setMaxConcurrentConsumers(maxConcurrentConsumers);
        dmlc.setSessionAcknowledgeMode(SQSSession.UNORDERED_ACKNOWLEDGE);
        dmlc.setConnectionFactory(connectionFactory);

        dmlc.setDestinationName(listenerQueueName);
        dmlc.setMessageListener(messageListener);

        return dmlc;
    }
}

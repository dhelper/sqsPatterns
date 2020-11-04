package common.sqs.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

public class JmsTemplateConfig {
    private final SQSConnectionFactory connectionFactory;

    public JmsTemplateConfig(SQSConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Bean(name = "defaultJmsTemplate")
    public JmsTemplate defaultJmsTemplate() {

        return new JmsTemplate(this.connectionFactory);
    }
}

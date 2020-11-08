package common.sqs.config;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class SqsConnectionFactoryConfig {
    @Value("${sqs.num.messages.prefetch:10}")
    private int numberOfMessagesToPrefetch;

    private final AmazonSQS sqsClient;

    public SqsConnectionFactoryConfig(AmazonSQS sqsClient) {
        this.sqsClient = sqsClient;
    }

    @Bean
    private SQSConnectionFactory getConnectionFactory() {
        return new SQSConnectionFactory(new ProviderConfiguration().withNumberOfMessagesToPrefetch(numberOfMessagesToPrefetch), sqsClient);
    }
}

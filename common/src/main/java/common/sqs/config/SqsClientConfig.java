package common.sqs.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class SqsClientConfig {
    @Value("${sqs.endpoint:http://localhost:9336}")
    private String sqsEndpoint;
    @Value("${sqs.signingRegion:us-west-2}")
    private String signingRegion;


    @Bean(name = "standardSqsClient")
    public AmazonSQS standardSqsClient() {

        return AmazonSQSClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsEndpoint, signingRegion))
                .build();
    }
}

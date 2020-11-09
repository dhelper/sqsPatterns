package senderServiceAcceptance;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.extern.slf4j.Slf4j;
import org.elasticmq.rest.sqs.SQSRestServer;
import org.elasticmq.rest.sqs.SQSRestServerBuilder;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class LocalSqsHooks {
    private final ScenarioContext context;
    private final AmazonSQS sqsClient;
    private final String queueName;
    private final int sqsMockPort;

    private static boolean localSqs = false;
    private SQSRestServer localSqsServer;

    public LocalSqsHooks(ScenarioContext context,
                         AmazonSQS sqsClient,
                         @Value("${sqs.sender.queue.name}") String queueName,
                         @Value("${sqs.mock.port}") int sqsMockPort
    ) {
        this.context = context;
        this.sqsClient = sqsClient;
        this.queueName = queueName;
        this.sqsMockPort = sqsMockPort;
    }

    @Before()
    public void startLocalSqs() {
        if (!localSqs) {
            localSqs = true;
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                log.info("Closing local SQS server");
                localSqsServer.stopAndWait();
            }));

            log.info("Starting local SQS server and creating the following queues :");
            localSqsServer = SQSRestServerBuilder.withPort(sqsMockPort).withInterface("localhost").start();
            localSqsServer.waitUntilStarted();

            CreateQueueResult result = sqsClient.createQueue(queueName);
            ScenarioContext.queueUrl = result.getQueueUrl();
            log.info("{}:{}", queueName, ScenarioContext.queueUrl);

        }
    }

    @After
    public void cleanLocalQueues(){
        log.info("Purging the following local queues :");

        log.info("{}", ScenarioContext.queueUrl);
        sqsClient.purgeQueue(new PurgeQueueRequest(ScenarioContext.queueUrl));
    }
}

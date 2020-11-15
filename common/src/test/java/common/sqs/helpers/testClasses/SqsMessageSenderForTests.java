package common.sqs.helpers.testClasses;

import com.amazonaws.services.sqs.AmazonSQS;
import common.sqs.helpers.SqsMessageSender;

public class SqsMessageSenderForTests extends SqsMessageSender {
    public SqsMessageSenderForTests(AmazonSQS sqsClient) {
        super(sqsClient, "queueForTests");
    }
}

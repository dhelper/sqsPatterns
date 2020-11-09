package common.sqs.helpers.testClasses;

import com.amazonaws.services.sqs.AmazonSQS;
import common.sqs.helpers.SqsMessageSenderBase;

public class SqsMessageSenderForTests extends SqsMessageSenderBase {
    public SqsMessageSenderForTests(AmazonSQS sqsClient) {
        super(sqsClient, "queueForTests");
    }
}

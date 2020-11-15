package common.sqs.helpers.testClasses;

import com.amazonaws.services.sqs.AmazonSQS;
import common.sqs.helpers.SqsRequestResponseBase;

public class SqsRequestResponseForTests extends SqsRequestResponseBase {
    public SqsRequestResponseForTests(AmazonSQS sqsClient) {
        super(sqsClient, "queueForTests");
    }
}

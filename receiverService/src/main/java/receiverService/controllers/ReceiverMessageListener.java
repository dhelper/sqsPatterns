package receiverService.controllers;

import common.sqs.helpers.SqsMessageListenerBase;
import lombok.extern.slf4j.Slf4j;
import messages.MyOtherMessage;
import messages.MySimpleMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReceiverMessageListener extends SqsMessageListenerBase {
    public ReceiverMessageListener() {
        registerMessage(MySimpleMessage.class, this::handleMessage);
        registerMessage(MyOtherMessage.class, this::handleOtherMessage);
    }

    private void handleMessage(MySimpleMessage mySimpleMessage){
        log.info("Message arrived:{}", mySimpleMessage);
    }

    private void handleOtherMessage(MyOtherMessage myOtherMessage) {
        log.info("Message arrived:{}", myOtherMessage);
    }
}

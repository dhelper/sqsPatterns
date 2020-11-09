package senderServiceAcceptance;

import io.cucumber.java.en.When;
import messages.MySimpleMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class ServerSteps {
    @Value("${server.servlet.contextPath}")
    private String baseUrl;

    @Value("${server.port}")
    private int serverPort;

    @When("server endpoint is called with a simple message:")
    public void serverEndpointIsCalledWithASimpleMessage(List<MySimpleMessage> messages) {
        for(var message : messages){
            sendMessageToServer(message);
        }
    }



    @When("server count endpoint is called:")
    public void serverCountEndpointIsCalled(List<String> messageTitles) {
        for(var title : messageTitles){
            sendCounterMessageToServer(title);
        }
    }

    private void sendMessageToServer(MySimpleMessage message) {
        var path = UriComponentsBuilder.fromHttpUrl("http://localhost")
                .port(serverPort)
                .path(baseUrl)
                .path("/send")
                .buildAndExpand()
                .toUriString();

        var restTemplate = new RestTemplate();

        restTemplate.postForEntity(path, message, Void.class);
    }

    private void sendCounterMessageToServer(String title) {
        var path = UriComponentsBuilder.fromHttpUrl("http://localhost")
                .port(serverPort)
                .path(baseUrl)
                .path("/count")
                .buildAndExpand()
                .toUriString();

        var restTemplate = new RestTemplate();

        restTemplate.postForEntity(path, title, Void.class);
    }
}

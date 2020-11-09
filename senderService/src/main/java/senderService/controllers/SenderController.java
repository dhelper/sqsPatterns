package senderService.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import senderService.controllers.entities.MessageData;
import senderService.logic.SenderService;

@RestController
@RequestMapping()
public class SenderController {
    private final SenderService senderService;
    private static int counter = 0;

    public SenderController(SenderService senderService) {
        this.senderService = senderService;
    }


    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody MessageData message) {
        senderService.send(message.getTitle(), message.getContent());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("count")
    public ResponseEntity<Void> sendCount(@RequestBody String message) {
        senderService.send(message, ++counter);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

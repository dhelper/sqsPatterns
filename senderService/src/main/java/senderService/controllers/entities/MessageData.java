package senderService.controllers.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MessageData {
    private String title;
    private String content;
}

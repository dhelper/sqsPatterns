package senderService.logic;

public interface SenderService {
    void send(String title, String content);
    void send(String title, int content);
}

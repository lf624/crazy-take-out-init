package com.crazy.task;

import com.crazy.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class WebsocketTask {
    @Autowired
    WebSocketServer webSocketServer;

    //@Scheduled(cron = "0/5 * * * * *")
    public void sendMessageToClient() {
        webSocketServer.sendToAllClient("来自服务端的消息：" +
                DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
    }
}

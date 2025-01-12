package com.crazy.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    private static Map<String, Session> sessionMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        System.out.println("客户端：" + sid + "建立连接");
        sessionMap.put(sid, session);
    }

    @OnMessage
    public void onMessage(String msg, @PathParam("sid") String sid) {
        System.out.println("收到来自客户端：" + sid + "的消息：" + msg);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("连接断开：" + sid);
        sessionMap.remove(sid);
    }

    public void sendToAllClient(String msg) {
        Collection<Session> sessions = sessionMap.values();
        sessions.forEach(session -> {
            try {
                // 服务器向客户端发送消息
                session.getBasicRemote().sendText(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

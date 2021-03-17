package io.vk.chat.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Component
@ServerEndpoint("/chat/{user}")
public class ChatWebSocket {

    private static final Logger logger = Logger.getLogger(ChatWebSocket.class.getName());

    public static AtomicInteger onLines = new AtomicInteger(0);
    public static List<ChatWebSocket> webSockets = new CopyOnWriteArrayList<>();

    private Session session;
    private String user;

    private void pushOnlineData() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "sys_dao");
        Map<String, Object> r = new HashMap<>();
        List<String> list = new ArrayList<>();
        for (ChatWebSocket websocket : webSockets) list.add(websocket.user);
        r.put("lines", list);
        map.put("data", r);
        pushMessage(map, null);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("user") String user) {
        onLines.incrementAndGet();
        this.session = session;
        this.user = user;
        webSockets.add(this);
        logger.info(user + "加入群聊");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "sys_tip");
        Map<String, Object> r = new HashMap<>();
        r.put("user", this.user);
        r.put("dao", "in");
//        map.put("data", this.user + "加入群聊");
        map.put("data", r);
        pushMessage(map, null);
        pushOnlineData();
    }

    @OnClose
    public void onClose() {
        onLines.decrementAndGet();
        webSockets.remove(this);
        logger.info(this.user + "退出群聊");
        Map<String, Object> map = new HashMap<>();
        map.put("type", "sys_tip");
        Map<String, Object> r = new HashMap<>();
        r.put("user", this.user);
        r.put("dao", "out");
//        map.put("data", this.user + "退出群聊");
        map.put("data", r);
        pushMessage(map, null);
        pushOnlineData();
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("user") String user) {
    }

    public static void pushMessage(Map<String, Object> map, String user) {
        ObjectMapper om = new ObjectMapper();
        if (0 >= webSockets.size()) {
            logger.info("当前暂无连接,消息未发送");
            return;
        }
        if (null == user) {
            for (ChatWebSocket webSocket : webSockets) {
                try {
                    webSocket.sendMessage(om.writeValueAsString(map));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            logger.info("针对所有人的消息 => " + map.toString() + " 的消息已经发送完毕");
            return;
        } else {
            for (ChatWebSocket webSocket : webSockets) {
                if (webSocket.user.equals(user)) {
                    try {
                        webSocket.sendMessage(om.writeValueAsString(map));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    logger.info("针对 " + user + " 的消息 => " + map.toString() + " 的消息已经发送完毕");
                    return;
                }
            }
        }
    }

    private void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

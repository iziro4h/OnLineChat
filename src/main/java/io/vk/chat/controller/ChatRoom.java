package io.vk.chat.controller;

import io.vk.chat.common.ChatWebSocket;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author 作者不是我
 * @date 2021/03/15 13:51
 * @desc
 */
@Controller
@RequestMapping("/chat")
public class ChatRoom {

    private final Logger logger = Logger.getLogger(this.getClass().getName());


    @GetMapping("/index")
    public String gotoIndex() {
        return "Index";
    }

    @PostMapping("/room")
    public String gotoRoom(@RequestParam("userShowName") String userShowName, Map<String, Object> tl) {
        logger.info(userShowName);
        tl.put("userShowName", userShowName);
        return "ChatRoom";
    }

    @ResponseBody
    @PostMapping("/pushMessage")
    public void sendMessage(@RequestBody Map<String, Object> par) {
        logger.info(par.toString());
        String user = par.get("user").toString().trim();
        String msg = par.get("msg").toString().trim();
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("msg", msg);
        Map<String, Object> r = new HashMap<>();
        r.put("type", "msg");
        r.put("data", map);
        ChatWebSocket.pushMessage(r, null);
    }
}

package io.vk.chat;

import io.vk.chat.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatApplicationTests {

    @Autowired
    private Users users;

    @Test
    void contextLoads() {
        System.out.println(users);
    }

}

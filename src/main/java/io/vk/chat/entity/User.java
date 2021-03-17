package io.vk.chat.entity;

import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author 作者不是我
 * @date 2021/03/16 19:36
 * @desc
 */
@Component
public class User implements Serializable {
    private String user;
    private String password;
    private String role;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

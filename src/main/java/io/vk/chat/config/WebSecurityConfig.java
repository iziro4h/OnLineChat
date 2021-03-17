package io.vk.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vk.chat.entity.User;
import io.vk.chat.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 作者不是我
 * @date 2021/03/15 13:46
 * @desc
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Users users;

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 指定两个内存用户并分配不同的权限
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        super.configure(auth);
        for (User user : users.getUsers()) auth.inMemoryAuthentication().withUser(user.getUser()).password(user.getPassword()).roles(user.getRole());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        //细化的管理不一样的 role 可以访问到的资源
        http.authorizeRequests()
                .antMatchers("/chat/**").hasRole("ADMIN")
                .anyRequest().authenticated().and().formLogin().loginProcessingUrl("/login")
                //登录成功的操作
                .successHandler((request, response, authentication) -> {
                    //登录成功后跳转到指定的HTML页面
                    response.sendRedirect("/chat/index");
                    //后续代码不需要了
                    //Object principal = authentication.getPrincipal();
                    //PrintWriter printWriter = response.getWriter();
                    //response.setStatus(200);
                    //Map<String, Object> map = new HashMap<>();
                    //map.put("status", 200);
                    //map.put("msg", principal);
                    //ObjectMapper om = new ObjectMapper();
                    //printWriter.write(om.writeValueAsString(map));
                    //printWriter.flush();
                    //printWriter.close();
                })
                .failureHandler((request, response, e) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter printWriter = response.getWriter();
                    response.setStatus(401);
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", 401);
                    if (e instanceof LockedException) map.put("msg", "账户已经被锁定,登录失败！");
                    else if (e instanceof BadCredentialsException) map.put("msg", "账号名或密码输入错误，登录失败！");
                    else if (e instanceof DisabledException) map.put("msg", "账号被禁用，登录失败！");
                    else if (e instanceof AccountExpiredException) map.put("msg", "账号已过期，登录失败！");
                    else if (e instanceof CredentialsExpiredException) map.put("msg", "密码已过期，登录失败！");
                    else map.put("msg", "登录失败！");
                    ObjectMapper objectMapper = new ObjectMapper();
                    printWriter.write(objectMapper.writeValueAsString(map));
                    printWriter.flush();
                    printWriter.close();
                })
                .and().logout().logoutUrl("/logout").clearAuthentication(true).invalidateHttpSession(true).addLogoutHandler((request, response, auth) -> {
        }).logoutSuccessHandler((request, response, auth) -> {
            response.sendRedirect("/login_page");
        }).permitAll().and().csrf().disable();

    }

}

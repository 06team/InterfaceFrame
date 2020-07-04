package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class UserController {
    @RequestMapping(value ="/index/login")
    public String login() {
        System.out.println("zjag");
/**
 * login代表逻辑视图名称，需要根据Spring MVC配置
 * 文件中internalResourceViewResolver的前缀和后缀找到对应的物理视图
 */
        return "login";
    }
    @RequestMapping(value ="/index/register")
    public String register() {
        return "register";
    }
}

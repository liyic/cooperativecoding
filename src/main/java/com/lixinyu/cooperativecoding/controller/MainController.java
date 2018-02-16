package com.lixinyu.cooperativecoding.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://127.0.0.1")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Controller
public class MainController{
    @GetMapping("/")
    public String session(HttpServletRequest request, Model model, Authentication authentication) {
        String username = authentication.getName();
        request.getSession().setAttribute("username", username);
        model.addAttribute("username", request.getSession().getAttribute("username"));
        return "index";
    }

    //自定义登录页实现
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PreAuthorize("hasAnyRole('User')")
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

}
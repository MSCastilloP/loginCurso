package com.curso.seguridad.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
@Controller
public class PublicController {

    @GetMapping("/")
    public String home(){
        return "<html><body>Login Page</body></html>";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

}

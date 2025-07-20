package com.chatterbox.app.controller;

import com.chatterbox.app.service.AuthService;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class AuthController {

    AuthService authService;

    @Autowired
    AuthController(AuthService authService){
        this.authService=authService;
    }

    @GetMapping("/getStatus")
    public String getStatus(Model model){
        return authService.getStatus();
    }


}

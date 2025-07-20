package com.chatterbox.app.security;

import com.chatterbox.app.entity.User;
import com.chatterbox.app.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsServiceAutoConfiguration {

    @Autowired
    private UserRepo userRepo;

    public User loadUserByUser(){

    }

}

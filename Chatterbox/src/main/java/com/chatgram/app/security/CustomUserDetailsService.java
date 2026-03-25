package com.chatgram.app.security;

import com.chatgram.app.entity.User;
import com.chatgram.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService extends UserDetailsServiceAutoConfiguration {

    @Autowired
    private UserRepository userRepository;

}

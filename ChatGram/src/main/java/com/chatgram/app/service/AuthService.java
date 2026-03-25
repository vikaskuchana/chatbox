package com.chatgram.app.service;

import com.chatgram.app.entity.User;
import com.chatgram.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    UserRepository userRepository;

    @Autowired
    AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String getStatus(){
        User user=new User();
        user.setUsername("vikas");
        user.setPassword("pass@123");
        userRepository.save(user);
        return "Successfully saved";
    }


}

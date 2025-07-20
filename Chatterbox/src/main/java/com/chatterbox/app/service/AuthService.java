package com.chatterbox.app.service;

import com.chatterbox.app.entity.User;
import com.chatterbox.app.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    UserRepo userRepo;

    @Autowired
    AuthService(UserRepo userRepo){
        this.userRepo=userRepo;
    }

    public String getStatus(){
        User user=new User();
        user.setUsername("vikas");
        user.setPassword("pass@123");
        userRepo.save(user);
        return "Successfully saved";
    }
}

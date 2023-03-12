package com.ecinema.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRespository userRespository;

    @Autowired
    public UserService(UserRespository userRespository) {
        this.userRespository = userRespository;
    }

    public List<User> getUsers() {
        return userRespository.findAll();
    }
}

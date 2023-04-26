package com.ecinema.models.businesslogic.services;

import com.ecinema.models.users.Role;
import com.ecinema.models.users.User;
import com.ecinema.models.users.enums.Status;
import com.ecinema.models.users.enums.UserTypes;
import com.ecinema.models.businesslogic.repositories.RoleRepository;
import com.ecinema.models.businesslogic.repositories.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
@Component
public class UserFactory {

    private  static UserRespository userRespository;
    private  static RoleRepository roleRepository;

    @Autowired
    public UserFactory(UserRespository userRespository, RoleRepository roleRepository) {
        UserFactory.roleRepository = roleRepository;
        UserFactory.userRespository = userRespository;
    }

    public static User create(String type, User user){

        switch (type.toLowerCase()){
            case "admin":
                if(!(userRespository.findOneByEmail(user.getEmail()) == null)){  // check for duplicates
                    return null;
                }
                user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                user.setActivity(Status.ACTIVE);
                user.setUserType(UserTypes.ADMIN);
                Role role = roleRepository.findByName("ADMIN");
                user.setRoles(Arrays.asList(role));
                userRespository.save(user);
                return user;
            case "customer":
                if(!(userRespository.findOneByEmail(user.getEmail()) == null)){  // check for duplicates
                    user.setPassword("error");
                    return user;
                }
                try {
                    user.setUserType(UserTypes.CUSTOMER);
                    Role role1 = roleRepository.findByName("CUSTOMER");
                    user.setRoles(Arrays.asList(role1));
                    user.setActivity(Status.INACTIVE);  // set user status to inactive until confirmed
                    user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
                    userRespository.save(user);     //save user in db
                }
                catch(Exception e){     // throw exception on failure
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating new user.");
                }
                return user;
        }
        return user;
    }
}


package com.ecinema.security;

import com.ecinema.repositories.UserRespository;
import com.ecinema.models.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRespository userRespository;

    @Autowired
    public CustomUserDetailsService(UserRespository userRespository) {
        this.userRespository = userRespository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         User user = userRespository.findOneByEmail(email);
         if(user != null){
             return new org.springframework.security.core.userdetails.User(
                 user.getEmail(),
                 user.getPassword(),
                     user.getRoles().stream().map((role) -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
             );
         }
         else{
             throw new UsernameNotFoundException("Invalid email or password");
         }
    }

}

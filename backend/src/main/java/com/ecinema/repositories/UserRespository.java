package com.ecinema.repositories;

import com.ecinema.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRespository extends JpaRepository<User, Integer> {
    public User findOneByEmail(String email);
    public User findOneByUserID(int userID);

    public User findOneByPassword(String password);
}

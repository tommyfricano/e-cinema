package com.ecinema.repositories;

import com.ecinema.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<User, Integer> {
    public User findOneByEmail(String email);
    public Optional<User> findByEmail(String email);
    public User findOneByUserID(int userID);

    public User findOneByPassword(String password);

    public User findByResetPasswordToken(String token);


    public List<User> findAllByOptInPromo(boolean optInPromo);

}

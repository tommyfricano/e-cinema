package com.ecinema.repositories;

import com.ecinema.users.User;
import com.ecinema.users.confirmation.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

    public void deleteByUser(User user);
}

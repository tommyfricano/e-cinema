package com.ecinema.repositories;

import com.ecinema.users.Role;
import com.ecinema.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}

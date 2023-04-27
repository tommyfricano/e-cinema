package com.ecinema.models.dataaccess;

import com.ecinema.models.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}

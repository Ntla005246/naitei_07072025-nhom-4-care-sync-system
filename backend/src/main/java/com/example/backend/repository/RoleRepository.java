package com.example.backend.repository;

import com.example.backend.constant.enums.RoleType; // Import RoleType
import com.example.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> { // Giả sử ID của Role là Long

    Optional<Role> findByRoleName(RoleType roleName);
}

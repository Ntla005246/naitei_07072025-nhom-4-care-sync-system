package com.example.backend.repository;

import com.example.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.roles r
            WHERE u.email = :email
            """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}

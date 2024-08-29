package com.toletter.Repository;

import com.toletter.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByNickname(String nickname);
}

package com.toletter.Repository;

import com.toletter.Entity.Auth;
import com.toletter.Enums.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsByEmailAndAuthType(String email, AuthType authType);

    @Modifying
    @Transactional
    @Query("DELETE FROM Auth e WHERE e.email = :email")
    void deleteByEmail(@Param("email") String email);
}

package com.toletter.Repository;

import com.toletter.Entity.SentBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SentBoxRepository extends JpaRepository<SentBox, Long> {

    Optional<SentBox> findByLetterId(Long letter);

    List<SentBox> findAllByUserNickname(String user_nickname);

}

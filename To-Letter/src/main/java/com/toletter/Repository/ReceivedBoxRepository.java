package com.toletter.Repository;

import com.toletter.Entity.Letter;
import com.toletter.Entity.ReceivedBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReceivedBoxRepository extends JpaRepository<ReceivedBox, Long> {
    @Query("SELECT r.letter FROM ReceivedBox r WHERE r.receivedTime < :nowTime AND r.userNickname = :userNickname")
    List<Letter> findAllByReceivedTimeBeforeAndUserNickname(LocalDateTime nowTime, String userNickname);

    Optional<ReceivedBox> findByLetterId(Long letter);

    List<ReceivedBox> findAllByUserNickname(String user_nickname);

}

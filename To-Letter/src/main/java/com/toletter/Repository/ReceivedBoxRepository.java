package com.toletter.Repository;

import com.toletter.Entity.ReceivedBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReceivedBoxRepository extends JpaRepository<ReceivedBox, Long> {

    Optional<ReceivedBox> findByLetterId(Long letter);

    List<ReceivedBox> findAllByUserEmail(String user_email);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReceivedBox r WHERE r.letter.id in :letterIds")
    void deleteAllByLetterIds(@Param("letterIds") List<Long> letterIds);
}

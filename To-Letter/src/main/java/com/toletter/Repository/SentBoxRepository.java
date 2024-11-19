package com.toletter.Repository;

import com.toletter.Entity.SentBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SentBoxRepository extends JpaRepository<SentBox, Long> {

    Optional<SentBox> findByLetterId(Long letter);

    List<SentBox> findAllByUserEmail(String user_email);

    @Modifying
    @Transactional
    @Query("DELETE FROM SentBox s WHERE s.letter.id in :letterIds")
    void deleteAllByLetterIds(@Param("letterIds") List<Long> letterIds);
}

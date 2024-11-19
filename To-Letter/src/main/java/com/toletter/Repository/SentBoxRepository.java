package com.toletter.Repository;

import com.toletter.Entity.ReceivedBox;
import com.toletter.Entity.SentBox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SentBoxRepository extends JpaRepository<SentBox, Long> {

    Slice<SentBox> findAllByUserEmailOrderBySentTimeDesc(String email, Pageable pageable);

    Optional<SentBox> findByLetterId(Long letter);

    @Modifying
    @Transactional
    @Query("DELETE FROM SentBox s WHERE s.letter.id in :letterIds")
    void deleteAllByLetterIds(@Param("letterIds") List<Long> letterIds);
}

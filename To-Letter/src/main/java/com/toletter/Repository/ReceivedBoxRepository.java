package com.toletter.Repository;

import com.toletter.DTO.letter.LetterDTO;
import com.toletter.Entity.ReceivedBox;
import com.toletter.Entity.User;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReceivedBoxRepository extends JpaRepository<ReceivedBox, Long> {

    Optional<ReceivedBox> findByLetterId(Long letter);

    Slice<ReceivedBox> findAllByUserEmailOrderByReceivedTimeDesc(String email, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReceivedBox r WHERE r.letter.id in :letterIds")
    void deleteAllByLetterIds(@Param("letterIds") List<Long> letterIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM ReceivedBox r WHERE r.userEmail = :userEmail")
    void deleteAllByLetterByUserEmail(@Param("userEmail") String userEmail);
}

package com.toletter.Repository;

import com.toletter.Entity.ReceivedBox;
import com.toletter.Entity.SentBox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentBoxRepository extends JpaRepository<SentBox, Long> {

    Slice<SentBox> findAllByUserEmailOrderBySentTimeDesc(String email, Pageable pageable);

    List<SentBox> findAllByUserEmail(String user_email);

}

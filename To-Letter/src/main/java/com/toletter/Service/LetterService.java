package com.toletter.Service;

import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.Entity.*;
import com.toletter.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.*;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterRepository letterRepository;
    private final SentBoxRepository sentBoxRepository;
    private final ReceivedBoxRepository receivedBoxRepository;
    private final UserService userService;

    // 메일 보내기
    public void sendLetter(SendLetterRequest sendLetterRequest, HttpServletRequest httpServletRequest){
        Letter letter = sendLetterRequest.toEntity();
        User user = userService.findUserByToken(httpServletRequest);

        // 메일 db 저장
        letter.setFromUserId(user.getId());
        letter.setViewCheck(false);
        letter.setTemporaryStorage(false);
        letterRepository.save(letter);

        // 보낸 메일함에 저장
        SentBox sentBox = new SentBox();
        sentBox.setUser_id(user.getId());
        sentBox.setSentTime(letter.getCreatedAt());
        sentBox.setLetter(letter);
        sentBoxRepository.save(sentBox);

        // 받는 메일함에 저장
        ReceivedBox receivedBox = new ReceivedBox();
        receivedBox.setUser_id(letter.getToUserId());
        receivedBox.setReceivedTime(letter.getArrivedAt());
        receivedBox.setLetter(letter);
        receivedBoxRepository.save(receivedBox);
    }
}

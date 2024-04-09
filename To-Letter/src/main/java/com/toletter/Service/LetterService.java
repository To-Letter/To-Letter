package com.toletter.Service;

import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.DTO.letter.Response.ReceivedLetterResponse;
import com.toletter.DTO.letter.SaveReceivedBox;
import com.toletter.DTO.letter.SaveSentBox;
import com.toletter.Entity.*;
import com.toletter.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.*;
import java.time.LocalDateTime;
import java.util.List;

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
        SaveSentBox saveBoxDTO = new SaveSentBox();
        saveBoxDTO.setFromUserId(user.getId());
        saveBoxDTO.setSentTime(letter.getCreatedAt());
        saveBoxDTO.setLetter(letter);
        sentBoxRepository.save(saveBoxDTO.toEntity());

        // 받는 메일함에 저장
        SaveReceivedBox saveReceivedBox = new SaveReceivedBox();
        saveReceivedBox.setToUserId(letter.getToUserId());
        saveReceivedBox.setReceivedTime(letter.getArrivedAt());
        saveReceivedBox.setLetter(letter);
        receivedBoxRepository.save(saveReceivedBox.toEntity());
    }

    // 메일 받기
    public ReceivedLetterResponse receiveLetter(HttpServletRequest httpServletRequest){
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        User user = userService.findUserByToken(httpServletRequest);

        List<Letter> listBox = receivedBoxRepository.findAllByReceivedTimeBeforeAndUserID(now, user.getId());
        return ReceivedLetterResponse.res(user.getId(), listBox);
    }
}

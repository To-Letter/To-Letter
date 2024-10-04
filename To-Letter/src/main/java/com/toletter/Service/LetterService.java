package com.toletter.Service;

import com.toletter.DTO.letter.GpsDTO;
import com.toletter.DTO.letter.LetterDTO;
import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.DTO.letter.Response.ReceivedLetterResponse;
import com.toletter.DTO.letter.SaveReceivedBox;
import com.toletter.DTO.letter.SaveSentBox;
import com.toletter.Entity.*;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {
    private final LetterRepository letterRepository;
    private final SentBoxRepository sentBoxRepository;
    private final ReceivedBoxRepository receivedBoxRepository;
    private final UserService userService;
    private final GPSService gpsService;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

    // 메일 보내기
    public void sendLetter(SendLetterRequest sendLetterRequest, HttpServletRequest httpServletRequest){
        Letter letter = sendLetterRequest.toEntity();
        User fromUser = userService.findUserByToken(httpServletRequest); // 보내는 유저
        User toUser = userRepository.findByNickname((letter.getToUserNickname())).orElseThrow(() -> new ErrorException("유저 없음.", ErrorCode.FORBIDDEN_EXCEPTION)); // 받는 유저

        Map fromUserGPS = gpsService.getGpsUrl(fromUser.getAddress()); // 보내는 유저 위도 경도 구함
        Map toUserGPS = gpsService.getGpsUrl(toUser.getAddress()); // 받는 유저 위도 경도 구함

        GpsDTO gps = new GpsDTO();
        gps.setLat1((Double)fromUserGPS.get("lat"));
        gps.setLon1((Double)fromUserGPS.get("lon"));
        gps.setLat2((Double)toUserGPS.get("lat"));
        gps.setLon2((Double)toUserGPS.get("lon"));

        // 도착하는 시간 구함.
        // 테스트 및 프론트 개발을 위해 1-5분으로 할 예정
        LocalDateTime now = LocalDateTime.now();
        int arrivedDay = this.getReceivedTime(gps);
        LocalDateTime arrivedTime = now.plusMinutes(arrivedDay);

        // 메일 db 저장
        letter.setFromUserNickname(fromUser.getNickname());
        letter.setArrivedAt(arrivedTime);
        letter.setViewCheck(false);
        letterRepository.save(letter);

        // 보낸 메일함에 저장
        SaveSentBox saveBoxDTO = new SaveSentBox();
        saveBoxDTO.setFromUserNickname(fromUser.getNickname());
        saveBoxDTO.setSentTime(letter.getCreatedAt());
        saveBoxDTO.setLetter(letter);
        sentBoxRepository.save(saveBoxDTO.toEntity());

        // 받는 메일함에 저장
        SaveReceivedBox saveReceivedBox = new SaveReceivedBox();
        saveReceivedBox.setToUserNickname(toUser.getNickname());
        saveReceivedBox.setReceivedTime(arrivedTime);
        saveReceivedBox.setLetter(letter);
        receivedBoxRepository.save(saveReceivedBox.toEntity());

        // 알림 보내기
        alarmService.scheduleTask(toUser.getNickname(), letter, arrivedDay);
    }

    // 메일 읽기
    public LetterDTO openLetter(Long letterID, HttpServletRequest httpServletRequest){
        User user = userService.findUserByToken(httpServletRequest);
        Letter letter = receivedBoxRepository.findByLetterId(letterID).orElseThrow().getLetter();
        if(!user.getEmail().equals(letter.getToUserNickname())){
            throw new ErrorException("메일의 소유주가 다릅니다. ", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        return LetterDTO.toDTO(letter);
    }

    // 메일 읽음 처리
    public ResponseEntity<String> viewCheckLetter(Long letterID, HttpServletRequest httpServletRequest){
        User user = userService.findUserByToken(httpServletRequest);
        Letter letter = receivedBoxRepository.findByLetterId(letterID).orElseThrow().getLetter();
        if(!user.getEmail().equals(letter.getToUserNickname())){
            throw new ErrorException("메일의 소유주가 다릅니다. ", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        letter.updateViewCheck();
        letterRepository.save(letter);
        return ResponseEntity.ok("메일 읽음 처리 성공");
    }

    // 메일 삭제
    public ResponseEntity<String> deleteLetter(Long letterID, HttpServletRequest httpServletRequest){
        User user = userService.findUserByToken(httpServletRequest);
        ReceivedBox receivedBox = receivedBoxRepository.findByLetterId(letterID).orElseThrow();
        if(!receivedBox.getUserNickname().equals(user.getNickname())){
            throw new ErrorException("메일의 소유주가 다릅니다. ", ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        receivedBoxRepository.delete(receivedBox);
        return ResponseEntity.ok("메일 삭제 성공");
    }

    // 모든 메일함 열기
    public ReceivedLetterResponse receiveLetter(HttpServletRequest httpServletRequest){
        User user = userService.findUserByToken(httpServletRequest);

        List<ReceivedBox> listBox = receivedBoxRepository.findAllByUserNickname(user.getNickname());
        List<LetterDTO> LetterList = listBox.stream()
                .map(ReceivedBox::getLetter)
                .filter(letter -> letter.getArrivedAt().isBefore(LocalDateTime.now()))
                .map(LetterDTO::toDTO)
                .collect(Collectors.toList());
        return ReceivedLetterResponse.res(user.getNickname(), LetterList);
    }

    // 안 읽은 메일함 열기
    public ReceivedLetterResponse receivedUnReadLetter(HttpServletRequest httpServletRequest){
        User user = userService.findUserByToken(httpServletRequest);

        List<ReceivedBox> letterList = receivedBoxRepository.findAllByUserNickname(user.getNickname());
        List<LetterDTO> unReadListBox = letterList.stream()
                .map(ReceivedBox::getLetter)
                .filter(Objects::nonNull)
                .filter(letter -> letter.getArrivedAt().isBefore(LocalDateTime.now()))
                .filter(letter -> !letter.getViewCheck())
                .map(LetterDTO::toDTO)
                .collect(Collectors.toList());

        return ReceivedLetterResponse.res(user.getNickname(), unReadListBox);
    }

    // 읽은 메일함 열기
    public ReceivedLetterResponse receivedReadLetter(HttpServletRequest httpServletRequest){
        User user = userService.findUserByToken(httpServletRequest);

        List<ReceivedBox> letterList = receivedBoxRepository.findAllByUserNickname(user.getNickname());
        List<LetterDTO> readListBox = letterList.stream()
                .map(ReceivedBox::getLetter)
                .filter(Objects::nonNull)
                .filter(letter -> letter.getArrivedAt().isBefore(LocalDateTime.now()))
                .filter(Letter::getViewCheck)
                .map(LetterDTO::toDTO)
                .collect(Collectors.toList());


        return ReceivedLetterResponse.res(user.getNickname(), readListBox);
    }

    // 거리에 따른 메일 도착 시간
    public int getReceivedTime(GpsDTO gps){
        double distance = gpsService.getDistance(gps);

        if(0 <= distance && distance < 10){
            return 1;
        } else if (distance < 30) {
            return 2;
        } else if (distance < 50) {
            return 3;
        } else if (distance < 80) {
            return 4;
        } else if (distance > 100) {
            return 5;
        } else {
            throw new ErrorException("400"+"거리가 나오지 않습니다.", ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }
}

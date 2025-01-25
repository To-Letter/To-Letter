package com.toletter.Service;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.letter.GpsDTO;
import com.toletter.DTO.letter.LetterDTO;
import com.toletter.DTO.letter.Request.DeleteLetterRequest;
import com.toletter.DTO.letter.Request.SendLetterRequest;
import com.toletter.DTO.letter.Response.LetterResponse;
import com.toletter.DTO.letter.Response.ReceivedLetterResponse;
import com.toletter.DTO.letter.Response.SentLetterResponse;
import com.toletter.DTO.letter.SaveReceivedBox;
import com.toletter.DTO.letter.SaveSentBox;
import com.toletter.Entity.*;
import com.toletter.Enums.LetterType;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.Repository.*;
import com.toletter.Service.Jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import javax.sql.rowset.serial.SerialClob;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final GPSService gpsService;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

    // 메일 보내기
    public ResponseDTO sendLetter(SendLetterRequest sendLetterRequest, CustomUserDetails userDetails) throws SQLException {
        User fromUser =  userDetails.getUser(); // 보내는 유저
        User toUser = userRepository.findByNickname(sendLetterRequest.getToUserNickname()).orElseThrow(() ->
                new ErrorException("메일 보내기 실패 / 보낼 유저가 없음(유저가 존재하지 않음)", 200, ErrorCode.FORBIDDEN_EXCEPTION)
        ); // 받는 유저
        // String으로 받은 편지내용을 Clob으로 변경
        Clob clobContent = new SerialClob(sendLetterRequest.getContents().toCharArray());

        Letter letter = sendLetterRequest.toEntity(toUser.getEmail(), clobContent);

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
        letter.setFromUserEmail(fromUser.getEmail());
        letter.setToUserNickname(toUser.getNickname());
        letter.setFromUserNickname(fromUser.getNickname());
        letter.setArrivedAt(arrivedTime);
        letter.setViewCheck(false);
        letterRepository.save(letter);

        // 보낸 메일함에 저장
        if(sendLetterRequest.isSaveLetterCheck()){
            SaveSentBox saveBoxDTO = new SaveSentBox();
            saveBoxDTO.setFromUserEmail(fromUser.getEmail());
            saveBoxDTO.setSentTime(letter.getCreatedAt());
            saveBoxDTO.setLetter(letter);
            sentBoxRepository.save(saveBoxDTO.toEntity());
        }

        // 받는 메일함에 저장
        SaveReceivedBox saveReceivedBox = new SaveReceivedBox();
        saveReceivedBox.setToUserEmail(toUser.getEmail());
        saveReceivedBox.setReceivedTime(arrivedTime);
        saveReceivedBox.setLetter(letter);
        receivedBoxRepository.save(saveReceivedBox.toEntity());

        // 알림 보내기
        alarmService.scheduleTask(toUser.getEmail(), letter.getId().toString(), arrivedDay);
        return ResponseDTO.res(200, "메일 보내기 성공", "");
    }

    // 모든 받은 메일함 열기
    public ResponseDTO receivedLetter (CustomUserDetails userDetails, Pageable pageable){
        User user =  userDetails.getUser();

        Slice<ReceivedBox> slice = receivedBoxRepository.findAllByUserEmailOrderByReceivedTimeDesc(user.getEmail(), pageable);
        List<LetterDTO> letterList = slice.stream()
                .map(ReceivedBox::getLetter)
                .filter(letter -> letter.getArrivedAt().isBefore(LocalDateTime.now()))
                .map(letter -> {
                    String content = convertClobToString(letter.getContent());
                    return LetterDTO.toDTO(letter, content);
                })
                .collect(Collectors.toList());

        return ResponseDTO.res(200, "모든 메일 보여주기", LetterResponse.res(letterList, pageable));
    }

    // 안 읽은 받은 메일함 열기
    public ResponseDTO receivedUnReadLetter(CustomUserDetails userDetails, Pageable pageable){
        User user =  userDetails.getUser();

        Slice<ReceivedBox> slice = receivedBoxRepository.findAllByUserEmailOrderByReceivedTimeDesc(user.getEmail(), pageable);
        List<LetterDTO> unReadListBox = slice.stream()
                .map(ReceivedBox::getLetter)
                .filter(Objects::nonNull)
                .filter(letter -> letter.getArrivedAt().isBefore(LocalDateTime.now()))
                .filter(letter -> !letter.getViewCheck())
                .map(letter -> {
                    String content = convertClobToString(letter.getContent());
                    return LetterDTO.toDTO(letter, content);
                })
                .collect(Collectors.toList());

        ReceivedLetterResponse receivedLetterResponse = ReceivedLetterResponse.res(user.getNickname(), unReadListBox);
        return ResponseDTO.res(200, "안 읽은 메일 보여주기", LetterResponse.res(unReadListBox, pageable));
    }

    // 읽은 받은 메일함 열기
    public ResponseDTO receivedReadLetter(CustomUserDetails userDetails, Pageable pageable){
        User user =  userDetails.getUser();

        Slice<ReceivedBox> slice = receivedBoxRepository.findAllByUserEmailOrderByReceivedTimeDesc(user.getEmail(), pageable);
        List<LetterDTO> readListBox = slice.stream()
                .map(ReceivedBox::getLetter)
                .filter(Objects::nonNull)
                .filter(letter -> letter.getArrivedAt().isBefore(LocalDateTime.now()))
                .filter(Letter::getViewCheck)
                .map(letter -> {
                    String content = convertClobToString(letter.getContent());
                    return LetterDTO.toDTO(letter, content);
                })
                .collect(Collectors.toList());

        ReceivedLetterResponse receivedLetterResponse = ReceivedLetterResponse.res(user.getNickname(), readListBox);
        return ResponseDTO.res(200, "읽은 메일 보여주기", LetterResponse.res(readListBox, pageable));
    }

    // 메일 읽음 처리
    public ResponseDTO viewCheckReceivedLetter(Long letterID, CustomUserDetails userDetails){
        Letter letter = receivedBoxRepository.findByLetterId(letterID).orElseThrow().getLetter();
        User user = userDetails.getUser();

        if(!user.getEmail().equals(letter.getToUserEmail())){
            return ResponseDTO.res(401, "메일 읽음 처리 실패 / 본인의 메일이 아님", "");
        }
        letter.updateViewCheck();
        letterRepository.save(letter);
        String content = this.convertClobToString(letter.getContent());
        LetterDTO letterDTO = LetterDTO.toDTO(letter, content);
        return ResponseDTO.res(200, "메일 읽기 성공", letterDTO);
    }

    // 보낸 메일함 열기
    public ResponseDTO viewSentBox(CustomUserDetails customUserDetails, Pageable pageable){
        User user = customUserDetails.getUser();

        Slice<SentBox> slice = sentBoxRepository.findAllByUserEmailOrderBySentTimeDesc(user.getEmail(), pageable);
        List<LetterDTO> sentListBox = slice.stream()
                .map(SentBox::getLetter)
                .filter(Objects::nonNull)
                .map(letter -> {
                    String content = convertClobToString(letter.getContent());
                    return LetterDTO.toDTO(letter, content);
                })
                .collect(Collectors.toList());

        return ResponseDTO.res(200, "보낸 모든 메일함 열기", SentLetterResponse.res(user.getNickname(), pageable, sentListBox));
    }

    // 메일 삭제
    public ResponseDTO deleteLetter(DeleteLetterRequest deleteLetterRequest, CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        List<Long> containNotIds = new ArrayList<>();
        boolean check = true;

        if(deleteLetterRequest.getLetterType().equals(LetterType.receivedLetter)){
            for(Long letterId : deleteLetterRequest.getLetterIds()){
                ReceivedBox receivedBox = receivedBoxRepository.findByLetterId(letterId).orElseThrow(() ->
                        new ErrorException("메일 삭제 실패 / 메일("+letterId+")이 없음", 200, ErrorCode.UNAUTHORIZED_EXCEPTION)
                );
                if(!receivedBox.getUserEmail().equals(user.getEmail())){
                    check = false;
                    containNotIds.add(letterId);
                }
            }
            if(!check){
                return ResponseDTO.res(403, "메일 삭제 실패 / 메일 주인이 아님", containNotIds);
            } else {
                receivedBoxRepository.deleteAllByLetterIds(deleteLetterRequest.getLetterIds());
            }
        } else {
            for(Long letterId : deleteLetterRequest.getLetterIds()){
                SentBox sentBox = sentBoxRepository.findByLetterId(letterId).orElseThrow(() ->
                        new ErrorException("메일 삭제 실패 / 메일("+letterId+")이 없음", 200, ErrorCode.UNAUTHORIZED_EXCEPTION)
                );
                if(!sentBox.getUserEmail().equals(user.getEmail())){
                    check = false;
                    containNotIds.add(letterId);
                }
            }
            if(!check){
                return ResponseDTO.res(403, "메일 삭제 실패 / 메일 주인이 아님", containNotIds);
            } else {
                sentBoxRepository.deleteAllByLetterIds(deleteLetterRequest.getLetterIds());
            }
        }
        return ResponseDTO.res(200, "메일 삭제 성공", "");
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
            throw new ErrorException("거리가 나오지 않습니다.", 404,ErrorCode.NOT_FOUND_EXCEPTION);
        }
    }

    public String convertClobToString(Clob clob) {
        StringBuilder stringBuilder = new StringBuilder();
        try(Reader reader = clob.getCharacterStream(); BufferedReader bufferedReader = new BufferedReader(reader)){
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        return stringBuilder.toString();
    }
}

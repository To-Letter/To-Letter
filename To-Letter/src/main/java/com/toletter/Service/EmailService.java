package com.toletter.Service;

import com.toletter.DTO.ResponseDTO;
import com.toletter.DTO.auth.Request.EmailSaveRequest;
import com.toletter.DTO.auth.Request.EmailVerifyRequest;
import com.toletter.Entity.Auth;
import com.toletter.Entity.User;
import com.toletter.Enums.AuthType;
import com.toletter.Enums.LoginType;
import com.toletter.Error.ErrorCode;
import com.toletter.Error.ErrorException;
import com.toletter.Repository.AuthRepository;
import com.toletter.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    // 인증 코드 생성
    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            if (random.nextBoolean()) { // 랜덤으로 true, false 리턴
                key.append((char)(random.nextInt(26) + 97)); // 소문자
            } else {
                key.append(random.nextInt(10)); // 숫자
            }
        }
        return  key.toString();
    }

    // 타임리프 설정하는 코드
    private String setContext(String code, AuthType authType) {
        Context context = new Context();
        context.setVariable("code", code);
        if(authType.equals(AuthType.secondAuth)){
            return templateEngine.process("emailAuth", context);
        }
        return templateEngine.process("updatePW", context);
    }

    // 2차 인증을 위한 이메일 보내기
    public ResponseDTO sendEmail(String toEmail) throws MessagingException {
        if(userRepository.findByEmail(toEmail).get().isSecondConfirmed()){
            return ResponseDTO.res(403, "2차 인증 이메일 전송 실패 / 2차 인증 완료한 유저", "");
        }

        if(authRepository.existsByEmailAndAuthType(toEmail, AuthType.secondAuth)){
            // 현재 시각 가져오기
            LocalDateTime currentDateTime = LocalDateTime.now();
            Auth auth = authRepository.findByEmail(toEmail).orElseThrow();

            // 이메일 인증 제한 시간 5분 지정
            if(currentDateTime.isAfter(auth.getCreatedDate().plusMinutes(5))){
                authRepository.deleteByEmail(toEmail);
                this.sendEmailForAuth(toEmail, AuthType.secondAuth);
                return ResponseDTO.res(201,"2차 인증 이메일 전송 실패 / 시간 초과하여 2차 인증 메일 다시 보냄", "");
            }
            return ResponseDTO.res(401, "2차 인증 이메일 전송 실패 / 이미 메일을 보냄", "");
        }

        this.sendEmailForAuth(toEmail, AuthType.secondAuth);
        return ResponseDTO.res(200, "2차 인증 메일 전송 성공", "");
    }

    // 비밀번호 변경을 위한 메일 보내기
    public ResponseDTO emailPassword(String toEmail) throws MessagingException {
        User user = userRepository.findByEmail(toEmail).orElseThrow();
        if(!userRepository.existsByEmail(toEmail) || !user.isSecondConfirmed() || user.getLoginType().equals(LoginType.kakaoLogin)){
            return ResponseDTO.res(401, "비밀번호 변경 이메일 전송 실패 / 유저 없음(혹은 2차인증이 되지 않은 유저임) / 카카오 유저임", "");
        }

        if(authRepository.existsByEmailAndAuthType(toEmail, AuthType.updatePW)){
            // 현재 시각 가져오기
            LocalDateTime currentDateTime = LocalDateTime.now();
            Auth auth = authRepository.findByEmail(toEmail).orElseThrow();

            // 이메일 인증 제한 시간 5분 지정
            if(currentDateTime.isAfter(auth.getCreatedDate().plusMinutes(5))){
                authRepository.deleteByEmail(toEmail);
                this.sendEmailForAuth(toEmail, AuthType.updatePW);
                return ResponseDTO.res(201,"비밀번호 변경 이메일 전송 실패 / 시간 초과, 인증 이메일 다시 보냄", "");
            }
            return ResponseDTO.res(403, "비밀번호 변경 이메일 전송 실패 / 이미 인증 이메일을 보냄", "");
        }
        this.sendEmailForAuth(toEmail, AuthType.updatePW);

        return ResponseDTO.res(200, "비밀번호 변경 메일 전송 성공", "");
    }

    // 실제 메일 전송
    public void sendEmailForAuth(String toEmail, AuthType authType) throws MessagingException {
        String randomCode = createCode(); //인증 코드 생성

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, toEmail); // 보낼 이메일 설정
        if(authType.equals(AuthType.secondAuth)){
            message.setSubject("[to-Letter] 2차 인증코드"); // 이메일 제목
        } else if(authType.equals(AuthType.updatePW)){
            message.setSubject("[to-Letter] 비밀번호 찾기 인증 코드"); // 이메일 제목
        }
        message.setFrom("to-Letter");
        message.setText(setContext(randomCode, authType), "utf-8", "html"); // 내용 설정(Template Process)

        //실제 메일 전송
        emailSender.send(message);

        // 데베에 저장
        this.saveDB(toEmail, randomCode, authType);
    }

    // 데베에 저장
    public void saveDB(String email, String randomCode, AuthType authType) {
        if(authRepository.existsByEmail(email)){
            throw new ErrorException("이미 인증 메일을 보냈습니다.", 200, ErrorCode.UNAUTHORIZED_EXCEPTION);
        }
        EmailSaveRequest emailSaveRequest = new EmailSaveRequest();
        emailSaveRequest.setEmail(email);
        emailSaveRequest.setRandomCode(randomCode);
        emailSaveRequest.setAuthType(authType);
        authRepository.save(emailSaveRequest.toEntity());
    }

    // 이메일 검증
    public ResponseDTO verifyEmail(EmailVerifyRequest emailVerifyRequest) {
        if(!authRepository.existsByEmailAndAuthType(emailVerifyRequest.getEmail(), emailVerifyRequest.getAuthType())){
            return ResponseDTO.res(404, "해당 메일로 인증을 하지 않았습니다.(메일이 없음)", "");
        }

        Auth auth = authRepository.findByEmail(emailVerifyRequest.getEmail()).orElseThrow();

        // 현재 시각 가져오기
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 이메일 인증 제한 시간 5분 지정
        if(currentDateTime.isAfter(auth.getCreatedDate().plusMinutes(5))){
            authRepository.deleteByEmail(auth.getEmail());
            return ResponseDTO.res(401,"이메일 인증 실패 / 시간 초과", "");
        }

        if(!emailVerifyRequest.getRandomCode().equals(auth.getRandomCode())){
            return ResponseDTO.res(403,"이메일 인증 실패 / 랜덤 코드 불일치", "");
        }
        // 인증 성공 시
        authRepository.deleteByEmail(auth.getEmail());
        User user = userRepository.findByEmail(emailVerifyRequest.getEmail()).orElseThrow(() -> new ErrorException("회원가입된 이메일이 없음.", 200, ErrorCode.NOT_FOUND_EXCEPTION));
        if(emailVerifyRequest.getAuthType().equals(AuthType.secondAuth)){
            user.setSecondConfirmed(true);
            userRepository.save(user);
        } else if (emailVerifyRequest.getAuthType().equals(AuthType.updatePW)){
            user.setChangePassWord(true);
            userRepository.save(user);
            return ResponseDTO.res(201, "비밀번호 변경 검증 성공", "");
        }

        return ResponseDTO.res(200, "2차 인증 성공", "");
    }

}

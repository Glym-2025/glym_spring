package glym.glym_spring.auth.service;

;
import glym.glym_spring.global.exception.CustomExceptionRefactor;
import glym.glym_spring.global.exception.errorcode.ErrorCodeRefactor;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor

public class EmailService {

    private final JavaMailSender mailSender;
    private final RedisTemplate authCodeRedisTemplate;

    @Value("${custom.mail.from}")
    private String fromAddress;
    @Value("${custom.mail.subject.signup}")
    private String subject;


    public void sendEmail(String to) throws MessagingException {

        String code = generateAuthCode(to);
        String content = loadEmailTemplate(code);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content,true);
        helper.setReplyTo(fromAddress);

        try{
            mailSender.send(mimeMessage);
        } catch (MailException e) {
           throw new CustomExceptionRefactor(ErrorCodeRefactor.EMAIL_SEND_FAILED, to);
        }
    }

    public void verifyEmail(String email, String code) {
        String key = "authCode:" + email;

        if(!authCodeRedisTemplate.hasKey(key)) {
            throw new CustomExceptionRefactor(ErrorCodeRefactor.EMAIL_CODE_NOT_FOUND, email);
        }
        String storedCode = authCodeRedisTemplate.opsForValue().get(key).toString();

        if(!storedCode.equals(code)) {
            throw new CustomExceptionRefactor(ErrorCodeRefactor.EMAIL_CODE_MISMATCH, email);
        }

        authCodeRedisTemplate.delete(key);
    }

    private String generateAuthCode(String email) {
        String code = UUID.randomUUID().toString().substring(0, 6);
        /*
        if(authCodeRedisTemplate.hasKey("authCode" + email)){   //사실 redis 는 덮어쓰기 때문에 있을 필요가 없음
            authCodeRedisTemplate.delete("authCode" + email);
        }*/
        authCodeRedisTemplate.opsForValue().set("authCode:" + email, code, 5, TimeUnit.MINUTES);
        return code;
    }

    private String loadEmailTemplate(String code) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            return template
                    .replace("{{code}}", code);

        } catch (IOException e) {
            throw new RuntimeException("이메일 템플릿 로딩 실패", e);
        }
    }


}

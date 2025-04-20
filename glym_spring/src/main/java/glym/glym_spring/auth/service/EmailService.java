package glym.glym_spring.auth.service;

import glym.glym_spring.global.exception.EmailSendException;
import glym.glym_spring.global.exception.EmailSendExceptionHandler;
import glym.glym_spring.global.exception.errorcode.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor

public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${custom.mail.from}")
    private String fromAddress;
    @Value("${custom.mail.subject.signup}")
    private String subject;


    public void sendEmail(String to) throws MessagingException {

        String code = generateAuthCode();
        String content = buildContent(code);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content,true);
        helper.setReplyTo(fromAddress);

        try{
            mailSender.send(mimeMessage);
        } catch (MailException e) {
           throw new EmailSendException(ErrorCode.EMAIL_SEND_FAILED, to);
        }

    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    private String buildContent(String code) {
        return String.format("<h1>인증코드: <strong>%s</strong></h1>", code);
    }


}

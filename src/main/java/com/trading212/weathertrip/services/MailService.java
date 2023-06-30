package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public MailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(String to, String content, boolean isMultipart, boolean isHtml, String subject) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            mimeMessageHelper.setFrom("no-reply@tripweather.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, isHtml);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());

            log.info("Send email to User : {}", to);
        } catch (MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    public void sendActivationEmail(User user){
        log.info("Sending activation mail to {}", user.getEmail());

        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("request", "http://localhost:8080/api/account/activate/?key=" + user.getActivationKey());
        String content = templateEngine.process("mail/activationEmail", context);

        sendEmail(user.getEmail(), content, false, true, "Потвърждение на имейл");
    }

    public void sendPasswordResetMail(User user) {
        log.info("Sending password reset mail to {}", user.getEmail());

        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("request", "http://localhost:8080/api/account/reset-password/finish");
        String content = templateEngine.process("mail/passwordResetEmail", context);
        sendEmail(user.getEmail(), content, false, true, "Забравена парола");
    }
}

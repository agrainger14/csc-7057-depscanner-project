package com.depscanner.notificationservice.service;

import com.depscanner.notificationservice.event.AdvisoryFoundEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @KafkaListener(topics = "advisory-found-topic")
    public void sendMail(AdvisoryFoundEvent advisoryFoundEvent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, "UTF-8");

            log.info("Advisory discovered... emailing user : " + advisoryFoundEvent.getUserEmail());
            mimeMessageHelper.setTo(advisoryFoundEvent.getUserEmail());
            mimeMessageHelper.setSubject("DepScanner Vulnerability Notification");

            Context context = new Context();
            context.setVariable("userEmail", advisoryFoundEvent.getUserEmail());
            context.setVariable("projectDetails", advisoryFoundEvent.getProjectResponse());
            context.setVariable("vulnDependencies", advisoryFoundEvent.getVulnDependencies());

            String emailContent = templateEngine.process("layouts/advisory-email-template", context);
            mimeMessageHelper.setText(emailContent, true);

            ClassPathResource logoResource = new ClassPathResource("static/img/depscannerlogo.png");
            mimeMessageHelper.addInline("logo", logoResource, "image/png");

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

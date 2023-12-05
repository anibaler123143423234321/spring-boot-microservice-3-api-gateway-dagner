package com.dagnerchuman.springbootmicroservice3ApiGateway.service;

import com.dagnerchuman.springbootmicroservice3ApiGateway.Dto.EmailValuesDto;
import com.netflix.discovery.converters.Auto;
import feign.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${mail.urlFront}")
    private String urlFront;

    @Value("${delete.urlDeleteFront}")
    private String urlDeleteFront;
    /*
    public void sendEmail() {
        try {
            // Enable debugging for the mail session
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.starttls.enable", "true");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dchumanlluen@gmail.com");
            message.setTo("dchumanlluen@gmail.com");
            message.setSubject("Prueba envío email simple");
            message.setText("Esto es el contenido del email");

            // Configure the mail sender with the debugging properties
            ((JavaMailSenderImpl) javaMailSender).setJavaMailProperties(props);

            // Send the email
            javaMailSender.send(message);
        } catch (Exception e) {
            // Handle exceptions, log, or rethrow as needed
            e.printStackTrace();
        }
    }

    */

    public void sendEmail(EmailValuesDto dto) {

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("username", dto.getUserName());
            model.put("url",urlFront + dto.getTokenPassword());
            context.setVariables(model);
            String htmlText = templateEngine.process("email-template",context);
            helper.setFrom(dto.getMailFrom());
            helper.setTo(dto.getMailTo());
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText,true);
            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        }
        }



    public void sendEmailDelete(EmailValuesDto dto) {

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("username", dto.getUserName());
            model.put("url",urlDeleteFront + dto.getTokenPassword());
            context.setVariables(model);
            String htmlText = templateEngine.process("eliminar-template",context);
            helper.setFrom(dto.getMailFrom());
            helper.setTo(dto.getMailTo());
            helper.setSubject(dto.getSubject());
            helper.setText(htmlText,true);
            javaMailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }



    }

package com.supportportal.service;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static com.supportportal.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

@Service
public class EmailService {

    public void sendNewPasswordEmail(String firstName, String username, String password, String email) throws MessagingException {
        Message message = createPasswordEmail(firstName, username, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(SMTP_HOST, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendRegistrationEmail(String firstName, String username, String password, String email) throws MessagingException  {
        Message message = createRegistrationEmail(firstName, username, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(SMTP_HOST, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendSubscriptionRequest(String email, String plan) throws MessagingException {
        Message message = createSubscriptionEmail(email, plan);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(SMTP_HOST, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    public void sendMessage(String email, String text) throws MessagingException {
        Message message = createMessage(email, text);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(SMTP_HOST, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createRegistrationEmail(String firstName, String username, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(INFO_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setSubject(EMAIL_REGISTRATION_SUBJECT);
        message.setText("Hello " + firstName + ", \n \nThank you for registering on the Tensys! Below is the login information: \n \n"
                + "username: " + username + "\n"
                + "password: " + password + "\n \n"
                + "You can log in now: https://tensys.org/login\n" +
                "\n \nTensys Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Message createPasswordEmail(String firstName, String username, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(INFO_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setSubject(EMAIL_NEW_PASSWORD_SUBJECT);
        message.setText("Hello " + firstName + ", \n \nBelow is your new login information: \n \n"
                + "username: " + username + "\n"
                + "password: " + password + "\n \n"
                + "You can log in now: https://tensys.org/login\n" +
                "\n \nTensys Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Message createSubscriptionEmail(String email, String plan) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(INFO_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(INFO_EMAIL, false));
        message.setSubject(EMAIL_NEW_SUBSCRIPTION_SUBJECT);
        message.setText(email + " - subscription request for plan: " + plan + "\n \n The Support Team");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Message createMessage(String email, String text) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(INFO_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(INFO_EMAIL, false));
        message.setSubject("TenSys message from " + email);
        message.setText(text);
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, SMTP_HOST);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_AUTH, "true");
        properties.put(SMTP_SSL_ENABLE, true);


        return Session.getInstance(properties, null);
    }
}

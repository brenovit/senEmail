package br.com.friends.notification.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.friends.notification.general.dto.NotificationRequest;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EmailSenderService {

	@Autowired
	private MailSMTPProperties mailSMTPProperties;

	private void sendMail(String subject, String message, String to) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", mailSMTPProperties.getHost());
			props.put("mail.smtp.port", mailSMTPProperties.getPort());
			props.put("mail.smtp.auth", mailSMTPProperties.getAuth());

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailSMTPProperties.getUsername(),
							mailSMTPProperties.getPassword());
				}
			});

			MimeMessage msg = new MimeMessage(session);

			InternetAddress[] replyTo = InternetAddress.parse("notaaqui@noreply.com", true);
			InternetAddress[] recipientsTo = InternetAddress.parse(to, true);

			msg.setRecipients(Message.RecipientType.TO, recipientsTo);
			msg.setFrom("notaaqui@noreply.com");
			msg.setReplyTo(replyTo);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setContent(message, "text/html");
			msg.setHeader("XPriority", "1");
			Transport.send(msg);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			log.info("Email sended");
		}
	}

	public void sendMail(NotificationRequest request) {
		log.info("Enviando email para: {}", request.toString());
		sendMail(request.getSubject(), request.getMessage(), request.getEmail());
	}
}

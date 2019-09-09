package dev.saseno.jakarta.digioh.email;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.mail.smtp.SMTPTransport;

import java.util.Properties;

public class SendEmailAttachment implements Runnable {

	private static final String SMTP_SERVER = "smtp.gmail.com";
	private static final String USERNAME = "";
	private static final String PASSWORD = "";
	
	private static final String EMAIL_SUBJECT = "DigiOH Augmented Reality Email Sender";

	private Properties props = null;
	private Session session = null;	
	
	private String toAddress = null;
	private String message = null;
	private String photoPath = null;
	
	public SendEmailAttachment(String toAddress, String message, String photoPath) {
		try {
			
			props = System.getProperties();
		    props.put("mail.debug", "true");
	        props.put("mail.smtp.user", USERNAME);
	        props.put("mail.smtp.password", PASSWORD);
	        props.put("mail.smtp.host", SMTP_SERVER);
		    
		    props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.port", "587");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.starttls.enable", "true"); //TLS
	        props.put("mail.smtp.ssl.trust", SMTP_SERVER);
	        		    	        
//		    props.put("mail.smtp.host", "smtp.gmail.com");
//		    props.put("mail.smtp.port", "465");
//		    props.put("mail.smtp.auth", "true");
//		    props.put("mail.smtp.socketFactory.port", "465");
//		    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

//	        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//	        props.put("mail.smtp.socketFactory.fallback", "false");
//	        props.put("mail.smtp.port", "465");
//	        props.put("mail.smtp.socketFactory.port", "465");
//	        props.put("mail.smtp.auth", "true");
	        
			session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });

//			session = Session.getDefaultInstance(props);			
			session.setDebug(true);
			
			this.toAddress 	= toAddress;
			this.message 	= message;
			this.photoPath 	= photoPath;
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void sendEmail() {

		try {

			System.err.println("----------------------");
			System.err.println("Email Sender");
			System.err.println("----------------------");

			System.err.println(">> " + toAddress);
			System.err.println(">> " + message);
			System.err.println(">> " + photoPath);			
			
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(USERNAME));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));
			msg.setSubject(EMAIL_SUBJECT);

			// text
			MimeBodyPart p1 = new MimeBodyPart();
			p1.setText(message);
			
			MimeBodyPart p2 = null;			
			if (photoPath != null) {
				p2 = new MimeBodyPart();
				FileDataSource fds = new FileDataSource(photoPath);
				p2.setDataHandler(new DataHandler(fds));
				p2.setFileName(fds.getName());
			}

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(p1);
			
			if (p2 != null) {
				mp.addBodyPart(p2);
			}

			msg.setContent(mp);
			msg.saveChanges();
			
//			Transport.send(msg);	

			SMTPTransport smtpTransport = new SMTPTransport(session, null);
			smtpTransport.connect(SMTP_SERVER, USERNAME, PASSWORD);
            //smtpTransport.issueCommand("AUTH XOAUTH2 " + new String(BASE64EncoderStream.encode(String.format("user=%s\1auth=Bearer %s\1\1", smtpUserName, smtpUserAccessToken).getBytes())), 235);
            smtpTransport.sendMessage(msg, msg.getAllRecipients());
            smtpTransport.close();
            
//			Transport transport = session.getTransport("smtp");
//			transport.connect(SMTP_SERVER, USERNAME, PASSWORD);
//			transport.sendMessage(msg, msg.getAllRecipients());
//			transport.close();
			
			System.err.println("----------------------");
			System.err.println("Email Sender OK");
			System.err.println("----------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			
			sendEmail();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
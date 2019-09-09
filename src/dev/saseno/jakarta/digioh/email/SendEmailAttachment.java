package dev.saseno.jakarta.digioh.email;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class SendEmailAttachment implements Runnable {

	private static final String USERNAME 	= "";
	private static final String PASSWORD 	= "";	

	private static final String NAME_SENDER   = "Digi Selfie 2019";
	private static final String EMAIL_SENDER  = "digiselfie.id@gmail.com";
	private static final String EMAIL_SUBJECT = "DigiOH Augmented Reality Email Sender";
	
	private static String EMAIL_FOOTER = "\n\n------\nDigi Selfie - 2019"; //additional footer...

	private static final String SMTP_SERVER = "smtp.gmail.com";
	private Properties props = null;
	private Session session = null;	
	
	private String[] toAddress = null;
	private String message = null;
	private String photoPath = null;
	
	public SendEmailAttachment(String toAddress, String message, String photoPath) {
		try {

			props = System.getProperties();
			props.put("mail.debug", "true");
			props.put("mail.smtp.user", USERNAME);
			props.put("mail.smtp.password", PASSWORD);
			props.put("mail.smtp.host", SMTP_SERVER);

			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true"); // TLS
			props.put("mail.smtp.ssl.trust", SMTP_SERVER);	        		    	        
	        
			session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
		
			session.setDebug(true);
			
			this.toAddress 	= toAddress.split(",");
			this.message 	= message;
			this.photoPath 	= photoPath;
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private InternetAddress[] getAddresses(String[] inputAddresses) {
		ArrayList<InternetAddress> result = new ArrayList<>();
		try {
			for (String address : inputAddresses) {
				try {
					result.addAll(Arrays.asList(InternetAddress.parse(address.trim(), false)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toArray(new InternetAddress[0]);
	}

	public void sendEmail() {

		try {

			System.err.println("----------------------");
			System.err.println("Email Sender Start");
			System.err.println("----------------------");

			//System.err.println(">> " + toAddress);
			//System.err.println(">> " + message);
			//System.err.println(">> " + photoPath);			
			
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(EMAIL_SENDER, NAME_SENDER));
			msg.setRecipients(Message.RecipientType.TO, getAddresses(toAddress));
			msg.setSubject(EMAIL_SUBJECT);

			// text
			MimeBodyPart p1 = new MimeBodyPart();
			p1.setText(message + EMAIL_FOOTER);
			
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
            
			Transport transport = session.getTransport("smtps");
			transport.connect(SMTP_SERVER, USERNAME, PASSWORD);
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
			
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
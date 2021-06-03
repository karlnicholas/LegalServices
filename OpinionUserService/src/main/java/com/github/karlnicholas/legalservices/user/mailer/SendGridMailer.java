package com.github.karlnicholas.legalservices.user.mailer;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;

@Service
public class SendGridMailer {
	private Logger logger = LoggerFactory.getLogger(SendGridMailer.class);
//	@Resource(mappedName = "java:jboss/mail/SendGrid")
//	private Session mailSession;
    private final JavaMailSender emailSender;

    public SendGridMailer(JavaMailSender emailSender) {
		super();
		this.emailSender = emailSender;
	}
//	public void sendSimpleMessage(String to, String subject, String text) {
//        SimpleMailMessage message = new SimpleMailMessage(); 
//        message.setFrom("noreply@baeldung.com");
//        message.setTo(to); 
//        message.setSubject(subject); 
//        message.setText(text);
//        emailSender.send(message);
//    }
//	public void sendMessageWithAttachment( String to, String subject, String text, String pathToAttachment) {
//	    
//	    MimeMessage message = emailSender.createMimeMessage();
//	     
//	    try {
//		    MimeMessageHelper helper = new MimeMessageHelper(message, true);
//		    
//		    helper.setFrom("noreply@baeldung.com");
//		    helper.setTo(to);
//		    helper.setSubject(subject);
//			helper.setText(text);
//
//			FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
//		    helper.addAttachment("Invoice", file);
//
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	        
//	    emailSender.send(message);
//	}	
	public boolean sendComment(String email, String comment, Locale locale) {
		return sendGridEmail(new EmailInformation(email, comment, locale), "/xsl/about.xsl");
	}
	
	public boolean sendEmail(ApplicationUser ApplicationUser, String emailResource) {
		return sendGridEmail(new EmailInformation(ApplicationUser), emailResource);
	}

	public boolean sendOpinionReport(ApplicationUser ApplicationUser, List<OpinionView> opinionCases) {
		return sendGridEmail(new EmailInformation(ApplicationUser, opinionCases), "/xsl/opinionreport.xsl");
	}

	public boolean sendSystemReport(ApplicationUser ApplicationUser, Map<String, Long> memoryMap) {
		return sendGridEmail(new EmailInformation(ApplicationUser, memoryMap), "/xsl/systemreport.xsl");
	}
	public boolean sendGridEmail(EmailInformation emailInformation, String emailResource) {
		
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			JAXBContext jc = JAXBContext.newInstance(EmailInformation.class);
			JAXBSource source = new JAXBSource(jc, emailInformation);
			// set up XSLT transformation
			InputStream is = getClass().getResourceAsStream(emailResource);
			StreamSource streamSource = new StreamSource(is);
			StringWriter htmlContent = null;
			try {
				htmlContent = new StringWriter();
				synchronized(this) {
					Transformer t = tf.newTransformer(streamSource);
					// run transformation
					t.transform(source, new StreamResult(htmlContent));
				}
			} catch (TransformerException e) {
				throw new RuntimeException(e); 
			} finally {
				htmlContent.close();
			}

//			MimeMessage message = new MimeMessage(mailSession);
		    MimeMessage message = emailSender.createMimeMessage();

			Multipart multiPart = new MimeMultipart("alternative");

			// Sets up the contents of the email message
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText("");

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlContent.toString(), "text/html; charset=utf-8");

			multiPart.addBodyPart(textPart); // <-- first
			multiPart.addBodyPart(htmlPart); // <-- second

			message.setContent(multiPart);
			message.setFrom(new InternetAddress("no-reply@op-opca.b9ad.pro-us-east-1.openshiftapps.com"));
			message.setSubject("Welcome to Court Opinions");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailInformation.getEmail()));
			// This is not mandatory, however, it is a good
			// practice to indicate the software which
			// constructed the message.
			message.setHeader("X-Mailer", "Court Opinions");

			// Adjust the date of sending the message
			message.setSentDate(new Date());

			// Sends the email
			Transport.send(message);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean sendGridPrint(EmailInformation emailInformation, String emailResource) {
		
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			JAXBContext jc = JAXBContext.newInstance(EmailInformation.class);
			JAXBSource source = new JAXBSource(jc, emailInformation);
//			Marshaller marshaller = jc.createMarshaller();
//			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			marshaller.marshal(emailInformation, System.out);
			// set up XSLT transformation
			InputStream is = getClass().getResourceAsStream(emailResource);
			StreamSource streamSource = new StreamSource(is);
			StringWriter htmlContent = null;
			try {
				htmlContent = new StringWriter();
				synchronized(this) {
					Transformer t = tf.newTransformer(streamSource);
					// run transformation
					t.transform(source, new StreamResult(htmlContent));
				}
			} catch (TransformerException e) {
				throw new RuntimeException(e); 
			} finally {
				htmlContent.close();
			}

			System.out.println(  htmlContent );
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}
}
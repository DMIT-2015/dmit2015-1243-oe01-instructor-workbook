package common.ejb;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.util.logging.Logger;

/**
 * Stateless session bean for sending text, HTML, and attachment-based emails using the Jakarta Mail API.
 *
 * <p>
 * This bean uses the default mail session configured in the WildFly mail subsystem. Before using this bean,
 * you must configure your email account in the WildFly server using the CLI.
 * </p>
 *
 * <h3>📌 WildFly Configuration Steps</h3>
 *
 * <ol>
 *   <li>Start the WildFly server.</li>
 *   <li>Open a terminal and run the following CLI commands to configure your mail session:</li>
 * </ol>
 *
 * {@snippet lang="shell" :
 * $JBOSS_HOME/bin/jboss-cli.sh
 * connect
 * batch
 * /subsystem=mail/mail-session=default/server=smtp/:write-attribute(name=username,value=yourGmailUsername@gmail.com)
 * /subsystem=mail/mail-session=default/server=smtp/:write-attribute(name=password,value=yourGmailAppPassword)
 * /subsystem=mail/mail-session=default/server=smtp/:write-attribute(name=tls,value=true)
 * /subsystem=mail/mail-session=default/:write-attribute(name=from,value=yourGmailUsername@gmail.com)
 * /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp/:write-attribute(name=host,value=smtp.gmail.com)
 * /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp/:write-attribute(name=port,value=587)
 * run-batch
 * reload
 * }
 *
 * <h3>💡 Usage Example</h3>
 *
 * {@snippet :
 * @Inject
 * private EmailSessionBean emailSessionBean;
 *
 * emailSessionBean.sendTextEmail("to@example.com", "Subject", "Body");
 * }
 */

@Stateless
public class EmailSessionBean {

	private static final Logger LOGGER = Logger.getLogger(EmailSessionBean.class.getName());

	@Resource(name = "java:jboss/mail/Default")
	private Session _defaultSession;

	@PostConstruct
	public void init() {
		// For Outlook email you need to enable starttls
//		_defaultSession.getProperties().setProperty("mail.smtp.starttls.enable","true");
	}

	@Asynchronous
	public void sendTextEmail(String mailToAddresses, String mailSubject, String mailBody) {
		try {
			Message mailMessage = new MimeMessage(_defaultSession);
			mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailToAddresses));
			mailMessage.setSubject(mailSubject);
			mailMessage.setText(mailBody);

			Transport.send(mailMessage);
			final String logMessage = String.format("Text email set successfully to %s", mailToAddresses);
			LOGGER.info(logMessage);
		} catch (MessagingException e) {
			final String logMessage = String.format("Failed to send text mail to %s: %s", mailToAddresses, e.getMessage());
			LOGGER.severe(logMessage);
		}
	}

	@Asynchronous
	public void sendHtmlEmail(String mailToAddresses, String mailSubject, String mailBody) {
		try {
			Message mailMessage = new MimeMessage(_defaultSession);
			mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailToAddresses));
			mailMessage.setSubject(mailSubject);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(mailBody, "text/html");
			multipart.addBodyPart(htmlPart);
			mailMessage.setContent(multipart);

			Transport.send(mailMessage);
			final String logMessage = String.format("HTML email set successfully to %s", mailToAddresses);
			LOGGER.info(logMessage);
		} catch (MessagingException e) {
			final String logMessage = String.format("Failed to send text mail to %s: %s", mailToAddresses, e.getMessage());
			LOGGER.severe(logMessage);
		}
	}

	@Asynchronous
	public void sendHtmlEmailWithAttachments(String mailToAddresses, String mailSubject, String mailBody, String[] files) {
		try {
			Message mailMessage = new MimeMessage(_defaultSession);
			mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailToAddresses));
			mailMessage.setSubject(mailSubject);
			Multipart multipart = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(mailBody, "text/html");
			multipart.addBodyPart(htmlPart);

			for (String filename : files) {
				if (!filename.isBlank()) {
					File currentFile = new File(filename);
					if (!currentFile.exists() || !currentFile.canRead()) {
						LOGGER.warning(String.format("Skipping unreadable or missing file  %s", filename));
						continue;
					}
					MimeBodyPart filePart = new MimeBodyPart();
					DataSource fileSource = new FileDataSource(filename);
					filePart.setDataHandler(new DataHandler(fileSource));
					File file = new File(filename);
					filePart.setFileName(file.getName());
					multipart.addBodyPart(filePart);
				}
			}

			mailMessage.setContent(multipart);
			Transport.send(mailMessage);
			final String logMessage = String.format("HTML email with attachment sent to %s", mailToAddresses);
			LOGGER.info(logMessage);
		} catch (MessagingException e) {
			final String logMessage = String.format("Failed to send email with attachments to %s: %s", mailToAddresses, e.getMessage());
			LOGGER.severe(logMessage);
		}
	}

}
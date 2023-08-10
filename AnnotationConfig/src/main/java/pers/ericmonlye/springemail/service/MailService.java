package pers.ericmonlye.springemail.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MailService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private ZoneId zoneId;
	
	/* constructor */
	public MailService() {
		this.zoneId = ZoneId.systemDefault();
	}
	
	/* JavaBean API */
	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
		
		return;
	}
	public String getTime() {
		return ZonedDateTime.now(this.zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
	}
	
	/* service API */
	public void sendRegisterMail(User user) {
		log.info("Welcome, {}! Your account is successfully registered. ", user.getName());
	}
	public void sendLoginMail(User user) {
		log.info("{} successfully logged in at {}", user.getName(), getTime());
	}
	public void sendLogoutMail(User user) {
		log.info("{} successfully logged out at {}", user.getName(), getTime());
	}
	public void sendDeleteMail(User user) {
		log.info("Your account {} has been successfully deleted from our database. ", user.getEmail());
	}
	public void sendEditPasswordMail(User user) {
		log.info("The password of your account {} has changed. Please log in again. ", user.getEmail());
	}
}

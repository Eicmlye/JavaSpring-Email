package pers.ericmonlye.springemail.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		return ZonedDateTime.now(this.zoneId).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
	
	/* service API */
	public void sendRegisterMail(User user) {
		log.info("Welcome, {}! You just registered for our site. ", user.getName());
	}
	public void sendLoginMail(User user) {
		log.info("{} successfully logged in at {}", user.getName(), getTime());
	}
	public void sendLogoutMail(User user) {
		log.info("{} successfully logged out at {}", user.getName(), getTime());
	}
}

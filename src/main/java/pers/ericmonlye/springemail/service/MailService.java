package pers.ericmonlye.springemail.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private ZoneId zoneId = ZoneId.systemDefault();
	
	/* constructor */
	public MailService() { /* Default constructor for IoC */
		/* DO NOTHING */
	}
	
	/* JavaBean API */
	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
		
		return;
	}
	public String getTime() {
		return ZonedDateTime.now(this.zoneId).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
	
	/* mail services */
	public void sendLoginMail(User user) {
		log.info("{} successfully logged in at {}", user.getName(), getTime());
	}
	public void sendRegisterMail(User user) {
		log.info("Welcome, {}! You just registered for our site. ", user.getName());
	}
}

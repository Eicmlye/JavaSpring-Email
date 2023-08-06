package pers.ericmonlye.hellospring.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private ZoneId zoneId = ZoneId.systemDefault();
	
	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
		
		return;
	}
	public String getTime() {
		return ZonedDateTime.now(this.zoneId).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
}

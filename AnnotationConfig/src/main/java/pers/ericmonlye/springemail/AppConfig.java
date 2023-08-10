package pers.ericmonlye.springemail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import pers.ericmonlye.springemail.service.*;

@Configuration
@ComponentScan
public class AppConfig {
	final static Logger log = LoggerFactory.getLogger(AppConfig.class);

	public static void main(String[] argv) {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
			UserService userService = context.getBean(UserService.class);
			
			userService.login("bob@example.com", "password");
			userService.logout("bob@example.com");
		}
		
		return;
	}
}

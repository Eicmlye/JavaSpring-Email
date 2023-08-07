package pers.ericmonlye.springemail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pers.ericmonlye.springemail.service.*;

public class Main {
	final static Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] argv) {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml")) {
			UserService userService = context.getBean(UserService.class);
			
			log.info(userService.getUser(1).getName());
		}
		
		return;
	}
}

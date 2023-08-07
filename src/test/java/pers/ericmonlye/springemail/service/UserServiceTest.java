package pers.ericmonlye.springemail.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

class UserServiceTest {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	void test() {
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml")) {
			UserService userService = context.getBean(UserService.class);
			
			assertEquals("Tom", userService.getUser(3).getName());
			assertEquals("Eric", userService.register("eric@example.com", "password", "Eric").getName());
			assertEquals(4, userService.getUser(4).getId());
			assertThrows(RuntimeException.class, () -> {
				userService.register("bob@example.com", "password", "Bobi");
			});
			assertEquals("Eric", userService.login("eric@example.com", "password").getName());
			assertThrows(RuntimeException.class, () -> {
				userService.login("alice@example.com", "paswod");
			});
			assertThrows(RuntimeException.class, () -> {
				userService.login("tom@example.cn", "password");
			});
		}
	}

}

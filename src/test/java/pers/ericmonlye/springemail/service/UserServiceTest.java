package pers.ericmonlye.springemail.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
			
			userService.login("bob@example.com", "password");
			assertEquals(true, userService.isLogin(1));
			userService.logout("bob@example.com");
			
			assertEquals("Tom", userService.getUser(3).getName());
			
			assertThrows(RuntimeException.class, () -> {
				userService.register("bob@example.com", "password", "Bobi");
			});
			assertThrows(RuntimeException.class, () -> {
				userService.login("alice@example.com", "paswod");
			});
			assertThrows(RuntimeException.class, () -> {
				userService.login("tom@example.cn", "password");
			});

			assertEquals("Eric", userService.register("eric@example.com", "password", "Eric").getName());
			assertEquals(4, userService.getUser(4).getId());
			assertEquals("Eric", userService.login("eric@example.com", "password").getName());
			userService.logout("eric@example.com");
		}
	}
}

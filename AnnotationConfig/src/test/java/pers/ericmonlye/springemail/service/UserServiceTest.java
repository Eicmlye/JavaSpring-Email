package pers.ericmonlye.springemail.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
class UserServiceTest {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	void test() {
		try (
				AnnotationConfigApplicationContext context = 
					new AnnotationConfigApplicationContext(UserServiceTest.class)
				) 
		{
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
			userService.delete("eric@example.com");
			
			userService.register("jim@example.com", "password", "Jim");
			userService.editPassword(userService.login("jim@example.com", "password"), "password", "newpassword");
			assertEquals("Jim", userService.login("jim@example.com", "newpassword").getName());
			userService.editPassword(userService.getUser("jim@example.com"), "newpassword", "password");
			userService.login("jim@example.com", "password");
			userService.delete("jim@example.com");
		}
	}
}

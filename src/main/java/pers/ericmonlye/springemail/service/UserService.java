package pers.ericmonlye.springemail.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private MailService mailService;
	private DataSource dataSource;
	
	/* registered user list cache */
	private List<User> users = new ArrayList<>(List.of(
			new User(1, "bob@example.com", "password", "Bob"),
			new User(2, "alice@example.com", "password", "Alice"),
			new User(3, "tom@example.com", "password", "Tom")
			));
	
	/* constructors */
	/*
	 * JavaBean dependency is defined by Beans.xml.
	 * Use IoC containers to initialize UserService.
	 * Setter DI method is used.
	 *
	 * ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
	 * UserService userService = context.getBean(UserService.class);
	 */
	public UserService() { /* Default constructor for IoC */
		/* DO NOTHING */
	}
	
	/* JavaBean API */
	public void setMailService(MailService mailService) { // Dependency Injection;
		this.mailService = mailService;
		
		return;
	}
	public void setDataSource(DataSource dataSource) { // Dependency Injection;
		this.dataSource = dataSource;
		
		return;
	}
	public User getUser(long id) {
		return this.users.stream().filter(user -> user.getId() == id).findFirst().orElseThrow();
	}
	
	/* user operations */
	public User register(String email, String password, String name) {
		users.forEach(user -> {
			if (user.getEmail().equalsIgnoreCase(email)) {
				log.warn("Email {} already exists. ", email);
				throw new RuntimeException("Email already exists. ");
			}
		});
		
		User user = new User(	users.stream().mapToLong(u -> u.getId()).max().getAsLong() + 1,
								email, password, name);
		users.add(user);
		mailService.sendRegisterMail(user);
		
		return user;
	}
	public User login(String email, String password) {
		for (User user : users) {
			if (user.getEmail().equalsIgnoreCase(email) && user.checkPassword(password)) {
				mailService.sendLoginMail(user);
				
				return user;
			}
		}
		
		log.warn("Wrong email address or password. ");
		throw new RuntimeException("Wrong email address or password. ");
	}
}
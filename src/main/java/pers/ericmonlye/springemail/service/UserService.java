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
	private List<User> loginUsers = new ArrayList<User>();
	
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
	public boolean isLogin(String email) {
		return loginUsers.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
	}
	public boolean isLogin(long id) {
		return loginUsers.stream().anyMatch(u -> u.getId() == id);
	}
	
	/* service API */
	public User getUser(String email) {
		if (isLogin(email)) {
			return loginUsers.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).toList().get(0);
		}

		if (dataSource.selectEmail().stream().anyMatch(e -> e.equalsIgnoreCase(email))) {
			return dataSource.selectByEmail(email);
		}

		log.warn("No existing account found. ");
		throw new RuntimeException("No existing account found. ");
	}
	public User getUser(long id) {
		if (isLogin(id)) {
			return loginUsers.stream().filter(u -> u.getId() == id).toList().get(0);
		}

		if (dataSource.selectId().stream().anyMatch(i -> i.equals(id))) {
			return dataSource.selectById(id);
		}

		log.warn("No existing account found. ");
		throw new RuntimeException("No existing account found. ");
	}
	public User register(String email, String password, String name) {
		dataSource.selectEmail().forEach(e -> {
			if (e.equalsIgnoreCase(email)) {
				log.warn("This email has already registered for an account. ");
				throw new RuntimeException("This email has already registered for an account. ");
			}
		});
		
		User user = new User(	dataSource.selectMaxId() + 1,
								email, password, name);
		dataSource.insertUser(user, password);
		mailService.sendRegisterMail(user);
		
		return user;
	}
	public User login(String email, String password) {
		if (isLogin(email)) {
			log.warn("This account has already logged in. ");
			throw new RuntimeException("This account has already logged in. ");
		}
		
		if (!dataSource.selectEmail().stream().anyMatch(e -> e.equalsIgnoreCase(email))) {
			log.warn("This email is not registered yet. ");
			throw new RuntimeException("This email is not registered yet. ");
		}
		
		if (!dataSource.selectByEmail(email).checkPassword(password)) {
			log.warn("Password incorrect. ");
			throw new RuntimeException("Password incorrect. ");
		}
		
		User user = dataSource.selectByEmail(email);
		loginUsers.add(user);
		mailService.sendLoginMail(user);
		
		return user;
	}
	public void logout(String email) {
		if (!isLogin(email)) {
			log.warn("This account has not logged in yet. ");
			throw new RuntimeException("This account has not logged in yet. ");
		}
		
		User user = loginUsers.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).toList().get(0);
		loginUsers.remove(user);
		mailService.sendLogoutMail(user);
		
		return;
	}
}
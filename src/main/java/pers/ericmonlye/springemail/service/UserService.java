package pers.ericmonlye.springemail.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public boolean isLogin(long id) {
		return loginUsers.stream().anyMatch(u -> u.getId() == id);
	}
	public boolean isLogin(String email) {
		return loginUsers.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
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
	public void delete(String email) {
		if (!isLogin(email)) {
			log.warn("Please log in before you delete the account. ");
			throw new RuntimeException("Please log in before you delete the account. ");
		}
		
		/* 
		 * Now that the account has logged in, this must be an existing 
		 * account, and the current user knows the correct password.
		 */
		User user = dataSource.selectByEmail(email);
		loginUsers.remove(user);
		dataSource.deleteUser(user);
		mailService.sendDeleteMail(user);
		
		return;
	}
	public User editPassword(User user) {
		String password = "";
		
		/* check password again */
		System.out.printf("Enter password for %s: ", user.getEmail());
		password = readToken();
		
		if (!user.checkPassword(password)) {
			throw new RuntimeException("Current password incorrect. ");
		}

		System.out.printf("Enter new password for %s: ", user.getEmail());
		password = readToken();
		
		loginUsers.get(loginUsers.indexOf(user)).setPassword(password);
		mailService.sendEditPasswordMail(user);
		logout(user.getEmail());
		dataSource.updateUser(user, password);
		
		return user;
	}
	public void editPassword(User user, String prevPassword, String newPassword) {
		/* check password again */
		if (!user.checkPassword(prevPassword)) {
			throw new RuntimeException("Current password incorrect. ");
		}

		System.out.printf("Enter new password for %s: ", user.getEmail());
		
		loginUsers.get(loginUsers.indexOf(user)).setPassword(newPassword);
		mailService.sendEditPasswordMail(user);
		logout(user.getEmail());
		dataSource.updateUser(user, newPassword);
		
		return;
	}

	private boolean isWhitespace(char ch) {
		/*
		 * Lines may end with "\r\n", where '\r' is '\u000D'.
		 */
		return (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
	}
	private String readToken() {
		String ret = "";
		char cache = '\0';
		
		try (BufferedReader buf = new BufferedReader(new NoCloseInputStreamReader(System.in))) {
			try {
				while (!isWhitespace(cache = (char)buf.read())) {
					ret += cache;
				}
				if (cache != '\n') {
					buf.readLine(); // skip remaining characters;
				}
			}
			catch (IOException e) {
//				log.warn("Read failed. ");
				log.warn(e.getMessage());
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (IOException e) {
			log.warn("Reader initalization failed. ");
			throw new RuntimeException(e.getMessage());
		}
		
		return ret;
	}
	/** 
	 * 关于标准输入流{@code System.in}被自动关闭后无法打开的解决方法
	 * https://blog.csdn.net/weixin_44843824/article/details/111778856
	 */
	private class NoCloseInputStreamReader extends InputStreamReader{
		public NoCloseInputStreamReader(InputStream in) {
			super(in);
		}
		
		public void close() throws IOException {
			// DO NOTHING;
		}
	}
}
package pers.ericmonlye.springemail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private long id;
	private String email;
	private String password;
	private String name;
	
	/* constructors */
	public User(long id, String email, String password, String name) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
	}

	/* JavaBean API */
	public long getId() {
		return this.id;
	}
	public String getEmail() {
		return this.email;
	}
	public String getName() {
		return this.name;
	}

	public boolean checkPassword(String inword) {
		return this.password.equals(inword);
	}
}

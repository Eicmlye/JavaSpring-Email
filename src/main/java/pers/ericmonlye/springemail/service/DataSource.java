package pers.ericmonlye.springemail.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSource {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/* JDBC info for MySQL */
	private String hostname;
	private String port;
	private String dbname;
	private String dburl;
	private String dbuser;
	private String dbpassword;
	private final String tablename;
	
	private long maxId;
	private boolean maxIdUpdated;
	
	/* constructors */
	public DataSource() { 
		this.hostname = "localhost";
		this.port = "3306";
		this.dbname = "springemail";
		
		/* URL format "jdbc:mysql://<hostname>:<port>/<dbname>" */
		this.dburl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbname; 
		this.dbuser = "Eric";
		this.dbpassword = "";
		setDbpassword();
		
		this.tablename = "Users";
		
		this.maxId = 0;
		this.maxIdUpdated = false;
	}
	
	/* JavaBean API */
	public void setDbpassword() {
		System.out.printf("Enter password for %s: ", dbuser);
		dbpassword = readToken();
		
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
	
	/* service API */
	public List<Long> selectId() {
		List<Long> ret = new ArrayList<Long>();
		
		// Request for connection;
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
			try (PreparedStatement ps = conn.prepareStatement(
					"SELECT id FROM " + tablename
					)) 
			{
				try (ResultSet result = ps.executeQuery()){
					while (result.next()) {
						ret.add(result.getLong("id"));
					}
				}
				catch (SQLException e) {
					log.warn("Execution failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Preparation failure. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException(e.getMessage());
		}
		
		return ret;
	}
	public List<String> selectEmail() {
		return selectAttr("email");
	}
	public List<String> selectName() {
		return selectAttr("name");
	}

	public User selectById(long id) {
		User ret = null;

		// Request for connection;
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
			try (PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM " + tablename + " WHERE id = ?"
					)) 
			{
				ps.setObject(1, id); // index begins from 1;
				try (ResultSet result = ps.executeQuery()){
					while (result.next()) {
						ret = new User(	result.getLong("id"), 
										result.getString("email"),
										result.getString("password"),
										result.getString("name")
										);
					}
				}
				catch (SQLException e) {
					log.warn("Execution failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Preparation failure. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException(e.getMessage());
		}
		
		return ret;
	}
	public User selectByEmail(String email) {
		return selectUser("email", email);
	}
	public User selectByName(String name) {
		return selectUser("name", name);
	}
	
	public long selectMaxId() {
		if (!maxIdUpdated) {
			// Request for connection;
			try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
				try (PreparedStatement ps = conn.prepareStatement(
						"SELECT MAX(id) AS id FROM " + tablename
						)) 
				{
					try (ResultSet result = ps.executeQuery()){
						while (result.next()) {
							maxId = result.getLong("id");
						}
					}
					catch (SQLException e) {
						log.warn("Execution failure. ");
						throw new RuntimeException(e.getMessage());
					}
				}
				catch (SQLException e) {
					log.warn("Preparation failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Connection failure. ");
				throw new RuntimeException(e.getMessage());
			}
			
			maxIdUpdated = true;
		}
		
		return maxId;
	}

	public void insertUser(User user, String password) {
		// Request for connection;
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
			try (PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO " + tablename + " VALUES (?,?,?,?)"
					)) 
			{
				ps.setObject(1, user.getId()); // index begins from 1;
				ps.setObject(2, user.getEmail());
				ps.setObject(3, password);
				ps.setObject(4, user.getName());
				try {
					ps.executeUpdate();
				}
				catch (SQLException e) {
					log.warn("Execution failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Preparation failure. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException(e.getMessage());
		}
		
		maxIdUpdated = false;
		
		return;
	}
	public void updateUser(User user, String password) {
		// Request for connection;
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
			try (PreparedStatement ps = conn.prepareStatement(
					"UPDATE " + tablename + " SET password = ?, name = ? WHERE email = ?"
					)) 
			{
				ps.setObject(1, password); // index begins from 1;
				ps.setObject(2, user.getName());
				ps.setObject(3, user.getEmail());
				try {
					ps.executeUpdate();
				}
				catch (SQLException e) {
					log.warn("Execution failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Preparation failure. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException(e.getMessage());
		}
		
		maxIdUpdated = false;
		
		return;
	}
	public void deleteUser(User user) {
		// Request for connection;
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
			try (PreparedStatement ps = conn.prepareStatement(
					"DELETE FROM " + tablename + " WHERE id = ?"
					)) 
			{
				ps.setObject(1, user.getId()); // index begins from 1;
				try {
					ps.executeUpdate();
				}
				catch (SQLException e) {
					log.warn("Execution failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Preparation failure. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException(e.getMessage());
		}
		
		maxIdUpdated = false;
		
		return;
	}
	
	private String str2Attr(String attr) {
		switch(attr) {
		case "email":
		case "name":
			return attr;
		case "id":
		case "password":
			log.warn("Attribute \"{}\" is not accessible. ", attr);
			throw new RuntimeException("Attribute \"" + attr + "\" is not accessible. ");
		default:
			log.warn("Unknown attribute \"{}\". ", attr);
			throw new RuntimeException("Unknown attribute " + attr + ". ");
		}
	}
	private String selectBy(String attr) {
		return "SELECT * FROM " + tablename + " WHERE " + str2Attr(attr) + " = ?";
	}
	private User selectUser(String attr, String attrVal) {
		User ret = null;

		// Request for connection;
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
			try (PreparedStatement ps = conn.prepareStatement(selectBy(attr))) 
			{
				ps.setObject(1, attrVal); // index begins from 1;
				try (ResultSet result = ps.executeQuery()){
					while (result.next()) {
						ret = new User(	result.getLong("id"), 
										result.getString("email"),
										result.getString("password"),
										result.getString("name")
										);
					}
				}
				catch (SQLException e) {
					log.warn("Execution failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Preparation failure. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException(e.getMessage());
		}
		
		return ret;
	}
	private List<String> selectAttr(String attr) {
		List<String> ret = new ArrayList<String>();
		
		// Request for connection;
		try (Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword)) {
			try (PreparedStatement ps = conn.prepareStatement(
					"SELECT " + str2Attr(attr) + " FROM " + tablename
					)) 
			{
				try (ResultSet result = ps.executeQuery()){
					while (result.next()) {
						ret.add(result.getString(attr));
					}
				}
				catch (SQLException e) {
					log.warn("Execution failure. ");
					throw new RuntimeException(e.getMessage());
				}
			}
			catch (SQLException e) {
				log.warn("Preparation failure. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException(e.getMessage());
		}
		
		return ret;
	}
}
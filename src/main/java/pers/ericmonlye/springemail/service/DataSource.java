package pers.ericmonlye.springemail.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSource {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/* JDBC info for MySQL */
	private String url = "jdbc:mysql://localhost:3306/jdbctest";
	private String user = "root";
	private String password = "";
	
	public void setPassword() {
		try (Scanner scanner = new Scanner(System.in)) {
			if (scanner.hasNextLine()) {
				System.out.printf("Enter password for %s:", user);
				this.password = scanner.nextLine();
			}
		}
	}
	
	public void connect() {
		// Request for connection;
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			try (PreparedStatement ps = conn.prepareStatement("")) {
//				ps.setObject(1, ""); // index begins from 1;
				try (ResultSet result = ps.executeQuery()){
					while (result.next()) {
						
					}
				}
			}
		}
		catch (SQLException e) {
			log.warn("Connection failure. ");
			throw new RuntimeException("Connection failure. ");
		}
	}
}

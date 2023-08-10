package pers.ericmonlye.springemail.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 关于标准输入流{@code System.in}被自动关闭后无法打开的解决方案
 * https://blog.csdn.net/weixin_44843824/article/details/111778856
 */
public class NoCloseInputStreamReader extends InputStreamReader {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/* constructor */
	public NoCloseInputStreamReader() {
		super(System.in);
	}
	public NoCloseInputStreamReader(InputStream in) {
		super(in);
	}
	
	/* Closable interface requirement */
	public void close() throws IOException {
		// DO NOTHING;
	}

	private boolean isWhitespace(char ch) { // Lines may end with "\r\n", where '\r' is '\0u000D' (ASCII-13);
		return (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
	}
	public String readToken() {
		String ret = "";
		char cache = '\0';
		
		try (BufferedReader buf = new BufferedReader(this)) {
			try {
				while (!isWhitespace(cache = (char)buf.read())) {
					ret += cache;
				}
				if (cache != '\n') {
					buf.readLine(); // skip remaining characters;
				}
			}
			catch (IOException e) {
				log.warn("Read failed. ");
				throw new RuntimeException(e.getMessage());
			}
		}
		catch (IOException e) {
			log.warn("Reader initalization failed. ");
			throw new RuntimeException(e.getMessage());
		}
		
		return ret;
	}
}

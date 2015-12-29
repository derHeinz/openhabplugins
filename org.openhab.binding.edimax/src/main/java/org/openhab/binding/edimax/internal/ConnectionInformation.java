package org.openhab.binding.edimax.internal;

/**
 * Information about a connect.
 * @author Heinz
 *
 */
public class ConnectionInformation {

	private String username;
	private String password;

	private String url;
	private int port;
	
	public ConnectionInformation(String user, String pw, String target, int portnum) {
		username = user;
		password = pw;
		url = target;
		port = portnum;
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getUrl() {
		return url;
	}
	public int getPort() {
		return port;
	}


}

package org.openhab.binding.edimax.internal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.DatatypeConverter;

import org.openhab.binding.edimax.internal.commands.AbstractCommand;
import org.openhab.binding.edimax.internal.commands.GetCurrent;
import org.openhab.binding.edimax.internal.commands.GetInternet;
import org.openhab.binding.edimax.internal.commands.GetNowPowerCommandCompound;
import org.openhab.binding.edimax.internal.commands.GetPower;
import org.openhab.binding.edimax.internal.commands.GetState;
import org.openhab.binding.edimax.internal.commands.GetMAC;
import org.openhab.binding.edimax.internal.commands.SetState;

public class HTTPSend {
	
	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF8\"?>\r\n";

	private static final String defaultUser = "admin";
	private static final String defaultPassword = "1234";

	protected static final int PORT = 10000;

	private static String completeURL(String anIp) {
		return "http://" + anIp;
	}
	
	private String password;
	
	public HTTPSend() {
		this(defaultPassword);
	}
	
	public HTTPSend(String aPw) {
		password = aPw;
	}

	/**
	 * Switch to.
	 * 
	 * @param anIp
	 * @param newState
	 * @return
	 * @throws IOException
	 */
	public Boolean switchState(String anIp, Boolean newState) throws IOException {
		String completeUrl = completeURL(anIp);
		ConnectionInformation ci = new ConnectionInformation(defaultUser, password, completeUrl, PORT);

		SetState setS = new SetState(newState);
		return setS.executeCommand(ci);
	}

	/**
	 * Returns state for device with given IP.
	 * 
	 * @param anIp
	 * @return
	 * @throws IOException
	 */
	public Boolean getState(String anIp) throws IOException {
		String completeUrl = completeURL(anIp);
		ConnectionInformation ci = new ConnectionInformation(defaultUser, password, completeUrl, PORT);

		GetState getS = new GetState();
		return getS.executeCommand(ci);
	}

	/**
	 * Receive the MAC address.
	 * 
	 * @param anIp
	 * @return
	 * @throws IOException
	 */
	public String getMAC(String anIp) throws IOException {
		String completeUrl = completeURL(anIp);
		ConnectionInformation ci = new ConnectionInformation(defaultUser, password, completeUrl, PORT);

		GetMAC getC = new GetMAC();
		return getC.executeCommand(ci);
	}

	/**
	 * Returns the current.
	 * 
	 * @param anIp
	 * @return
	 * @throws IOException
	 */
	public BigDecimal getCurrent(String anIp) throws IOException {
		String completeUrl = completeURL(anIp);
		ConnectionInformation ci = new ConnectionInformation(defaultUser, password, completeUrl, PORT);

		GetCurrent getC = new GetCurrent();
		return getC.executeCommand(ci);
	}

	/**
	 * Gets the actual power.
	 * 
	 * @param anIp
	 * @return
	 * @throws IOExceptionif
	 *             (mac != null) { // found a device! Device d = new Device();
	 *             d.ip = portScanUsage.getIp(); d.mac = mac; discovered.add(d);
	 *             }
	 */
	public BigDecimal getPower(String anIp) throws IOException {
		String completeUrl = completeURL(anIp);
		ConnectionInformation ci = new ConnectionInformation(defaultUser, password, completeUrl, PORT);

		GetPower getC = new GetPower();
		return getC.executeCommand(ci);
	}

	public static String executePost(String targetURL, int targetPort, String targetURlPost, String urlParameters,
			String username, String password) throws IOException {
		String complete = targetURL + ":" + targetPort + "/" + targetURlPost;

		HttpURLConnection connection = null;
		try {
			// Create connection
			URL url = new URL(complete);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));

			String userpass = username + ":" + password;
			String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
			connection.setRequestProperty("Authorization", basicAuth);

			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(urlParameters.getBytes());
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if
															// not Java 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		String url = "http://192.168.2.105";

		ConnectionInformation ci = new ConnectionInformation(defaultUser, defaultPassword, url, PORT);

		GetNowPowerCommandCompound comp = new GetNowPowerCommandCompound();
		// comp.addCommand(new GetPower());
		// comp.addCommand(new GetCurrent());
		// comp.addCommand(new GetEnergyDay());

		AbstractCommand cmd = new GetInternet();
		Object executeCommand = cmd.executeCommand(ci);

		if (executeCommand instanceof Map) {
			Map m = (Map) executeCommand;
			for (Object e : m.entrySet()) {
				Object key = ((Entry) e).getKey();
				Object val = ((Entry) e).getValue();
				System.out.println(key + " " + val);
			}
		} else {
			System.out.println(executeCommand);
		}
	}

}

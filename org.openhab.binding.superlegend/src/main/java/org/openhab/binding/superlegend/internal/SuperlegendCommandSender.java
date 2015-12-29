package org.openhab.binding.superlegend.internal;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class SuperlegendCommandSender {

	/**
	 * Port on device side.
	 */
	private static final int port = 5577;

	/**
	 * Fade in smoothly to the color which is already set.
	 */
	private static final byte[] SWITCH_ON = new byte[] { 0x71, 0x23, 0x0f, (byte) 0xa3 };;

	/**
	 * Shut down smoothly (fade out). You have to leave the TCP connection open
	 * for the bulb to answer, otherwise it won't shut down.
	 */
	private static final byte[] SWITCH_OFF = new byte[] { 0x71, 0x24, 0x0f, (byte) 0xa4 };

	/**
	 * Send to receive information.
	 */
	private static final byte[] RECEIVE_INFOS = new byte[] { (byte) 0x81, (byte) 0x8a, (byte) 0x8b, (byte) 0x96 };

	/**
	 * Length of packet that is received for infos.
	 */
	private static final int RECEIVE_INFOS_LENGTH = 14;

	/**
	 * Creates a color packet.
	 * 
	 * @param aColor
	 * @return
	 */
	private static byte[] colorPaket(Color aColor) {
		/**
		 * 0x31, X, Y, Z, 0, f0, 0f, J X = r, Y = g, Z = b J is a checksum
		 */
		byte r = (byte) aColor.getRed();
		byte g = (byte) aColor.getGreen();
		byte b = (byte) aColor.getBlue();

		// FYI: 0x31 + 0xf0 + 0x0f = 48
		byte c = (byte) (48 + r + g + b); // billig algorithmus fuer checksum
											// byte

		return new byte[] { 0x31, r, g, b, 0x00, (byte) 0xf0, 0x0f, c };
	}

	private void sendCommand(String deviceIp, byte[] someBytes) throws UnknownHostException, IOException {
		Socket socket = new Socket(deviceIp, port);

		try {
			writeBytes(socket, someBytes);
		} catch (IOException e) {
			throw e;
		} finally {
			socket.close();
		}
	}

	/**
	 * Receivale buffer.
	 */
	private byte[] buffer = new byte[1024];

	/**
	 * Read some bytes that come in through the stream.
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	protected byte[] readIncomingBytes(final InputStream inputStream, int expectedLength) throws IOException {

		int offset = 0;

		// wait at least 2 seconds if nothing happens.
		long bufferSeconds = TimeUnit.SECONDS.toNanos(2);
		long startTime = System.nanoTime();

		while ((System.nanoTime() - startTime) < bufferSeconds) {
			if (inputStream.available() > 0) {
				int read = inputStream.read(buffer, offset, buffer.length - offset);
				offset += read;
			} else {
				// wait some time to receive next.
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// abort the wait time if enough received.
			if (expectedLength != 0 && expectedLength == offset) {
				break;
			}

		}

		// copy to the result
		byte[] returnBuffer = new byte[offset];
		System.arraycopy(buffer, 0, returnBuffer, 0, offset);
		return returnBuffer;

	}

	private void writeBytes(Socket aSocket, byte[] someBytes) throws IOException {
		aSocket.getOutputStream().write(someBytes);
		aSocket.getOutputStream().flush();
	}

	/**
	 * Returns information about the given device.
	 * 
	 * @param deviceIp
	 * @return
	 * @throws IOException
	 */
	public SuperlegendDeviceState getInformation(String deviceIp) throws IOException {
		Socket socket = null;
		try {
			socket = new Socket(deviceIp, port);
			InputStream inputStream = socket.getInputStream();
			writeBytes(socket, RECEIVE_INFOS);
			byte[] bytesReceived = readIncomingBytes(inputStream, RECEIVE_INFOS_LENGTH);
			return parseDeviceState(bytesReceived);
		} finally {
			// whatever happens close the socket
			if (socket != null) {
				socket.close();
			}
		}
	}

	private SuperlegendDeviceState parseDeviceState(byte[] bytesReceived) {

		// some things to write out:
		// System.out.println("\nRead:");
		// for (byte b : bytesReceived) {
		// System.out.print(b + ",");
		// }

		// check unknown values
		if (RECEIVE_INFOS_LENGTH != bytesReceived.length) {
			throw new RuntimeException("Unexpected Failure, did not receive enough information.");
		}
		assertBytesEquals((byte) -127, bytesReceived[0]);
		assertBytesEquals((byte) 68, bytesReceived[1]);
		// 2 declares whether on or off
		boolean on;
		if (35 == bytesReceived[2]) {
			on = true;
		} else if (36 == bytesReceived[2]) {
			on = false;
		} else {
			throw new RuntimeException("Unknown state.");
		}

		// those values are still unknown - however they can be changed by the
		// app so they probably mean something I still don't know.
		// assertBytesEquals((byte) 97, bytesReceived[3]);
		// assertBytesEquals((byte) 33, bytesReceived[4]);
		// assertBytesEquals((byte) 16, bytesReceived[5]);

		// next values are int values of r, g, b
		int r = bytesReceived[6];
		if (r < 0) { // byte to int
			r += 256;
		}
		int g = bytesReceived[7];
		if (g < 0) { // byte to int
			g += 256;
		}
		int b = bytesReceived[8];
		if (b < 0) { // byte to int
			b += 256;
		}
		assertBytesEquals((byte) 0, bytesReceived[9]);
		assertBytesEquals((byte) 4, bytesReceived[10]);
		assertBytesEquals((byte) 0, bytesReceived[11]);
		assertBytesEquals((byte) -16, bytesReceived[12]);

		byte calculatedChecksum = calculateChecksum(bytesReceived);
		// check checksum byte
		assertBytesEquals(calculatedChecksum, bytesReceived[13], "Checksum unsuccessful.");

		return new SuperlegendDeviceState(on, r, g, b);
	}

	/**
	 * Uses the first bytes in order to calculate the last byte. Technically it
	 * takes all elements except the last to calculate the checksum.
	 * 
	 * @param data
	 * @return
	 */
	private byte calculateChecksum(byte[] data) {
		byte calculatedChecksum = 0;
		for (int i = 0; i < data.length - 1; i++) {
			calculatedChecksum += data[i];
		}
		return calculatedChecksum;
	}

	private void assertBytesEquals(byte a, byte b, String anErrorHint) {
		if (a != b) {
			String error = "Unexpected Failure " + a + " does not equal " + b + ".";
			if (anErrorHint != null) {
				error += (" " + anErrorHint);
			}
			throw new RuntimeException(error);
		}
	}

	private void assertBytesEquals(byte a, byte b) {
		assertBytesEquals(a, b, null);
	}

	/**
	 * Switches color of the device.
	 * 
	 * @param device
	 * @param color
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void switchColor(String deviceIp, Color color) throws IOException {
		sendCommand(deviceIp, colorPaket(color));
	}

	/**
	 * Switch on.
	 * 
	 * @param device
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void switchOn(String deviceIp) throws IOException {
		sendCommand(deviceIp, SWITCH_ON);
	}

	/**
	 * Switch off.
	 * 
	 * @param device
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void switchOff(String deviceIp) throws IOException {
		sendCommand(deviceIp, SWITCH_OFF);
	}

	/*
	 * There are some stored procedures: all have the same pattern like:<br>
	 * 0x61, A, B, 0x0f, C<br>
	 * Where A is a code for the function.<br>
	 * B is the frequency a.k.a speed.<br>
	 * C is the checksum calculated. Following codes are present:
	 * 0x24 - hold current color<br>
	 * 
	 * 0x25 - Seven color crossfeed: RED,YELLOW,GREEN,CYAN,BLUE,PINK,WHITE<br>
	 * 0x26 - red blink / gradual change <br>
	 * 0x27 - green blink  / gradual change <br>
	 * 0x28 - blue blink  / gradual change<br>
	 * 0x29 - yellow blink  / gradual change<br>
	 * 0x30 - flashlight: red, green, blue, magenta, cyan, yellow, white<br>
	 * 0x31 - flashlight: red<br>
	 * 0x32 - flashlight: green<br>
	 * 0x33 - flashlight: blue<br>
	 * 0x34 - flashlight: yellow<br>
	 * 0x35 - flashlight: cyan<br>
	 * 0x36 - flashlight: magenta<br>
	 * 0x37 - flashlight: white<br>
	 * 
	 * 0x38 - Seven color jumping: RED,YELLOW,GREEN,CYAN,BLUE,PINK,WHITE <br>
	 * 0x39 - flashlight: white<br>
	 * 0x40 - flashlight: white<br>
	 */

	public static void main(String[] args) throws IOException {
		String deviceIp = "192.168.2.109";
		byte[] data = { 0x61, 0x39, 0x10, (byte) 0x0f, 0 };
		// { 0x61, 0x26, 0x10, 0x0f, (byte) 0xa6 }; red
		// {0x61, 0x28, 0x10, 0x0f, (byte) 0xa8}; blue
		// {0x61, (byte) 0xCF, (byte) 0x01, (byte) 0x01, 0};// { 0x61, 0x26,
		// 0x10, 0x0f, (byte) 0xa6 };
		// data = {0x61, 0x28, 0x10, 0x0f, 0xa8};
		SuperlegendCommandSender sender = new SuperlegendCommandSender();

		// sender.switchOn(deviceIp);
		byte calculateChecksum = sender.calculateChecksum(data);
		data[data.length - 1] = calculateChecksum;
		sender.sendCommand(deviceIp, data);

	}

	public static void main2(String[] args) throws IOException {
		// I know my device:
		String deviceIp = "192.168.2.109";
		SuperlegendCommandSender sender = new SuperlegendCommandSender();

		SuperlegendDeviceState someInformation;
		Color col = Color.BLUE;

		sender.switchOn(deviceIp);

		// byte[] data = { 0x61, 0x26, 0x10, 0x0f, (byte) 0xa6 };

		byte[] data = { 0x61, (byte) col.getRed(), (byte) col.getGreen(), (byte) col.getBlue(), 0 };
		byte calculateChecksum = sender.calculateChecksum(data);
		data[data.length - 1] = calculateChecksum;
		sender.sendCommand(deviceIp, data);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// someThings(deviceIp, sender);
	}

	private static void someThings(String deviceIp, SuperlegendCommandSender sender) throws IOException {
		SuperlegendDeviceState someInformation;
		Color col;
		System.out.println("\n##########");
		System.out.println("on");
		sender.switchOn(deviceIp);
		someInformation = sender.getInformation(deviceIp);
		System.out.println(someInformation);

		System.out.println("\n##########");
		col = Color.blue;
		System.out.println("giving color: " + col);
		sender.switchColor(deviceIp, col);
		someInformation = sender.getInformation(deviceIp);
		System.out.println(someInformation);

		System.out.println("\n##########");
		col = Color.WHITE;
		System.out.println("giving color: " + col);
		sender.switchColor(deviceIp, col);
		someInformation = sender.getInformation(deviceIp);
		System.out.println(someInformation);

		System.out.println("\n##########");
		System.out.println("off");
		sender.switchOff(deviceIp);
		someInformation = sender.getInformation(deviceIp);
		System.out.println(someInformation);
	}
}

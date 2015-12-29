package org.openhab.binding.dreamcheeky.internal.mailnotifier;

import java.io.IOException;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDManager;

public class NativeMailNotifierDriver {

	private static NativeMailNotifierDriver instance;

	public static final NativeMailNotifierDriver theInstance() {
		if (instance == null) {
			instance = new NativeMailNotifierDriver();
		}
		return instance;
	}

	private HIDDevice dreamCheeky;

	public void openMailNotifierConnection() throws IOException {
		if (dreamCheeky == null) {
			ClassPathLibraryLoader.loadNativeHIDLibrary();
			HIDManager instance = HIDManager.getInstance();
			dreamCheeky = instance.openById(7476, 4, null);

			// you need this initialization sequence
			dreamCheeky.write(new byte[] { 0x00, 0x1F, 0x01, 0x29, 0x00,
					(byte) 0xB8, 0x54, 0x2C, 0x03 });
			dreamCheeky.write(new byte[] { 0x00, 0x00, 0x01, 0x29, 0x00,
					(byte) 0xB8, 0x54, 0x2C, 0x04 });
		}
	}

	public void closeMailNotifierConnection() {
		if (dreamCheeky != null) {
			try {
				dreamCheeky.close();
			} catch (IOException e) {
				// we can ignore that - if close fails what shall I do?
				e.printStackTrace();
			}
		}
	}

	/**
	 * Switches color to the color given.
	 */
	public void switchColor(MailNotifierColor aColor) throws IOException {
		openMailNotifierConnection();

		
		try {
			switchColorInternal(aColor);
		} catch (IOException e) {
			// retry once.
			closeMailNotifierConnection();
			openMailNotifierConnection();
			switchColorInternal(aColor);
		}

	}

	private void switchColorInternal(MailNotifierColor aColor)
			throws IOException {
		byte r = (byte) aColor.red;
		byte g = (byte) aColor.green;
		byte b = (byte) aColor.blue;
		dreamCheeky.write(new byte[] { 0x00, r, g, b, 0x00, 0x00, 0x54, 0x2C,
				0x05 });
	}

	/**
	 * Switches color from start to end using a gradient paint this means
	 * smoothly changing color by changing it in many smaller steps. This method
	 * calculates the number of steps using a difference analysis on both
	 * colors.
	 */
	public void displayGradient(MailNotifierColor aStartColor,
			MailNotifierColor aEndColor) throws IOException {
		double distance = MailNotifierColor.distance(aStartColor, aEndColor);

		double distanceNormalized = distance / ((double) 200) * 40;
		displayGradient(aStartColor, aEndColor, (int) distanceNormalized);
	}

	private void displayGradient(MailNotifierColor aStartColor,
			MailNotifierColor aEndColor, int aSteps) throws IOException {
		float stepR = (aEndColor.red - aStartColor.red) / (float) aSteps;
		float stepG = (aEndColor.green - aStartColor.green) / (float) aSteps;
		float stepB = (aEndColor.blue - aStartColor.blue) / (float) aSteps;

		MailNotifierColor tempCurrentColor = new MailNotifierColor(0, 0, 0);
		for (int i = 1; i <= aSteps; i++) {
			tempCurrentColor.red = aStartColor.red + (int) (i * stepR);
			tempCurrentColor.green = aStartColor.green + (int) (i * stepG);
			tempCurrentColor.blue = aStartColor.blue + (int) (i * stepB);

			switchColor(tempCurrentColor);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		NativeMailNotifierDriver instance = theInstance();
		instance.openMailNotifierConnection();
		// for (int i = 0; i < 3; i++) {

		MailNotifierColor tempCol = new MailNotifierColor(64, 8, 0);
		MailNotifierColor tempCol2 = new MailNotifierColor(15, 56, 64);
		// new MailNotifierColor(60, 60, 60);
		instance.displayGradient(tempCol, tempCol2);
		//colorGradientCheck(instance, tempCol, MailNotifierColor.RED, 4);

		// colorGradientCheck(instance, MailNotifierColor.DARK_BLUE,
		// MailNotifierColor.LIGHT_BLUE, 10);

		// colorGradientCheck(instance, MailNotifierColor.RED,
		// MailNotifierColor.NO_COLOR, 20);

		// colorGradientCheck(instance, MailNotifierColor.RED,
		// new MailNotifierColor(64, 64, 64), 20);

		// colorGradientCheck(instance, MailNotifierColor.RED,
		// MailNotifierColor.NO_COLOR, 20);

		// }

		instance.closeMailNotifierConnection();
	}

}

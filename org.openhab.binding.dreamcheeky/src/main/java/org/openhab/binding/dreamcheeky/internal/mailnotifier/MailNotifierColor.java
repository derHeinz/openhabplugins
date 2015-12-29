package org.openhab.binding.dreamcheeky.internal.mailnotifier;

public class MailNotifierColor {

	public static final MailNotifierColor RED = new MailNotifierColor(55, 5, 12);
	public static final MailNotifierColor GREEN = new MailNotifierColor(5, 62,
			12);
	public static final MailNotifierColor GREY = new MailNotifierColor(30, 30,
			30);
	public static final MailNotifierColor YELLOW = new MailNotifierColor(64,
			43, 12);
	public static final MailNotifierColor LIGHT_GREY = new MailNotifierColor(
			12, 12, 12);
	public static final MailNotifierColor LIGHT_BLUE = new MailNotifierColor(0,
			0, 64);
	public static final MailNotifierColor PINK = new MailNotifierColor(32, 0,
			32);
	public static final MailNotifierColor DARK_BLUE = new MailNotifierColor(0,
			0, 32);
	public static final MailNotifierColor NO_COLOR = new MailNotifierColor(0,
			0, 0);

	/**
	 * Color definitions.
	 */
	int red;
	int green;
	int blue;

	public MailNotifierColor(int r, int g, int b) {
		if (r > 64 || r < 0) {
			throw new RuntimeException("Red out of scope " + r);
		}
		if (g > 64 || g < 0) {
			throw new RuntimeException("Green out of scope " + g);
		}
		if (b > 64 || b < 0) {
			throw new RuntimeException("Blue out of scope " + b);
		}
		red = r;
		green = g;
		blue = b;
	}

	@Override
	public String toString() {
		return getClass().getCanonicalName() + "[r=" + red + ",g=" + green
				+ ",b=" + blue + "]";
	}

	/**
	 * Visible distance between two colors - also answers are they same.
	 */
	public static double distance(MailNotifierColor c1, MailNotifierColor c2) {

		double rmean = (c1.red + c2.red) / 2;
		int r = c1.red - c2.red;
		int g = c1.green - c2.green;
		int b = c1.blue - c2.blue;
		double weightR = 2 + rmean / 65;
		double weightG = 4.0;
		double weightB = 2 + (64 - rmean) / 65;
		return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);

	}

}
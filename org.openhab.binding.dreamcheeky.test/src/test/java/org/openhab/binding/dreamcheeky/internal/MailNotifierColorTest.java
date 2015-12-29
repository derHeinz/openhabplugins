package org.openhab.binding.dreamcheeky.internal;

import org.junit.Test;
import org.openhab.binding.dreamcheeky.internal.mailnotifier.MailNotifierColor;

public class MailNotifierColorTest {

	@Test
	public void checkColorDistance() {
		double dist;

		dist = MailNotifierColor.distance(new MailNotifierColor(64, 8, 0),
				MailNotifierColor.RED);
		System.out.println(dist + "-> 4");

		dist = MailNotifierColor.distance(MailNotifierColor.DARK_BLUE,
				MailNotifierColor.LIGHT_BLUE);
		System.out.println(dist + "-> 10");

		dist = MailNotifierColor.distance(MailNotifierColor.RED,
				MailNotifierColor.NO_COLOR);
		System.out.println(dist + "-> 20");

		dist = MailNotifierColor.distance(MailNotifierColor.RED,
				new MailNotifierColor(64, 64, 64));
		System.out.println(dist + "-> 10");

		dist = MailNotifierColor.distance(MailNotifierColor.LIGHT_BLUE,
				MailNotifierColor.LIGHT_BLUE);
		System.out.println(dist + "-> 0");

		// dist = MailNotifierColor.distance(MailNotifierColor.YELLOW,
		// MailNotifierColor.RED);
		// System.out.println(dist);
		//
		// dist = MailNotifierColor.distance(MailNotifierColor.DARK_BLUE,
		// MailNotifierColor.RED);
		// System.out.println(dist);
		//
		// dist = MailNotifierColor.distance(MailNotifierColor.YELLOW,
		// MailNotifierColor.PINK);
		// System.out.println(dist);
		//
		// dist = MailNotifierColor.distance(MailNotifierColor.NO_COLOR,
		// new MailNotifierColor(64, 64, 64));
		// System.out.println(dist);

	}

}

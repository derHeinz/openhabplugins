package org.openhab.binding.superlegend.internal;

import java.awt.Color;

/**
 * State for superlegend devices.
 * @author Heinz
 *
 */
public class SuperlegendDeviceState {

	private boolean on;

	private int red;

	private int green;

	private int blue;

	public SuperlegendDeviceState(boolean aOn, int r, int g, int b) {
		on = aOn;
		red = r;
		green = g;
		blue = b;
	}

	public boolean isOn() {
		return on;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public Color getColor() {
		return new Color(red, green, blue);
	}

	@Override
	public String toString() {
		return "Superlegend Device is " + (on ? "on" : "off") + " and set to " + getColor();
	}

}

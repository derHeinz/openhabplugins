package org.openhab.binding.superlegend.internal;

import java.awt.Color;

import org.openhab.binding.superlegend.internal.SuperlegendCommandSender.FUNCTION;

/**
 * State for superlegend devices. This one is queried from the device.
 * 
 * @author Heinz
 * 
 */
public class SuperlegendDeviceState {

	/**
	 * Whether the device is in color only mode. If true it shows a color only,
	 * otherwise it's in function mode - check function for the function.
	 */
	private boolean colorMode;

	/**
	 * If it is in function mode this is the function that it's currently in.
	 */
	private FUNCTION function;

	/**
	 * If it is in function mode this is the frequency, the function is updated
	 * with.
	 */
	private byte frequency;

	private boolean on;

	private int red;

	private int green;

	private int blue;

	public SuperlegendDeviceState(boolean aOn, boolean aColorMode) {
		on = aOn;
		colorMode = aColorMode;
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

	public boolean isColorMode() {
		return colorMode;
	}

	public FUNCTION getFunction() {
		return function;
	}

	public void setFunction(FUNCTION function) {
		this.function = function;
	}

	public byte getFrequency() {
		return frequency;
	}

	public void setFrequency(byte frequency) {
		this.frequency = frequency;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	@Override
	public String toString() {
		String text = "Superlegend Device is " + (on ? "on" : "off") + " and set to mode "
				+ (colorMode ? "color" : "function") + ".";
		if (!colorMode) {
			text += " Function: " + function.name() + " freq: " + frequency;
		}
		text += " Color: " + getColor();
		return text;
	}

}

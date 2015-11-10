package util;
import java.util.Scanner;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * <i>Singleton</i> class to assist with basic keyboard input operations. Only a single <i>Input</i> object will ever be created. The <i>Input</i> class clusters related input operations and will build one <i>Scanner</i> object to use with input.
 * <i>Scanner</i> objects are big and complex; thus, this approach reduces the overhead associated with the repeated creation of <i>Scanner</i> objects.
 * 
 * @author Rex Woollard
 */
public final class Input {
	/** Keyword <i>static</i> makes this a <i>class</i> oriented variable, rather than <i>object</i> oriented variable; only one instance of an <i>Input</i> object will ever exist, and the instance is tracked by this reference variable. */ 
	public final static Input instance = new Input();
	/** Each input method allows for a number failed attempts before throwing an exception. */
	public final static int MAX_ATTEMPTS = 3;
	/** Object-oriented instance variable, but since only one <i>Input</i> can ever be created, only one <i>Scanner</i> object will ever be created. */
	private Scanner scannerKeyboard;

	/** A <i>private</i> constructor guarantees that no <i>Input</i> object can be created from outside the <i>Input</i> class; this is essential for the <i>singleton</i> design pattern. */
	private Input() {	
		scannerKeyboard = new Scanner(System.in);
		}

	/**
	 * Presents a prompt to the user and retrieves an <i>double</i> value. 
	 * @param prompt   reference to a <i>String</i> object whose contents will be displayed to the user as a prompt.
	 * @return <i>double</i> value input from keyboard
	 */
	public double getDouble(String prompt) throws NumberFormatException, RuntimeException {
		System.out.print(prompt);
		int numAttempts = 0;
		do {
			try {
				++numAttempts;
				String stringInput = scannerKeyboard.nextLine();
				return Double.parseDouble(stringInput);
			} catch (NumberFormatException nfe) {
				if (numAttempts >= MAX_ATTEMPTS)
					return 0.0;
				System.out.format("Invalid double: %s", prompt);
			}
		} while(numAttempts < MAX_ATTEMPTS);
		throw new RuntimeException("Too many failed attempts.");
	} // end double getDouble()

	/**
	 * Presents a prompt to the user and retrieves a <i>double</i> value which is within the range of <i>low</i> to <i>high</i> (inclusive). 
	 * @param prompt reference to a <i>String</i> object whose contents will be displayed to the user as a prompt.
	 * @param low lower boundary on the range of legitimate values
	 * @param high upper boundary on the range of legitimate values
	 * @return <i>double</i> value input from keyboard
	 */
	public double getDouble(String prompt, double low, double high) throws NumberFormatException, RuntimeException {
		int numAttempts = 0;
		double returnValue;
		do {
			System.out.printf("%s (%.1f-%.1f): ", prompt, low, high);
			try {
				++numAttempts;
				String stringInput = scannerKeyboard.nextLine();
				returnValue = Double.parseDouble(stringInput);
				if (returnValue >= low && returnValue <= high)
					return returnValue;
			} catch (NumberFormatException nfe) {
				if (numAttempts >= MAX_ATTEMPTS)
					return low; // snap to the lowest legitimate value
			}
			System.out.println("Invalid double.");
		} while (numAttempts < MAX_ATTEMPTS);
		throw new RuntimeException("Too many failed attempts.");
	} // end double getDouble()

	/**
	 * Presents a prompt to the user and retrieves an <i>int</i> value. 
	 * @param prompt   reference to a <i>String</i> object whose contents will be displayed to the user as a prompt.
	 * @return <i>int</i> value input from keyboard
	 */
	public int getInt(String prompt) throws NumberFormatException, RuntimeException {
		System.out.print(prompt);
		int numAttempts = 0;
		do {
			try {
				++numAttempts;
				String stringInput = scannerKeyboard.nextLine();
				return Integer.parseInt(stringInput);
			} catch (NumberFormatException nfe) {
				if (numAttempts >= MAX_ATTEMPTS)
					return 0; // snap to 0
				System.out.format("Invalid integer: %s", prompt);
			}
		} while(numAttempts < MAX_ATTEMPTS);
		throw new RuntimeException("Too many failed attempts.");
	} // end int getInt()

	/**
	 * Presents a prompt to the user and retrieves an <i>int</i> value which is within the range of <i>low</i> to <i>high</i> (inclusive). 
	 * @param prompt reference to a <i>String</i> object whose contents will be displayed to the user as a prompt.
	 * @param low lower boundary on the range of legitimate values
	 * @param high upper boundary on the range of legitimate values
	 * @return <i>int</i> value input from keyboard
	 */
	public int getInt(String prompt, int low, int high) throws NumberFormatException, RuntimeException {
		int numAttempts = 0;
		int returnValue;
		do {
			System.out.printf("%s (%d-%d): ", prompt, low, high);
			try {
				++numAttempts;
				String stringInput = scannerKeyboard.nextLine();
				returnValue = Integer.parseInt(stringInput);
				if (returnValue >= low && returnValue <= high)
					return returnValue;
			} catch (NumberFormatException nfe) {
				if (numAttempts >= MAX_ATTEMPTS)
					return low; // snap to the lowest legitimate value
			}
			System.out.println("Invalid integer.");
		} while (numAttempts < MAX_ATTEMPTS);
		throw new RuntimeException("Too many failed attempts.");
	} // end int getInt()

	/**
	 * Presents a prompt to the user and retrieves a <i>reference-to-String</i>. 
	 * @param prompt reference to a <i>String</i> object whose contents will be displayed to the user as a prompt.
	 * @return <i>reference-to-String</i> object created by keyboard input
	 */
	public String getString(String prompt) {
		System.out.print(prompt);
		//scannerKeyboard.useDelimiter("\r\n"); // Setting this delimiter ensures that we capture everything up to the <Enter> key. Without this, input stops at the next whitespace (space, tab, newline etc.).
		String input = scannerKeyboard.nextLine();
		//scannerKeyboard.reset(); // The preceding use of useDelimiter() changed the state of the Scanner object. reset() re-establishes the original state.
		return input;
	} // end String getString()

	/**
	 * Presents a prompt to the user and retrieves a <i>boolean</i> value. 
	 * @param prompt reference to a <i>String</i> object whose contents will be displayed to the user as a prompt.
	 * @return <i>boolean</i> value input from keyboard
	 */
	public boolean getBoolean(String prompt) {
		System.out.print(prompt);
		return scannerKeyboard.nextBoolean();
	} // end boolean getBoolean()
} // end class Input
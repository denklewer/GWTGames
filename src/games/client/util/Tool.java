package games.client.util;

import java.util.Vector;

public class Tool {
	
	private Tool() {
	}
	
	public static Vector<Integer> getDigits(int num) {
    	Vector<Integer> digits = new Vector<Integer>();
    	
    	if (num == 0) {
    		digits.addElement(new Integer(0));
    	}
		
		for (; num > 0 ; num /= 10) {
			int digit = num % 10;
			digits.insertElementAt(new Integer(digit), 0);
		}
		
		
		return digits;
    }

}

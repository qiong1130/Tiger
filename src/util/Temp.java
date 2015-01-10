package util;

public class Temp {

	public static int count = 0;

	public Temp() {
		
	}

	 // Factory pattern
	public static String next() {
		 return "x_" + (Temp.count++);
	}

}

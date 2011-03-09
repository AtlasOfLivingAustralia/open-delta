package au.org.ala.delta.util;

public class Platform {
	
	public static boolean isWindowsAero() {		
		String osversion = System.getProperty("os.version");	
		double v =Double.parseDouble(osversion);
		return v >= 6;
	}

}

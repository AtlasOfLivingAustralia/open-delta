/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.util;

public class Platform {
	
	public static boolean isWindowsAero() {		
		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().contains("windows")) {
			String osversion = System.getProperty("os.version");	
			double v =Double.parseDouble(osversion);
			return v >= 6;
		}
		return false;
	}
	
	public static boolean isWindows() {
		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().contains("windows")) {
			return true;
		}
		
		return false;		
	}


}

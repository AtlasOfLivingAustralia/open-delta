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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class LocalConfigFiles {
	private String _appKey;
	
	private static String LINE_ENDING = System.getProperty("line.ending");
		
	public LocalConfigFiles(String appKey) {
		_appKey = appKey;
	}
	
	public File getWebsearchIndexFile() {
		return getFile("websearch.ind", "/au/org/ala/delta/intkey/resources/websearch.ind");
	}
	
	private File getFile(String filename, String resourcePath) {
		File f = new File(String.format("%s%s%s", getSettingsDirectory().getAbsolutePath(), File.separator, filename));		
		if (!f.exists()) {
			InputStream is = LocalConfigFiles.class.getResourceAsStream(resourcePath);
			if (is != null) {
				try {
					List<String> lines = IOUtils.readLines(is);
					FileWriter writer = new FileWriter(f);
					IOUtils.writeLines(lines, LINE_ENDING, writer);
					writer.flush();
					writer.close();
				} catch (IOException ioex) {
					throw new RuntimeException(ioex);
				}
			}
		}
		
		return f;		
	}
	
	public File getSettingsDirectory() {
	    String userHome = System.getProperty("user.home");
	    if(userHome == null) {
	        throw new IllegalStateException("user.home==null");
	    }
	    File home = new File(userHome);
	    File settingsDirectory = new File(home, String.format(".open-delta%s.%s", File.separator, _appKey));
	    if(!settingsDirectory.exists()) {
	        if(!settingsDirectory.mkdirs()) {
	            throw new IllegalStateException(settingsDirectory.toString());
	        }
	    }
	    return settingsDirectory;
	}	

}

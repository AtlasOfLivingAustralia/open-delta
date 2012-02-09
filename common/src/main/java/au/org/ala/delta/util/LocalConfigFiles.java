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

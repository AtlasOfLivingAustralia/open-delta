package au.org.ala.delta.dist;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.dist.io.DistOutputFileManager;

public class DistContext extends DeltaContext {

	private boolean _phylipFormat;
	
	public DistContext() {
		_phylipFormat = false;
	}

	public DistOutputFileManager getOutputFileManager() {
		return (DistOutputFileManager)_outputFileSelector;
	}

	public boolean isPhylipFormat() {
		return _phylipFormat;
	}
	
	public void usePhylipFormat() {
		_phylipFormat = true;
	}
	
	protected void createOutputFileManager() {
		_outputFileSelector = new DistOutputFileManager();
	}
}

package au.org.ala.delta.dist;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.dist.io.DistOutputFileManager;

/**
 * A placeholder for state information provided by DIST specific directives.
 */
public class DistContext extends DeltaContext {

	private static final String DEFAULT_ITEMS_FILE_NAME = "ditems";
	
	private boolean _phylipFormat;
	private boolean _matchOverlap;
	private int _minimumNumberOfComparisons;
	private String _itemsFileName;
	
	
	
	public DistContext() {
		_phylipFormat = false;
		_matchOverlap = false;
		_itemsFileName = DEFAULT_ITEMS_FILE_NAME;
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

	public void matchOverlap() {
		_matchOverlap = true;
	}
	
	public boolean getMatchOverlap() {
		return _matchOverlap;
	}
	
	public void setMinimumNumberOfComparisons(int minimumNumberOfComparisons) {
		_minimumNumberOfComparisons = minimumNumberOfComparisons;
	}
	
	public int getMinimumNumberOfComparisons() {
		return _minimumNumberOfComparisons;
	}
	
	public String getItemsFileName() {
		return _itemsFileName;
	}
	
	public void setItemsFileName(String fileName) {
		_itemsFileName = fileName;
	}
 }

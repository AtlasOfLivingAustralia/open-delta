package au.org.ala.delta.ui;

import javax.swing.JComponent;

public interface SearchController {
	
	String getTitle();
	
	JComponent getOwningComponent();

	boolean findNext(SearchOptions options);

}

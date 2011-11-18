package au.org.ala.delta.delfor;

import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.model.MutableDeltaDataSet;

public class DelforContext extends ImportContext {

	public DelforContext(MutableDeltaDataSet dataSet) {
		super(dataSet);
	}
	
	@Override
	public void setMaximumNumberOfItems(int items) {
		super.setMaximumNumberOfItems(items);
		
		for (int i=1; i<=items; i++) {
			getDataSet().addItem();
		}
	}

	
	
}

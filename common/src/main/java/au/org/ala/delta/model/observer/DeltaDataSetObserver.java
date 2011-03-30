package au.org.ala.delta.model.observer;

/**
 * This interface should be implemented by classes interested in being notified of changes to a DeltaDataSet.
 */
public interface DeltaDataSetObserver {

	public void itemAdded(DeltaDataSetChangeEvent event);
	
	public void itemDeleted(DeltaDataSetChangeEvent event);
	
	public void itemMoved(DeltaDataSetChangeEvent event);
	
	public void itemEdited(DeltaDataSetChangeEvent event);
	
	public void itemSelected(DeltaDataSetChangeEvent event);
	
	public void characterAdded(DeltaDataSetChangeEvent event);
	
	public void characterDeleted(DeltaDataSetChangeEvent event);
	
	public void characterMoved(DeltaDataSetChangeEvent event);
	
	public void characterEdited(DeltaDataSetChangeEvent event);
	
	public void characterSelected(DeltaDataSetChangeEvent event);
}

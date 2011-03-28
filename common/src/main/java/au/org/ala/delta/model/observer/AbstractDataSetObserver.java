package au.org.ala.delta.model.observer;

/**
 * Provides empty implementations of the DeltaDataSetObserver interface.
 * A convenience class for classes interested in only a subset of the DeltaDataSetObserver interface.
 */
public abstract class AbstractDataSetObserver implements DeltaDataSetObserver {

	@Override
	public void itemAdded(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemDeleted(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemMoved(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemEdited(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemSelected(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterAdded(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterDeleted(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterMoved(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterEdited(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterSelected(DeltaDataSetChangeEvent event) {}

}

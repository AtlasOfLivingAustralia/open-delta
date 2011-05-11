package au.org.ala.delta.model;

import au.org.ala.delta.model.observer.DeltaDataSetObserver;

/**
 * An ObservableDeltaDataSet allows interested objects to be notified to changes
 * to the DeltaDataSet.
 */
public interface ObservableDeltaDataSet extends DeltaDataSet {
	/**
	 * Adds an observer interested in receiving notification of changes to this data set.
	 * Duplicate observers are ignored.
	 * @param observer the observer to add.
	 */
	public void addDeltaDataSetObserver(DeltaDataSetObserver observer);
	
	/**
	 * Prevents an observer from receiving further notifications of changes to this data set.
	 * @param observer the observer to remove.
	 */
	public void removeDeltaDataSetObserver(DeltaDataSetObserver observer);
}

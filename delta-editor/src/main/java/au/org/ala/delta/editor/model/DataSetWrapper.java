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
package au.org.ala.delta.editor.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.model.ObservableDeltaDataSet;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;

/**
 * A wrapper for an observable DeltaDataSet.  It allows DeltaDataSetObservers to
 * be isolated from the actual data set so that references to closed views don't 
 * hang around in the model and prevent garbage collection.
 */
public class DataSetWrapper extends au.org.ala.delta.model.DataSetWrapper implements DeltaDataSetObserver {

	/** Maintains a list of objects interested in being notified of changes to this model */
	private List<SwingDeltaDataSetObserver> _observerList = new ArrayList<SwingDeltaDataSetObserver>();

	public DataSetWrapper(ObservableDeltaDataSet dataSet) {
		super(dataSet);
		_wrappedDataSet.addDeltaDataSetObserver(this);
	}
	
	private boolean contains(DeltaDataSetObserver observer) {
	
		for (SwingDeltaDataSetObserver swingObserver : _observerList) {
			if (observer == swingObserver.getObserver()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds an observer interested in receiving notification of changes to this model.
	 * Duplicate observers are ignored.
	 * @param observer the observer to add.
	 */
	public void addDeltaDataSetObserver(DeltaDataSetObserver observer) {
		if (contains(observer)) {
			return;
		}
		_observerList.add(new SwingDeltaDataSetObserver(observer));
	}

	/**
	 * Prevents an observer from receiving further notifications of changes to this model.
	 * @param observer the observer to remove.
	 */
	public void removeDeltaDataSetObserver(DeltaDataSetObserver observer) {
		Iterator<SwingDeltaDataSetObserver> i = _observerList.iterator();
		while (i.hasNext()) {
			if (observer == i.next().getObserver()) {
				i.remove();
				return;
			}
		}
	}

	@Override
	public void itemAdded(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemAdded(event);
		}
	}

	@Override
	public void itemDeleted(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemDeleted(event);
		}
	}

	@Override
	public void itemMoved(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemMoved(event);
		}
	}

	@Override
	public void itemEdited(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemEdited(event);
		}
	}

	@Override
	public void itemSelected(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).itemSelected(event);
		}
	}

	@Override
	public void characterAdded(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterAdded(event);
		}
	}

	@Override
	public void characterDeleted(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterDeleted(event);
		}
	}
	
	@Override
	public void characterTypeChanged(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterTypeChanged(event);
		}
	}

	@Override
	public void characterMoved(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterMoved(event);
		}
	}

	@Override
	public void characterEdited(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterEdited(event);
		}
	}

	@Override
	public void characterSelected(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).characterSelected(event);
		}	
	}

	@Override
	public void imageEdited(DeltaDataSetChangeEvent event) {
		for (int i=_observerList.size()-1; i>=0; i--) {
			_observerList.get(i).imageEdited(event);
		}	
	}
}

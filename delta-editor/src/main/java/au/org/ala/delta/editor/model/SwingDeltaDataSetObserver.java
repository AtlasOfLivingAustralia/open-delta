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

import javax.swing.SwingUtilities;

import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.model.observer.DeltaDataSetObserver;

/**
 * Ensures dataset notifications occur on the event dispatch thread.
 */
public class SwingDeltaDataSetObserver implements DeltaDataSetObserver {

	private DeltaDataSetObserver _observer;
	
	public SwingDeltaDataSetObserver(DeltaDataSetObserver observer) {
		_observer = observer;
	}
	
	public DeltaDataSetObserver getObserver() {
		return _observer;
	}
	
	@Override
	public void itemAdded(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.itemAdded(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.itemAdded(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}
	
	private void handleException(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void itemDeleted(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.itemDeleted(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.itemDeleted(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
		
	}

	@Override
	public void itemMoved(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.itemMoved(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.itemMoved(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	@Override
	public void itemEdited(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.itemEdited(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.itemEdited(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
		
	}

	@Override
	public void itemSelected(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.itemSelected(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.itemSelected(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	@Override
	public void characterAdded(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.characterAdded(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.characterAdded(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	@Override
	public void characterDeleted(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.characterDeleted(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.characterDeleted(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	@Override
	public void characterMoved(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.characterMoved(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.characterMoved(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	@Override
	public void characterEdited(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.characterEdited(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.characterEdited(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	@Override
	public void characterSelected(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.characterSelected(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.characterSelected(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	@Override
	public void imageEdited(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.imageEdited(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.imageEdited(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}
	
	@Override
	public void characterTypeChanged(final DeltaDataSetChangeEvent event) {
		if (SwingUtilities.isEventDispatchThread()) {
			_observer.characterTypeChanged(event);
		}
		else {
			try {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						_observer.characterTypeChanged(event);	
					}
				});
			} catch (Exception e) {
				handleException(e);
			}
		}
	}
}

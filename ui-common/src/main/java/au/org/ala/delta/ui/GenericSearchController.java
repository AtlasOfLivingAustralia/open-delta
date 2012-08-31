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
package au.org.ala.delta.ui;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.util.SearchableModel;

public abstract class GenericSearchController<T> implements SearchController {

	private GenericSearchPredicate<T> _predicate;
	private T _lastResult;
	private String _lastResultTerm;
	private ResourceMap _messages;
	private String _titleMessageKey;

	public GenericSearchController(String titleMessageKey) {
		SingleFrameApplication application = (SingleFrameApplication) Application.getInstance();
		_messages = application.getContext().getResourceMap();
		_titleMessageKey = titleMessageKey;
	}

	protected ResourceMap getMessages() {
		return _messages;
	}

	private T findImpl(GenericSearchPredicate<T> predicate, int startFrom) {
		SearchableModel<T> model = getSearchableModel();
		T result = model.first(predicate, startFrom, predicate.getOptions().getSearchDirection());
		if (result == null && predicate.getOptions().isWrappedSearch()) {
			int restartFrom = 1;
			if (predicate.getOptions().getSearchDirection() == SearchDirection.Backward) {
				restartFrom = model.size();
			}
			result = model.first(predicate, restartFrom, predicate.getOptions().getSearchDirection());
		}

		if (result != null) {
			selectItem(result);
		} else {
			clearSelection();
		}

		return result;
	}

	protected abstract void selectItem(T item);

	protected abstract void clearSelection();

	protected abstract SearchableModel<T> getSearchableModel();

	protected abstract int getSelectedIndex();

	protected abstract int getIndexOf(T object);

	protected abstract GenericSearchPredicate<T> createPredicate(SearchOptions options);

	@Override
	public String getTitle() {
		return _messages.getString(_titleMessageKey);
	}

	@Override
	public boolean findNext(SearchOptions options) {
		
		if (!StringUtils.equals(_lastResultTerm, options.getSearchTerm())) {
			_lastResult  = null;
			_lastResultTerm = null;
		}

		int delta = options.getSearchDirection() == SearchDirection.Forward ? 1 : -1;

		int startFrom = getSelectedIndex() + 1;
		if (_lastResult != null) {			
			int lastResultIndex = getIndexOf(_lastResult) + 1;
			if (lastResultIndex == getSelectedIndex() + 1) {
				startFrom = lastResultIndex + delta;
			}
		}

		int size = getSearchableModel().size();

		if (startFrom < 1) {
			startFrom = options.isWrappedSearch() ? size : 1;
		} else if (startFrom > size) {
			startFrom = options.isWrappedSearch() ? 1 : size;
		}

		_predicate = createPredicate(options);

		T result = findImpl(_predicate, startFrom);
		if (result == null && _lastResult != null && !options.isWrappedSearch()) {
			selectItem(_lastResult);
		} else {
			_lastResult = result;
			_lastResultTerm = options.getSearchTerm();
		}

		return _lastResult != null;
	}

}

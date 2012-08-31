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
package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * The DelegatingDataSetTranslator iterates through the Items and Attributes of a DeltaDataSet, raising
 * events for instances of the IterativeTranslator to handle.
 */
public class DelegatingDataSetTranslator implements DataSetTranslator {

	private static Logger logger = Logger.getLogger(DelegatingDataSetTranslator.class.getName());
	
	protected DeltaContext _context;
	private List<IterativeTranslator> _translators;
	private List<DataSetFilter> _filters;
	
	public DelegatingDataSetTranslator(DeltaContext context) {
		_context = context;
		_translators = new ArrayList<IterativeTranslator>();
		_filters = new ArrayList<DataSetFilter>();
	}
	
	public DelegatingDataSetTranslator(DeltaContext context, DataSetFilter filter, IterativeTranslator translator) {
		this(context);
		_translators.add(translator);
		_filters.add(filter);
	}
	
	public void add(Pair<IterativeTranslator, DataSetFilter> translator) {
		_translators.add(translator.getFirst());
		_filters.add(translator.getSecond());
	}
	
	public void translateItems() {
		
		MutableDeltaDataSet dataSet = _context.getDataSet();
		
		beforeFirstItem();
		
		int numItems = dataSet.getMaximumNumberOfItems();
        if (_context.getStopAfterItem() != null) {
            numItems = Math.min(numItems, _context.getStopAfterItem());
        }

		for (int i=1; i<=numItems; i++) {
			Item item = dataSet.getItem(i);
			String description = RTFUtils.stripFormatting(item.getDescription());
			KeywordSubstitutions.put(KeywordSubstitutions.NAME, description);
			
			item(item);	
		}
		
		afterLastItem();
	}

	protected void item(Item item) {
		for (int i=0; i<_translators.size(); i++) {
			translateItem(item, _translators.get(i), _filters.get(i));
		}
	}

	protected void translateItem(Item item, IterativeTranslator translator, DataSetFilter filter) {
		if (filter.filter(item)) {
			translator.beforeItem(item);
			
			MutableDeltaDataSet dataSet = _context.getDataSet();
			
			int numChars = dataSet.getNumberOfCharacters();
			for (int i=1; i<=numChars; i++) {
				
				Character character = dataSet.getCharacter(i);
				Attribute attribute = item.getAttribute(character);

				if (filter.filter(item, character)) {
					logger.fine(item.getItemNumber() + ", "+character.getCharacterId()+" = "+attribute.getValueAsString());
					translator.beforeAttribute(attribute);
					
					translator.afterAttribute(attribute);
				}	
				
				
			}
			
		    translator.afterItem(item);
		}
	}
	
	public void translateCharacters() {
		
		beforeFirstCharacter();
		
		MutableDeltaDataSet dataSet = _context.getDataSet();
		int numChars = dataSet.getNumberOfCharacters();
		for (int i=1; i<=numChars; i++) {
			
			Character character = dataSet.getCharacter(i);
			
			for (int j=0; j<_translators.size(); j++) {
				translateCharacter(character, _translators.get(j), _filters.get(j));
			}
		}
		
		afterLastCharacter();
	}
	
	protected void translateCharacter(Character character, IterativeTranslator translator, DataSetFilter filter) {
		if (filter.filter(character)) {
			translator.beforeCharacter(character);
		
			translator.afterCharacter(character);
		}	
	}
	
	
	@Override
	public void translateOutputParameter(OutputParameter parameter) {
		for (IterativeTranslator translator : _translators) {
			translator.translateOutputParameter(parameter);
		}
	}
	
	protected void beforeFirstItem() {
		for (IterativeTranslator translator : _translators) {
			translator.beforeFirstItem();
		}
	}
	
	protected void afterLastItem() {
		for (IterativeTranslator translator : _translators) {
			translator.afterLastItem();
		}
	}
	
	protected void beforeFirstCharacter() {
		for (IterativeTranslator translator : _translators) {
			translator.beforeFirstCharacter();
		}
	}
	
	protected void afterLastCharacter() {
		for (IterativeTranslator translator : _translators) {
			translator.afterLastCharacter();
		}
	}
}

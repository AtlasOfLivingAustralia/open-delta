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
package au.org.ala.delta.translation.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.translation.Words;
import au.org.ala.delta.translation.Words.Word;
import au.org.ala.delta.translation.attribute.CommentedValueList.CommentedValues;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

/**
 * Translates an Attribute into natural language.
 */
public abstract class AttributeTranslator {

	private Map<String, String> _separators;
	protected StringBuilder _translatedValue;
	protected AttributeFormatter _attributeFormatter;
	protected boolean _omitOr;
	protected String _comma;
	protected boolean _omitInapplicables;
	protected boolean _omitFinalComma;
	protected boolean _omitAllCommas;

	public AttributeTranslator(AttributeFormatter formatter, boolean omitOr) {
		_separators = new HashMap<String, String>();
		_separators.put("&", Words.word(Word.AND));
		_separators.put("-", Words.word(Word.TO));
		_attributeFormatter = formatter;
		_omitOr = omitOr;
		_comma = Words.word(Word.COMMA);
		_omitInapplicables = false;
		_omitFinalComma = false;
		_omitAllCommas = false;
	}
	
	/**
	 * After this method has been invoked, the comma character used in
	 * translated descriptions will be the one specified by the 
	 * Vocabulary entry of "Word.ALTERNATE_COMMA"
	 */
	public void useAlternateComma() {
		_comma = Words.word(Word.ALTERNATE_COMMA);
	}
	
	/**
	 * After this method has been invoked, any portion of an attribute 
	 * that has been coded as inapplicable (e.g. 11,3/-) will be ommitted 
	 * from the translated description. (Otherwise "or inapplicable" will
	 * be output).
	 */
	public void omitInapplicables() {
		_omitInapplicables = true;
	}
	
	/**
	 * Omits the comma when translating multiple values separated by &.
	 * The comma before the final or remains unaffected.
	 */
	public void omitFinalComma() {
		_omitFinalComma = true;
	}
	
	public void omitAllCommas() {
		_omitAllCommas = true;
	}


	/**
	 * Translates the supplied ParsedAttribute into a natural language
	 * description of the attribute.
	 * 
	 * @param attribute
	 *            the attribute to translate.
	 * @return a String containing the natural language description of the
	 *         attribute.
	 */
	public String translate(CommentedValueList attribute) {
		
		_translatedValue = new StringBuilder();
		_translatedValue.append(translateCharacterComment(attribute.getCharacterComment()));

		List<CommentedValues> commentedValues = getValues(attribute);
		boolean valueOutput = false;
		for (int i = 0; i < commentedValues.size(); i++) {
			String nextValue = commentedValues(commentedValues.get(i));

			if (valueOutput && StringUtils.isNotEmpty(nextValue)) {
				if (!_omitAllCommas) {
					_translatedValue.append(_comma);
				}
				_translatedValue.append(" ");
				if (!_omitOr) {
					_translatedValue.append(Words.word(Word.OR)+" ");
				}
			}
			valueOutput = valueOutput | StringUtils.isNotEmpty(nextValue);
			_translatedValue.append(nextValue);

		}

		return _translatedValue.toString().trim();
	}

	protected List<CommentedValues> getValues(CommentedValueList attribute) {
		List<CommentedValues> commentedValues = new ArrayList<CommentedValues>(attribute.getCommentedValues());
		
		if (_omitInapplicables && commentedValues.size() > 1) {
			Iterator<CommentedValues> allValues = commentedValues.iterator();
			while (allValues.hasNext()) {
				CommentedValues values = allValues.next();
				// Inapplicable can't appear with other values.
				if (values.getNumValues() == 1 && Attribute.INAPPICABLE.equals(values.getValue(0))) {
					allValues.remove();
				}
			}
		}
		
		return commentedValues;
	}

	public String commentedValues(CommentedValues commentedValues) {

		StringBuilder output = new StringBuilder();

		// Special case - if an attribute is marked not applicable and has a
		// comment
		// the "not applicable" is omitted from the description.
		if (!isInapplicableWithComment(commentedValues)) {
			Values values = commentedValues.getValues();
			output.append(translateValues(values));
		}
		output.append(comment(commentedValues));
		return output.toString();
	}

	private boolean isInapplicableWithComment(CommentedValues commentedValues) {

		return isPseudoValueWithComment(commentedValues, Attribute.INAPPICABLE);
	}
	
	private boolean isUnknownWithComment(CommentedValues commentedValues) {
		return isPseudoValueWithComment(commentedValues, Attribute.UNKNOWN);
	}
	
	private boolean isPseudoValueWithComment(CommentedValues commentedValues, String pseudoValue) {
		Values values = commentedValues.getValues();
		String comment = commentedValues.getComment();
		return (StringUtils.isNotEmpty(comment) && values.getNumValues() == 1 && pseudoValue.equals(values.getValue(0)));

	}

	public String translateCharacterComment(String comment) {
		StringBuilder output = new StringBuilder();
		comment = _attributeFormatter.formatCharacterComment(comment);
		if (StringUtils.isNotEmpty(comment)) {
			output.append(comment);
			output.append(" ");
		}
		return output.toString();
	}

	public String comment(CommentedValues commentedValues) {
		String comment = commentedValues.getComment();
		
		// An attribute that consists of a comment and a pseudo value that
		// does not normally translate (ie. - or U), the character comment
		// formatting rules are applied.
		if (isInapplicableWithComment(commentedValues) || isUnknownWithComment(commentedValues)) {
			comment = _attributeFormatter.formatCharacterComment(comment);
		}
		else {
			comment = _attributeFormatter.formatComment(comment);
		}
		StringBuilder output = new StringBuilder();
		if (StringUtils.isNotEmpty(comment)) {
			output.append(" ");

			output.append(comment);
		}
		return output.toString();
	}

	public String translateValues(Values values) {
		if (values == null) {
			return "";
		}
		List<String> valueStrings = values.getValues();
		if (valueStrings.isEmpty()) {
			return "";
		}

		StringBuilder output = new StringBuilder();
		String separator = values.getSeparator();
		if ("&".equals(separator)) {

			output.append(translateAndSeparatedValues(valueStrings));
		} else if ("-".equals(separator)) {
			output.append(translateRangeSeparatedValues(valueStrings, rangeSeparator()));
		} else if (StringUtils.isNotBlank(separator)) {

			output.append(translateRangeSeparatedValues(valueStrings, separator));

		} else {
			String value = getTranslatedValue(valueStrings.get(0));

			if (StringUtils.isNotEmpty(value)) {
				output.append(value);
			}
		}
		return output.toString();

	}

	protected String translateRangeSeparatedValues(List<String> valueStrings, String rangeSeparator) {

		StringBuilder output = new StringBuilder();
		List<String> translatedValues = translateValues(valueStrings);

		if (!translatedValues.isEmpty()) {
			for (int i = 0; i < translatedValues.size() - 1; i++) {
				String value = translatedValues.get(i);

				output.append(value);

				output.append(rangeSeparator);
			}
			output.append(translatedValues.get(translatedValues.size() - 1));
		}
		return output.toString();
	}

	protected String translateAndSeparatedValues(List<String> valueStrings) {

		StringBuilder output = new StringBuilder();

		List<String> translatedValues = translateValues(valueStrings);
		if (!translatedValues.isEmpty()) {
			output.append(translatedValues.get(0));
			for (int i = 1; i < translatedValues.size(); i++) {

				if (translatedValues.size() > 2) {
					
					if ((i < translatedValues.size() - 1) || !_omitFinalComma) {
						output.append(_comma);
					}
				}
				output.append(" ");
				if (i == translatedValues.size() - 1) {
					output.append(Words.word(Word.AND)).append(" ");
				}
				output.append(translatedValues.get(i));

			}
		}
		return output.toString();
	}

	protected List<String> translateValues(List<String> valueStrings) {
		List<String> translatedValues = new ArrayList<String>();
		for (int i = 0; i < valueStrings.size(); i++) {
			String value = getTranslatedValue(valueStrings.get(i));
			if (StringUtils.isNotEmpty(value)) {
				translatedValues.add(value);
			}
		}
		return translatedValues;
	}

	protected String getTranslatedValue(String value) {

		if (Attribute.VARIABLE.equals(value)) {
			return Words.word(Word.VARIABLE);
		}
		if (Attribute.UNKNOWN.equals(value)) {
			return Words.word(Word.UNKNOWN);
		}
		if (Attribute.INAPPICABLE.equals(value)) {
			return Words.word(Word.NOT_APPLICABLE);
		}
		return translateValue(value);
	}

	public abstract String rangeSeparator();

	public abstract String translateValue(String value);


}

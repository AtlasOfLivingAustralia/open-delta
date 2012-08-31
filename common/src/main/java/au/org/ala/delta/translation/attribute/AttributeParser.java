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

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.org.ala.delta.directives.AbstractStreamParser;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.attribute.AttrChunk;
import au.org.ala.delta.model.attribute.ChunkType;
import au.org.ala.delta.translation.attribute.CommentedValueList.CommentedValues;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

/**
 * The AttributeParser is responsible for parsing Strings representing attributes into a format
 * suitable for translation. (e.g. into natural language)
 */
public class AttributeParser {

	private static final char OR_SEPARATOR = '/';
	private static final char COMMENT_START = '<';
	private static final String AND_SEPARATOR = "&";
	private static final String RANGE_SEPARATOR = "-";
	
	/**
	 * Parses the supplied attribute value into a ParsedAttribute object.
	 * @param attribute the attribute value to parse
	 * @return a new ParsedAttribute
	 */
	public CommentedValueList parse(String attribute) {
		
		AttributeStreamParser parser = new AttributeStreamParser(attribute);
		try {
			parser.parse();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return parser.getParsedAttribute();
	}
	
	
	class AttributeStreamParser extends AbstractStreamParser {
		
		private CommentedValueList _parsedAttribute;
		
		public AttributeStreamParser(String attributeValue) {
			super(null, new StringReader(attributeValue));
		}
		
		public CommentedValueList getParsedAttribute() {
			return _parsedAttribute;
		}
		
		
		@Override
		public void parse() throws ParseException {
			_parsedAttribute = readParsedAttribute();
		}
		
		public CommentedValueList readParsedAttribute() throws ParseException {
			
			readNext();
			String characterComment = "";
			if (_currentChar == COMMENT_START) {
				characterComment = readComment();
			}
			List<CommentedValues> commentedValues = new ArrayList<CommentedValues>();
			while (_currentInt >= 0) {
				commentedValues.add(readCommentedValues());
				
				assert _currentInt < 0 || _currentChar == OR_SEPARATOR;
				if (_currentChar == OR_SEPARATOR) {
					readNext();
				}
			}
			
			return new CommentedValueList(characterComment, commentedValues);
		}
		
		public CommentedValues readCommentedValues() throws ParseException {
			
			Values values = readValues();
			String comment = "";
			// Comments are optional.
			if (_currentChar == COMMENT_START) {
				comment = readComment();
			}
			return new CommentedValues(values, comment);
		}
		
		public Values readValues() throws ParseException {
			StringBuilder valuesBuilder = new StringBuilder();
			while (_currentInt >= 0 && _currentChar != COMMENT_START && _currentChar != OR_SEPARATOR) {
				valuesBuilder.append(_currentChar);
				readNext();
			}
			if (valuesBuilder.length() == 0) {
				return null;
			}
			
			String valuesString = valuesBuilder.toString();
			Values values;
			String separator = "";
			if (valuesString.indexOf(RANGE_SEPARATOR) > 0) {
				separator = RANGE_SEPARATOR;
			}
			if (valuesString.indexOf(AND_SEPARATOR) > 0) {
				separator = AND_SEPARATOR;
			}
			if ("".equals(separator)) {
				values = new Values(valuesString.toString());
			}
			else {
				values = new Values(valuesString.split(separator), separator);
			}
			return values;
		}
		
		
	}

    public CommentedValueList toCommentedValues(Attribute attribute) {
        String characterComment = null;
        List<CommentedValues> commentedValues = new ArrayList<CommentedValues>();
        int i = 0;
        Iterator<AttrChunk> chunks = attribute.parsedAttribute().iterator();
        while (chunks.hasNext()) {
            AttrChunk chunk = chunks.next();

            // The character comment is treated differently to value comments.
            if (i==0 && chunk.isTextChunk()) {
                characterComment = chunk.getAsText(true);
            }
            else {
                String comment = "";
                StringBuilder value = new StringBuilder();
                while (chunks.hasNext() && chunk.getType() != ChunkType.CHUNK_OR) {
                    if (chunk.isTextChunk()) {
                        if (comment.length() > 0) {
                            throw new IllegalArgumentException("Unexpected second comment");
                        }
                        comment = chunk.getAsText(true);
                    }
                    else {
                        value.append(chunk.getAsText(true));
                    }
                    chunk = chunks.next();
                }
                Values values;
                String separator = "";
                if (value.indexOf(RANGE_SEPARATOR) > 0) {
                    separator = RANGE_SEPARATOR;
                }
                if (value.indexOf(AND_SEPARATOR) > 0) {
                    separator = AND_SEPARATOR;
                }
                if ("".equals(separator) || (attribute.getCharacter().getCharacterType().isNumeric() && RANGE_SEPARATOR.equals(separator))) {
                    values = new Values(value.toString());
                }
                else {
                    values = new Values(value.toString().split(separator), separator);
                }


                commentedValues.add(new CommentedValues(values, comment));

            }

            i++;
        }
        return new CommentedValueList(characterComment, commentedValues);
    }
	
}

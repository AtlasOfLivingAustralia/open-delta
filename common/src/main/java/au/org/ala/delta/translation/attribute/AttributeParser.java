package au.org.ala.delta.translation.attribute;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.directives.AbstractStreamParser;
import au.org.ala.delta.translation.attribute.ParsedAttribute.CommentedValues;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

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
	public ParsedAttribute parse(String attribute) {
		
		AttributeStreamParser parser = new AttributeStreamParser(attribute);
		try {
			parser.parse();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return parser.getParsedAttribute();
	}
	
	
	class AttributeStreamParser extends AbstractStreamParser {
		
		private ParsedAttribute _parsedAttribute;
		
		public AttributeStreamParser(String attributeValue) {
			super(null, new StringReader(attributeValue));
		}
		
		public ParsedAttribute getParsedAttribute() {
			return _parsedAttribute;
		}
		
		
		@Override
		public void parse() throws Exception {
			_parsedAttribute = readParsedAttribute();
		}
		
		public ParsedAttribute readParsedAttribute() throws Exception {
			
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
			
			return new ParsedAttribute(characterComment, commentedValues);
		}
		
		public CommentedValues readCommentedValues() throws Exception {
			
			Values values = readValues();
			String comment = "";
			// Comments are optional.
			if (_currentChar == COMMENT_START) {
				comment = readComment();
			}
			return new CommentedValues(values, comment);
		}
		
		public Values readValues() throws Exception {
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
	
}

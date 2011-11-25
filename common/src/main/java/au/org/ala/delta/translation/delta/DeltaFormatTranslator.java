package au.org.ala.delta.translation.delta;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericAttribute;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.AbstractIterativeTranslator;
import au.org.ala.delta.translation.ItemListTypeSetter;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.attribute.AttributeParser;
import au.org.ala.delta.translation.attribute.CommentedValueList;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;
import au.org.ala.delta.translation.print.CharacterListTypeSetter;
import au.org.ala.delta.util.Utils;

/**
 * The DeltaFormatTranslator can reformat the output of the 
 * ITEM DESCRIPTIONS and CHARACTER LIST directives.  It is used when the
 * TRANSLATE INTO DELTA FORMAT directive is specified.
 */
public class DeltaFormatTranslator extends AbstractIterativeTranslator {

	protected PrintFile _printer;
	protected ItemFormatter _itemFormatter;
	protected CharacterFormatter _characterFormatter;
	protected AttributeFormatter _attributeFormatter;
	protected AttributeParser _parser;
	protected CharacterListTypeSetter _typeSetter;
	protected ItemListTypeSetter _itemTypeSetter;
	
	
	public DeltaFormatTranslator(
			DeltaContext context, 
			PrintFile printer, 
			ItemFormatter itemFormatter,
			CharacterFormatter characterFormatter,
			AttributeFormatter attributeFormatter,
			CharacterListTypeSetter typeSetter,
			ItemListTypeSetter itemTypeSetter) {
		
		_printer = printer;
		_printer.setIndentOnLineWrap(true);
		_itemFormatter = itemFormatter;
		_characterFormatter = characterFormatter;
		 _parser = new AttributeParser();
		 _typeSetter = typeSetter;
		 _itemTypeSetter = itemTypeSetter;
		 _attributeFormatter = attributeFormatter;
	}
	
	@Override
	public void beforeFirstItem() {
		_printer.setLineWrapIndent(0);
		_printer.setIndent(0);
		_printer.writeBlankLines(2, 0);
		_itemTypeSetter.beforeFirstItem();
	}

	@Override
	public void beforeItem(Item item) {
		StringBuilder itemDescription = new StringBuilder();
		itemDescription.append("# ");
		itemDescription.append(_itemFormatter.formatItemDescription(item));
		itemDescription.append("/");
		outputLine(itemDescription.toString());
	}

	@Override
	public void afterItem(Item item) {
		_printer.printBufferLine();
		_printer.writeBlankLines(1, 0);
	}

	@Override
	public void beforeAttribute(Attribute attribute) {
		
		StringBuilder attributeValue = new StringBuilder();
		
		au.org.ala.delta.model.Character character = attribute.getCharacter();
		attributeValue.append(Integer.toString(character.getCharacterId()));
		
		String value = getAttributeValue(attribute);
	    value = _itemFormatter.defaultFormat(value);
		if (StringUtils.isNotEmpty(value)) {
			CommentedValueList parsedAttribute = _parser.parse(value);
			String charComment = parsedAttribute.getCharacterComment();
			attributeValue.append(charComment);
			
			if (!value.equals(charComment)) {
				attributeValue.append(",");
				attributeValue.append(value.substring(charComment.length()));
			}
			
		}
		else {
			attributeValue.append(",");
		}
		// This is here for CONFOR compatibility, which makes testing
		// easier.
		attributeValue.append(" ");
		
		output(attributeValue.toString());
	}

	protected String getAttributeValue(Attribute attribute) {
		
		String value = null;
		if (attribute instanceof TextAttribute) {
			value = getTextAttributeValue(attribute);
		}
		else if (attribute instanceof MultiStateAttribute) {
            MultiStateAttribute msAttr = (MultiStateAttribute) attribute;
            value = getMultiStateAttributeValue(msAttr);
        }
		else if (attribute instanceof NumericAttribute) {
			value = getNumericAttributeValue((NumericAttribute)attribute);
		}
		return Utils.despaceRtf(value, false);
	}
	
	protected String getTextAttributeValue(Attribute attribute) {
		return attribute.getValueAsString();
	}
	
	protected String getMultiStateAttributeValue(MultiStateAttribute attribute) {
		String value = attribute.getValueAsString();
		if (attribute.isImplicit()) {
            value = Integer.toString(attribute.getImplicitValue());
        }
		return value;
	}
	
	protected String getNumericAttributeValue(NumericAttribute attribute) {
		String value = attribute.getValueAsString();
		return _attributeFormatter.formatNumericAttribute(value);
	}

	@Override
	public void afterAttribute(Attribute attribute) {
		_itemTypeSetter.afterAttribute(attribute);
	}

	@Override
	public void afterLastItem() {}

	@Override
	public void attributeComment(String comment) {}

	@Override
	public void attributeValues(Values values) {}

	
	private void output(String value) {
		_printer.writeJustifiedText(value, -1);
	}
	
	@Override
	public void beforeFirstCharacter() {
		_typeSetter.beforeFirstCharacter();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void beforeCharacter(Character character) {
		_printer.setIndent(0);
		StringBuilder charDescription = new StringBuilder();
		charDescription.append("#");
		charDescription.append(_characterFormatter.formatCharacterDescription(character));
		charDescription.append("/");
		outputLine(charDescription.toString());
		
		if (character.getCharacterType().isMultistate()) {
			outputCharacterStates((MultiStateCharacter)character);
		}
		else if (character.getCharacterType().isNumeric()) {
			outputUnits((NumericCharacter<? extends Number>)character);
		}
	}
	
	protected void outputLine(String line) {
		_printer.outputLine(line);
	}
	
	public void afterCharacter(Character character) {
		_printer.writeBlankLines(1, 0);
	}
	
	protected void outputCharacterStates(MultiStateCharacter character) {
		for (int i=1; i<=character.getNumberOfStates(); i++) {
			outputState(character, i);
		}
	}
	
	protected void outputUnits(NumericCharacter<? extends Number> character) {
		if (character.hasUnits()) {
			_typeSetter.beforeStateDescription();
			outputLine(_characterFormatter.formatUnits(character)+"/");
		}
	}
	
	protected void outputState(MultiStateCharacter character, int stateNumber) {
		_typeSetter.beforeStateDescription();
		outputLine(_characterFormatter.formatState(character, stateNumber)+"/");
	}
	
	@Override
	public void translateOutputParameter(OutputParameter parameter) {
		_printer.setIndent(0);
		
		if (parameter.fullText.startsWith("#")) {
			outputLine("*"+parameter.fullText.substring(1));
		}
		else {
			if (parameter.fullText.contains(" #")) { 
				outputLine(parameter.fullText.replaceAll(" #", " *"));
			}
			else {
				_printer.setIndent(7);
				outputLine(parameter.fullText);
			}
		}
	}
}

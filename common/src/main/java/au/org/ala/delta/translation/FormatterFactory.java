package au.org.ala.delta.translation;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.model.format.AttributeFormatter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;

/**
 * Creates formatter classes using the formatting options supplied in the
 * DeltaContext.
 */
public class FormatterFactory {
	
	private DeltaContext _context;
	
	public FormatterFactory(DeltaContext context) {
		_context = context;
	}
	
	public ItemFormatter createItemFormatter(ItemListTypeSetter typeSetter) {
		return createItemFormatter(typeSetter, CommentStrippingMode.RETAIN, false);
	}
	
	public ItemFormatter createItemFormatter(ItemListTypeSetter typeSetter, CommentStrippingMode mode, boolean includeNumber) {
		ItemFormatter formatter = null;
		if (_context.isOmitTypeSettingMarks()) {
			formatter = new ItemFormatter(includeNumber, mode, AngleBracketHandlingMode.RETAIN, true, false, false);
		}
		else if (typeSetter == null) {
			formatter = new ItemFormatter(includeNumber, mode, AngleBracketHandlingMode.RETAIN, false, false, false);
		}
		else {
			formatter = new TypeSettingItemFormatter(typeSetter);
		}
		formatter.setRtfToHtml(_context.getOutputHtml());
			
		return formatter;
	}
	
	public CharacterFormatter createCharacterFormatter() {
		return createCharacterFormatter(false, false);
	}
	
	public CharacterFormatter createCharacterFormatter(boolean includeNumber, boolean capitaliseFirst) {
		CommentStrippingMode mode = CommentStrippingMode.STRIP_ALL;
		if (_context.getTranslateType() == TranslateType.IntKey) {
			if (_context.getOmitInnerComments()) {
				mode = CommentStrippingMode.STRIP_INNER;
			}
		}
		return createCharacterFormatter(includeNumber, capitaliseFirst, mode);
	}
	
	public CharacterFormatter createCharacterFormatter(boolean includeNumber, boolean capitaliseFirst, CommentStrippingMode mode) {
		CharacterFormatter formatter =  new CharacterFormatter(includeNumber, mode, AngleBracketHandlingMode.RETAIN, _context.isOmitTypeSettingMarks(), capitaliseFirst);
		
		if (_context.getOmitInnerComments()) {
			mode = CommentStrippingMode.STRIP_INNER;
		}
		
		formatter.setRtfToHtml(_context.getOutputHtml());
		
		return formatter;
	}
	
	public AttributeFormatter createAttributeFormatter() {
		AttributeFormatter formatter = null;
		if (_context.isOmitTypeSettingMarks()) {
			formatter = new AttributeFormatter(false, true, CommentStrippingMode.RETAIN);
		}
		else {
			formatter = new TypeSettingAttributeFormatter();
		}
		formatter.setRtfToHtml(_context.getOutputHtml());
		
		return formatter;
	}
}

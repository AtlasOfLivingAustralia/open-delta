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
	
	public ItemFormatter createItemFormatter(TypeSetter typeSetter) {
		return createItemFormatter(typeSetter, CommentStrippingMode.RETAIN);
	}
	
	public ItemFormatter createItemFormatter(TypeSetter typeSetter, CommentStrippingMode mode) {
		if (_context.isOmitTypeSettingMarks()) {
			return new ItemFormatter(false, mode, AngleBracketHandlingMode.RETAIN, true, false, false);
		}
		else if (typeSetter == null) {
			return new ItemFormatter(false, mode, AngleBracketHandlingMode.RETAIN, false, false, false);
		}
		else {
			return new TypeSettingItemFormatter(typeSetter);
		}
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
		return new CharacterFormatter(includeNumber, mode, AngleBracketHandlingMode.RETAIN, _context.isOmitTypeSettingMarks(), capitaliseFirst);
		
	}
	
	public AttributeFormatter createAttributeFormatter() {
		if (_context.isOmitTypeSettingMarks()) {
			return new AttributeFormatter(false, true, CommentStrippingMode.RETAIN);
		}
		else {
			return new TypeSettingAttributeFormatter();
		}
	}
}

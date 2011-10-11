package au.org.ala.delta.translation;

import au.org.ala.delta.model.format.ItemFormatter;

/**
 * Extends the ItemFormatter to apply typesetting marks to the item description as appropriate.
 */
public class TypeSettingItemFormatter extends ItemFormatter {

	private ItemListTypeSetter _typeSetter;
	
	public TypeSettingItemFormatter(ItemListTypeSetter typeSetter) {
		this(typeSetter, false);
	}	
	public TypeSettingItemFormatter(ItemListTypeSetter typeSetter, boolean includeNumber) {
		super(includeNumber, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, false, false, false);
		_typeSetter = typeSetter;
	}

	@Override
	public String defaultFormat(String text) {
		
		String formatted = super.defaultFormat(text);
		return _typeSetter.typeSetItemDescription(formatted);
	}
	
	@Override
	public String defaultFormat(String text, CommentStrippingMode commentStrippingMode, AngleBracketHandlingMode angleBracketHandlingMode, boolean stripFormatting, boolean capitaliseFirstWord) {
		
		String formatted = super.defaultFormat(text, commentStrippingMode, angleBracketHandlingMode, stripFormatting, capitaliseFirstWord);
		return _typeSetter.typeSetItemDescription(formatted);
	}
	
}

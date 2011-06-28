package au.org.ala.delta.translation;

import au.org.ala.delta.model.format.ItemFormatter;

/**
 * Extends the ItemFormatter to apply typesetting marks to the item description as appropriate.
 */
public class TypeSettingItemFormatter extends ItemFormatter {

	private TypeSetter _typeSetter;
	
	public TypeSettingItemFormatter(TypeSetter typeSetter) {
		super(false, false, false, false, false);
		_typeSetter = typeSetter;
	}

	@Override
	public String defaultFormat(String text) {
		
		String formatted = super.defaultFormat(text);
		return _typeSetter.typeSetItemDescription(formatted);
	}
	
	@Override
	public String defaultFormat(String text, boolean stripComments, boolean stripFormatting) {
		
		String formatted = super.defaultFormat(text, stripComments, stripFormatting);
		return _typeSetter.typeSetItemDescription(formatted);
	}
	
}

package au.org.ala.delta.translation;

import au.org.ala.delta.model.format.ItemFormatter;

/**
 * Extends the ItemFormatter to apply typesetting marks to the item description as appropriate.
 */
public class TypeSettingItemFormatter extends ItemFormatter {

	private TypeSetter _typeSetter;
	
	public TypeSettingItemFormatter(TypeSetter typeSetter) {
		super(false, false, false, true, false);
		_typeSetter = typeSetter;
	}

	@Override
	public String defaultFormat(String text) {
		
		String formatted = super.defaultFormat(text);
		return _typeSetter.typeSetItemDescription(formatted);
	}
	
	
}

package au.org.ala.delta.model.format;

import au.org.ala.delta.model.Item;

/**
 * Knows how to format items in a standard way.
 */
public class ItemFormatter extends Formatter {
	
	private boolean _includeNumber;
	private String variant;
	
	public ItemFormatter() {
		this(true, false, false, false, true);
	}
	
	public ItemFormatter(boolean includeNumber, boolean stripComments, boolean replaceAngleBrackets, boolean stripRtf, boolean useShortVariant) {
		super(stripComments, replaceAngleBrackets, stripRtf);
		_includeNumber = includeNumber;

		if (useShortVariant) {
			variant = "(+)";
		}
		else {
			variant = "(variant)";
		}
	}
	
	/**
	 * Formats the supplied item in a standard way according to the parameters supplied at 
	 * construction time.
	 * @param Item the item to format.
	 * @return a String representing the supplied Item.
	 */
	public String formatItemDescription(Item item) {
	
		return formatItemDescription(item, _stripComments);
		
	}
	
	/**
	 * Formats the supplied item in a standard way according to the parameters supplied at 
	 * construction time.
	 * @param Item the item to format.
	 * @param stripComments true if comments should be removed from the description.
	 * @return a String representing the supplied Item.
	 */
	public String formatItemDescription(Item item, boolean stripComments) {
		
		StringBuilder builder = new StringBuilder();
		if (_includeNumber) {
			builder.append(item.getItemNumber()).append(". ");
		}
		if (item.isVariant()) {
			builder.append(variant).append(" ");
		}
		String description = item.getDescription();
		description = defaultFormat(description, stripComments, _stripFormatting);
		
		builder.append(description);
		return builder.toString();
	}
}

package au.org.ala.delta.model.format;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.rtf.RTFUtils;

/**
 * Knows how to format items in a standard way.
 */
public class ItemFormatter {
	
	private boolean _includeNumber;
	private boolean _stripRtf;
	private String variant;
	
	public ItemFormatter() {
		this(true, false, true);
	}
	
	public ItemFormatter(boolean includeNumber, boolean stripRtf, boolean useShortVariant) {
		_includeNumber = includeNumber;
		_stripRtf = stripRtf;
		if (useShortVariant) {
			variant = "(+) ";
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
	
		StringBuilder builder = new StringBuilder();
		if (_includeNumber) {
			builder.append(item.getItemNumber()).append(". ");
		}
		if (item.isVariant()) {
			builder.append(variant);
		}
		String description = item.getDescription();
		if (StringUtils.isNotEmpty(description) && _stripRtf) {
			description = RTFUtils.stripFormatting(description);
		}
		builder.append(description);
		return builder.toString();
		
	}

}

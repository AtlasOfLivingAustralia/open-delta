package au.org.ala.delta.translation;

import au.org.ala.delta.model.format.AttributeFormatter;

/**
 * Extends the AttributeFormatter to recognise numeric ranges in comments
 * and format the separator correctly.
 */
public class TypeSettingAttributeFormatter extends AttributeFormatter {

	private static final String DEFAULT_RANGE_SEPARATOR = "\u2013";
	
	private String _numericRangeSeparator;
	
	public TypeSettingAttributeFormatter() {
		this(DEFAULT_RANGE_SEPARATOR);
	}
	
	public TypeSettingAttributeFormatter(String numericRangeSeparator) {
		super(false, false, CommentStrippingMode.RETAIN);
		_numericRangeSeparator = numericRangeSeparator;
	}
	
	public TypeSettingAttributeFormatter(String numericRangeSeparator, CommentStrippingMode commentMode, AngleBracketHandlingMode angleMode) {
		super(false, false, commentMode, angleMode);
		_numericRangeSeparator = numericRangeSeparator;
	}

	@Override
	public String formatComment(String comment) {
		comment = super.formatComment(comment);
		if (comment.startsWith("-")) {
			comment = _numericRangeSeparator+comment.substring(1);
		}
		return comment;
	}
}

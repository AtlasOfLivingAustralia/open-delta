package au.org.ala.delta.model.format;

public class DirectiveTextFormatter {

	/** 
	 * Converts any trailing space on a line and any newlines into a single
	 * space character.
	 * @param text the text to format.
	 * @return the formatted text.
	 */
	public static String newLinesToSpace(String text) {
		
		text = text.replaceAll(" +[\r\n]+", " ");
		text = text.replaceAll("[\r\n]+", " ");
		
		return text;
	}
}

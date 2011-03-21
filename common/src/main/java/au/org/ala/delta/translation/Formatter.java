package au.org.ala.delta.translation;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Utils;

/**
 * Performs context sensitive formatting of Items, Characters and Attributes.
 */
public class Formatter {

	private DeltaContext _context;
	
	public Formatter(DeltaContext context) {
		_context = context;
	}
	
	
	public String formatTaxonName(String name) {
		
		if (_context.isReplaceAngleBrackets()) {
			name = Utils.removeComments(name, 0, false, true, false, true);
			name = RTFUtils.stripFormatting(name);
		}
		return name;
	}
	
	public String formatAttribute(Character character, String attribute) {
		attribute = Utils.removeComments(attribute, 1, true, true, false, true);
		attribute = RTFUtils.stripFormatting(attribute);
		if (character.getCharacterType().isNumeric()) {
			attribute = formatNumericAttribute((NumericCharacter<?>)character, attribute);
		}
		else if (character.getCharacterType().isMultistate()) {
			attribute = formatMultiStateAttribute((MultiStateCharacter)character, attribute);
		}
		return attribute;
	}
	
	public String formatCharacterName(String name) {
		if (_context.isReplaceAngleBrackets()) {
			name = Utils.removeComments(name, 1, false, false, false, true);
			
		}
		else {
			name = Utils.removeComments(name, 1, false, false, false, true);
		}
		name = RTFUtils.stripFormatting(name);
		return name;
	}
	
	private String formatNumericAttribute(NumericCharacter<?> character, String attribute) {
		
		return attribute + " " + character.getUnits();
	}
	
	private String formatMultiStateAttribute(MultiStateCharacter character, String attribute) {
		
		String[] values = attribute.split("/");
		
		for (int i=0; i<values.length; i++) {
			
			String[] v2s = values[i].split("&");
			
			for (int j = 0; j<v2s.length; j++) {
				
				String[] v3s = v2s[j].split("-");
				
				for (int k = 0; k<v3s.length; k++) {
					
					v3s[k] = getState(v3s[k], character);
				}
				
				v2s[j] = StringUtils.join(v3s, " to ");
			}
			
			values[i] = StringUtils.join(v2s, " and ");
			
		}
		
		return StringUtils.join(values, " or ");
	}
	
	
	private String getState(String value, MultiStateCharacter character) {
		String valueWithoutComments = removeComments(value);
		
		if ("V".equals(valueWithoutComments)) {
			return "variable";
		}
		if ("U".equals(valueWithoutComments)) {
			return "unknown";
		}
		if ("-".equals(valueWithoutComments)) {
			return "not applicable";
		}
		try {
			int stateNum = Integer.parseInt(valueWithoutComments);
			return character.getState(stateNum);
		}
		catch (Exception e) {
			System.out.println(valueWithoutComments);
		}
		return "";
	}
	
	private String removeComments(String value) {
		// Only 2 valid locations for comments, before and after the number.
		value = value.trim();
		int closeCommentPos = value.lastIndexOf('>');
		if (closeCommentPos < 0) {
			return value;
		}
		if (value.startsWith("<")) {
			return value.substring(closeCommentPos).trim();
		}
		else {
			return value.substring(closeCommentPos, value.length()).trim();
		}
	}
	
}

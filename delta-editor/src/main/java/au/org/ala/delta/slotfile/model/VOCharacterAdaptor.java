package au.org.ala.delta.slotfile.model;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.impl.CharacterData;
import au.org.ala.delta.slotfile.TextType;
import au.org.ala.delta.slotfile.VOCharBaseDesc;
import au.org.ala.delta.slotfile.VOCharTextDesc;

public class VOCharacterAdaptor implements CharacterData {
	
	private VOCharBaseDesc _charBase;
	private VOCharTextDesc _textDesc;
	
	public VOCharacterAdaptor(VOCharBaseDesc charBase, VOCharTextDesc textDesc) {
		_charBase = charBase;
		_textDesc = textDesc;
	}

	
	@Override
	public String getName() {
		List<String> states = new ArrayList<String>();
		String[] text = _textDesc.ReadAllText(TextType.RTF, states);
		return text[0];
	}

	
	@Override
	public String getDescription() {
		List<String> states = new ArrayList<String>();
		String[] text = _textDesc.ReadAllText(TextType.RTF, states);
		return text[1];
	}
	
	public VOCharBaseDesc getCharBaseDesc() {
		return _charBase;
	}

}

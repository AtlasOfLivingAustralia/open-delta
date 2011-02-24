package au.org.ala.delta.slotfile.model;

import au.org.ala.delta.model.impl.AttributeData;
import au.org.ala.delta.slotfile.Attribute;
import au.org.ala.delta.slotfile.TextType;
import au.org.ala.delta.slotfile.VOCharBaseDesc;
import au.org.ala.delta.slotfile.VOItemDesc;

public class VOAttributeAdaptor implements AttributeData {

	
	
	private VOItemDesc _itemDesc;
	private VOCharBaseDesc _charBaseDesc;
	
	
	public VOAttributeAdaptor(VOItemDesc itemDesc, VOCharBaseDesc charBaseDesc) {
		_itemDesc = itemDesc;
		_charBaseDesc = charBaseDesc;
		
	}

	@Override
	public String getValue() {
		
		return _itemDesc.readAttributeAsText(_charBaseDesc.getUniId(), TextType.RTF);
	}

	@Override
	public void setValue(String value) {
		Attribute attribute = new Attribute(value, _charBaseDesc);
		_itemDesc.writeAttribute(attribute);
	}

}

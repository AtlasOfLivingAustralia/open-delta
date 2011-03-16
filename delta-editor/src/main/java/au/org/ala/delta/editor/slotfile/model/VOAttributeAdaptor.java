package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.Attribute;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.impl.AttributeData;

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

	@Override
	public boolean isStatePresent(int stateNumber) {
		int stateId = _charBaseDesc.uniIdFromStateNo(stateNumber);
		Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
		if (attribute == null) {
			return false;
		}
		return attribute.encodesState(_charBaseDesc, stateId, true);
	}

	@Override
	public boolean isSimple() {
		if ((_itemDesc == null) || (_charBaseDesc == null)) {
			return true;
		}
		Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
		if (attribute != null) {
			return attribute.isSimple(_charBaseDesc);
		}
		
		return true;
	}
	
	

}

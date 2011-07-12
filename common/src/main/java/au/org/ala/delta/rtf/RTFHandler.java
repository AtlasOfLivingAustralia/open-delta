package au.org.ala.delta.rtf;

import java.util.List;

public interface RTFHandler {
	
	void startParse();
	
	void onKeyword(String keyword, boolean hasParam, int param);
	
	void onHeaderGroup(String group);
	
	void onTextCharacter(char ch);
	
	void onCharacterAttributeChange(List<AttributeValue> changes);
	
	void onParagraphAttributeChange(List<AttributeValue> changes);
	
	void startParagraph();
	
	void endParagraph();
	
	void endParse();
}

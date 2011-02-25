package au.org.ala.delta.rtf;

public interface RTFHandler {
	
	void startParse();
	
	void onKeyword(String keyword, boolean hasParam, int param);
	
	void onHeaderGroup(String group);
	
	void onTextCharacter(char ch);
	
	void endParse();

}

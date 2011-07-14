package au.org.ala.delta.rtf;

import java.util.List;

public class RTFHandlerAdapter implements RTFHandler {

    @Override
    public void startParse() {
    }

    @Override
    public void onKeyword(String keyword, boolean hasParam, int param) {
    }

    @Override
    public void onHeaderGroup(String keyword, String content) {
    }

    @Override
    public void onTextCharacter(char ch) {
    }

    @Override
    public void endParse() {
    }

    @Override
    public void onCharacterAttributeChange(List<AttributeValue> changes) {
    }

    @Override
    public void onParagraphAttributeChange(List<AttributeValue> changes) {
    }

    @Override
    public void startParagraph() {
    }

    @Override
    public void endParagraph() {
    }

}

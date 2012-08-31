/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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

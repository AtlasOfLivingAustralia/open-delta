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

import java.util.HashMap;
import java.util.Map;

public abstract class Keyword {

    public static Map<String, Keyword> KEYWORDS = new HashMap<String, Keyword>();

    public static void registerKeyword(Keyword keywordDesc) {
        KEYWORDS.put(keywordDesc.getKeyword(), keywordDesc);
    }

    static {

        // Attribute keywords (keywords that alter character or paragraph attributes)
        for (CharacterAttributeType attribute : CharacterAttributeType.values()) {
            registerKeyword(new CharacterAttributeKeyword(attribute.keyword(), attribute, 1, false));
        }

        for (ParagraphAttributeType attribute : ParagraphAttributeType.values()) {
            registerKeyword(new ParagraphAttributeKeyword(attribute.keyword(), attribute, 1, false));
        }

        // Character literal keywords...
        registerKeyword(new CharacterKeyword("\r", '\r'));
        registerKeyword(new CharacterKeyword("\n", '\n'));
        registerKeyword(new CharacterKeyword("line", '\n'));
        registerKeyword(new CharacterKeyword("tab", '\t'));
        registerKeyword(new CharacterKeyword("page", '\f'));
        registerKeyword(new CharacterKeyword("lquote", (char) 0x2018));
        registerKeyword(new CharacterKeyword("rquote", (char) 0x2019));
        registerKeyword(new CharacterKeyword("ldblquote", (char) 0x201c));
        registerKeyword(new CharacterKeyword("rdblquote", (char) 0x201d));
        registerKeyword(new CharacterKeyword("bullet", (char) 0x2022));
        registerKeyword(new CharacterKeyword("endash", (char) 0x2013));
        registerKeyword(new CharacterKeyword("emdash", (char) 0x2014));
        registerKeyword(new CharacterKeyword("enspace", (char) 0x2002));
        registerKeyword(new CharacterKeyword("emspace", (char) 0x2003));

        // This is the cheats way of allowing an escaped grouping bracket.
        registerKeyword(new CharacterKeyword("{", '{'));
        registerKeyword(new CharacterKeyword("}", '}'));

        // Destinations...
        registerKeyword(new DestinationKeyword("fonttbl", DestinationState.Header));
        registerKeyword(new DestinationKeyword("colortbl", DestinationState.Header));
        registerKeyword(new DestinationKeyword("info", DestinationState.Header));
        registerKeyword(new DestinationKeyword("stylesheet", DestinationState.Header));
        
        registerKeyword(new DestinationKeyword("*", DestinationState.Skip));

        // Special keywords
        registerKeyword(new UnicodeKeyword("u"));
        registerKeyword(new CodePageKeyword());
    }

    protected String _keyword;
    protected KeywordType _type;

    public Keyword(String keyword, KeywordType type) {
        _keyword = keyword;
        _type = type;
    }

    public String getKeyword() {
        return _keyword;
    }

    public KeywordType getKeywordType() {
        return _type;
    }

    public static AttributeKeyword findCharacterAttributeKeyword(CharacterAttributeType attrType) {
        for (Keyword kwd : KEYWORDS.values()) {
            if (kwd instanceof CharacterAttributeKeyword) {
                CharacterAttributeKeyword attrKwd = (CharacterAttributeKeyword) kwd;
                if (attrKwd.getType() == attrType) {
                    return attrKwd;
                }
            }
        }

        return null;
    }
    
    public static AttributeKeyword findParagraphAttributeKeyword(ParagraphAttributeType attrType) {
        for (Keyword kwd : KEYWORDS.values()) {
            if (kwd instanceof ParagraphAttributeKeyword) {
                ParagraphAttributeKeyword attrKwd = (ParagraphAttributeKeyword) kwd;
                if (attrKwd.getType() == attrType) {
                    return attrKwd;
                }
            }
        }

        return null;
    }

    public static CharacterKeyword findKeywordForCharacter(char ch) {
        for (Keyword kwd : KEYWORDS.values()) {
            if (kwd instanceof CharacterKeyword) {
                CharacterKeyword charKwd = (CharacterKeyword) kwd;
                if (charKwd.getOutputChar() == ch) {
                    return charKwd;
                }
            }
        }

        return null;
    }

}

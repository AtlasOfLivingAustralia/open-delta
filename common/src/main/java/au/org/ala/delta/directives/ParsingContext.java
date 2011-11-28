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
package au.org.ala.delta.directives;

import java.io.File;

public class ParsingContext {

    private File _file;
    private long _currentOffset;
    private long _currentLine;
    private long _currentDirectiveStartLine;
    private long _currentDirectiveStartOffset;
    private long _currentDirectiveEndOffset;
    private StringBuilder _currentDirectiveText;
    
    public ParsingContext() {

    }
    
    public void setCurrentDirectiveText(StringBuilder text) {
    	_currentDirectiveText = text;
    }
    
    public String getCurrentDirectiveText() {
    	return _currentDirectiveText.toString();
    }

    public File getFile() {
        return _file;
    }

    public void setFile(File file) {
        _file = file;
    }

    public long getCurrentOffset() {
        return _currentOffset;
    }

    public void setCurrentOffset(long offset) {
        _currentOffset = offset;
    }

    public void setCurrentLine(long line) {
        _currentLine = line;
    }

    public long getCurrentLine() {
        return _currentLine;
    }

    public void incrementCurrentOffset() {
        _currentOffset++;
    }

    public void incrementCurrentLine() {
        _currentLine++;
    }

    public long getCurrentDirectiveStartLine() {
        return _currentDirectiveStartLine;
    }

    public void setCurrentDirectiveStartLine(long line) {
        _currentDirectiveStartLine = line;
    }

    public long getCurrentDirectiveStartOffset() {
        return _currentDirectiveStartOffset;
    }

    public void setCurrentDirectiveStartOffset(long offset) {
        _currentDirectiveStartOffset = offset;
    }

	public void markDirectiveEnd() {
		_currentDirectiveEndOffset = _currentOffset-1;
	}
	
	public long getDirectiveEndOffset() {
		return _currentDirectiveEndOffset;
	}
}

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
package au.org.ala.delta.model.attribute;

import java.math.BigDecimal;


public class AttrChunk {

	private int _type;
	private BigDecimal _numVal;
	private String _strVal;
	private int _stateVal;

	public AttrChunk() {
		this(ChunkType.CHUNK_STOP, 0);
	}

	public AttrChunk(int chunkType) {
		this(chunkType, 0);
	}

	public AttrChunk(int chunkType, int stateId) {
		_type = chunkType;
		_stateVal = stateId;
	}

	public AttrChunk(int chunkType, BigDecimal src) {
		this(chunkType);
		_numVal = src;
	}

	public AttrChunk(String src) {
		_strVal = src;
		_type = (src.length() < 0x0000ffff) ? ChunkType.CHUNK_TEXT : ChunkType.CHUNK_LONGTEXT;
	}

	public AttrChunk(BigDecimal src) {
		this(ChunkType.CHUNK_NUMBER);

		_numVal = src;
	}

	public int getType() {
		return _type;
	}

	public String getString() {
		return _strVal;
	}
	
	public void setString(String strVal) {
		_strVal = strVal;		
	}

	public BigDecimal getNumber() {
		return _numVal;
	}

	public int getStateId() {
		return _stateVal;
	}

	public String getAsText(boolean encloseInCommentBrackets) {
		return new DefaultAttributeChunkFormatter(encloseInCommentBrackets).formatChunk(this);
	}
	
	public String getAsText(AttributeChunkFormatter formatter) {
		return formatter.formatChunk(this);
	}

	public void setStateId(int stateVal) {
		_stateVal = stateVal;		
	}

	public void setNumber(BigDecimal d) {
		_numVal = d;		
	}
	
	public boolean isTextChunk() {		
		return _type == ChunkType.CHUNK_TEXT || _type == ChunkType.CHUNK_LONGTEXT;
	}


}

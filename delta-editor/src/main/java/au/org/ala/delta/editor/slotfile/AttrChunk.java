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
package au.org.ala.delta.editor.slotfile;

public class AttrChunk {

	private int _type;
	private DeltaNumber _numVal;
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

	public AttrChunk(int chunkType, DeltaNumber src) {
		this(chunkType);
		_numVal = src;
	}

	public AttrChunk(String src) {
		_strVal = src;
		_type = (src.length() < 0x0000ffff) ? ChunkType.CHUNK_TEXT : ChunkType.CHUNK_LONGTEXT;
	}

	public AttrChunk(DeltaNumber src) {
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

	public DeltaNumber getNumber() {
		return _numVal;
	}

	public int getStateId() {
		return _stateVal;
	}

	public String getAsText(VOCharBaseDesc charBase) {
		String dest = null;
		switch (_type) {
			case ChunkType.CHUNK_STOP:
				dest = "";
				break;

			case ChunkType.CHUNK_TEXT:
			case ChunkType.CHUNK_LONGTEXT:
				if (charBase == null)
					dest = _strVal;
				else
					dest = "<" + _strVal + ">";
				break;
			case ChunkType.CHUNK_STATE:
				if (charBase != null) {
					dest = charBase.stateNoFromUniId(_stateVal) + "";
				} else {
					dest = "State " + _stateVal;
				}
				break;

			case ChunkType.CHUNK_NUMBER:
				dest = _numVal.asString();
				break;
			case ChunkType.CHUNK_EXLO_NUMBER:
				dest = "(" + _numVal.asString() + "-)";
				break;
			case ChunkType.CHUNK_EXHI_NUMBER:
				dest = "(-" + _numVal.asString() + ")";
				break;
			case ChunkType.CHUNK_VARIABLE:
				dest = "V";
				break;
			case ChunkType.CHUNK_UNKNOWN:
				dest = "U";
				break;
			case ChunkType.CHUNK_INAPPLICABLE:
				dest = "-";
				break;

			case ChunkType.CHUNK_OR:
				dest = "/";
				break;

			case ChunkType.CHUNK_AND:
				dest = "&";
				break;

			case ChunkType.CHUNK_TO:
				dest = "-";
				break;

			/*
			 * case CHUNK_IMPLICIT: if (charBase) { TStateId implicitId =
			 * charBase->GetCodedImplicit(); if (implicitId == STATEID_NULL)
			 * implicitId = charBase->GetUncodedImplicit(); if (implicitId !=
			 * STATEID_NULL) dest =
			 * std::string(ultoa(charBase->StateNoFromUniId(implicitId), numBuf,
			 * 10)); } break;
			 */

			default:
				dest = "";
				break;
		}

		return dest;
	}

	public void setStateId(int stateVal) {
		_stateVal = stateVal;		
	}

	public void setNumber(DeltaNumber d) {
		_numVal = d;		
	}
	
	public boolean isTextChunk() {		
		return _type == ChunkType.CHUNK_TEXT || _type == ChunkType.CHUNK_LONGTEXT;
	}


}

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
package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.AttrChunk;
import au.org.ala.delta.editor.slotfile.Attribute;
import au.org.ala.delta.editor.slotfile.Attribute.AttrIterator;
import au.org.ala.delta.editor.slotfile.ChunkType;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.NumericRange;
import au.org.ala.delta.model.attribute.ParsedAttribute;
import au.org.ala.delta.model.impl.AttributeData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements AttributeData using the functionality provided by the VOItemDesc, VOCharBaseDesc
 * and Attribute classes.
 */
public class VOAttributeAdaptor implements AttributeData {

    private VOItemDesc _itemDesc;
    private VOCharBaseDesc _charBaseDesc;
    private DeltaVOP _vop;

    public VOAttributeAdaptor(VOItemDesc itemDesc, VOCharBaseDesc charBaseDesc) {
        _itemDesc = itemDesc;
        _charBaseDesc = charBaseDesc;
        _vop = (DeltaVOP)itemDesc.getVOP();

    }

    @Override
    public String getValueAsString() {
    	synchronized (_vop) {
    		return _itemDesc.readAttributeAsText(_charBaseDesc.getUniId(), TextType.RTF);
		}
    }

    @Override
    public void setValueFromString(String value) {
    	synchronized (_vop) {
	        Attribute attribute = new Attribute(value, _charBaseDesc);
	        _itemDesc.writeAttribute(attribute);
    	}
    }

    @Override
    public boolean isStatePresent(int stateNumber) {
    	synchronized (_vop) {
	        int stateId = _charBaseDesc.uniIdFromStateNo(stateNumber);
	        Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	        if (attribute == null) {
	            return false;
	        }
	        return attribute.encodesState(_charBaseDesc, stateId, true);
    	}
    }

    @Override
    public void setStatePresent(int stateNumber, boolean present) {
        if (isStatePresent(stateNumber) != present) {
            toggleStatePresent(stateNumber);
        }
    }

    /**
     * If the specified state is present, it will be removed otherwise it will
     * be added.
     * 
     * @param stateNumber
     *            the state number to toggle.
     */
    public void toggleStatePresent(int stateNumber) {
    	synchronized (_vop) {
	        Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	
	        boolean changeMade = false;
	        int stateId = _charBaseDesc.uniIdFromStateNo(stateNumber);
	        if (attribute != null) {
	            if (!attribute.isSimple(_charBaseDesc)) {
	                return;
	            }
	            int previousStateNumber = 0;
	            AttrIterator i = (AttrIterator) attribute.iterator();
	            AttrIterator prevStateIterator = (AttrIterator) attribute.iterator();
	
	            while (i.hasNext()) {
	                AttrChunk chunk = i.get();
	                if (chunk.getType() == ChunkType.CHUNK_STATE) {
	                    int currentStateNumber = _charBaseDesc.stateNoFromUniId(chunk.getStateId());
	                    if (currentStateNumber == stateNumber) {
	                        if (i.getPos() == 0) {
	                            // The attribute iterator implementation doesn't
	                            // throw
	                            // concurrent modification exceptions so we can get
	                            // away with this....
	                            attribute.erase(i.getPos());
	                            i = (AttrIterator) attribute.iterator();
	                            if (i.hasNext()) {
	                                chunk = i.get();
	                                if (chunk.getType() == ChunkType.CHUNK_OR) {
	                                    attribute.erase(i.getPos());
	                                }
	                            }
	
	                        } else if (previousStateNumber != 0) {
	                            prevStateIterator.increment();
	                            AttrChunk previousChunk = prevStateIterator.get();
	                            if (previousChunk.getType() == ChunkType.CHUNK_OR) {
	                                i.increment();
	                                attribute.erase(prevStateIterator.getPos(), i.getPos());
	                            }
	                        }
	                        changeMade = true;
	                        break;
	                    } else if (_charBaseDesc.testCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE)) {
	                        attribute.erase(i.getPos());
	                    } else if (currentStateNumber > stateNumber) {
	                        int pos = attribute.insert(i.getPos(), new AttrChunk(ChunkType.CHUNK_STATE, stateId));
	                        pos = attribute.insert(pos, new AttrChunk(ChunkType.CHUNK_OR));
	                        i.setPos(pos);
	                        changeMade = true;
	                        break;
	                    } else {
	                        prevStateIterator.setPos(i.getPos());
	                        previousStateNumber = currentStateNumber;
	                    }
	                } else if (chunk.getType() != ChunkType.CHUNK_OR) {
	                    if (_charBaseDesc.testCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE)) {
	                        attribute.erase(i.getPos());
	                        break;
	                    }
	                }
	                i.increment();
	            }
	            if (i.getPos() == attribute.end() && !changeMade) {
	                if (attribute.getNChunks() > 0) {
	                    attribute.insert(attribute.end(), new AttrChunk(ChunkType.CHUNK_OR));
	                }
	                attribute.insert(attribute.end(), new AttrChunk(ChunkType.CHUNK_STATE, stateId));
	                changeMade = true;
	            }
	        } else {
	            attribute = new Attribute();
	            attribute.setCharId(_charBaseDesc.getUniId());
	            attribute.insert(0, new AttrChunk(ChunkType.CHUNK_STATE, stateId));
	            changeMade = true;
	        }
	
	        if (changeMade) {
	            _itemDesc.writeAttribute(attribute);
	        }
    	}
    }

    @Override
    public boolean isSimple() {
    	synchronized (_vop) {
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

    @Override
    public boolean isUnknown() {
    	synchronized (_vop) {
	    	 Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	    	 return attribute == null || attribute.isUnknown();
    	}
    }
    
    @Override
    public boolean isCodedUnknown() {
    	synchronized (_vop) {
	    	 Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	    	 
	    	 return attribute != null && attribute.isUnknown();
    	}
    }

    @Override
    public boolean isInapplicable() {
    	synchronized (_vop) {
	    	 Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	    	 return attribute != null && attribute.isInapplicable();
    	}
    }
    
    @Override
    public boolean isExclusivelyInapplicable(boolean ignoreComment) {
    	synchronized (_vop) {
	    	 Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	    	 return attribute != null && attribute.isExclusivelyInapplicable(ignoreComment);
    	}
    }

    @Override
    public FloatRange getRealRange() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRealRange(FloatRange range) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> getPresentStateOrIntegerValues() {
    	synchronized (_vop) {
	        Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	        List<Integer> stateIds = new ArrayList<Integer>();
	        short[] pseudoValues = new short[1];
	        if (attribute != null) {
	            attribute.getEncodedStates(_charBaseDesc, stateIds, pseudoValues);
	        }
	        Set<Integer> states = new HashSet<Integer>(stateIds.size());
	        for (int id : stateIds) {
	            states.add(_charBaseDesc.stateNoFromUniId(id));
	        }
	        return states;
    	}
    }

    @Override
    public List<Integer> getPresentStatesAsList() {
    	synchronized (_vop) {
	        Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	        List<Integer> stateIds = new ArrayList<Integer>();
	        short[] pseudoValues = new short[1];
	        if (attribute != null) {
	            attribute.getEncodedStates(_charBaseDesc, stateIds, pseudoValues);
	        }
	        List<Integer> states = new ArrayList<Integer>(stateIds.size());
	        for (int id : stateIds) {
	            states.add(_charBaseDesc.stateNoFromUniId(id));
	        }
	        return states;
    	}
    }
    
    @Override
    public void setPresentStateOrIntegerValues(Set<Integer> values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVariable() {
    	synchronized (_vop) {
	        boolean variable = false;
	        Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
	        if (attribute != null) {
	            List<Integer> stateIds = new ArrayList<Integer>();
	            short[] pseudoValues = new short[1];
	            attribute.getEncodedStates(_charBaseDesc, stateIds, pseudoValues);
	            variable = ((pseudoValues[0] & VOItemDesc.PSEUDO_VARIABLE) != 0);
	        }
	
	        return variable;
    	}
    }

    @Override
    public boolean hasValueSet() {
        return !getPresentStateOrIntegerValues().isEmpty() || !StringUtils.isEmpty(getValueAsString());
    }

	@Override
	public boolean isRangeEncoded() {
		synchronized (_vop) {
			 Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
		     if (attribute != null) {
		         for (AttrChunk chunk : attribute) {
		        	if (chunk.getType() == ChunkType.CHUNK_TO) {
		        		return true;
		        	}
		         }
		     }
		     return false;
		}
	}

	@Override
    public boolean isCommentOnly() {
		synchronized (_vop) {
	    	Attribute attribute = _itemDesc.readAttribute(_charBaseDesc.getUniId());
		     if (attribute != null) {
		         for (AttrChunk chunk : attribute) {
		        	if ((chunk.getType() != ChunkType.CHUNK_TEXT) &&
		        	    (chunk.getType() != ChunkType.CHUNK_LONGTEXT)) {
		        		return false;
		        	}
		         }
		     }
		     return true;
		}
    }

	@Override
	public List<NumericRange> getNumericValue() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ParsedAttribute parsedAttribute() {
		throw new UnsupportedOperationException();
	}

    @Override
    public boolean isInherited() {
        return false;
    }
}

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

import java.awt.Point;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.util.Utils;

public class VOImageDesc extends VOAnyDesc {

	private ImageFixedData _fixedData;

	public static String[] OLKeywords = new String[] { "text", "item", "feature", "state", "value", "units", "enter", "subject", "sound", "heading", "keyword", "ok", "cancel", "notes", "imagenotes",
			"comment" };
	
	public static final int HAS_X = 1;
	public static final int HAS_Y = 2;
	public static final int HAS_W = 4;
	public static final int HAS_H = 8;
	public static final int HAS_ALL_DIMS = (HAS_X | HAS_Y | HAS_W | HAS_H);
	public static final int OL_OMIT_DESCRIPTION = 0x1;
	public static final int OL_INCLUDE_COMMENTS = 0x2;
	public static final int OL_CENTER_TEXT = 0x4;
	public static final int OL_INTEGRAL_HEIGHT = 0x8;
	public static final int OLOC_FLAG_COLOUR_MASK = 0x00ffffff;
	public static final int OLOC_FLAG_HOTSPOT   =  0x01000000;
	public static final int OLOC_FLAG_POPUP     =  0x02000000;
	public static final int OLOC_FLAG_COLOUR    =  0x04000000;
	

	public VOImageDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		usedIds = new HashSet<Integer>();
		synchronized (getVOP()) {
			_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
			short diskFixedSize = _slotFile.readShort();

			assert diskFixedSize == ImageFixedData.SIZE;

			_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
			_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
			_fixedData = new ImageFixedData();
			_fixedData.read(_slotFile);

			dataSeek(0);
		}

		// Logger.debug("ImageDesc: OwnerID=%d imageType=%d nameLen=%d nOverlays=%d, alignment=%d", _fixedData.ownerId, _fixedData.imageType, _fixedData.nameLen, _fixedData.nOverlays,
		// _fixedData.alignment);
	}

	@Override
	public int getTypeId() {
		return VODescFactory.VOImageDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Image description";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	public void storeQData() {
		makeTemp();
		byte[] trailerBuf = null;
		int trailerLeng = 0;

		// If the size of TFixedData has been increased (due to a newer program version)
		// re-write the whole slot, using the new size.
		if (_fixedData.fixedSize < ImageFixedData.SIZE) {
		      // Save a copy of all "variable" data
		      trailerBuf = dupTrailingData(trailerLeng);
		      if (trailerBuf != null) {
		    	  trailerLeng = trailerBuf.length;
		      }
		      _dataOffs = SlotFile.SlotHeader.SIZE + ImageFixedData.SIZE; ///// Adjust DataOffs accordingly
		      _fixedData.fixedSize = ImageFixedData.SIZE;
		      // Do seek to force allocation of large enough slot
		      dataSeek(trailerLeng);
		}

		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData.write(_slotFile);

		if (trailerBuf != null) { // If fixedData was resized, re-write the saved, variable-length data
		    dataSeek(0);
		    dataWrite(trailerBuf);
		    dataTruncate();
		}
	}

	public void setOwnerId(int id) {
		_fixedData.ownerId = id;
		setDirty();
	}

	public int getOwnerId() {
		return _fixedData.ownerId;
	}

	public void setImageType(int type) {
		_fixedData.imageType = type;
		setDirty();
	}

	public int getImageType() {
		return _fixedData.imageType;
	}

	public void setButtonAlignment(int align) {
		_fixedData.alignment = (short) align;
		setDirty();
	}

	int GetButtonAlignment() {
		return _fixedData.alignment;
	}
	
	enum ParseState {
	    TEXT,
	    PATH, // Same as text, but without RTF
	    MODIFIER,
	    NOWHERE
	  };

	// Parse a string containing overlay information, and build up the
	// corresponding list of TImageOverlay objects
	// It is assumed that the string includes the surroundings brackets,
	// and any non-space text outside of brackets is considered an error.
	public void parseOverlays(String buffer) throws ParseException {
		List<ImageOverlay> overlayList = new ArrayList<ImageOverlay>();
		usedIds.clear();
		String modifiers = "XYWHTNCMPFE";
		

		  int commentLevel = 0;
		  int textStart = -1;
		  int nHidden = 0;
		  //bool inQuote = false;
		  ParseState parseState = ParseState.NOWHERE;
		  char dims = 0;
		  boolean inRTF = false;
		  boolean inParam = false;
		  boolean inHotspot = false;
		  int bracketLevel = 0;
		  ImageOverlay anOverlay = new ImageOverlay();
		  OverlayLoc olLocation = new OverlayLoc();
		  OverlayLoc hsLocation = new OverlayLoc();
		 
		  for (int i = 0; i < buffer.length(); ++i) {
			  boolean evaluated = false;
			  boolean saveIt = false;
		      char ch = buffer.charAt(i);

		      // If accumulating text, keep track of RTF markup
		      if (commentLevel == 1 && parseState == ParseState.TEXT)  {
		          if (inRTF)
		            {
		              ++nHidden;
		              if (Character.isDigit(ch) || (!inParam && ch == '-')) {
		                  inParam = true;
		              }
		              else if (inParam || !(Character.isLetter(ch))) {
		                  inParam = inRTF = false;
		                  if (ch == '\'' && buffer.charAt(i-i) == '\\')
		                    ++nHidden;
		                  else if (ch != ' ')
		                    --nHidden;
		                }
		            }
		          else if (ch == '{')
		            {
		              ++bracketLevel;
		              ++nHidden;
		            }
		          else if (ch == '}')
		            {
		              --bracketLevel;
		              ++nHidden;
		            }
		          else if (ch == '\\')
		            {
		              ++nHidden;
		              inRTF = true;
		              inParam = false;
		            }
		        }
		      if (Character.isSpaceChar(ch))
		        continue;
		      else if (ch == '<' && (i == 0 || buffer.charAt(i-1) != '|'))
		        {
		          if (++commentLevel == 2)
		            {
		              if (parseState == ParseState.TEXT && textStart != -1)
		                {
		                  int textLen = i - textStart - 1;
		                  if (textLen > 0)
		                    anOverlay.overlayText += buffer.substring(textStart + 1, textStart + 1 + textLen);
		                }
		              textStart = i;  // Start saving comment string....
		            }
		          evaluated = true;
		        }
		      else if (ch == '>' && (i == 0 || buffer.charAt(i-1) != '|'))
		        {
		          if (--commentLevel < 0)
		            throw new ParseException("EIP_UNMATCHED_CLOSEBRACK", i - nHidden);
		          if (commentLevel == 0)
		            saveIt = true;  // used to be goto SaveIt:
		          else if (commentLevel == 1)
		          // We've finished a nested comment (that is, an overlay comment, not an image comment)
		          // so append its text to the current overlay's comment field.
		            {
		              int textLen = i - textStart - 1;
		              if (textLen > 0)
		                {
		                  if (anOverlay.comment.length() > 0)
		                    anOverlay.comment += ' ';
		                  anOverlay.comment += buffer.substring(textStart + 1, textStart+textLen);
		                }
		              textStart = (parseState == ParseState.TEXT) ? i : -1;
		            }
		          evaluated = true;
		        }
		      else if (commentLevel > 1)
		        continue;
		      // This bit of code is to simulate the behaviour of a goto label statement
		      // in the c++...
		      if (evaluated && !saveIt) {
		    	  continue;
		      }
		      else if (saveIt || (ch == '@' && (i == 0 || buffer.charAt(i-1) != '@')))
		        {
		//SaveIt:
		          // Should check and save whatever we have built up
		          if ((parseState == ParseState.TEXT || parseState == ParseState.PATH) && textStart != -1)
		            {
		              int textLen = i - textStart - 1;
		              if (textLen > 0)
		                anOverlay.overlayText += buffer.substring(textStart + 1, textStart + textLen);
		            }
		          textStart = -1;
		          if (anOverlay.type != OverlayType.OLNONE) {
		              // Only comments lack positioning information...
		              // But I think we should perhaps insert an "empty" location for
		              // them anyway, just to provide a space to old an ID, so that
		              // we can identify them readily
		              anOverlay.location.add(0, olLocation);
		              if (anOverlay.type != OverlayType.OLCOMMENT &&
		                  anOverlay.type != OverlayType.OLSOUND &&
		                  anOverlay.type != OverlayType.OLSUBJECT)
		                {
		                  if (dims != HAS_ALL_DIMS)
		                    throw new ParseException("EIP_MISSING_DIMENSIONS", i - nHidden);
		                  if (inHotspot)
		                    {
		                      anOverlay.location.add(hsLocation);
		                      inHotspot = false;
		                    }
		                }
		              // Assign an ID to every location
		              for (OverlayLoc loc : anOverlay.location) {
		                  loc.ID = getNextId(anOverlay.type);
		              }
		              overlayList.add(anOverlay);
		            }
		          anOverlay.clearAll();
		          //hsLocation.ClearAll(true);
		          olLocation.clearAll();
		          dims = 0;
		          inHotspot = false;
		          // If we jumped here because we hit a closing bracket, tidy up and move on
		          if (ch != '@')
		            {
		              parseState = ParseState.NOWHERE;
		              continue;
		            }
		          // Read and match the keyword.
		          parseState = ParseState.MODIFIER;
		          int oldi = i;
		          int j = i + 1;
		          while (j < buffer.length() && Character.isLetter(buffer.charAt(j)))
		            ++j;
		          String keyWord = buffer.substring(i + 1, --j);
		          i = j;
		          for (int k = 0; k < OverlayType.LIST_END; ++k)
		            {
		              if(keyWord == OLKeywords[k])
		                {
		                  anOverlay.type = k;
		                  break;
		                }
		            }

		          switch (anOverlay.type)
		            {
		              case OverlayType.OLCOMMENT:
		              case OverlayType.OLSUBJECT:
		              case OverlayType.OLSOUND:
		                parseState = ParseState.TEXT;
		                if (anOverlay.type == OverlayType.OLSOUND)
		                  parseState = ParseState.PATH;
		                while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
		                  ;
		                if (i >= buffer.length())
		                  throw new ParseException("EIP_DATA_MISSING", i - nHidden);
		                textStart = --i;
		                dims = HAS_ALL_DIMS;
		                break;

		              case OverlayType.OLSTATE:
		                {
		                  if (_fixedData.imageType != ImageType.IMAGE_CHARACTER)
		                    throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
		                  if ((getVOP() == null) || _fixedData.ownerId == VOUID_NULL)
		                    throw new ParseException("EIP_UNKNOWN_OWNER", i - nHidden);
		                  int stateNo = readIntegerValue(buffer, ++i, false, false, 10);
		                  VOCharBaseDesc charBase = (VOCharBaseDesc)getVOP().getDescFromId(_fixedData.ownerId);
		                  if (charBase == null)
		                    throw new ParseException("EIP_UNKNOWN_OWNER", i - nHidden);
		                  if ((anOverlay.stateId = charBase.uniIdFromStateNo(stateNo)) == VOCharBaseDesc.STATEID_NULL)
		                    throw new ParseException("EIP_BAD_STATE_NUMBER", i - nHidden);
		                  break;
		                }

		              case OverlayType.OLVALUE:
		              // In brackets to allow declaration of local variables
		                {
		                  if (_fixedData.imageType != ImageType.IMAGE_CHARACTER)
		                    throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
		                  while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
		                    ;
		                  if (i >= buffer.length())
		                    throw new ParseException("EIP_DATA_MISSING", i - nHidden);
		                  String stringStart = buffer.substring(i);
		                  int pos = 0;
		               
		                  int endPtr = anOverlay.minVal.setFromString(stringStart);
		                  if (endPtr != pos && stringStart.charAt(endPtr) == '-')
		                    {
		                      pos = endPtr + 1;
		                      
		                      endPtr = anOverlay.maxVal.setFromString(stringStart.substring(pos));
		                    }
		                  if (endPtr != 0)
		                    i += --endPtr;
		                  break;
		                }

		              case OverlayType.OLKEYWORD:
		                {
		                	while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
			                    ;
		                  if (i >= buffer.length())
		                	  throw new ParseException("EIP_DATA_MISSING", i - nHidden);
		                  boolean quoted = buffer.charAt(i) == '\"';
		                  int l;
		                  if (quoted)
		                    {
		                      l = ++i;
		                      while (l < buffer.length() && buffer.charAt(l) != '\"')
		                        ++l;
		                    }
		                  else
		                    {
		                      l = i;
		                      while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
				                    ;
		                    }
		                  anOverlay.keywords = buffer.substring(i, l);
		                  i = l;
		                  break;
		                }

		              case OverlayType.OLNOTES:
		              // Legacy stuff - silently convert NOTES to IMAGENOTES if we
		              // are not looking at a character image.
		              // Then drop through
		                if (_fixedData.imageType != ImageType.IMAGE_CHARACTER)
		                  anOverlay.type = OverlayType.OLIMAGENOTES;
		              case OverlayType.OLOK:
		              case OverlayType.OLCANCEL:
		              case OverlayType.OLIMAGENOTES:
		                olLocation.W = olLocation.H = Short.MIN_VALUE;
		                dims = HAS_W | HAS_H;
		                break;

		              case OverlayType.OLITEM:
		                if (_fixedData.imageType != ImageType.IMAGE_TAXON)
		                  throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
		                break;

		              case OverlayType.OLFEATURE:
		              case OverlayType.OLUNITS:
		              case OverlayType.OLENTER:
		                if (_fixedData.imageType != ImageType.IMAGE_CHARACTER)
		                	throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
		                break;

		              case OverlayType.OLHEADING:
		                if (_fixedData.imageType != ImageType.IMAGE_STARTUP)
		                	throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
		                break;

		              case OverlayType.OLTEXT:
		                break;

		              default: // Unmatched "keyword"
		                // Should report this condition, but for now, just save it all as a comment.
		                anOverlay.type = OverlayType.OLCOMMENT;
		                parseState = ParseState.TEXT;
		                i = oldi;
		                textStart = i - 1;
		                dims = HAS_ALL_DIMS;
		                break;
		            }
		        }
		      else if (parseState == ParseState.MODIFIER && modifiers.indexOf(Character.toUpperCase(ch)) >= 0)
		        {
		          ch = Character.toUpperCase(ch);

		          long val=0;
		          if (ch == 'X' || ch == 'Y' || ch == 'W' || ch == 'H')
		            {
		              val = readIntegerValue(buffer, ++i,
		                    anOverlay.type == OverlayType.OLUNITS && (ch == 'X' || ch == 'Y'),
		                    true, 10);
		              if (dims == HAS_ALL_DIMS &&
		                  (anOverlay.type == OverlayType.OLSTATE ||
		                   anOverlay.type == OverlayType.OLVALUE ||
		                   anOverlay.type == OverlayType.OLKEYWORD))
		                {
		                  if (inHotspot)
		                    anOverlay.location.add(hsLocation);
		                  hsLocation.clearAll();
		                  inHotspot = true;
		                  dims = 0;
		                }
		            }
		          switch (ch)
		            {
		              case 'X':
		                if ((dims & HAS_X) != 0)
		                  throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
		                dims |= HAS_X;
		                if (inHotspot)
		                  hsLocation.X = (short)val;
		                else
		                  olLocation.X = (short)val;
		                break;

		              case 'Y':
		                if ((dims & HAS_Y) != 0)
		                	throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
		                dims |= HAS_Y;
		                if (inHotspot)
		                  hsLocation.Y = (short)val;
		                else
		                  olLocation.Y = (short)val;
		                break;

		              case 'W':
		                if ((dims & HAS_W) != 0)
		                	throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
		                dims |= HAS_W;
		                if (inHotspot)
		                  hsLocation.W = (short)val;
		                else
		                  olLocation.W = (short)val;
		                break;

		              case 'H':
		                if ((dims & HAS_H) != 0)
		                	throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
		                dims |= HAS_H;
		                if (inHotspot)
		                  hsLocation.H = (short)val;
		                else
		                  {
		                    olLocation.H = (short)val;
		                    if (val <= 0)
		                      olLocation.flags |= OL_INTEGRAL_HEIGHT;
		                  }
		                break;

		              case 'C':
		                olLocation.flags |= OL_INCLUDE_COMMENTS;
		                break;

		              case 'M':
		                olLocation.flags |= OL_CENTER_TEXT;
		                break;

		              case 'N':
		                olLocation.flags |= OL_OMIT_DESCRIPTION;
		                break;

		              case 'P':
		                if (!inHotspot)
		                	throw new ParseException("EIP_NOT_HOTSPOT", i - nHidden);
		                hsLocation.flags |= OLOC_FLAG_POPUP;
		                break;

		              case 'E':
		                if (!inHotspot)
		                	throw new ParseException("EIP_NOT_HOTSPOT", i - nHidden);
		                hsLocation.drawType = OLDrawType.ellipse;
		                break;

		              case 'F':
		                val = readIntegerValue(buffer, ++i, false, true, 16);
		                hsLocation.flags |= OLOC_FLAG_COLOUR;
		                hsLocation.flags |= val & OLOC_FLAG_COLOUR_MASK;
		                break;

		              case 'T':
		                // Skip over
		            	  while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
			                    ;

		                // Check for '=' sign
		                if (i < buffer.length() && buffer.charAt(i) == '=')
		                	while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
			                    ;
		                textStart = --i;
		                parseState = ParseState.TEXT;
		                break;

		              default:
		                break; // Should never be reached
		            }
		        }
		      else if (parseState == ParseState.MODIFIER && ch != ',')
		        throw new ParseException("EIP_BAD_SYMBOL", i - nHidden);
		      else if (commentLevel == 0)
		    	  throw new ParseException("EIP_BAD_SYMBOL", i - nHidden);
		      else if (parseState == ParseState.NOWHERE)
		        {
		          anOverlay.type = OverlayType.OLCOMMENT;
		          parseState = ParseState.TEXT;
		          textStart = i - 1;
		          dims = HAS_ALL_DIMS;
		        }
		    }
		  writeAllOverlays(overlayList);
	}

	public String readFileName() {
		dataSeek(0);
		return readString(_fixedData.nameLen);
	}

	/**
	 * Writes the image file name to the slot file.
	 * @param fileName the name of the image file.
	 */
	public void writeFileName(String fileName) {
	    byte[] trailerBuf = null;
		int trailerLeng = 0;

		if (fileName.length() != _fixedData.nameLen) { // Save a copy of any following data!
		    trailerBuf = dupTrailingData(_fixedData.nameLen);
		    if (trailerBuf != null) {
		    	trailerLeng = trailerBuf.length;
		    }
		}

		// Seek to force allocation of large enough slot
		dataSeek(fileName.length() + trailerLeng);
		dataSeek(0);
		dataWrite(stringToBytes(fileName));
		if (fileName.length() != _fixedData.nameLen) {
		    _fixedData.nameLen = (short)fileName.length();
		    setDirty();
		    if (trailerBuf != null) {
		        dataWrite(trailerBuf);
		        dataTruncate();
		    }
		}
	}

	public String getSubjectText() {
		return getSubjectText(true);
	}

	public String getSubjectText(boolean useFilename) {
		List<ImageOverlay> overlays = readAllOverlays();
		String subject = "";
		for (ImageOverlay overlay : overlays) {
			if (overlay.type == OverlayType.OLSUBJECT) {
				subject = overlay.overlayText;
				break;
			}
		}

		if ((subject == null || subject.length() == 0) && useFilename) {
			subject = readFileName();
		}

		return subject;
	}

	public VOImageDesc clone(int newOwnerId) {
		throw new NotImplementedException();
	}

	public List<ImageOverlay> readAllOverlays() {
		synchronized (getVOP()) {
			List<ImageOverlay> dest = new ArrayList<VOImageDesc.ImageOverlay>();
			usedIds = new HashSet<Integer>();
			if (_fixedData.nOverlays > 0) {
				// Skip over name...
				dataSeek(_fixedData.nameLen);

				for (int i = 0; i < _fixedData.nOverlays; ++i) {
					ImageOverlay anOverlay = readSingleOverlay(true);
					dest.add(anOverlay);
				}
				// Code to cope with legacy situation, where IDs were not stored. This could
				// eventually be discarded.
				boolean newIds = false;
				for (ImageOverlay overlay : dest) {

					if (overlay.location.size() == 0) {
						overlay.location.add(new OverlayLoc());
					}
					for (OverlayLoc loc : overlay.location) {
						if (loc.ID == 0) {
							newIds = true;
							loc.ID = getNextId(overlay.type);
						}
					}
				}
				if (newIds)
					writeAllOverlays(dest);

			}

			return dest;
		}
	}

	public ImageOverlay readOverlay(int Id) {
		synchronized (getVOP()) {
			int startLoc = getOverlayStart(Id);
			if (startLoc != -1) {
				dataSeek(startLoc);
				return readSingleOverlay();
			}
			return null;
		}
	}

	public OverlayLoc readLocation(int Id) {
		ImageOverlay overlay = readOverlay(Id);

		if (overlay != null) {
			for (OverlayLoc loc : overlay.location) {
				if (loc.ID == Id) {
					return loc;
				}
			}
		}
		return null;
	}

	public void writeAllOverlays(List<ImageOverlay> overlays) {
		synchronized (getVOP()) {
			
			usedIds.clear();
			dataSeek(_fixedData.nameLen);
		    for (ImageOverlay overlay : overlays) {
		        writeSingleOverlay(overlay);
		    }
		    if (_fixedData.nOverlays != overlays.size()) {
		        _fixedData.nOverlays = (short)overlays.size();
		        setDirty();
		    }
		}
	}

	public boolean hasId(int Id) {
		// If we haven't initialized the list of IDs, do so now
		if (usedIds.isEmpty() && _fixedData.nOverlays > 0) {
			readAllOverlays();
		}
		return usedIds.contains(Id);

	}

	public boolean replaceOverlay(ImageOverlay src, boolean frontOnly) {
		throw new NotImplementedException();
	}

	public boolean replaceLocation(OverlayLoc src) {
		throw new NotImplementedException();
	}

	public int insertOverlay(ImageOverlay src) {
		return insertOverlay(src, 0);
	}

	public int insertOverlay(ImageOverlay src, int placeId) {
		throw new NotImplementedException();
	}

	public int insertLocation(OverlayLoc src, int placeId) {
		throw new NotImplementedException();
	}

	public boolean removeOverlay(int Id) {
		throw new NotImplementedException();
	}

	public boolean removeLocation(int Id) {
		throw new NotImplementedException();
	}

	public int getBaseId(int Id) {
		throw new NotImplementedException();
	}

	protected int getOverlayStart(int Id) {
		return getOverlayStart(Id, null);
	}

	protected int getOverlayStart(int Id, int[] basePtr) {
		synchronized (getVOP()) {
			if (hasId(Id)) {

				dataSeek(_fixedData.nameLen);
				for (int i = 0; i < _fixedData.nOverlays; ++i) {
					int curPos = dataTell();
					// First part gives type of the overlay,
					// followed by the lengths of remaining info
					short[] valBuf = readShortArray(4);
					// Reads in:
					// 0 : overlay type
					// 1 : length of location list
					// 2 : length of overlay text
					// 3 : length of (image level) comment (usually 0)
					// Next part is the location of this overlay, and its hotspots
					// Starts with a 4-byte value. Lowest byte indicates the draw type
					// Highest three bytes contain the ID
					for (int j = 0; j < valBuf[1]; ++j) {
						int aValue = readInt();

						int curId = aValue >> 8;
						if (basePtr != null && basePtr.length > 0 && j == 0) {
							basePtr[0] = curId;
						}
						if (curId == Id) {
							// Found what we were looking for...
							return curPos;
						}
						// Skip over rest of "location" information...
						dataSeek(4 + 2 + 2 + 2 + 2, SeekDirection.FROM_CUR);
					}
					int olType = valBuf[0];
					dataSeek(valBuf[2] + valBuf[3], SeekDirection.FROM_CUR);
					if (olType == OverlayType.OLSTATE)
						dataSeek(4, SeekDirection.FROM_CUR);
					else if (olType == OverlayType.OLVALUE)
						dataSeek(DeltaNumber.size(), SeekDirection.FROM_CUR);
					else if (olType == OverlayType.OLKEYWORD) {
						short textLen = readShort();
						dataSeek(textLen, SeekDirection.FROM_CUR);
					}
				}
			}
			return -1;
		}
	}

	protected ImageOverlay readSingleOverlay() {
		return readSingleOverlay(false);
	}

	protected ImageOverlay readSingleOverlay(boolean isNew) {
		// First part gives type of the overlay,
		// followed by the lengths of remaining info
		short[] valBuf = new short[4];
		for (int i = 0; i < valBuf.length; ++i) {
			valBuf[i] = _slotFile.readShort();
		}
		// Reads in:
		// 0 : overlay type
		// 1 : length of location list
		// 2 : length of overlay text
		// 3 : length of (image level) comment (usually 0)

		ImageOverlay dest = new ImageOverlay();
		dest.type = valBuf[0];

		for (int j = 0; j < valBuf[1]; ++j) {
			OverlayLoc olLoc = new OverlayLoc();

			// Starts with a 4-byte value. Lowest byte indicates the draw type
			// Highest three bytes contain the ID
			// These should perhaps be made separate, but would "break" the
			// original design, which stored only the drawType and not the ID
			int aValue = readInt();
			olLoc.drawType = OLDrawType.fromOrdinal(aValue & 0xFF);
			olLoc.ID = aValue >> 8;

			if (olLoc.ID != 0) {
				// Paranoia about duplicate IDs
				if (!usedIds.contains(olLoc.ID)) {
					usedIds.add(olLoc.ID);
				} else if (isNew) {
					olLoc.ID = 0;
				}
			}
			// DataRead(&olLoc.drawType, sizeof(olLoc.drawType));
			olLoc.flags = readInt();
			olLoc.X = readShort();
			olLoc.Y = readShort();
			olLoc.W = readShort();
			olLoc.H = readShort();

			if (olLoc.H <= 0) {
				olLoc.flags |= ImageOverlay.OL_INTEGRAL_HEIGHT;
			}
			dest.location.add(olLoc);
		}

		dest.overlayText = readString(valBuf[2]);
		dest.comment = readString(valBuf[3]);

		if (dest.type == OverlayType.OLSTATE) {
			dest.stateId = readInt();
		} else if (dest.type == OverlayType.OLVALUE) {
			dest.minVal = readNumber();
			dest.maxVal = readNumber();
		} else if (dest.type == OverlayType.OLKEYWORD) {
			short textLen = readShort();
			dest.keywords = readString(textLen);
		}

		return dest;
	}

	protected int writeSingleOverlay(ImageOverlay src) {
		return writeSingleOverlay(src, false);
	}

	protected int writeSingleOverlay(ImageOverlay anOverlay, boolean reuseIds) {
		
		int retVal = 0;
		short[] valBuf = new short[4];
		valBuf[0] = (short)anOverlay.type;
		valBuf[1] = (short)anOverlay.location.size();
		valBuf[2] = (short)anOverlay.overlayText.length();
		valBuf[3] = (short)anOverlay.comment.length();
		dataWrite(valBuf);
		
		for (OverlayLoc olLoc : anOverlay.location) {
		  
		    if (olLoc.ID == 0 || (!reuseIds && usedIds.contains(olLoc.ID)))
		        olLoc.ID = getNextId(anOverlay.type, false);
		      usedIds.add(olLoc.ID);
		      if (retVal == 0)
		        retVal = olLoc.ID;
		      int aValue = (olLoc.drawType.ordinal() & 0xff) | (olLoc.ID << 8);
		      dataWrite(aValue);
		      dataWrite(olLoc.flags);
		      dataWrite(olLoc.X);
		      dataWrite(olLoc.Y);
		      dataWrite(olLoc.W);
		      dataWrite(olLoc.H);
		    }
		  dataWrite(stringToBytes(anOverlay.overlayText));
		  dataWrite(stringToBytes(anOverlay.comment));
		  if (anOverlay.type == OverlayType.OLSTATE)
		      dataWrite(anOverlay.stateId);
		  else if (anOverlay.type == OverlayType.OLVALUE) {
		      dataWrite(anOverlay.minVal.toBinary());
		      dataWrite(anOverlay.maxVal.toBinary());
		  }
		  else if (anOverlay.type == OverlayType.OLKEYWORD) {
		      short textLen = (short)anOverlay.keywords.length();
		      dataWrite(textLen);
		      dataWrite(stringToBytes(anOverlay.keywords));
		   }
		  return retVal;
	}

	protected int readIntegerValue(String buffer, int pos) {
		return readIntegerValue(buffer, pos, false, true, 10);
	}

	protected int readIntegerValue(String buffer, int pos, boolean allowTilde) {
		return readIntegerValue(buffer, pos, allowTilde, true, 10);
	}

	protected int readIntegerValue(String buffer, int pos, boolean allowTilde, boolean allowEquals) {
		return readIntegerValue(buffer, pos, allowTilde, allowEquals, 10);
	}

	protected int readIntegerValue(String buffer, int pos, boolean allowTilde, boolean allowEquals, int base) {
		// Eat up any leading white-space
		while (pos < buffer.length() && Character.isWhitespace(buffer.charAt(pos))) {
			++pos;
		}

		// Check for '=' sign
		if (allowEquals && pos < buffer.length() && buffer.charAt(pos) == '=')
			while (++pos < buffer.length() && Character.isWhitespace(buffer.charAt(pos)))
				;

		if (pos >= buffer.length()) {
			throw new RuntimeException("EIP_DATA_MISSING:" + pos);
		}

		if (allowTilde && buffer.charAt(pos) == '~')
			return Short.MIN_VALUE;

		String candidate = buffer.substring(pos, buffer.length() - 1);
		int[] endPtr = new int[] { 0 };
		int retVal = Utils.strtol(candidate, endPtr);
		if (endPtr[0] == 0) {
			throw new RuntimeException("Bad symbol: " + candidate);
		} else {
			// pos += --endPtr - string;
		}
		return retVal;
	}

	protected Set<Integer> usedIds;

	public int getNextId() {
		return getNextId(OverlayType.OLTEXT, true);
	}

	public int getNextId(int overlayType) {
		return getNextId(overlayType, true);
	}

	public int getNextId(int olType, boolean reserve) {
		int i = ImageOverlay.ID_OVERLAY_FIRST;
		if (olType == OverlayType.OLOK)
			i = ImageOverlay.ID_OK;
		else if (olType == OverlayType.OLCANCEL)
			i = ImageOverlay.ID_CANCEL;
		else if (olType == OverlayType.OLNOTES)
			i = ImageOverlay.ID_NOTES;
		else if (olType == OverlayType.OLIMAGENOTES)
			i = ImageOverlay.ID_IMAGE_NOTES;
		else
			while (usedIds.contains(i)) {
				++i;
			}
		if (reserve) {
			usedIds.add(i);
		}

		return i;
	}

	// Fixed data...

	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE;
	public static final int ownerIdOffs = fixedSizeOffs + 2;
	public static final int imageTypeOffs = ownerIdOffs + 4;
	public static final int nameLenOffs = imageTypeOffs + 4;
	public static final int nOverlaysOffs = nameLenOffs + 2;
	public static final int alignmentOffs = nOverlaysOffs + 2;

	public class ImageFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 4 + 4 + 2 + 2 + 2;

		public ImageFixedData() {
			super("Image Desc");
		}

		short fixedSize = SIZE;
		int ownerId;
		int imageType;
		short nameLen;
		short nOverlays; // Number of overlay blocks; will an unsigned short be large enough?
		short alignment;

		@Override
		public void read(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(SIZE);

			fixedSize = b.getShort();
			ownerId = b.getInt();
			imageType = b.getInt();
			nameLen = b.getShort();
			nOverlays = b.getShort();
			alignment = b.getShort();
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(ownerId);
			file.write(imageType);
			file.write(nameLen);
			file.write(nOverlays);
			file.write(alignment);
		}

	}

	public class ButtonAlignment {
		public static final int ALIGN_DEFAULT = -1;
		public static final int ALIGN_NONE = 0;
		public static final int ALIGN_VERTICAL = 1;
		public static final int ALIGN_HORIZONTAL = 2;
	}

	public class ImageOverlay {

		public static final int OVERLAY_TYPE_COUNT = OverlayType.LIST_END; // Total number of Overlay types we know about (+ 1)

		public static final int OLOC_FLAG_COLOUR_MASK = 0x00ffffff;
		public static final int OLOC_FLAG_HOTSPOT = 0x01000000;
		public static final int OLOC_FLAG_POPUP = 0x02000000;
		public static final int OLOC_FLAG_COLOUR = 0x04000000;

		public static final byte OL_OMIT_DESCRIPTION = 0x1;
		public static final byte OL_INCLUDE_COMMENTS = 0x2;
		public static final byte OL_CENTER_TEXT = 0x4;
		public static final byte OL_INTEGRAL_HEIGHT = 0x8;

		public static final int OLSHOW_CONTROL = 0x80000000;
		public static final int OLSHOW_HOTSPOT = 0x40000000;
		public static final int OLSHOW_POPUPS = 0x20000000;
		public static final int OLSHOW_ALL = (OLSHOW_CONTROL | OLSHOW_HOTSPOT | OLSHOW_POPUPS);
		public static final int OLSHOW_IDMASK = ~(OLSHOW_CONTROL | OLSHOW_HOTSPOT | OLSHOW_POPUPS);
		public static final int ID_OVERLAY_FIRST = 0x100;

		public static final int ID_OK = 1;
		public static final int ID_CANCEL = 2;
		public static final int ID_NOTES = 8;
		public static final int ID_OUTLINE = 9;
		public static final int ID_BUTTON_BLOCK = 0xA;
		public static final int ID_IMAGE_NOTES = 0XB;

		public int type;
		public String overlayText;
		public String comment;

		public List<OverlayLoc> location; // List of locations - first is that of the "main" overlay
		// object, (required for all overlay objects except comments)
		// then (optionally) those of any associated "hotspots"

		public int stateId; // Might be appropriate to use a "union" here, but
		public DeltaNumber minVal; // since TDeltaNumber has a constructor, it is not allowable
		public DeltaNumber maxVal; // But for any given overlay, depending on type, we really
		public String keywords; // need only 1 of stateId, (minVal && maxVal), or keywords
		public String displayText; // Buffer for constructing entire text string

		public ImageOverlay() {
			this(OverlayType.OLNONE);
		}

		public String toString() {
			return String.format("Overlay: Type=%d, overlayText=%s, Comment=%s, StateID=%d, minVal=%d, maxVal=%d, KeyWords=%s, displayText=%s\nLocations: %s", type, overlayText, comment, stateId,
					minVal, maxVal, keywords, displayText, location);
		}

		public ImageOverlay(int aType) {
			type = aType;
			location = new ArrayList<VOImageDesc.OverlayLoc>();
		}

		// Should "keywords" be a string? eventually I think this information will be a set of
		// group IDs. But this "group" mechanism doesn't yet exist...

		// These next members are used for drawing purposes, rather than for
		// storing actual information about the overlay.
		// TImageWindow* parent; // Pointer to "owner" window
		// void SetParent(TImageWindow* newParent) { Destroy(); parent = newParent; }

		// void Hide(unsigned long id = OLSHOW_ALL);
		// void Show(unsigned long id = OLSHOW_ALL, HDC hDCPaint = 0, TOverlayLoc::visibility how=TOverlayLoc::yes, bool isErasure = false);
		// void Destroy(unsigned long id = OLSHOW_ALL);
		// void Recreate(unsigned long id = OLSHOW_ALL, bool doCreate=true);
		// //void Refresh();
		// void Rescale(unsigned long id = OLSHOW_ALL, unsigned int flags = 0);
		// void Deselect();
		// void ChangeFont();
		// void ValidateControl(TControl* control);
		// OverlayFontType GetFontType();
		// bool HasVisibleElement() const { return !location.empty(); }
		public boolean hasVisibleElement() {
			return !(type == OverlayType.OLCOMMENT || type == OverlayType.OLSUBJECT || type == OverlayType.OLSOUND || location.isEmpty());
		}

		public boolean hasTextBox() {
			throw new NotImplementedException();
		}

		public int getNHotSpots() {
			return Math.max(0, (int) location.size() - 1);
		}

		public boolean IsButton() {
			return type == OverlayType.OLOK || type == OverlayType.OLCANCEL || type == OverlayType.OLNOTES || type == OverlayType.OLIMAGENOTES;
		}

		public int getX() {
			return getX(OLSHOW_CONTROL);
		}

		public int getX(int id) {
			throw new NotImplementedException();
		}

		public int getY() {
			return getY(OLSHOW_CONTROL);
		}

		public int getY(int id) {
			throw new NotImplementedException();
		}

		public int getHeight(int id, double yscale) {
			throw new NotImplementedException();
		}

		public int getWidth(int id) {
			throw new NotImplementedException();
		}

		public OverlayLoc getLocation(int Id) {
			throw new NotImplementedException();
		}

		// TControl* GetBaseControl() const;
		public int containsPointInHotspot(Point testPt) {
			throw new NotImplementedException();
		}

		public boolean containsId(int id) {
			throw new NotImplementedException();
		}

		public boolean canSelect() {
			return type == OverlayType.OLSTATE || type == OverlayType.OLVALUE;
		}

		public String getValueString() {
			throw new NotImplementedException();
		}

		public String getDisplayText() {
			throw new NotImplementedException();
		}

		public void updateText() {
			throw new NotImplementedException();
		}

		// static const char const * punct;

		public void clearAll() {
			overlayText = "";
			comment = "";
			location.clear();
			keywords = "";
			minVal.setFromValue(0);
			maxVal.setFromValue(0);

			stateId = 0;
			type = OverlayType.OLNONE;
		}

	}

	public class OverlayLoc {

		public OLDrawType drawType;
		public int flags;
		public int ID;
		short X;
		short Y;
		short W;
		short H;

		public OverlayLoc() {
		}

		public void clearAll() {
			drawType = OLDrawType.Unknown;
			ID = flags = 0;
			X = Y = W = H = 0;
		}

		@Override
		public String toString() {
			return String.format("OverlayLoc: drawType=%s, flags=%d, ID=%d, X=%d, Y=%d, W=%d, H=%d", drawType, flags, ID, X, Y, W, H);
		}

	}

	public enum OLDrawType {
		Unknown, frame, rectangle, line, arrow, ellipse;

		public static OLDrawType fromOrdinal(int ord) {
			return values()[ord];
		}
	}

	public class OverlayType {
		// NOTE! Changes here must be made to both array OLKeywords and enum OverlayType
		// They MUST have entries "in parallel".
		// The negative values are used for "unnamed" overlay types, that are used
		// internally for editing.
		public static final int OLBUTTONBLOCK = -3; // Used only when modifying aligned push-buttons
		public static final int OLHOTSPOT = -2; // Not a "real" overlay type; used for convenience in editing
		public static final int OLNONE = -1; // Undefined; the remaining values MUST correspond
												// with array OLKeywords.
		public static final int OLTEXT = 0; // Use a literal text string
		public static final int OLITEM = 1; // Use name of the item
		public static final int OLFEATURE = 2; // Use name of the character
		public static final int OLSTATE = 3; // Use name of the state (selectable)
		public static final int OLVALUE = 4; // Use specified values or ranges (selectable)
		public static final int OLUNITS = 5; // Use units (for numeric characters)
		public static final int OLENTER = 6; // Create edit box for data entry
		public static final int OLSUBJECT = 7; // Has text for menu entry
		public static final int OLSOUND = 8; // Has name of .WAV sound file
		public static final int OLHEADING = 9; // Using heading string for the data-set
		public static final int OLKEYWORD = 10; // Use specified keyword(s)
		public static final int OLOK = 11; // Create OK pushbutton
		public static final int OLCANCEL = 12; // Create Cancel pushbutton
		public static final int OLNOTES = 13; // Create Notes pushbutton (for character notes)
		public static final int OLIMAGENOTES = 14; // Create Notes pushbutton (for notes about the image)
		public static final int OLCOMMENT = 15; // Not a "real" overlay type, but used to save comments addressed
		// to images rather than overlays
		public static final int LIST_END = 16; // Insert new overlay types just BEFORE this!

	}

}

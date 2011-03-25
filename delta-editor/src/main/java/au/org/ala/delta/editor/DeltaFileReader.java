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
package au.org.ala.delta.editor;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.editor.slotfile.Attribute;
import au.org.ala.delta.editor.slotfile.CharType;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.TextType;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOCharTextDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSetFactory;
import au.org.ala.delta.editor.slotfile.model.VOCharacterAdaptor;
import au.org.ala.delta.editor.slotfile.model.VOItemAdaptor;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.StateValue;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;
import au.org.ala.delta.util.CodeTimer;
import au.org.ala.delta.util.IProgressObserver;

public class DeltaFileReader {

	/**
	 * Private CTOR for static class
	 */
	private DeltaFileReader() {
	}

	public static DeltaContext readDeltaFileFully(String filename) {
		return readDeltaFileFully(filename, null);
	}

	public static DeltaDataSet readDeltaFile(String fileName, IProgressObserver observer) {

		if (observer != null) {
			observer.progress("Loading file " + fileName, 0);
		}

		DeltaVOP vop = new DeltaVOP(fileName, false);
		SlotFileDataSetFactory factory = new SlotFileDataSetFactory(vop);
		DeltaDataSet dataSet = new SlotFileDataSet(vop, factory);
		
		return dataSet;
	}

	public static DeltaContext readDeltaFileFully(String filename, IProgressObserver observer) {

		if (observer != null) {
			observer.progress("Loading file " + filename, 0);
		}

		CodeTimer t = new CodeTimer("Reading Delta File");

		DeltaContext context = new DeltaContext();

		context.setVariable("HEADING", filename);

		DeltaVOP vop = new DeltaVOP(filename, false);

		

		int nChars = vop.getDeltaMaster().getNChars();
		int nItems = vop.getDeltaMaster().getNItems();
		int nDirectives = vop.getDeltaMaster().getNItems();

		int progmax = nChars + nItems + nDirectives;
		int progress = 0;

		context.setNumberOfCharacters(nChars);
		context.setMaximumNumberOfItems(nItems);
		context.initializeMatrix();

		// Chars
		for (int i = 1; i <= nChars; ++i) {
			int charId = vop.getDeltaMaster().uniIdFromCharNo(i);
			VOCharBaseDesc charDesc = (VOCharBaseDesc) vop.getDescFromId(charId);

			VOCharTextDesc textDesc = charDesc.readCharTextInfo(0, (short) 0);
			List<String> states = new ArrayList<String>();
			
			int charType = charDesc.getCharType();
			Character chr = null;
			switch (charType) {
			case CharType.TEXT:
				chr = new TextCharacter(i);
				break;
			case CharType.INTEGER:
				chr = new IntegerCharacter(i);
				break;
			case CharType.UNORDERED:
				chr = new UnorderedMultiStateCharacter(i);
				break;
			case CharType.ORDERED:
				chr = new OrderedMultiStateCharacter(i);
				break;
			case CharType.REAL:
				chr = new RealCharacter(i);
				break;
			default:
				throw new RuntimeException("Unrecognized character type: " + charType);
			}

			chr.setImpl(new VOCharacterAdaptor(charDesc, textDesc));
			if (chr instanceof MultiStateCharacter) {
				populateStates(charDesc, (MultiStateCharacter) chr, states);
			} else if (chr instanceof NumericCharacter) {
				if (charDesc.getNStates() > 0) {
					@SuppressWarnings("unchecked")
					NumericCharacter<? extends Number> c = (NumericCharacter<? extends Number>) chr;
					int idState = charDesc.uniIdFromStateNo(1);
					if (idState < states.size()) {
						c.setUnits(states.get(idState));
					}
				}
			}

			chr.setDescription(textDesc.readFeatureText(TextType.RTF));
			chr.setMandatory(charDesc.testCharFlag(VOCharBaseDesc.CHAR_MANDATORY));
			chr.setExclusive(charDesc.testCharFlag(VOCharBaseDesc.CHAR_EXCLUSIVE));
			
			progress++;
			if (observer != null && progress % 10 == 0) {
				int percent = (int) (((double) progress / (double) progmax) * 100);
				observer.progress("Loading characters", percent);
			}
		}

		CodeTimer t1 = new CodeTimer("Reading Items");
		// Items...
		for (int i = 1; i <= nItems; ++i) {
			int itemId = vop.getDeltaMaster().uniIdFromItemNo(i);
			VOItemDesc itemDesc = (VOItemDesc) vop.getDescFromId(itemId);
			Item item = new Item(new VOItemAdaptor(vop, itemDesc, i), i);
			item.setDescription(itemDesc.getAnsiName());
			

			List<Attribute> attrs = itemDesc.readAllAttributes();
			for (Attribute attr : attrs) {
				if (attr.getCharId() >= 0) {
					int charIndex = vop.getDeltaMaster().charNoFromUniId(attr.getCharId());
					Character c = context.getCharacter(charIndex);

					StateValue sv = new StateValue(c, item, attr.getAsText(0, vop));
					context.getMatrix().setValue(charIndex, i, sv);
				}
			}

			progress++;
			if (observer != null && progress % 10 == 0) {
				int percent = (int) (((double) progress / (double) progmax) * 100);
				observer.progress("Loading Items", percent);
			}

		}

		t1.stop(false);

		readDirectives(observer, vop, progmax, progress);

		t.stop(false);

		
		vop.close();
	

		return context;

	}

	/**
	 * @param observer
	 * @param vop
	 * @param progmax
	 * @param progress
	 */
	private static void readDirectives(IProgressObserver observer, DeltaVOP vop, int progmax, int progress) {
		CodeTimer t2 = new CodeTimer("Reading Directives");

		for (int i = 1; i <= vop.getDeltaMaster().getNDirFiles(); ++i) {
			int uid = vop.getDeltaMaster().uniIdFromDirFileNo(i);
			VODirFileDesc dirDesc = (VODirFileDesc) vop.getDescFromId(uid);
			DirectiveFile dirFile = new DirectiveFile(dirDesc.getFileName());
			dirFile.progType = dirDesc.getProgType();
			for (int j = 1; j <= dirDesc.getNDirectives(); ++j) {
				dirDesc.readDirective(j);
				if (observer != null && progress % 10 == 0) {
					int percent = (int) (((double) progress / (double) progmax) * 100);
					observer.progress("Loading Directives", percent);
				}
			}
		}

		t2.stop(true);
	}

	private static void populateStates(VOCharBaseDesc charBase, MultiStateCharacter chr, List<String> states) {
		chr.setNumberOfStates(charBase.getNStatesUsed());

		int uncodedImplicitStateId = charBase.getUncodedImplicit();
		if (uncodedImplicitStateId != VOCharBaseDesc.STATEID_NULL) {
			chr.setUncodedImplicitState(uncodedImplicitStateId);
			chr.setCodedImplicitState(charBase.getCodedImplicit());
		}

		for (int j = 0; j < charBase.getNStatesUsed(); ++j) {
			int stateId = charBase.uniIdFromStateNo(j + 1);
			chr.setState(j + 1, states.get(stateId));
		}

	}

}

class DirectiveFile {

	public int progType;
	public int type;
	public String name;
	public List<String> directives = new ArrayList<String>();

	public DirectiveFile(String name) {
		this.name = name;
	}
}

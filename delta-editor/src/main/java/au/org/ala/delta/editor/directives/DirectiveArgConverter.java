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
package au.org.ala.delta.editor.directives;

import java.math.BigDecimal;
import java.util.List;

import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.CharType;
import au.org.ala.delta.editor.slotfile.DeltaNumber;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirArgs;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirListData;
import au.org.ala.delta.editor.slotfile.VOItemDesc;

/**
 * Creates and populates a Dir object from the arguments supplied to a 
 * AbstractDirective.
 */
public class DirectiveArgConverter {

	
	private ItemDescriptionConverter _itemDescriptionConverter = new ItemDescriptionConverter();
	private CharacterNumberConverter _characterNumberConverter = new CharacterNumberConverter();
	private ItemNumberConverter _itemNumberConverter = new ItemNumberConverter();
	
	private DeltaVOP _vop;
	public DirectiveArgConverter(DeltaVOP vop) {
		_vop = vop;
	}
	
	public Dir fromDirective(DirectiveInstance directive) {

		Dir dir = new Dir();
		Directive directiveDescription = directive.getDirective();
		dir.setDirType(directiveDescription.getNumber());
		populateArgs(dir, directive.getDirectiveArguments(), directiveDescription.getArgType());

		return dir;
	}
	
	/**
	 * Converts the arguments encoded in the slotfile Dir into the
	 * model DirectiveArguments.
	 * @param directive the directive to convert.
	 * @return a new instance of DirectiveArguments populated from the 
	 * supplied Dir object.
	 */
	public DirectiveArguments convertArgs(Dir directive, int argType) {
		
		DirectiveArguments directiveArgs = new DirectiveArguments();
		List<DirArgs> args = directive.args;
		for (DirArgs arg : args) {
			DirectiveArgument<?> directiveArgument = directiveArgumentFromDirArgs(argType, arg);
			directiveArgs.add(directiveArgument);
		}
		return directiveArgs;
		
	}
	
	
	private DirectiveArgument<?> directiveArgumentFromDirArgs(int argType, DirArgs arg) {
		IdConverter converter = idConverterFor(argType);
		Object id = converter.convertFromSlotFileId(arg.getId());
		
		DirectiveArgument<?> directiveArgument = null;
		if (id == null) {
			directiveArgument = new DirectiveArgument<Integer>();
		}
		else if (id instanceof String) {
			directiveArgument = new DirectiveArgument<String>((String)id);
		}
		else {
			directiveArgument = new DirectiveArgument<Integer>((Integer)id);
		}
		
		directiveArgument.setText(arg.text);
		directiveArgument.setComment(arg.comment);
		
		// This is a special case - the actual attribute data is stored.
		if (argType == DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES) {
			directiveArgument.setText(getAttributeText(arg));
		}
		
		directiveArgument.setValue(new BigDecimal(arg.getValue().asString()));
		converter = idConverterForData(directiveArgument, argType);
		for (DirListData data : arg.getData()) {
			directiveArgument.getData().add(converter.convertData(data));
		}
		
		return directiveArgument;
	}
	
	private String getAttributeText(DirArgs arg) {
		VOCharBaseDesc desc = (VOCharBaseDesc)_vop.getDescFromId(arg.getId());
		desc.getCharType();
		int showComments = CharType.isText(desc.getCharType()) ? 0 : 1;
		return arg.attrib.getAsText(showComments, _vop);
	}

	private void populateArgs(Dir dir, DirectiveArguments args, int argType) {
		if (args == null || argType == DirectiveArgType.DIRARG_INTERNAL) {
			dir.resizeArgs(0);
		}
		else {
			for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
				IdConverter converter = idConverterFor(argType);
				
				DirArgs dirArg = new DirArgs(converter.convertToSlotFileId(arg.getId()));
				dirArg.setText(arg.getText());
				dirArg.comment = arg.getComment();
				BigDecimal value = arg.getValue();
				if (value != null) {
					dirArg.setValue(value.toPlainString());
				}
				
				converter = idConverterForData(arg, argType);
				for (BigDecimal dataValue : arg.getData()) {
					DirListData data = new DirListData();
					converter.convertData(data, dataValue);
					dirArg.getData().add(data);
				}
				dir.args.add(dirArg);
			}
		}
		
	}

 
	interface IdConverter {
		public int convertToSlotFileId(Object id);
		
		public Object convertFromSlotFileId(int id);
		
		public BigDecimal convertData(DirListData data);
		
		public void convertData(DirListData data, BigDecimal value);
	}
	
	class CharacterNumberConverter implements IdConverter {
		@Override
		public int convertToSlotFileId(Object id) {		
			if (id == null) {
				return 0;
			}
			return _vop.getDeltaMaster().uniIdFromCharNo((Integer)id);
		}
		@Override
		public Object convertFromSlotFileId(int id) {
			return charNumFromId(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(charNumFromId(data.getIntNumb()));
		}
		
		@Override
		public void convertData(DirListData data, BigDecimal value) {
			data.setIntNumb((Integer)convertToSlotFileId(value.intValue()));
		}
		
		private int charNumFromId(int id) {
			return _vop.getDeltaMaster().charNoFromUniId(id);
		}
	}
	
	class ItemNumberConverter implements IdConverter {
		@Override
		public int convertToSlotFileId(Object id) {
			return _vop.getDeltaMaster().uniIdFromItemNo((Integer)id);
		}
		@Override
		public Object convertFromSlotFileId(int id) {
			return convert(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(convert(data.getIntNumb()));
		}
		
		@Override
		public void convertData(DirListData data, BigDecimal value) {
			data.setIntNumb((Integer)convertFromSlotFileId(value.intValue()));
		}
		
		private int convert(int id) {
			return _vop.getDeltaMaster().itemNoFromUniId(id);
		}
	}
	
	class ItemDescriptionConverter extends ItemNumberConverter {
		@Override
		public int convertToSlotFileId(Object id) {
			// Delimeters have a funny behaviour.
			if (id instanceof Integer) {
				int idInt = (Integer)id;
				if (idInt != Integer.MIN_VALUE) {
					return super.convertToSlotFileId(id);
				}
				return idInt;
			}
			VOItemDesc item = _vop.getItemFromName((String)id, false);
			if (item == null) {
				throw new IllegalArgumentException("No such item "+id);
			}
			return item.getUniId();
		}
		/**
		 * The conversion in this direction converts into an integer
		 * (item number) not description for compatibility with the existing
		 * code.
		 */
		@Override
		public Object convertFromSlotFileId(int id) {
			if (id > 0) {
				return _vop.getDeltaMaster().itemNoFromUniId(id);
			}
			return id;
		}
	
	}
	
	class DirectIntegerConverter implements IdConverter {
		@Override
		public int convertToSlotFileId(Object id) {
			return (Integer)id;
		}
		@Override
		public Object convertFromSlotFileId(int id) {
			return Integer.valueOf(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(data.getIntNumb());
		}
		
		@Override
		public void convertData(DirListData data, BigDecimal value) {
			data.setIntNumb((Integer)convertFromSlotFileId(value.intValue()));
		}
	}
	class DirectRealConverter extends DirectIntegerConverter {
		@Override
		public int convertToSlotFileId(Object id) {
			if (id == null) {
				return -1;
			}
			return (Integer)id;
		}
		@Override
		public Object convertFromSlotFileId(int id) {
			return Integer.valueOf(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(data.asString());
		}
		
		@Override
		public void convertData(DirListData data, BigDecimal value) {
			data.setAsDeltaNumber(new DeltaNumber(value.toPlainString()));
		}
	}
	
	class NullConverter implements IdConverter {
		@Override
		public int convertToSlotFileId(Object id) {
			return 0;
		}
		@Override
		public Object convertFromSlotFileId(int id) {
			return null;
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return null;
		}
		@Override
		public void convertData(DirListData data, BigDecimal value) {
		}
	}
	
	/**
	 * The KeyStates directive is difficult because the encoding of the
	 * data array depends on the type of character represented by the current
	 * arg.
	 */
	class KeyStatesConverter extends CharacterNumberConverter {
		private int _characterNumber;
		public KeyStatesConverter(int charNumber) {
			_characterNumber = charNumber;
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			VOCharBaseDesc charBase = getCharBase();
			
			if (CharType.isMultistate(charBase.getCharType())) {
				int stateId = data.getIntNumb();
				return new BigDecimal(charBase.stateNoFromUniId(stateId));
			}
			return new BigDecimal(data.asString());
		}
		private VOCharBaseDesc getCharBase() {
			int charBaseId = _vop.getDeltaMaster().uniIdFromCharNo(_characterNumber);
			VOCharBaseDesc charBase = (VOCharBaseDesc)_vop.getDescFromId(charBaseId);
			return charBase;
		}
		@Override
		public void convertData(DirListData data, BigDecimal value) {
			VOCharBaseDesc charBase = getCharBase();
			if (CharType.isMultistate(charBase.getCharType())) {
				int stateNum = value.intValue();
				data.setIntNumb((charBase.uniIdFromStateNo(stateNum)));
			}
			else {
				data.setAsDeltaNumber(new DeltaNumber(value.toPlainString()));
			}
		}
	}
	
	private IdConverter idConverterFor(int argType) {
		
		switch (argType) {
		case DirectiveArgType.DIRARG_NONE:
		case DirectiveArgType.DIRARG_TRANSLATION:
		case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		case DirectiveArgType.DIRARG_INTERNAL: // Not sure about this - existing code treats it like text.
		case DirectiveArgType.DIRARG_COMMENT: // Will actually be handled within DirInComment
		case DirectiveArgType.DIRARG_TEXT: // What about multiple lines of text? Should line breaks ALWAYS be
											// preserved?
		case DirectiveArgType.DIRARG_FILE:
		case DirectiveArgType.DIRARG_OTHER:
		case DirectiveArgType.DIRARG_INTKEY_ONOFF:
		case DirectiveArgType.DIRARG_INTEGER:
		case DirectiveArgType.DIRARG_CHARGROUPS:
		case DirectiveArgType.DIRARG_ALLOWED:
			return new NullConverter();

		case DirectiveArgType.DIRARG_TEXTLIST:
			return new DirectIntegerConverter();
			
		case DirectiveArgType.DIRARG_REAL:
			return new DirectRealConverter();
			
		case DirectiveArgType.DIRARG_ITEM:
		case DirectiveArgType.DIRARG_ITEMREALLIST:
		case DirectiveArgType.DIRARG_ITEMLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEM:
		case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
			return _itemNumberConverter;
			
		case DirectiveArgType.DIRARG_CHARLIST:
		case DirectiveArgType.DIRARG_CHARREALLIST:
		case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		case DirectiveArgType.DIRARG_CHARTEXTLIST:
		
		case DirectiveArgType.DIRARG_CHAR:
		case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
		case DirectiveArgType.DIRARG_KEYSTATE:
		case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		case DirectiveArgType.DIRARG_PRESET:
		case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
			return _characterNumberConverter;
			
		case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMFILELIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		case DirectiveArgType.DIRARG_ITEMCHARLIST:
			return _itemDescriptionConverter;
		}
		return new NullConverter();
	}
	
	private IdConverter idConverterForData(DirectiveArgument<?> arg, int argType) {
		
		switch (argType) {
		case DirectiveArgType.DIRARG_NONE:
		case DirectiveArgType.DIRARG_TRANSLATION:
		case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		case DirectiveArgType.DIRARG_INTERNAL: // Not sure about this - existing code treats it like text.
		case DirectiveArgType.DIRARG_COMMENT: // Will actually be handled within DirInComment
		case DirectiveArgType.DIRARG_TEXT: // What about multiple lines of text? Should line breaks ALWAYS be								// preserved?
		case DirectiveArgType.DIRARG_FILE:
		case DirectiveArgType.DIRARG_OTHER:
		case DirectiveArgType.DIRARG_INTKEY_ONOFF:
		case DirectiveArgType.DIRARG_INTEGER:
		case DirectiveArgType.DIRARG_REAL:
		case DirectiveArgType.DIRARG_TEXTLIST:
		case DirectiveArgType.DIRARG_CHAR:
		case DirectiveArgType.DIRARG_ITEM:
		case DirectiveArgType.DIRARG_ITEMLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEM:
		case DirectiveArgType.DIRARG_CHARLIST:
		case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
		case DirectiveArgType.DIRARG_CHARTEXTLIST:
		case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMFILELIST:
		case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
		case DirectiveArgType.DIRARG_ALLOWED: 
			return new NullConverter();
			
		case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		case DirectiveArgType.DIRARG_ITEMCHARLIST:
		case DirectiveArgType.DIRARG_CHARGROUPS:
			return _characterNumberConverter;
			
		case DirectiveArgType.DIRARG_ITEMREALLIST:
		case DirectiveArgType.DIRARG_CHARREALLIST:
		case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
			return new DirectRealConverter();
			
		case DirectiveArgType.DIRARG_KEYSTATE:
			return new KeyStatesConverter((Integer)arg.getId());
		
		case DirectiveArgType.DIRARG_PRESET:
		case DirectiveArgType.DIRARG_CHARINTEGERLIST:
			return new DirectIntegerConverter();
		}
		return new NullConverter();
	}
}

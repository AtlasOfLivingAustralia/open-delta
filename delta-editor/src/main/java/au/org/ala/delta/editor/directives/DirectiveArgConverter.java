package au.org.ala.delta.editor.directives;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

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
		Object id = converter.convertId(arg.getId());
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

	private void populateArgs(Dir dir, DirectiveArguments args, int directiveType) {
		if (args == null || directiveType == DirectiveArgType.DIRARG_INTERNAL) {
			dir.resizeArgs(0);
		}
		else {
			for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
				IdConverter converter = idConverterFor(directiveType);
				
				DirArgs dirArg = new DirArgs(converter.convertId(arg.getId()));
				dirArg.setText(arg.getText());
				dirArg.comment = arg.getComment();
				for (BigDecimal value : arg.getData()) {
					DirListData data = new DirListData();
					data.setAsDeltaNumber(new DeltaNumber(value.toPlainString()));
					dirArg.getData().add(data);
				}
				dir.args.add(dirArg);
			}
		}
		
	}

 
	interface IdConverter {
		public int convertId(Object id);
		
		public Object convertId(int id);
		
		public BigDecimal convertData(DirListData data);
	}
	
	class CharacterNumberConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return _vop.getDeltaMaster().uniIdFromCharNo((Integer)id);
		}
		@Override
		public Object convertId(int id) {
			return convert(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(convert(data.getIntNumb()));
		}
		private int convert(int id) {
			return _vop.getDeltaMaster().charNoFromUniId(id);
		}
	}
	
	class ItemNumberConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return _vop.getDeltaMaster().uniIdFromItemNo((Integer)id);
		}
		@Override
		public Object convertId(int id) {
			return convert(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(convert(data.getIntNumb()));
		}
		private int convert(int id) {
			return _vop.getDeltaMaster().itemNoFromUniId(id);
		}
	}
	
	class ItemDescriptionConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			throw new NotImplementedException();
		}
		/**
		 * The conversion in this direction converts into an integer
		 * (item number) not description for compatibility with the existing
		 * code.
		 */
		@Override
		public Object convertId(int id) {
			if (id > 0) {
				return _vop.getDeltaMaster().itemNoFromUniId(id);
			}
			return id;
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			throw new UnsupportedOperationException();
		}
	}
	
	class DirectIntegerConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return (Integer)id;
		}
		@Override
		public Object convertId(int id) {
			return Integer.valueOf(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(data.getIntNumb());
		}
	}
	class DirectRealConverter extends DirectIntegerConverter {
		@Override
		public int convertId(Object id) {
			return (Integer)id;
		}
		@Override
		public Object convertId(int id) {
			return Integer.valueOf(id);
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return new BigDecimal(data.asString());
		}
	}
	
	class NullConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return 0;
		}
		@Override
		public Object convertId(int id) {
			return null;
		}
		@Override
		public BigDecimal convertData(DirListData data) {
			return null;
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
			int charBaseId = _vop.getDeltaMaster().uniIdFromCharNo(_characterNumber);
			VOCharBaseDesc charBase = (VOCharBaseDesc)_vop.getDescFromId(charBaseId);
			if (CharType.isMultistate(charBase.getCharType())) {
				int stateId = data.getIntNumb();
				return new BigDecimal(charBase.stateNoFromUniId(stateId));
			}
			return new BigDecimal(data.asString());
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
			return new NullConverter();
		
		case DirectiveArgType.DIRARG_INTEGER:
		case DirectiveArgType.DIRARG_TEXTLIST:
			return new DirectIntegerConverter();
			
		case DirectiveArgType.DIRARG_REAL:
			return new DirectRealConverter();
			
		case DirectiveArgType.DIRARG_ITEM:
		case DirectiveArgType.DIRARG_ITEMREALLIST:
		case DirectiveArgType.DIRARG_ITEMLIST:
		case DirectiveArgType.DIRARG_ITEMCHARLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEM:
		case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
			return _itemNumberConverter;
			
		case DirectiveArgType.DIRARG_CHARLIST:
		case DirectiveArgType.DIRARG_CHARREALLIST:
		case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		case DirectiveArgType.DIRARG_CHARTEXTLIST:
		
		case DirectiveArgType.DIRARG_CHAR:
		case DirectiveArgType.DIRARG_CHARGROUPS:
		case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
		case DirectiveArgType.DIRARG_KEYSTATE:
		case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		case DirectiveArgType.DIRARG_PRESET:
			return _characterNumberConverter;
			
		case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMFILELIST:
			return _itemDescriptionConverter;

		case DirectiveArgType.DIRARG_ALLOWED:
			throw new NotImplementedException();

		case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
			throw new NotImplementedException();
	
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
			
		case DirectiveArgType.DIRARG_ALLOWED:
			throw new NotImplementedException();

		case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
			throw new NotImplementedException();
	
		}
		return new NullConverter();
	}
}

package au.org.ala.delta.editor.directives;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgs;
import au.org.ala.delta.directives.args.IdTextList;
import au.org.ala.delta.directives.args.IdTextList.IdTextArg;
import au.org.ala.delta.directives.args.IntegerArg;
import au.org.ala.delta.directives.args.IntegerList;
import au.org.ala.delta.directives.args.TextArg;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.VODirFileDesc;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirArgs;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;

/**
 * Creates and populates a Dir object from the arguments supplied to a AbstractDirective.
 */
public class DirectiveArgConverter {

	
	private ItemDescriptionConverter _itemDescriptionConverter = new ItemDescriptionConverter();
	private CharacterNumberConverter _characterNumberConverter = new CharacterNumberConverter();
	private ItemNumberConverter _itemNumberConverter = new ItemNumberConverter();
	
	private DeltaVOP _vop;
	public DirectiveArgConverter(DeltaVOP vop) {
		_vop = vop;
	}
	
	public Dir fromDirective(AbstractDirective<? extends AbstractDeltaContext> directive) {

		Dir dir = new Dir();
		Directive directiveDescription = ConforDirType.typeOf(directive);
		dir.setDirType(directiveDescription.getNumber());

		PopulateArgs populateArgs = argsPopulatorFor(directive);
		populateArgs.populateArgs(dir, directive.getDirectiveArgs());

		return dir;
	}

	interface PopulateArgs {
		public void populateArgs(Dir dir, DirectiveArgs args);	
	}

	class PopulateTextArgs implements PopulateArgs {
		@Override
		public void populateArgs(Dir dir, DirectiveArgs args) {
			String text = ((TextArg) args).getText();
			dir.resizeArgs(1);
			dir.args.get(0).text = text;
		}
	}
	
	class PopulateNumberArg implements PopulateArgs {
		@Override
		public void populateArgs(Dir dir, DirectiveArgs args) {

			int value = ((IntegerArg) args).getValue();
			dir.resizeArgs(1);
			dir.args.get(0).setValue(value);
		}
	}
	
	class PopulateNumberListArg implements PopulateArgs {
		@Override
		public void populateArgs(Dir dir, DirectiveArgs args) {
			
			List<Integer> values = ((IntegerList) args).getArgList();
			for (int value : values) {
				DirArgs dirArgs = new DirArgs();
				dirArgs.setValue(value);
				dir.args.add(dirArgs);
			}
		}
	}

	class NoArgs implements PopulateArgs {
		public void populateArgs(Dir dir, DirectiveArgs args) {}
	}
	
	class PopulateIdListArg implements PopulateArgs {
		
		private IdConverter<Integer> _idConverter;
		public PopulateIdListArg(IdConverter<Integer> idConverter) {
			_idConverter = idConverter;
		}
		
		public void populateArgs(Dir dir, DirectiveArgs args) {
			
			List<Integer> argList = ((IntegerList) args).getArgList();
			processArgList(argList, dir);
		}
		
		public void processArgList(List<Integer> args, Dir dir) {
			for (int number : args) {
				int id = _idConverter.convertId(number);
				DirArgs dirArgs = new DirArgs();
				dirArgs.setId(id);
				dir.args.add(dirArgs);
			}
		}
	}
	
	class PopulateIdArg implements PopulateArgs  {
		
		private IdConverter<Integer> _idConverter;
		public PopulateIdArg(IdConverter<Integer> idConverter) {
			_idConverter = idConverter;
		}
		public void populateArgs(Dir dir, DirectiveArgs args) {
			int value = ((IntegerArg) args).getValue();
			int id = _idConverter.convertId(value);
			
			dir.args.add(new DirArgs(id));
		}
	}
	
	class PopulateIdTextListArg<T> implements PopulateArgs {
		
		private IdConverter<T> _idConverter;
		public PopulateIdTextListArg(IdConverter<T> idConverter) {
			_idConverter = idConverter;
		}
		public void populateArgs(Dir dir, DirectiveArgs args) {
			@SuppressWarnings("unchecked")
			IdTextList<T> idTextList = (IdTextList<T>)args;
			
			DirArgs arg = new DirArgs(VODirFileDesc.VOUID_NAME);
			arg.text = idTextList.getDelimiter();
			dir.args.add(arg);
			
			for (IdTextArg<T> charText : idTextList.getCharacterTextList()) {
				int id = _idConverter.convertId(charText.getId());
				arg = new DirArgs(id);
				arg.comment = charText.getComment();
				arg.text = charText.getText();
				dir.args.add(arg);
			}
		}
	}
 
	interface IdConverter<T> {
		public int convertId(T id);
	}
	
	class CharacterNumberConverter implements IdConverter<Integer> {
		@Override
		public int convertId(Integer id) {
			return _vop.getDeltaMaster().uniIdFromCharNo(id);
		}
	}
	
	class ItemNumberConverter implements IdConverter<Integer> {
		@Override
		public int convertId(Integer id) {
			return _vop.getDeltaMaster().uniIdFromItemNo(id);
		}
	}
	
	class ItemDescriptionConverter implements IdConverter<String> {
		@Override
		public int convertId(String id) {
			throw new NotImplementedException();
		}
	}
	
	class NoopConverter implements IdConverter<Integer> {
		@Override
		public int convertId(Integer id) {
			return id;
		}
	}
	
	
 
	
	private PopulateArgs argsPopulatorFor(AbstractDirective<? extends AbstractDeltaContext> directive) {
		int argType = directive.getArgType();
		switch (argType) {
		case DirectiveArgType.DIRARG_NONE:
		case DirectiveArgType.DIRARG_TRANSLATION:
		case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		case DirectiveArgType.DIRARG_INTERNAL: // Not sure about this - existing code treats it like text.
			return new NoArgs();
		case DirectiveArgType.DIRARG_COMMENT: // Will actually be handled within DirInComment
		case DirectiveArgType.DIRARG_TEXT: // What about multiple lines of text? Should line breaks ALWAYS be
											// preserved?
		case DirectiveArgType.DIRARG_FILE:
		case DirectiveArgType.DIRARG_OTHER:
		
			return new PopulateTextArgs();
		
		case DirectiveArgType.DIRARG_INTEGER:
		case DirectiveArgType.DIRARG_REAL:
			return new PopulateNumberArg();
			
		case DirectiveArgType.DIRARG_CHAR:
			return new PopulateIdArg(_characterNumberConverter);
		case DirectiveArgType.DIRARG_ITEM:
			return new PopulateIdArg(_itemNumberConverter);
		case DirectiveArgType.DIRARG_CHARLIST:
			return new PopulateIdListArg(_characterNumberConverter);
		case DirectiveArgType.DIRARG_ITEMLIST:
			return new PopulateIdListArg(_itemNumberConverter);
		case DirectiveArgType.DIRARG_TEXTLIST:
			return new PopulateIdTextListArg<Integer>(new NoopConverter());
		case DirectiveArgType.DIRARG_CHARTEXTLIST:
			return new PopulateIdTextListArg<Integer>(_characterNumberConverter);
		case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMFILELIST:
			return new PopulateIdTextListArg<String>(_itemDescriptionConverter);
		case DirectiveArgType.DIRARG_CHARINTEGERLIST:
			
		case DirectiveArgType.DIRARG_CHARREALLIST:
		case DirectiveArgType.DIRARG_ITEMREALLIST:

			break;

		case DirectiveArgType.DIRARG_CHARGROUPS:

			break;

		case DirectiveArgType.DIRARG_ITEMCHARLIST:

			break;

		case DirectiveArgType.DIRARG_ALLOWED:

			break;

		case DirectiveArgType.DIRARG_KEYSTATE:

			break;

		case DirectiveArgType.DIRARG_PRESET:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ONOFF:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ITEM:

			break;

		case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
			break;
		case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:

			break;

		case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
			break;
		
			
		}
		return new NoArgs();
	}
}

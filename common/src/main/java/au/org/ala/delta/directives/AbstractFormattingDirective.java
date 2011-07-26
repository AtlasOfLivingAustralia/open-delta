package au.org.ala.delta.directives;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.directives.args.TextListParser;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;

/**
 * Base class for handling the TYPESETTING MARKS and FORMATTING MARKS directives.
 */
public abstract class AbstractFormattingDirective extends AbstractCustomDirective {

	public AbstractFormattingDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_TEXTLIST;
	}
	
	
	@Override
	protected TextListParser<?> createParser(DeltaContext context, StringReader reader) {
		return new IntegerTextListParser(context, reader);
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
	
		boolean hasDelimiter = !StringUtils.isEmpty(args.get(0).getText());
		
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			
			int id = ((Integer)arg.getId());
			String markText = cleanWhiteSpace(arg.getText());
			boolean allowWhiteSpace = hasDelimiter && markText.startsWith(" ");
			TypeSettingMark mark = new TypeSettingMark(id, markText.trim(), allowWhiteSpace);
			processMark(context, mark);
		}
		
	}
	
	protected abstract void processMark(DeltaContext context, TypeSettingMark mark);
	
	protected String cleanWhiteSpace(String input) {
	
		Pattern p = Pattern.compile("(\\W)\\s(\\W)");
		Matcher m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		p = Pattern.compile("(\\W)\\s(\\w)");
		m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		p = Pattern.compile("(\\w)\\s(\\W)");
		m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		return  input;
	}
}

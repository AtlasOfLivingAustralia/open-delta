package au.org.ala.delta.directives;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;

/**
 * Processes the TYPESETTING MARKS directive.
 */
public class TypeSettingMarks extends AbstractTextListDirective {

	public TypeSettingMarks() {
		super("typesetting", "marks");
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_TEXTLIST;
	}
	
	@Override
	public void process(DeltaContext context, String data) throws Exception {
		IntegerTextListParser parser = new IntegerTextListParser(context, new StringReader(data));
		
		parser.parse();
		
		DirectiveArguments args = parser.getDirectiveArgs();
	
		boolean hasDelimiter = !StringUtils.isEmpty(args.get(0).getText());
		
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			
			MarkPosition markPos = MarkPosition.fromId((Integer)arg.getId());
			String markText = cleanWhiteSpace(arg.getText());
			boolean allowWhiteSpace = hasDelimiter && markText.startsWith(" ");
			TypeSettingMark mark = new TypeSettingMark(markPos, markText.trim(), allowWhiteSpace);
			context.addTypeSettingMark(mark);
		}
		
	}
	
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

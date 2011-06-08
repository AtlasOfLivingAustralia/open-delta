package au.org.ala.delta.directives;

import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.IdTextList;
import au.org.ala.delta.directives.args.IdTextList.IdTextArg;
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
		
		IdTextList<Integer> args = parser.getDirectiveArgs();
	
		boolean hasDelimiter = !StringUtils.isEmpty(args.getDelimiter());
		
		List<IdTextArg<Integer>> list = args.getIdTextList();
		
		for (IdTextArg<Integer> arg : list) {
			
			MarkPosition markPos = MarkPosition.fromId(arg.getId());
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

package au.org.ala.delta.key.directives;

import java.io.StringReader;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.PresetCharactersParser;
import au.org.ala.delta.key.KeyContext;

public class PresetCharactersDirective extends AbstractTextDirective {

    public PresetCharactersDirective() {
        super("preset", "characters");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        String data = directiveArguments.getFirstArgumentText().trim();
        StringReader reader = new StringReader(data);
        PresetCharactersParser parser = new PresetCharactersParser(context, reader);
        parser.parse();
        addPresetCharacters((KeyContext) context, parser.getDirectiveArgs());
    }

    protected void addPresetCharacters(KeyContext context, DirectiveArguments args) {
        for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
            List<Integer> argValues = arg.getDataList();
            int characterNumber = (Integer) arg.getId();
            int columnNumber = argValues.get(0);
            int groupNumber = argValues.get(1);
            context.setPresetCharacter(characterNumber, columnNumber, groupNumber);
        }
    }

}

package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractCustomDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IdWithIdListParser;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.ItemNumberValidator;
import au.org.ala.delta.key.KeyContext;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.HashSet;

public class TreatCharactersAsVariableDirective extends AbstractCustomDirective {

    public TreatCharactersAsVariableDirective() {
        super("treat", "characters", "as", "variable");
    }

    @Override
    protected DirectiveArgsParser createParser(DeltaContext context, StringReader reader) {
        return new TreatCharacterAsVariableParser(context, reader);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_ITEMCHARLIST;
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        KeyContext keyContext = (KeyContext) context;
        for (DirectiveArgument<?> arg : directiveArguments.getDirectiveArguments()) {
            keyContext.setVariableCharactersForTaxon((Integer) arg.getId(), new HashSet<Integer>(arg.getDataList()));

        }
    }

    private class TreatCharacterAsVariableParser extends IdWithIdListParser {

        public TreatCharacterAsVariableParser(DeltaContext context, Reader reader) {
            super(context, reader, new ItemNumberValidator(context), new CharacterNumberValidator(context));
        }

        @Override
        protected Object readId() throws ParseException {
            expect('#');

            mark();
            readNext();
            if (Character.isDigit(_currentChar)) {
                reset();
                return readListId(_validator);
            } else {
                // Only integers are excepted
                throw DirectiveError.asException(DirectiveError.Error.INVALID_TAXON_NUMBER, _position);
            }
        }

    }

}

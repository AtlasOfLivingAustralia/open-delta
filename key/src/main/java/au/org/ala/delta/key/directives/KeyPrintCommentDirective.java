package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.key.KeyContext;

public class KeyPrintCommentDirective extends AbstractTextDirective {

    public KeyPrintCommentDirective() {
        super("print", "comment");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        KeyContext keyContext = (KeyContext) context;
        String content = directiveArguments.getFirstArgumentText().trim();
        keyContext.setTypeSettingFileHeaderText(content);
    }

}

package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.ConforDirective;
import au.org.ala.delta.intkey.directives.invocation.FileTaxaDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.InvocationFactory;

public class FileTaxaDirective extends AbstractDirective<IntkeyContext> {
    
    public FileTaxaDirective() {
        super("file", "taxa");
    }

    @Override
    public void process(IntkeyContext context, String data) throws Exception {
        FileTaxaDirectiveInvocation invc = InvocationFactory.createFileTaxaInvocation(data);
        invc.execute();
    }

}

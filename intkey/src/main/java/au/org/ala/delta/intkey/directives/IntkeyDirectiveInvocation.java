package au.org.ala.delta.intkey.directives;



/**
 * Command pattern. Represents a call to one of the Intkey
 * directives with specific arguments.
 * @author Chris
 *
 */
public interface IntkeyDirectiveInvocation {
    
    public abstract void execute(IntkeyContext context);
    
}

package au.org.ala.delta.intkey.directives;



/**
 * Command pattern. Represents a call to one of the Intkey
 * directives with specific arguments.
 * @author Chris
 *
 */
public interface IntkeyDirectiveInvocation {
    
    /**
     * Perform execution
     * @param context State object to set values on
     * @return success
     */
    public abstract boolean execute(IntkeyContext context);
    
}

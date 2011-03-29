package au.org.ala.delta.intkey.directives.invocation;


/**
 * Command pattern. Represents a call to one of the Intkey
 * directives with specific arguments.
 * @author Chris
 *
 */
public abstract class IntkeyDirectiveInvocation {
    
    public void execute() {
        //save in command history
        
        //write to output files etc.
        doExecute();
    }
    
    protected abstract void doExecute();
    
}

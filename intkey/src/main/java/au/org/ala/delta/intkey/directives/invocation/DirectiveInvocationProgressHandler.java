package au.org.ala.delta.intkey.directives.invocation;

/**
 * Handles progress messages generated while executing a long running directive
 * invocation.
 * 
 * @author ChrisF
 * 
 */
public interface DirectiveInvocationProgressHandler {

    void progress(String message);
}

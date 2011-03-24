package au.org.ala.delta.directives;

public abstract class AbstractDirective<T extends AbstractDeltaContext> {
    
    public abstract void process(T context);
}

package au.org.ala.delta.util;


public interface Visitor<T> {

	boolean visit(T item);
	
}

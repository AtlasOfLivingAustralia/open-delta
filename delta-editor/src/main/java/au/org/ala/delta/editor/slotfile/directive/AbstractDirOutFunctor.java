package au.org.ala.delta.editor.slotfile.directive;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.translation.PrintFile;
import au.org.ala.delta.translation.delta.DeltaWriter;

/**
 * Base class for directive export functors.  Provides utility methods
 * for helping to write the directive.
 */
public abstract class AbstractDirOutFunctor implements DirectiveFunctor {
	
	protected StringBuilder _textBuffer;
	protected DeltaWriter _deltaWriter;
	/**
	 * Writes the directive name then delegates to subclass to supply the
	 * arguments.
	 */
	@Override
	public void process(DirectiveInOutState state) {
		
		_textBuffer = new StringBuilder();
		_deltaWriter = new DeltaWriter(state.getPrinter());
		writeDirective(state);
		writeDirectiveArguments(state);
	}
	
	/**
	 * Should be overridden by subclasses to write the directive arguments.
	 */
	protected abstract void writeDirectiveArguments(DirectiveInOutState state);
	
	/**
	 * Writes the name of the directive to the output supplied by the 
	 * DirectiveInOutState.
	 */
	protected void writeDirective(DirectiveInOutState state) {
		
		_textBuffer.append('*');
		DirectiveInstance directive = state.getCurrentDirective();
	    if (directive.isCommented()) {
	    	_textBuffer.append("COMMENT ");
	    }
	    
	    Directive directiveInfo = directive.getDirective();
	    _textBuffer.append(directiveInfo.joinNameComponents().toUpperCase());
		state.getPrinter().writeJustifiedText(_textBuffer.toString(), -1);
		_textBuffer = new StringBuilder();
	}
	
	/**
	 * Writes a single line of text to the printer supplied by the 
	 * DirectiveInOutState.
	 */
	protected void writeLine(DirectiveInOutState state, String text) {
		PrintFile printer = state.getPrinter();
		printer.writeJustifiedText(text, -1);
		printer.printBufferLine();
	}
	
	protected void outputTextBuffer(int startIndent, int wrapIndent, boolean preserveNewLines) {
		_deltaWriter.outputTextBuffer(_textBuffer.toString(), startIndent, wrapIndent, preserveNewLines);
		_textBuffer = new StringBuilder();
	}
		
}

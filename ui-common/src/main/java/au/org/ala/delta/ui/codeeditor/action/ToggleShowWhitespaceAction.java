package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeEditor;



public class ToggleShowWhitespaceAction extends EditorAction {

    public ToggleShowWhitespaceAction(CodeEditor textArea) {
        super(textArea);
    }

    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        codeEditor.toggleShowWhitespace();
    }

}

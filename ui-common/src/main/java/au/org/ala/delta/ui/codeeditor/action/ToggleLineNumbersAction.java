package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeTextArea;


public class ToggleLineNumbersAction extends EditorAction {

    private static final long serialVersionUID = 1L;

    public ToggleLineNumbersAction(CodeTextArea textArea) {
        super(textArea);
    }


    public void actionPerformed(ActionEvent e) {
        codeEditor.setShowLineNumbers(!codeEditor.getShowLineNumbers());
    }

}

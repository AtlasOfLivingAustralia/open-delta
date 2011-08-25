package au.org.ala.delta.ui.codeeditor.action;

import java.awt.event.ActionEvent;

import au.org.ala.delta.ui.codeeditor.CodeEditor;


public class CommentBlockAction extends EditorAction {

    public CommentBlockAction(CodeEditor textArea) {
        super(textArea);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
        if ((e.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
            if ((e.getModifiers() & ActionEvent.SHIFT_MASK) > 0) {
                codeEditor.uncommentSelectedLines();
            } else {
                codeEditor.commentSelectedLines();                        
            }
        }
    }

}

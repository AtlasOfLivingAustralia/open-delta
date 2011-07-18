package au.org.ala.delta.intkey.ui;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JTextPane;
import javax.swing.text.EditorKit;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class TextReportDisplayDialog extends JDialog {
    private JTextPane _textPane;
    private JScrollPane _scrollPane;

    private String _contentSource;

    public TextReportDisplayDialog(EditorKit editorKit, String contentSource, String title) {

        _scrollPane = new JScrollPane();
        getContentPane().add(_scrollPane, BorderLayout.CENTER);

        _textPane = new JTextPane();
        _textPane.setEditable(false);
        _scrollPane.setViewportView(_textPane);

        _contentSource = contentSource;

        _textPane.setEditorKit(editorKit);
        _textPane.setText(contentSource);

        // ensure that top of text is visible after text has been inserted.
        _textPane.setCaretPosition(0);

        _contentSource = contentSource;

        setTitle(title);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenu mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        JMenu mnWindow = new JMenu("Window");
        menuBar.add(mnWindow);

    }

    public void setEditorKit(EditorKit editorKit) {
        _textPane.setEditorKit(editorKit);
    }

    public void setContent(String source) {
        _textPane.setText(source);
    }

}

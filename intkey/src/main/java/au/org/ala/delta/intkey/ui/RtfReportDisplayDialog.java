package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.EditorKit;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import java.awt.Dimension;

public class RtfReportDisplayDialog extends IntkeyDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 7668085877552672061L;

    private JTextPane _textPane;
    private JScrollPane _scrollPane;

    private String _contentSource;
    private JMenuBar _menuBar;
    private JMenu _mnuFile;
    private JMenuItem _mnuItSaveAs;
    private JMenu _mnuEdit;
    private JMenuItem _mnuItFind;
    private JMenuItem _mnuItCopy;
    private JMenuItem _mnuItSelectAll;
    private JMenu _mnuWindow;
    private JMenuItem _mnuItCascade;
    private JMenuItem _mnuItTile;
    private JMenuItem _mnuItCloseAll;

    @Resource
    String fileFilterDescription;

    @Resource
    String fileWriteError;

    @Resource
    String fileChooserTitle;

    /**
     * @wbp.parser.constructor
     */
    public RtfReportDisplayDialog(Dialog owner, EditorKit editorKit, String contentSource, String title) {
        super(owner, false);
        initialize(editorKit, contentSource, title);
    }

    public RtfReportDisplayDialog(Frame owner, EditorKit editorKit, String contentSource, String title) {
        super(owner, false);
        initialize(editorKit, contentSource, title);
    }

    public void initialize(EditorKit editorKit, String contentSource, String title) {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(RtfReportDisplayDialog.class, this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(RtfReportDisplayDialog.class);
        resourceMap.injectFields(this);

        setPreferredSize(new Dimension(800, 450));

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

        _menuBar = new JMenuBar();
        setJMenuBar(_menuBar);

        _mnuFile = new JMenu();
        _mnuFile.setName("rtfReportDisplayDialog_mnuFile");
        _menuBar.add(_mnuFile);

        _mnuItSaveAs = new JMenuItem();
        _mnuItSaveAs.setAction(actionMap.get("rtfReportDisplayDialog_mnuItSaveAs"));
        _mnuFile.add(_mnuItSaveAs);

        _mnuEdit = new JMenu();
        _mnuEdit.setName("rtfReportDisplayDialog_mnuEdit");
        _mnuEdit.setEnabled(false);
        _menuBar.add(_mnuEdit);

        _mnuItFind = new JMenuItem();
        _mnuItFind.setAction(actionMap.get("rtfReportDisplayDialog_mnuItFind"));
        _mnuEdit.add(_mnuItFind);

        _mnuEdit.addSeparator();

        _mnuItCopy = new JMenuItem();
        _mnuItCopy.setAction(actionMap.get("rtfReportDisplayDialog_mnuItCopy"));
        _mnuEdit.add(_mnuItCopy);

        _mnuEdit.addSeparator();

        _mnuItSelectAll = new JMenuItem();
        _mnuItSelectAll.setAction(actionMap.get("rtfReportDisplayDialog_mnuItSelectAll"));
        _mnuEdit.add(_mnuItSelectAll);

        _mnuWindow = new JMenu();
        _mnuWindow.setName("rtfReportDisplayDialog_mnuWindow");
        _mnuWindow.setEnabled(false);
        _menuBar.add(_mnuWindow);

        _mnuItCascade = new JMenuItem();
        _mnuItCascade.setAction(actionMap.get("rtfReportDisplayDialog_mnuItCascade"));
        _mnuWindow.add(_mnuItCascade);

        _mnuItTile = new JMenuItem();
        _mnuItTile.setAction(actionMap.get("rtfReportDisplayDialog_mnuItTile"));
        _mnuWindow.add(_mnuItTile);

        _mnuItCloseAll = new JMenuItem();
        _mnuItCloseAll.setAction(actionMap.get("rtfReportDisplayDialog_mnuItCloseAll"));
        _mnuWindow.add(_mnuItCloseAll);

    }

    public void setEditorKit(EditorKit editorKit) {
        _textPane.setEditorKit(editorKit);
    }

    public void setContent(String source) {
        _textPane.setText(source);
    }

    @Action
    public void rtfReportDisplayDialog_mnuItSaveAs() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(fileChooserTitle);
        chooser.setFileFilter(new FileNameExtensionFilter(fileFilterDescription, "rtf"));
        chooser.setAcceptAllFileFilterUsed(false);

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile != null) {
                if (!selectedFile.getName().endsWith(".rtf")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".rtf");
                }

                try {
                    FileWriter fw = new FileWriter(selectedFile);
                    fw.append(_contentSource);
                    fw.flush();
                    fw.close();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, String.format(fileWriteError, selectedFile.getAbsolutePath()));
                }
            }
        }
    }

    @Action
    public void rtfReportDisplayDialog_mnuItFind() {

    }

    @Action
    public void rtfReportDisplayDialog_mnuItCopy() {

    }

    @Action
    public void rtfReportDisplayDialog_mnuItSelectAll() {

    }

    @Action
    public void rtfReportDisplayDialog_mnuItCascade() {

    }

    @Action
    public void rtfReportDisplayDialog_mnuItTile() {

    }

    @Action
    public void rtfReportDisplayDialog_mnuItCloseAll() {

    }

}

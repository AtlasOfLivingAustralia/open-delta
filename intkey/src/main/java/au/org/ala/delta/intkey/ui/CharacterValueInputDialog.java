package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.image.ImageSettings;

public abstract class CharacterValueInputDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 361631735179369565L;
    
    protected JPanel _buttonPanel;
    protected JButton _btnImages;
    protected JButton _btnFullText;
    protected JButton _btnSearch;
    protected JButton _btnCancel;
    protected JButton _btnNotes;
    protected JButton _btnHelp;
    protected JPanel _pnlMain;
    protected JLabel _lblCharacterDescription;
    protected Character _ch;
    protected CharacterFormatter _formatter;
    protected ImageSettings _imageSettings;

    public CharacterValueInputDialog(Frame owner, Character ch, ImageSettings imageSettings) {
        super(owner, true);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(CharacterValueInputDialog.class, this); 
        
        getContentPane().setLayout(new BorderLayout(0, 0));

        setResizable(false);
        setPreferredSize(new Dimension(600, 200));
        
        _ch = ch;
        _imageSettings = imageSettings;
        
        _buttonPanel = new JPanel();
        _buttonPanel.setBorder(new EmptyBorder(0, 20, 10, 20));
        getContentPane().add(_buttonPanel, BorderLayout.SOUTH);
        _buttonPanel.setLayout(new GridLayout(0, 4, 5, 2));
        
        JButton _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("characterValueInputDialog_OK"));
        _buttonPanel.add(_btnOk);

        _btnImages = new JButton();
        _btnImages.setAction(actionMap.get("characterValueInputDialog_Images"));
        if (ch.getImageCount() == 0) {
            _btnImages.setEnabled(false);
        }
        _buttonPanel.add(_btnImages);

        _btnFullText = new JButton();
        _btnFullText.setAction(actionMap.get("characterValueInputDialog_FullText"));
        _btnFullText.setEnabled(false);
        _buttonPanel.add(_btnFullText);

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("characterValueInputDialog_Search"));
        _btnSearch.setEnabled(false);
        _buttonPanel.add(_btnSearch);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("characterValueInputDialog_Cancel"));        
        _buttonPanel.add(_btnCancel);

        _btnNotes = new JButton();
        _btnNotes.setAction(actionMap.get("characterValueInputDialog_Notes"));
        _btnNotes.setEnabled(false);
        _buttonPanel.add(_btnNotes);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("characterValueInputDialog_Help"));
        _btnHelp.setEnabled(false);
        _buttonPanel.add(_btnHelp);

        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _lblCharacterDescription = new JLabel();
        _lblCharacterDescription.setBorder(new EmptyBorder(0, 0, 5, 0));
        _formatter = new CharacterFormatter(false, false, false, true, true);
        _lblCharacterDescription.setText(_formatter.formatCharacterDescription(_ch));
        _pnlMain.add(_lblCharacterDescription, BorderLayout.NORTH);
        
        setLocationRelativeTo(owner);
    }

    abstract void handleBtnOKClicked();
    abstract void handleBtnCancelClicked();
    abstract void handleBtnImagesClicked();
    
    // Button action handlers
    
    @Action
    public void characterValueInputDialog_OK() {
        handleBtnOKClicked();
    }
    
    @Action
    public void characterValueInputDialog_Images() {
        handleBtnImagesClicked();
    }
    
    @Action
    public void characterValueInputDialog_FullText() {
    }
    
    @Action
    public void characterValueInputDialog_Search() {
    }
    
    @Action
    public void characterValueInputDialog_Cancel() {
        handleBtnCancelClicked();
    }
    
    @Action
    public void characterValueInputDialog_Notes() {
    }
    
    @Action
    public void characterValueInputDialog_Help() {
    }
}

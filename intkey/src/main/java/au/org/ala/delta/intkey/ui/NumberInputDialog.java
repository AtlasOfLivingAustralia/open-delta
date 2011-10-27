package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.Formatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.image.ImageSettings;

public abstract class NumberInputDialog extends CharacterValueInputDialog {
    /**
     * 
     */
    private static final long serialVersionUID = -7872379958955241882L;
    protected JTextField _txtInput;
    private JLabel _lblUnits;

    public NumberInputDialog(Frame owner, NumericCharacter<?> ch, ImageSettings imageSettings, boolean displayNumbering, boolean enableImagesButton) {
        super(owner, ch, imageSettings, displayNumbering, enableImagesButton);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(NumberInputDialog.class);
        resourceMap.injectFields(this);

        JPanel panel = new JPanel();
        _pnlMain.add(panel, BorderLayout.CENTER);

        _txtInput = new JTextField();
        _txtInput.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                NumberInputDialog.this.handleBtnOKClicked();
            }
        });
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.add(_txtInput);
        _txtInput.setColumns(30);

        _lblUnits = new JLabel();
        panel.add(_lblUnits);

        if (ch.getUnits() != null) {
            _lblUnits.setText(new Formatter(CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false).defaultFormat(ch.getUnits()));
        } else {
            _lblUnits.setVisible(false);
        }
    }

}

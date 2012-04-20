package au.org.ala.delta.intkey.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * This panel is shown in place of the available characters list in the main
 * Intkey window when no matching taxa remain. It includes a button to allow an
 * additional number of "mismatches" (by increasing the tolerance value)
 * 
 * @author ChrisF
 * 
 */
public class AllowMismatchMessagePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 693756123167309540L;

    private JLabel _lblMessage;
    private JPanel _pnlButton;
    private JButton _btnHelp;

    @Resource
    String allowOneMismatchCaption;

    @Resource
    String allowMultipleMismatchesCaption;

    private IntkeyContext _context;
    private String _helpTopicId;
    private int _currentToleranceValue;

    public AllowMismatchMessagePanel(String message, String helpTopicId, IntkeyContext context) {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(AllowMismatchMessagePanel.class, this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AllowMismatchMessagePanel.class);
        resourceMap.injectFields(this);

        _context = context;

        _currentToleranceValue = _context.getTolerance();

        _helpTopicId = helpTopicId;

        setBackground(Color.WHITE);
        setLayout(new GridLayout(0, 1, 0, 0));

        _lblMessage = new JLabel(message);
        _lblMessage.setVerticalAlignment(SwingConstants.BOTTOM);
        _lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        add(_lblMessage);

        _pnlButton = new JPanel();
        _pnlButton.setBackground(Color.WHITE);
        add(_pnlButton);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("AllowMismatchMessagePanel_Help"));
        _pnlButton.add(_btnHelp);

        String misMatchButtonCaption;
        if (_currentToleranceValue == 0) {
            misMatchButtonCaption = allowOneMismatchCaption;
        } else {
            misMatchButtonCaption = MessageFormat.format(allowMultipleMismatchesCaption, _currentToleranceValue + 1);
        }

        JButton btnAllowMismatch = new JButton(misMatchButtonCaption);
        btnAllowMismatch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                _context.setTolerance(_currentToleranceValue + 1);
            }
        });
        _pnlButton.add(btnAllowMismatch);
    }

    @Action
    public void AllowMismatchMessagePanel_Help(ActionEvent event) {
        UIUtils.displayHelpTopic(_helpTopicId, UIUtils.getMainFrame(), event);
    }

}

package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.rtf.RTFUtils;

/**
 * Displays information about an image.
 */
public class AboutImageDialog extends JDialog {

    private static final long serialVersionUID = 3136873729289853125L;
    private int _width;
    private int _height;
    private String _type;
    private int _numColours;
    private String _fileName;
    private String _caption;
    private ResourceMap _resources;
    private JButton _okButton;

    public AboutImageDialog(JComponent parent, String caption, URL imagePath, BufferedImage image, String imageType) {
        init(caption, imagePath, image, imageType);
    }

    public AboutImageDialog(JDialog parent, String caption, URL imagePath, BufferedImage image, String imageType) {
        super(parent, true);
        init(caption, imagePath, image, imageType);
        this.pack();
        setLocationRelativeTo(parent);
    }

    private void init(String caption, URL imagePath, BufferedImage image, String imageType) {
        _caption = RTFUtils.stripFormatting(caption);

        if (imagePath.getProtocol() != "file") {
            _fileName = imagePath.toString();
        } else {
            _fileName = imagePath.getFile();
        }

        _width = image.getWidth();
        _height = image.getHeight();
        _type = imageType;
        countColours(image);

        ApplicationContext context = Application.getInstance().getContext();
        ActionMap actions = context.getActionMap(this);
        _resources = context.getResourceMap();

        createUI();

        _okButton.setAction(actions.get("okPressed"));
    }

    private void createUI() {
        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new CompoundBorder(new EmptyBorder(10, 5, 0, 5), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        getContentPane().add(panel, BorderLayout.CENTER);

        JLabel captionLabel = new JLabel("Caption:");
        captionLabel.setName("captionLabel");

        JLabel captionValueLabel = new JLabel(_caption);

        JLabel pathNameLabel = new JLabel("Pathname:");
        pathNameLabel.setName("pathNameLabel");

        JLabel pathNameValueLabel = new JLabel(_fileName);

        JLabel imageTypeLabel = new JLabel("Image type:");
        imageTypeLabel.setName("imageTypeLabel");

        JLabel widthLabel = new JLabel("Width:");
        widthLabel.setName("widthLabel");

        JLabel heightLabel = new JLabel("Height:");
        heightLabel.setName("heightLabel");

        String coloursMessage = _resources.getString("aboutImageDialog.colourLabel.text", _numColours);
        JLabel imageTypeValueLabel = new JLabel(_type + ", " + coloursMessage);

        JLabel widthValueLabel = new JLabel(Integer.toString(_width));

        JLabel heightValueLabel = new JLabel(Integer.toString(_height));
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_panel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl_panel.createParallelGroup(Alignment.LEADING).addComponent(captionLabel).addComponent(pathNameLabel).addComponent(imageTypeLabel).addComponent(widthLabel)
                                        .addComponent(heightLabel))
                        .addGap(10)
                        .addGroup(
                                gl_panel.createParallelGroup(Alignment.LEADING).addComponent(widthValueLabel).addComponent(imageTypeValueLabel).addComponent(pathNameValueLabel)
                                        .addComponent(captionValueLabel).addComponent(heightValueLabel)).addContainerGap()));
        gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(
                gl_panel.createSequentialGroup().addContainerGap().addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(captionLabel).addComponent(captionValueLabel))
                        .addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(pathNameLabel).addComponent(pathNameValueLabel))
                        .addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(imageTypeLabel).addComponent(imageTypeValueLabel))
                        .addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(widthLabel).addComponent(widthValueLabel))
                        .addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(heightLabel).addComponent(heightValueLabel))
                        .addContainerGap()));
        panel.setLayout(gl_panel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonPanel.setName("okButton");
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        _okButton = new JButton("Ok");
        buttonPanel.add(_okButton);

    }

    /** There is probably a better way to do this */
    private void countColours(BufferedImage image) {
        Set<Integer> colours = new HashSet<Integer>();

        for (int x = 0; x < _width; x++) {
            for (int y = 0; y < _height; y++) {
                colours.add(image.getRGB(x, y));
            }
        }

        _numColours = colours.size();

    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Action
    public void okPressed() {
        setVisible(false);
    }
}

package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

public class BusyGlassPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 3178455415213119732L;
    
    @Resource
    Icon icon;
    private JLabel _lblMessage;
    private JPanel _pnlMessage;
    private JPanel _pnlImage;
    private JLabel _lblBusyBar;

    public BusyGlassPane(String message) {
        super(null, false);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(BusyGlassPane.class);
        resourceMap.injectFields(this);

        this.setVisible(false);
        this.setOpaque(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setLayout(new GridLayout(0, 1, 0, 0));
                
                        _pnlMessage = new JPanel();
                        _pnlMessage.setOpaque(false);
                        add(_pnlMessage);
                        _pnlMessage.setLayout(new BorderLayout(0, 0));
                        
                                _lblMessage = new JLabel(message);
                                _lblMessage.setFont(new Font("Tahoma", Font.BOLD, 11));
                                _pnlMessage.add(_lblMessage, BorderLayout.SOUTH);
                                _lblMessage.setAlignmentY(Component.BOTTOM_ALIGNMENT);
                                _lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
                                _lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
        
                _pnlImage = new JPanel();
                _pnlImage.setOpaque(false);
                add(_pnlImage);
                _pnlImage.setLayout(new BorderLayout(0, 0));
                
                        _lblBusyBar = new JLabel("");
                        _pnlImage.add(_lblBusyBar, BorderLayout.NORTH);
                        _lblBusyBar.setAlignmentY(Component.TOP_ALIGNMENT);
                        _lblBusyBar.setHorizontalAlignment(SwingConstants.CENTER);
                        _lblBusyBar.setAlignmentX(Component.CENTER_ALIGNMENT);
                        _lblBusyBar.setIcon(icon);
        MouseInputListener blockMouseEvents = new MouseInputAdapter() {
        };
        this.addMouseMotionListener(blockMouseEvents);
        this.addMouseListener(blockMouseEvents);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        System.out.println("Setting icon");
        this.icon = icon;
    }
    
    public void setMessage(String message) {
        _lblMessage.setText(message);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 50));
        Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);
    }

}

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

    @Resource
    Icon icon;

    public BusyGlassPane(String message) {
        super(null, false);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(BusyGlassPane.class);
        resourceMap.injectFields(this);

        this.setVisible(false);
        this.setOpaque(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setLayout(new GridLayout(0, 1, 0, 0));

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        add(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JLabel lblMessage = new JLabel(message);
        lblMessage.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel.add(lblMessage, BorderLayout.SOUTH);
        lblMessage.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel panel_1 = new JPanel();
        panel_1.setOpaque(false);
        add(panel_1);
        panel_1.setLayout(new BorderLayout(0, 0));

        JLabel lblBusyBar = new JLabel("");
        panel_1.add(lblBusyBar, BorderLayout.NORTH);
        lblBusyBar.setAlignmentY(Component.TOP_ALIGNMENT);
        lblBusyBar.setHorizontalAlignment(SwingConstants.CENTER);
        lblBusyBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBusyBar.setIcon(icon);
        MouseInputListener blockMouseEvents = new MouseInputAdapter() {
        };
        this.addMouseMotionListener(blockMouseEvents);
        this.addMouseListener(blockMouseEvents);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 50));
        Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);
    }

}

/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class BusyGlassPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 3178455415213119732L;

    @Resource
    Icon icon;

    @Resource
    String cancelButtonCaption;

    private JLabel _lblMessage;
    private JPanel _pnlMessage;
    private JPanel _pnlImage;
    private JLabel _lblBusyBar;
    private JButton _btnCancel;

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
        _pnlImage.setLayout(new BoxLayout(_pnlImage, BoxLayout.Y_AXIS));

        _lblBusyBar = new JLabel("");
        _pnlImage.add(_lblBusyBar);
        _lblBusyBar.setAlignmentY(Component.TOP_ALIGNMENT);
        _lblBusyBar.setHorizontalAlignment(SwingConstants.CENTER);
        _lblBusyBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        _lblBusyBar.setIcon(icon);

        _btnCancel = new JButton(cancelButtonCaption);
        _btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        _btnCancel.setEnabled(false);
        _btnCancel.setVisible(false);
        _btnCancel.setCursor(Cursor.getDefaultCursor());
        _pnlImage.add(_btnCancel);
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

    public void setWorkerForCancellation(final SwingWorker<?, ?> worker) {
        _btnCancel.setEnabled(true);
        _btnCancel.setVisible(true);

        for (ActionListener oldListener : _btnCancel.getActionListeners()) {
            _btnCancel.removeActionListener(oldListener);
        }

        _btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                worker.cancel(false);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 50));
        Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);
    }

}

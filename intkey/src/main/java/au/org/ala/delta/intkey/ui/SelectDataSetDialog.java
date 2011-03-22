package au.org.ala.delta.intkey.ui;

import javax.swing.JDialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JLabel;
import java.awt.BorderLayout;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingConstants;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SelectDataSetDialog extends JDialog {
    
    private String _selectedFilePath;
    private boolean _fileSelected;
    
    private JTextField txtFldFilePath;
    
    public SelectDataSetDialog(Frame owner) {
        super(owner, true);
        setMinimumSize(new Dimension(650, 500));
        
        _selectedFilePath = null;
        _fileSelected = false;
        
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        
        setResizable(false);
        setTitle("Select Data Set");
        setSize(new Dimension(650, 500));
        setName("SelectDataSetDialog");
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel = new JLabel("Select by title:");
        lblNewLabel.setBorder(new EmptyBorder(10, 10, 0, 0));
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel.setName("lblSelectByTitle");
        getContentPane().add(lblNewLabel, BorderLayout.NORTH);
        
        JPanel pnlList = new JPanel();
        pnlList.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(pnlList, BorderLayout.CENTER);
        pnlList.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane = new JScrollPane();
        pnlList.add(scrollPane, BorderLayout.CENTER);
        
        JList listDataSets = new JList();
        scrollPane.setViewportView(listDataSets);
        
        JPanel pnlListButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnlListButtons.getLayout();
        flowLayout.setHgap(20);
        pnlList.add(pnlListButtons, BorderLayout.SOUTH);
        
        JButton btnOk = new JButton("Ok");
        pnlListButtons.add(btnOk);
        
        JButton btnCancel = new JButton("Cancel");
        pnlListButtons.add(btnCancel);
        
        JButton btnHelp = new JButton("Help");
        pnlListButtons.add(btnHelp);
        
        JPanel pnlSelectFile = new JPanel();
        pnlSelectFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSelectFile.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(pnlSelectFile, BorderLayout.SOUTH);
        pnlSelectFile.setLayout(new BorderLayout(0, 0));
        
        JLabel lblSelectByName = new JLabel("Select by name of initialization file:");
        lblSelectByName.setBorder(new EmptyBorder(0, 0, 10, 0));
        lblSelectByName.setHorizontalAlignment(SwingConstants.LEFT);
        lblSelectByName.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnlSelectFile.add(lblSelectByName, BorderLayout.NORTH);
        
        txtFldFilePath = new JTextField();
        pnlSelectFile.add(txtFldFilePath, BorderLayout.CENTER);
        txtFldFilePath.setColumns(10);
        
        JPanel pnlBrowseButton = new JPanel();
        pnlSelectFile.add(pnlBrowseButton, BorderLayout.SOUTH);
        
        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.setAction(actionMap.get("browseForFile"));
        pnlBrowseButton.add(btnBrowse);
    }

    public String getSelectedFilePath() {
        return _selectedFilePath;
    }

    public boolean isFileSelected() {
        return _fileSelected;
    }
    
    @Action
    public void browseForFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data Initialization Files (*.ini, *.ink)", "ini", "ink");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(SelectDataSetDialog.this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           _selectedFilePath = chooser.getSelectedFile().getAbsolutePath();
           _fileSelected =  true;
           //SelectDataSetDialog.this.setVisible(false);
           SelectDataSetDialog.this.dispose();
        }
    }

}

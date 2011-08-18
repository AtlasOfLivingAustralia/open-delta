package au.org.ala.delta.intkey.ui;

import javax.swing.JDialog;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;

public class TaxonInformationDialog extends JDialog {

    private List<Item> _taxa;
    private int _selectedIndex;
    private ItemFormatter _formatter;
    private JPanel _mainPanel;
    private JPanel _comboPanel;
    private JComboBox _comboBox;
    private JPanel _btnPanel;
    private JButton _btnDisplay;
    private JButton _btnMultipleImages;
    private JButton _btnDeselectAll;
    private JButton _btnDone;
    private JPanel _pnlCenter;
    private JPanel _pnlNavigationButtons;
    private JButton _btnStart;
    private JButton _btnPrevious;
    private JButton _btnForward;
    private JButton _btnEnd;
    private JPanel _pnlLists;
    private JPanel _pnlListOther;
    private JScrollPane _sclPnOther;
    private JList _listOther;
    private JLabel _lblOther;
    private JPanel _pnlListIllustrations;
    private JLabel _lblIllustrations;
    private JScrollPane _sclPnIllustrations;
    private JList _listIllustrations;

    public TaxonInformationDialog(List<Item> taxa) {

        setTitle("Taxon Information");
        getContentPane().setLayout(new BorderLayout(0, 0));

        _mainPanel = new JPanel();
        getContentPane().add(_mainPanel, BorderLayout.CENTER);
        _mainPanel.setLayout(new BorderLayout(0, 0));

        _comboPanel = new JPanel();
        _comboPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        _mainPanel.add(_comboPanel, BorderLayout.NORTH);
        _comboPanel.setLayout(new BorderLayout(0, 0));

        _comboBox = new JComboBox();
        _comboPanel.add(_comboBox, BorderLayout.CENTER);

        _btnPanel = new JPanel();
        _btnPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        _mainPanel.add(_btnPanel, BorderLayout.SOUTH);

        _btnDisplay = new JButton("Display");
        _btnPanel.add(_btnDisplay);

        _btnMultipleImages = new JButton("Multiple Images");
        _btnPanel.add(_btnMultipleImages);

        _btnDeselectAll = new JButton("Deselect All");
        _btnPanel.add(_btnDeselectAll);

        _btnDone = new JButton("Done");
        _btnPanel.add(_btnDone);

        _pnlCenter = new JPanel();
        _mainPanel.add(_pnlCenter, BorderLayout.CENTER);
        _pnlCenter.setLayout(new BorderLayout(0, 0));

        _pnlNavigationButtons = new JPanel();
        _pnlCenter.add(_pnlNavigationButtons, BorderLayout.NORTH);

        _btnStart = new JButton("start");
        _pnlNavigationButtons.add(_btnStart);

        _btnPrevious = new JButton("previous");
        _pnlNavigationButtons.add(_btnPrevious);

        _btnForward = new JButton("forward");
        _pnlNavigationButtons.add(_btnForward);

        _btnEnd = new JButton("end");
        _pnlNavigationButtons.add(_btnEnd);

        _pnlLists = new JPanel();
        _pnlCenter.add(_pnlLists, BorderLayout.CENTER);

        _pnlListOther = new JPanel();
        _pnlLists.add(_pnlListOther);
        _pnlListOther.setLayout(new BorderLayout(0, 0));

        _listOther = new JList();

        _sclPnOther = new JScrollPane();
        _sclPnOther.setViewportView(_listOther);
        _pnlListOther.add(_sclPnOther, BorderLayout.CENTER);

        _lblOther = new JLabel("Other");
        _pnlListOther.add(_lblOther, BorderLayout.NORTH);

        _pnlListIllustrations = new JPanel();
        _pnlLists.add(_pnlListIllustrations);
        _pnlListIllustrations.setLayout(new BorderLayout(0, 0));

        _lblIllustrations = new JLabel("Illustrations");
        _pnlListIllustrations.add(_lblIllustrations, BorderLayout.NORTH);

        _listIllustrations = new JList();

        _sclPnIllustrations = new JScrollPane();

        _sclPnIllustrations.setViewportView(_listIllustrations);
        _pnlListIllustrations.add(_sclPnIllustrations, BorderLayout.CENTER);

        _formatter = new ItemFormatter(false, false, true, false, true, false);
        
        _taxa = taxa;
        initialize();
    }

    private void initialize() {
        // fill combobox with taxa names

        DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        for (Item taxon : _taxa) {
            comboModel.addElement(_formatter.formatItemDescription(taxon));
        }
        
        displayTaxon(0);
    }

    private void displayTaxon(int index) {
        Item selectedTaxon = _taxa.get(_selectedIndex);
        
        //Update other list
        
        
        //Update illustrations list
        DefaultListModel illustrationsListModel = new DefaultListModel(); 
        for (Image img: selectedTaxon.getImages()) {
            illustrationsListModel.addElement(img.getSubjectTextOrFileName());
        }
        
        //update button state
        _btnStart.setEnabled(index > 0);
        _btnPrevious.setEnabled(index > 0);
        _btnForward.setEnabled(index < _taxa.size());
        _btnEnd.setEnabled(index < _taxa.size());
        
    }

    public void firstTaxon() {
        _selectedIndex = 0;
        displayTaxon(_selectedIndex);
    }

    public void lastTaxon() {
        _selectedIndex = _taxa.size() - 1;
        displayTaxon(_selectedIndex);
    }

    public void nextTaxon() {
        _selectedIndex++;
        displayTaxon(_selectedIndex);
    }

    public void previousTaxon() {
        _selectedIndex--;
        displayTaxon(_selectedIndex);
    }

}

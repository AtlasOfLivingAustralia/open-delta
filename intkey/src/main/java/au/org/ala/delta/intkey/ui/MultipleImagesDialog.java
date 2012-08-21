package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JList;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MultipleImagesDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 4208552376030099677L;

    @Resource
    String title;

    @Resource
    String allImagesCurrentTaxonCaption;

    @Resource
    String firstImageSelectedTaxaCaption;

    @Resource
    String allImagesSelectedTaxaCaption;

    @Resource
    String closeAllOpenWindowsCaption;

    @Resource
    String selectBySubjectCaption;

    @Resource
    String noImagesForSpecifiedTaxaMsg;

    private ImageSettings _imageSettings;
    private boolean _displayContinuous;
    private boolean _displayScaled;
    private List<Item> _selectedTaxa;
    private List<String> _imageSubjects;
    private Item _currentSelectedTaxon;
    private IntkeyUI _mainUI;

    private JPanel _pnlButtons;
    private JButton _btnOk;
    private JButton _btnCancel;
    private JPanel _pnlMain;
    private JPanel _pnlMainLeft;
    private JRadioButton _rdbtnAllImagesCurrentTaxon;
    private JRadioButton _rdbtnFirstImageSelectedTaxa;
    private JRadioButton _rdbtnAllImagesSelectedTaxa;
    private JCheckBox _chckbxCloseOpenWindows;
    private JPanel _pnlMainRight;
    private JLabel _lblSelectBySubject;
    private JScrollPane _scrollPane;
    private JList _listSubjects;
    private ButtonGroup radioButtonGroup;

    public MultipleImagesDialog(Frame owner, boolean modal, Item currentSelectedTaxon, List<Item> allSelectedTaxa, List<String> imageSubjects, ImageSettings imageSettings, boolean displayContinuous,
            boolean displayScaled, IntkeyUI mainUI) {
        super(owner, modal);
        init(currentSelectedTaxon, allSelectedTaxa, imageSubjects, imageSettings, displayContinuous, displayScaled, mainUI);
    }

    public MultipleImagesDialog(Dialog owner, boolean modal, Item currentSelectedTaxon, List<Item> allSelectedTaxa, List<String> imageSubjects, ImageSettings imageSettings, boolean displayContinuous,
            boolean displayScaled, IntkeyUI mainUI) {
        super(owner, modal);
        init(currentSelectedTaxon, allSelectedTaxa, imageSubjects, imageSettings, displayContinuous, displayScaled, mainUI);
    }

    private void init(Item currentSelectedTaxon, List<Item> allSelectedTaxa, List<String> imageSubjects, ImageSettings imageSettings, boolean displayContinuous, boolean displayScaled, IntkeyUI mainUI) {

        _imageSettings = imageSettings;
        _imageSubjects = imageSubjects;
        _displayContinuous = displayContinuous;
        _displayScaled = displayScaled;
        _selectedTaxa = allSelectedTaxa;
        _currentSelectedTaxon = currentSelectedTaxon;
        _mainUI = mainUI;

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(FindInTaxaDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);

        setTitle(title);

        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("MultipleImagesDialog_OK"));
        _pnlButtons.add(_btnOk);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("MultipleImagesDialog_Cancel"));
        _pnlButtons.add(_btnCancel);

        _pnlMain = new JPanel();
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new GridLayout(0, 2, 0, 0));

        _pnlMainLeft = new JPanel();
        _pnlMainLeft.setBorder(new EmptyBorder(10, 10, 0, 0));
        _pnlMain.add(_pnlMainLeft);
        _pnlMainLeft.setLayout(new BoxLayout(_pnlMainLeft, BoxLayout.Y_AXIS));

        _rdbtnAllImagesCurrentTaxon = new JRadioButton(allImagesCurrentTaxonCaption);
        _rdbtnAllImagesCurrentTaxon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _listSubjects.setEnabled(true);
            }
        });
        _pnlMainLeft.add(_rdbtnAllImagesCurrentTaxon);

        _rdbtnFirstImageSelectedTaxa = new JRadioButton(firstImageSelectedTaxaCaption);
        _rdbtnFirstImageSelectedTaxa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _listSubjects.clearSelection();
                _listSubjects.setEnabled(false);
            }
        });
        _pnlMainLeft.add(_rdbtnFirstImageSelectedTaxa);

        _rdbtnAllImagesSelectedTaxa = new JRadioButton(allImagesSelectedTaxaCaption);
        _rdbtnAllImagesSelectedTaxa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _listSubjects.setEnabled(false);
            }
        });
        _pnlMainLeft.add(_rdbtnAllImagesSelectedTaxa);

        radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(_rdbtnAllImagesCurrentTaxon);
        radioButtonGroup.add(_rdbtnFirstImageSelectedTaxa);
        radioButtonGroup.add(_rdbtnAllImagesSelectedTaxa);

        if (_selectedTaxa.size() < 2) {
            _rdbtnAllImagesSelectedTaxa.setEnabled(false);
            _rdbtnFirstImageSelectedTaxa.setEnabled(false);
            _rdbtnAllImagesCurrentTaxon.setSelected(true);
        } else {
            _rdbtnAllImagesSelectedTaxa.setSelected(true);
        }

        _chckbxCloseOpenWindows = new JCheckBox(closeAllOpenWindowsCaption);
        _chckbxCloseOpenWindows.setBorder(new CompoundBorder(new EmptyBorder(20, 0, 0, 0), UIManager.getBorder("CheckBoxMenuItem.border")));
        _pnlMainLeft.add(_chckbxCloseOpenWindows);
        _chckbxCloseOpenWindows.setSelected(true);

        _pnlMainRight = new JPanel();
        _pnlMainRight.setBorder(new EmptyBorder(10, 10, 10, 10));
        _pnlMain.add(_pnlMainRight);
        _pnlMainRight.setLayout(new BorderLayout(0, 0));

        _lblSelectBySubject = new JLabel(selectBySubjectCaption);
        _lblSelectBySubject.setBorder(new EmptyBorder(0, 0, 5, 0));
        _pnlMainRight.add(_lblSelectBySubject, BorderLayout.NORTH);

        _scrollPane = new JScrollPane();
        _pnlMainRight.add(_scrollPane, BorderLayout.CENTER);

        _listSubjects = new JList();

        DefaultListModel subjectsListModel = new DefaultListModel();
        for (String imageSubject : _imageSubjects) {
            subjectsListModel.addElement(imageSubject);
        }
        _listSubjects.setModel(subjectsListModel);

        _scrollPane.setViewportView(_listSubjects);
    }

    @Action
    public void MultipleImagesDialog_OK() {
        //Close any windows that are already open
        IntKeyDialogController.closeWindows();
        
        List<Image> imagesToDisplay = new ArrayList<Image>();

        // Use to keep track of what taxon each image belongs to
        Map<Image, Item> imageTaxonMap = new HashMap<Image, Item>();

        if (_rdbtnAllImagesCurrentTaxon.isSelected()) {
            for (Image img : _currentSelectedTaxon.getImages()) {
                imagesToDisplay.add(img);
                imageTaxonMap.put(img, _currentSelectedTaxon);
            }
        } else if (_rdbtnFirstImageSelectedTaxa.isSelected()) {
            for (Item taxon : _selectedTaxa) {
                List<Image> taxonImages = taxon.getImages();
                if (taxonImages.size() > 0) {
                    Image img = taxonImages.get(0);
                    imagesToDisplay.add(img);
                    imageTaxonMap.put(img, taxon);
                }
            }
        } else {
            // All images of selected taxa must be selected
            for (Item taxon : _selectedTaxa) {
                for (Image img : taxon.getImages()) {
                    imagesToDisplay.add(img);
                    imageTaxonMap.put(img, taxon);
                }
            }
        }

        // if subjects are selected in the "select by subject" list, filter out
        // any
        // images who subjects do not contain the text from one of the selected
        // items
        List<String> selectedSubjects = new ArrayList<String>();
        for (Object objSubject : _listSubjects.getSelectedValues()) {
            selectedSubjects.add((String) objSubject);
        }

        if (!selectedSubjects.isEmpty()) {
            List<Image> imagesToRemove = new ArrayList<Image>();
            for (Image img : imagesToDisplay) {
                boolean match = false;
                String imgSubject = img.getSubjectText();
                for (String selectedSubject : selectedSubjects) {
                    if (imgSubject.toLowerCase().contains(selectedSubject.toLowerCase())) {
                        match = true;
                    }
                }
                if (!match) {
                    imagesToRemove.add(img);
                }
            }

            imagesToDisplay.removeAll(imagesToRemove);
        }

        if (_chckbxCloseOpenWindows.isSelected()) {
            IntKeyDialogController.closeWindows();
        }

        if (imagesToDisplay.isEmpty()) {
            _mainUI.displayInformationMessage(noImagesForSpecifiedTaxaMsg);
        } else {
            for (Image img : imagesToDisplay) {
                displayTaxonImage(img, imageTaxonMap.get(img));
            }
            IntKeyDialogController.tileWindows();
        }

        this.setVisible(false);
    }

    private void displayTaxonImage(Image img, Item taxon) {
        List<Item> taxonInList = new ArrayList<Item>();
        taxonInList.add(taxon);

        try {
            TaxonImageDialog dlg = new TaxonImageDialog(UIUtils.getMainFrame(), _imageSettings, taxonInList, false, !_displayContinuous, !_displayScaled, _imageSubjects, _mainUI);
            dlg.displayImagesForTaxon(taxon, taxon.getImages().indexOf(img));
            ((SingleFrameApplication) Application.getInstance()).show(dlg);
        } catch (IllegalArgumentException ex) {
            // Display error message if unable to display
            _mainUI.displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
        }
    }

    @Action
    public void MultipleImagesDialog_Cancel() {
        this.setVisible(false);
    }

}

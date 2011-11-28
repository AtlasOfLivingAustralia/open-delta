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
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.ResourceSettings;
import au.org.ala.delta.model.format.Formatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.util.Pair;

public class TaxonInformationDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -369093284637981457L;

    private List<Item> _taxa;
    private List<Item> _taxaWithImages;

    private int _selectedIndex;
    private ItemFormatter _itemFormatter;
    private Formatter _imageDescriptionFormatter;
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

    private IntkeyContext _context;
    private ResourceSettings _infoSettings;
    private ImageSettings _imageSettings;

    private List<Image> _images;
    private List<Pair<String, String>> _definedDirectiveCommands;

    private List<InformationDialogCommand> _cmds;

    private boolean _imageDisplayEnabled;

    /**
     * Calls Desktop.getDesktop on a background thread as it's slow to
     * initialise
     */
    private SwingWorker<Desktop, Void> _desktopWorker;

    @Resource
    String noImagesCaption;
    private JButton _btnWebSearch;

    public TaxonInformationDialog(Frame owner, List<Item> taxa, IntkeyContext context, boolean imageDisplayEnabled) {
        super(owner, false);

        setPreferredSize(new Dimension(550, 280));
        setMinimumSize(new Dimension(550, 280));

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonInformationDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(TaxonInformationDialog.class, this);

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
        _comboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                displayTaxon(_comboBox.getSelectedIndex());
            }
        });

        _comboPanel.add(_comboBox, BorderLayout.CENTER);

        _btnPanel = new JPanel();
        _btnPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        _mainPanel.add(_btnPanel, BorderLayout.SOUTH);

        _btnDisplay = new JButton();
        _btnDisplay.setAction(actionMap.get("displaySelectedTaxonInformation"));
        _btnPanel.add(_btnDisplay);

        _btnMultipleImages = new JButton();
        _btnMultipleImages.setAction(actionMap.get("displayMultipleImages"));
        _btnMultipleImages.setEnabled(!context.displayContinuous() && imageDisplayEnabled);
        _btnPanel.add(_btnMultipleImages);
        
        _btnWebSearch = new JButton();
        _btnWebSearch.setAction(actionMap.get("webSearch"));
        _btnWebSearch.setEnabled(false);
        _btnPanel.add(_btnWebSearch);

        _btnDeselectAll = new JButton();
        _btnDeselectAll.setAction(actionMap.get("deselectAllTaxonInformation"));
        _btnPanel.add(_btnDeselectAll);

        _btnDone = new JButton("Done");
        _btnDone.setAction(actionMap.get("done"));
        _btnPanel.add(_btnDone);

        _pnlCenter = new JPanel();
        _mainPanel.add(_pnlCenter, BorderLayout.CENTER);
        _pnlCenter.setLayout(new BorderLayout(0, 0));

        _pnlNavigationButtons = new JPanel();
        _pnlCenter.add(_pnlNavigationButtons, BorderLayout.NORTH);

        _btnStart = new JButton();
        _btnStart.setAction(actionMap.get("firstTaxon"));
        _pnlNavigationButtons.add(_btnStart);

        _btnPrevious = new JButton();
        _btnPrevious.setAction(actionMap.get("previousTaxon"));
        _pnlNavigationButtons.add(_btnPrevious);

        _btnForward = new JButton();
        _btnForward.setAction(actionMap.get("nextTaxon"));
        _pnlNavigationButtons.add(_btnForward);

        _btnEnd = new JButton();
        _btnEnd.setAction(actionMap.get("lastTaxon"));
        _pnlNavigationButtons.add(_btnEnd);

        _pnlLists = new JPanel();
        _pnlCenter.add(_pnlLists, BorderLayout.CENTER);
        _pnlLists.setLayout(new GridLayout(0, 2, 0, 0));

        _pnlListOther = new JPanel();
        _pnlListOther.setBorder(new EmptyBorder(5, 5, 5, 5));
        _pnlLists.add(_pnlListOther);
        _pnlListOther.setLayout(new BorderLayout(0, 0));

        _listOther = new JList();
        _listOther.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    int selectedIndex = _listOther.getSelectedIndex();
                    _cmds.get(selectedIndex).execute();
                }
            }

        });

        _sclPnOther = new JScrollPane();
        _sclPnOther.setViewportView(_listOther);
        _pnlListOther.add(_sclPnOther, BorderLayout.CENTER);

        _lblOther = new JLabel("Other");
        _pnlListOther.add(_lblOther, BorderLayout.NORTH);

        _pnlListIllustrations = new JPanel();
        _pnlListIllustrations.setBorder(new EmptyBorder(5, 5, 5, 5));
        _pnlLists.add(_pnlListIllustrations);
        _pnlListIllustrations.setLayout(new BorderLayout(0, 0));

        _lblIllustrations = new JLabel("Illustrations");
        _pnlListIllustrations.add(_lblIllustrations, BorderLayout.NORTH);

        _listIllustrations = new JList();
        _listIllustrations.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    if (_imageDisplayEnabled) {
                        int selectedListIndex = _listIllustrations.getSelectedIndex();
                        displaySelectedTaxonImage(selectedListIndex);
                    } else {
                        _context.getUI().displayErrorMessage(UIUtils.getResourceString("ImageDisplayDisabled.error"));
                    }
                }
            }

        });

        _sclPnIllustrations = new JScrollPane();

        _sclPnIllustrations.setViewportView(_listIllustrations);
        _pnlListIllustrations.add(_sclPnIllustrations);

        _context = context;
        _imageDisplayEnabled = imageDisplayEnabled;
        _definedDirectiveCommands = _context.getTaxonInformationDialogCommands();

        _infoSettings = _context.getInfoSettings();
        _imageSettings = _context.getImageSettings();
        _itemFormatter = new ItemFormatter(_context.displayNumbering(), CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE, true, false, false);
        _imageDescriptionFormatter = new Formatter(CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);

        _taxa = taxa;
        _taxaWithImages = new ArrayList<Item>();
        for (Item taxon : taxa) {
            if (taxon.getImageCount() > 0) {
                _taxaWithImages.add(taxon);
            }
        }

        initialize();

        loadDesktopInBackground();

        this.pack();
    }

    private void initialize() {
        // fill combobox with taxa names

        DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        for (Item taxon : _taxa) {
            String formattedItemName = _itemFormatter.formatItemDescription(taxon);
            formattedItemName = formattedItemName.replace("<", "");
            formattedItemName = formattedItemName.replace(">", "");
            comboModel.addElement(formattedItemName);
        }
        _comboBox.setModel(comboModel);

        displayTaxon(0);
    }

    /**
     * We do this because Desktop.getDesktop() can be very slow
     */
    private void loadDesktopInBackground() {
        _desktopWorker = new SwingWorker<Desktop, Void>() {

            protected Desktop doInBackground() {
                if (Desktop.isDesktopSupported()) {
                    return Desktop.getDesktop();
                } else {
                    return null;
                }
            }
        };
        _desktopWorker.execute();
    }

    private void displayTaxon(int index) {
        _selectedIndex = index;
        Item selectedTaxon = _taxa.get(_selectedIndex);

        _comboBox.setSelectedIndex(_selectedIndex);

        // Update other list
        // _fileNames = new ArrayList<String>();
        _cmds = new ArrayList<InformationDialogCommand>();
        DefaultListModel otherListModel = new DefaultListModel();
        for (Pair<String, String> fileNameTitlePair : selectedTaxon.getLinkFiles()) {
            String fileName = fileNameTitlePair.getFirst();
            String fileTitle = fileNameTitlePair.getSecond();

            if (fileTitle != null) {
                otherListModel.addElement(fileTitle);
            } else {
                otherListModel.addElement(fileName);
            }

            _cmds.add(new OpenLinkFileCommand(fileName, fileTitle));
        }

        for (Pair<String, String> subjectDirectiveCommandPair : _definedDirectiveCommands) {
            String subject = subjectDirectiveCommandPair.getFirst();
            String directiveCommand = subjectDirectiveCommandPair.getSecond();

            otherListModel.addElement(subject);
            _cmds.add(new RunDirectiveCommand(directiveCommand, subject));
        }

        _listOther.setModel(otherListModel);

        // Update illustrations list
        _images = new ArrayList<Image>();
        DefaultListModel illustrationsListModel = new DefaultListModel();
        for (Image img : selectedTaxon.getImages()) {
            _images.add(img);
            illustrationsListModel.addElement(_imageDescriptionFormatter.defaultFormat(img.getSubjectTextOrFileName()));
        }

        if (_images.isEmpty()) {
            illustrationsListModel.addElement(noImagesCaption);
        }

        _listIllustrations.setModel(illustrationsListModel);

        // update button state
        _btnStart.setEnabled(index > 0);
        _btnPrevious.setEnabled(index > 0);
        _btnForward.setEnabled(index < _taxa.size() - 1);
        _btnEnd.setEnabled(index < _taxa.size() - 1);

    }

    @Action
    public void firstTaxon() {
        displayTaxon(0);
    }

    @Action
    public void lastTaxon() {
        displayTaxon(_taxa.size() - 1);
    }

    @Action
    public void nextTaxon() {
        if (_selectedIndex < _taxa.size() - 1) {
            displayTaxon(_selectedIndex + 1);
        }
    }

    @Action
    public void previousTaxon() {
        if (_selectedIndex > 0) {
            displayTaxon(_selectedIndex - 1);
        }
    }

    @Action
    public void displaySelectedTaxonInformation() {
        int[] selectedCommandIndicies = _listOther.getSelectedIndices();
        for (int idx : selectedCommandIndicies) {
            _cmds.get(idx).execute();
        }

        int[] selectedImageIndicies = _listIllustrations.getSelectedIndices();

        if (!_imageDisplayEnabled && selectedImageIndicies.length > 0) {
            _context.getUI().displayErrorMessage(UIUtils.getResourceString("ImageDisplayDisabled.error"));
        } else {
            for (int idx : selectedImageIndicies) {
                displaySelectedTaxonImage(idx);
            }
        }
    }

    @Action
    public void displayMultipleImages() {

    }

    @Action
    public void webSearch() {
        // TODO 
    }

    @Action
    public void deselectAllTaxonInformation() {
        _listOther.clearSelection();
        _listIllustrations.clearSelection();
    }

    @Action
    public void done() {
        this.setVisible(false);
    }

    public Item getSelectedTaxon() {
        return _taxa.get(_selectedIndex);
    }

    private void displaySelectedTaxonImage(int imageIndex) {
        Item selectedTaxon = getSelectedTaxon();
        TaxonImageDialog dlg = new TaxonImageDialog(UIUtils.getMainFrame(), _imageSettings, _taxaWithImages, false, !_context.displayContinuous(), _context.displayScaled());
        dlg.displayImagesForTaxon(selectedTaxon);
        dlg.showImage(imageIndex);
        dlg.setVisible(true);
    }

    /**
     * Display all images for the selected taxon that contain the supplied text.
     * If the supplied text is null or is an empty string, all available images
     * for the selected taxon will be displayed
     * 
     * @param text
     */
    public void displayImagesWithTextInSubject(String text) {
        for (int i = 0; i < _images.size(); i++) {
            Image img = _images.get(i);
            String subjectText = img.getSubjectText();

            if (StringUtils.isEmpty(text) || subjectText.toLowerCase().contains(text.toLowerCase())) {
                displaySelectedTaxonImage(i);
            }

        }
    }

    /**
     * Display any non-image information attached to the current taxon with a
     * subject that contains the supplied text. Link files other than .rtf files
     * are ignored. If the supplied text is null or is an empty string, all
     * available non-image information items (other than .rtf files) for the
     * selected taxon will be displayed
     * 
     * @param text
     */
    public void displayOtherItemsWithTextInDescription(String text) {
        for (InformationDialogCommand cmd : _cmds) {

            // Ignore any link files other than rtf files
            if (cmd instanceof OpenLinkFileCommand) {
                OpenLinkFileCommand openLinkFileCmd = (OpenLinkFileCommand) cmd;
                if (!openLinkFileCmd.getLinkFileName().toLowerCase().endsWith(".rtf")) {
                    continue;
                }
            }

            if (StringUtils.isEmpty(text) || cmd.getDescription().toLowerCase().contains(text.toLowerCase())) {
                cmd.execute();
            }
        }
    }

    private interface InformationDialogCommand {
        public void execute();

        public String getDescription();
    }

    private class OpenLinkFileCommand implements InformationDialogCommand {

        private String _linkFileName;
        private String _description;

        public OpenLinkFileCommand(String linkFileName, String description) {
            _linkFileName = linkFileName;
            _description = description;
        }

        @Override
        public void execute() {
            URL linkFileURL = _infoSettings.findFileOnResourcePath(_linkFileName, false);

            try {
                _context.getUI().displayFile(linkFileURL, _description);
            } catch (Exception ex) {
                _context.getUI().displayErrorMessage("Badly formed URL: " + linkFileURL.toString());
            }
        }

        @Override
        public String getDescription() {
            return _description;
        }

        public String getLinkFileName() {
            return _linkFileName;
        }
    }

    private class RunDirectiveCommand implements InformationDialogCommand {

        private String _directiveCommand;
        private String _description;

        public RunDirectiveCommand(String directiveCommand, String description) {
            _directiveCommand = directiveCommand;
            _description = description;
        }

        @Override
        public void execute() {
            // substitute ?S for selected taxon number
            Item selectedTaxon = getSelectedTaxon();
            int taxonNumber = selectedTaxon.getItemNumber();

            String command = _directiveCommand.replaceAll("\\?S", Integer.toString(taxonNumber));

            // parse and run directive
            _context.parseAndExecuteDirective(command);
        }

        @Override
        public String getDescription() {
            return _description;
        }
    }
}

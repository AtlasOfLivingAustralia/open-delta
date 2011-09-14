package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.io.FileUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.ResourceSettings;
import au.org.ala.delta.model.format.Formatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.ui.image.AudioPlayer;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;
import au.org.ala.delta.util.Pair;

public class TaxonInformationDialog extends JDialog {

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

    /**
     * Calls Desktop.getDesktop on a background thread as it's slow to
     * initialise
     */
    private SwingWorker<Desktop, Void> _desktopWorker;

    @Resource
    String noImagesCaption;

    public TaxonInformationDialog(Frame owner, List<Item> taxa, IntkeyContext context) {
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
        _btnPanel.add(_btnMultipleImages);

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
                    int selectedListIndex = _listIllustrations.getSelectedIndex();
                    displaySelectedTaxonImage(selectedListIndex);
                }
            }

        });

        _sclPnIllustrations = new JScrollPane();

        _sclPnIllustrations.setViewportView(_listIllustrations);
        _pnlListIllustrations.add(_sclPnIllustrations);

        _context = context;
        _definedDirectiveCommands = _context.getTaxonInformationDialogCommands();

        _infoSettings = _context.getInfoSettings();
        _imageSettings = _context.getImageSettings();
        _itemFormatter = new ItemFormatter(false, false, true, false, true, false);
        _imageDescriptionFormatter = new Formatter(false, false, false, true);

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

            _cmds.add(new OpenLinkFileCommand(fileName));
        }

        for (Pair<String, String> subjectDirectiveCommandPair : _definedDirectiveCommands) {
            String subject = subjectDirectiveCommandPair.getFirst();
            String directiveCommand = subjectDirectiveCommandPair.getSecond();

            otherListModel.addElement(subject);
            _cmds.add(new RunDirectiveCommand(directiveCommand));
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
        for (int idx: selectedCommandIndicies) {
            _cmds.get(idx).execute();
        }
        
        int[] selectedImageIndicies = _listIllustrations.getSelectedIndices();
        for (int idx: selectedImageIndicies) {
            displaySelectedTaxonImage(idx);
        }
    }

    @Action
    public void displayMultipleImages() {

    }

    @Action
    public void webSearch() {
        // TODO should we even bother with this?
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

    private interface InformationDialogCommand {
        public void execute();
    }

    private void displaySelectedTaxonImage(int imageIndex) {
        Item selectedTaxon = getSelectedTaxon();
        TaxonImageDialog dlg = new TaxonImageDialog(this, _imageSettings, _taxaWithImages, false);
        dlg.displayImagesForTaxon(selectedTaxon);
        dlg.showImage(imageIndex);
        dlg.setVisible(true);
    }

    private class OpenLinkFileCommand implements InformationDialogCommand {

        private String _linkFileName;

        public OpenLinkFileCommand(String linkFileName) {
            _linkFileName = linkFileName;
        }

        @Override
        public void execute() {
            URL linkFileURL = _infoSettings.findFileOnResourcePath(_linkFileName);

            try {
                Desktop desktop = _desktopWorker.get();

                if (_linkFileName.toLowerCase().endsWith(".rtf")) {
                    File rtfFile = new File(linkFileURL.toURI());
                    String rtfSource = FileUtils.readFileToString(rtfFile);
                    RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(TaxonInformationDialog.this, new SimpleRtfEditorKit(null), rtfSource, "blah");
                    ((SingleFrameApplication) Application.getInstance()).show(dlg);
                } else if (linkFileURL.getProtocol().equals("http")) {
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(linkFileURL.toURI());
                    }
                } else if (_linkFileName.toLowerCase().endsWith(".ink")) {
                    File inkFile = new File(linkFileURL.toURI());
                    System.out.println(inkFile.getAbsolutePath());
                } else if (_linkFileName.toLowerCase().endsWith(".wav")) {
                    AudioPlayer.playClip(linkFileURL);
                } else {
                    if (desktop.isSupported(Desktop.Action.OPEN)) {
                        desktop.open(new File(linkFileURL.toURI()));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class RunDirectiveCommand implements InformationDialogCommand {

        private String _directiveCommand;

        public RunDirectiveCommand(String directiveCommand) {
            _directiveCommand = directiveCommand;
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
    }
}

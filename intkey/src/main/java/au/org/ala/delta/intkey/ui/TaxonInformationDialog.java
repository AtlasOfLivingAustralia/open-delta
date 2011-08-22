package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileReader;
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
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.io.FileUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.ResourceSettings;
import au.org.ala.delta.model.format.Formatter;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;
import au.org.ala.delta.util.Pair;

public class TaxonInformationDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -369093284637981457L;

    private List<Item> _taxa;
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

    private ResourceSettings _infoSettings;
    private ImageSettings _imageSettings;
    
    private List<Image> _images;
    private List<InformationDialogCommand> _cmds;

    @Resource
    String noImagesCaption;

    public TaxonInformationDialog(Frame owner, List<Item> taxa, ResourceSettings infoSettings, ImageSettings imageSettings) {
        super(owner, true);

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

        _pnlListOther = new JPanel();
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
                    Item selectedTaxon = _taxa.get(_selectedIndex);
                    ImageDialog dlg = new ImageDialog(TaxonInformationDialog.this, _imageSettings);
                    dlg.setImages(selectedTaxon.getImages());
                    dlg.setVisible(true);
                }
            }
            
        });

        _sclPnIllustrations = new JScrollPane();

        _sclPnIllustrations.setViewportView(_listIllustrations);
        _pnlListIllustrations.add(_sclPnIllustrations, BorderLayout.CENTER);

        _infoSettings = infoSettings;
        _imageSettings = imageSettings;
        _itemFormatter = new ItemFormatter(false, false, true, false, true, false);
        _imageDescriptionFormatter = new Formatter(false, false, false, true);

        _taxa = taxa;
        initialize();

        this.pack();
    }

    private void initialize() {
        // fill combobox with taxa names

        DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
        for (Item taxon : _taxa) {
            comboModel.addElement(_itemFormatter.formatItemDescription(taxon));
        }
        _comboBox.setModel(comboModel);

        displayTaxon(0);
    }

    private void displayTaxon(int index) {
        _selectedIndex = index;
        Item selectedTaxon = _taxa.get(_selectedIndex);

        _comboBox.setSelectedIndex(_selectedIndex);

        // Update other list
        //_fileNames = new ArrayList<String>();
        _cmds = new ArrayList<InformationDialogCommand>();
        DefaultListModel otherListModel = new DefaultListModel();
        for (Pair<String, String> fileNameTitlePair: selectedTaxon.getLinkFiles()) {
            String fileName = fileNameTitlePair.getFirst();
            String fileTitle = fileNameTitlePair.getSecond();
            
            if (fileTitle != null) {
                otherListModel.addElement(fileTitle);
            } else {
                otherListModel.addElement(fileName);
            }
            
            _cmds.add(new OpenLinkFileCommand(fileName));
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
        displayTaxon(_selectedIndex + 1);
    }

    @Action
    public void previousTaxon() {
        displayTaxon(_selectedIndex - 1);
    }

    @Action
    public void displaySelectedTaxonInformation() {

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

    private interface InformationDialogCommand {
        public void execute();
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
                if (_linkFileName.toLowerCase().endsWith(".rtf")) {
                    File rtfFile = new File(linkFileURL.toURI());
                    String rtfSource = FileUtils.readFileToString(rtfFile);
                    RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(TaxonInformationDialog.this, new SimpleRtfEditorKit(), rtfSource, "blah");
                    ((SingleFrameApplication)Application.getInstance()).show(dlg);
                } else if (_linkFileName.toLowerCase().startsWith("http")) {
                    Desktop.getDesktop().browse(linkFileURL.toURI());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

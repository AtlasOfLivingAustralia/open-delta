package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ActionMap;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.util.LocalConfigFiles;

public class WebSearchDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static Pattern SEARCH_ENGINE_REGEX = Pattern.compile("^(.*)\\s+[\"](.*)[\"]$");

	private final JPanel contentPanel = new JPanel();
	private JLabel lblSearchFor;
	private JTextField txtSearch;
	private JComboBox cmbSearchEngine;
	
    @Resource
    String websearchTitle;
    @Resource
    String searchForLabel;
    @Resource
    String usingLabel;


	/**
	 * Create the dialog.
	 */
	public WebSearchDialog(Dialog owner) {
		
		super(owner);
		setName("WebSearchDialog");
		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(WebSearchDialog.class);
		resourceMap.injectFields(this);
		ActionMap actionMap = Application.getInstance().getContext().getActionMap(WebSearchDialog.class, this);

		setTitle(websearchTitle);
		setBounds(100, 100, 450, 250);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			lblSearchFor = new JLabel(searchForLabel);
		}

		txtSearch = new JTextField();
		txtSearch.setColumns(10);

		final JLabel lblUsing = new JLabel(usingLabel);

		cmbSearchEngine = new JComboBox();
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPanel
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								gl_contentPanel.createParallelGroup(Alignment.LEADING).addComponent(lblSearchFor, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
										.addComponent(cmbSearchEngine, 0, 404, Short.MAX_VALUE).addComponent(txtSearch, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
										.addComponent(lblUsing, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)).addContainerGap()));
		gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPanel.createSequentialGroup().addContainerGap().addComponent(lblSearchFor).addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(txtSearch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(27).addComponent(lblUsing)
						.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(cmbSearchEngine, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(41, Short.MAX_VALUE)));
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("[OK]");
				okButton.setAction(actionMap.get("WebSearch_search"));
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("[Cancel]");
				cancelButton.setAction(actionMap.get("WebSearch_cancel"));
				buttonPane.add(cancelButton);
			}
		}

		LocalConfigFiles files = new LocalConfigFiles("intkey");
		File f = files.getWebsearchIndexFile();
		if (f.exists()) {
			loadSearchEngines(f, cmbSearchEngine);
		}

		cmbSearchEngine.requestFocus();
	}
	
	@Action
	public void WebSearch_cancel() {
		this.dispose();
	}

	@Action
	public void WebSearch_search() {
		String term = txtSearch.getText().trim();
		if (!org.apache.commons.lang.StringUtils.isEmpty(term)) {
			SearchEngineDescriptor selected = (SearchEngineDescriptor) cmbSearchEngine.getSelectedItem();
			if (selected != null) {
				if (Desktop.isDesktopSupported()) {
					URI uri = URI.create(selected.getUrl(term));
					try {
						Desktop.getDesktop().browse(uri);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void setSearchTerm(String term) {
		txtSearch.setText(term);
	}

	private void loadSearchEngines(File file, JComboBox cmb) {

		List<SearchEngineDescriptor> engines = new ArrayList<WebSearchDialog.SearchEngineDescriptor>();
		try {
			List<String> lines = FileUtils.readLines(file);
			for (String line : lines) {
				SearchEngineDescriptor desc = parseSearchEngineDescriptor(line);
				if (desc != null) {
					engines.add(desc);
				} else {
					System.err.println("Could not parse search engine descriptor: " + line);
				}
			}

			ComboBoxModel model = new DefaultComboBoxModel(engines.toArray());
			cmb.setModel(model);
			if (model.getSize() > 0) {
				model.setSelectedItem(model.getElementAt(0));
			}

		} catch (IOException ioex) {
			throw new RuntimeException(ioex);
		}
	}

	private SearchEngineDescriptor parseSearchEngineDescriptor(String line) {
		Matcher m = SEARCH_ENGINE_REGEX.matcher(line);
		if (m.find()) {
			return new SearchEngineDescriptor(m.group(1), m.group(2));
		}
		return null;
	}

	public static class SearchEngineDescriptor {

		private String _name;
		private String _urlPattern;

		public SearchEngineDescriptor(String name, String urlPattern) {
			_name = name;
			_urlPattern = urlPattern;
		}

		public String getName() {
			return _name;
		}

		public String getUrlPattern() {
			return _urlPattern;
		}

		public String getUrl(String searchTerm) {
			try {
				return _urlPattern.replaceAll("@name", URLEncoder.encode(searchTerm, "utf-8"));
			} catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public String toString() {
			return _name;
		}

	}
}

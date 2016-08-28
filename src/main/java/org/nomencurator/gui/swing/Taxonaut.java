/*
 * Taxonaut.java: a java based GUI for Nomencurator
 *
 * Copyright (c) 2002, 2003, 2004, 2014, 2015, 2016 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.gui.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import javax.swing.border.TitledBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.tree.TreePath;

import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import org.nomencurator.io.DataExchanger;
import org.nomencurator.io.ComparisonQueryParameter;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.NameUsageQueryManager;
import org.nomencurator.io.NameUsageQueryParameter;
import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.QueryManager;
import org.nomencurator.io.QueryMode;
import org.nomencurator.io.QueryEvent;
import org.nomencurator.io.QueryListener;
import org.nomencurator.io.QueryParameter;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;
import org.nomencurator.io.MultiplexNameUsageQuery;

import org.nomencurator.io.gbif.NubExchanger;

import org.nomencurator.io.poi.ss.POIAdaptor;

import org.nomencurator.model.Agent;
import org.nomencurator.model.Annotation;

import org.nomencurator.model.Appearance;
import org.nomencurator.model.Author;
import org.nomencurator.model.DefaultNameUsageNode;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
/*
import org.nomencurator.model.NameUsageNode;
*/
import org.nomencurator.model.Publication;

import org.nomencurator.model.Person;

import org.nomencurator.model.gbif.NubNameUsage;

/*
import org.nomencurator.model.UBioNameUsageNode;
*/
import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.table.NameTableModel;
import org.nomencurator.gui.swing.table.NameTableStringModel;

import org.nomencurator.gui.swing.table.NameTreeTableMode;
import org.nomencurator.gui.swing.table.NameTreeTableModel;
import org.nomencurator.gui.swing.table.NameTreeTableStringModel;

import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

import org.nomencurator.model.Rank;

import org.nomencurator.util.CollectionUtility;
import org.nomencurator.util.XMLResourceBundleControl;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code Taxonaut} provides a graphical user interface 
 * to explore Nomencurator data model
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	27 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class Taxonaut<T extends NameUsage<?>>
    extends JApplet
    implements ActionListener,
	       ChangeListener,
	       ListSelectionListener,
	       PropertyChangeListener,
	       QueryListener<T>,
	       QueryResultListener<T>,
	       TreeSelectionListener
{
    private static final long serialVersionUID = -8305009500981105134L;

    public static final boolean WAIT = true;
    public static final boolean DEFAULT = false;

    /** <tt>MenuBar</tt> to show the menu */
    protected MenuBar menu;

    /** <tt>CardLaytout</tt> to switch <tt>Component</tt>s showing status */
    protected CardLayout statusLayout;

    /** A place holder <tt>JPanel</tt> to show status */
    protected JPanel statusPanel;

    /** A progress bar to show status */
    protected PlaceableProgressBar progress;

    /** Designater of the <tt>progress</tt> to switch to */
    protected static final String PROGRESS_BAR = "progressBar";

    /** A <tt>JLabel</tt> to show status */
    protected JLabel statusLabel;

    /** Designater of the <tt>statusLabel</tt> to switch to */
    protected static final String STATUS_LABEL = "statusLabel";

    /** A split pane to divide the window into query part of upper and result part of lower */
    protected JSplitPane querySplitPane;

    /** A split pane to divide the reslut part into reslut list of lfet and detail part of right */
    protected JSplitPane resultSplitPane;

    /** <tt>JTabbledPane</tt> to switch between query GUI depending on type of query */
    protected JTabbedPane queryTabs;

    /** <tt>JTabbledPane</tt> to contain lists of query results */
    protected JTabbedPane resultTabs;

    /** <tt>JTabbledPane</tt> to swith between <tt>Compoennt<tt>s showing detail of query results */
    protected JTabbedPane detailTabs;

    protected NameUsageQueryPanel<T> nameUsageQueryPanel;

    protected RankQueryPanel<T> localQueryPanel;

    protected NameListPane<T> nameListPane;
 
    protected NameTable/*<T>*/ nameList;
    protected NameTree tree;
    protected JScrollPane treePane;

    protected HierarchiesPane<T> hierarchiesPane;

    protected UnitedNameTreeModel unitedTreeModel;

    protected AlignerTree alignerTree;

    protected JTextArea detailText;

    protected NameUsagePanel nameUsagePanel;
    protected AppearancePanel appearancePanel;
    protected PublicationPanel publicationPanel;
    protected AuthorPanel authorPanel;

    protected AnnotationPanel annotationPanel;

    protected JPanel linkTypePanel;

    protected LanguageMenu languageMenu;

    protected ExcelFileChooser excelFileChooser;

    @Getter @Setter
    protected NameUsageExchanger<T> nameUsageExchanger;

    protected QueryManager<T, NameUsageExchanger<T>> queryManager;
    //protected ObjectExchanger queryManager;

    protected QueryMessages queryMessages;

    protected ExecutorService executor;

    @Setter
    private static String version = "3.1.2";

    @Getter
    private static String softwareName = "Taxonaut";

    protected String uBioKey;

    protected MultiplexNameUsageQuery<T> queryThread;

    protected Collection<JComponent> components;

    protected enum TextPropertyKey {
	EMPTY,
	STETUS_TEXT,
	NAME_QUERY,
	LOCAL_QUERY,
	NAME_USAGES,
	NAME_TREE,
	HIERARCHIES,
	DETAIL,
	PROCESSING
    }

    protected static TextPropertyKey[] textPropertyKeys = TextPropertyKey.values();

    protected String[] textProperties = new String[textPropertyKeys.length];

    /**
     * Returns version number in {@code String}
     *
     * @return version number
     */
    public static String getVersion()
    {
	return version;
    }

    /**
     * Constructs a {@code Taxonaut} using default {@code Locale}
     */
    public Taxonaut()
    {
	this(Locale.getDefault());
    }

    /**
     * Constructs a {@code Taxonaut} using {@code locale}
     *
     * @param locale {@code Locale} to display the GUI
     */
    public Taxonaut(Locale locale)
    {
	super();
	getContentPane().setLayout(new BorderLayout());
	createComponents(locale);
	layoutComponents();
	setComponentsSize();
	setQueryManager(new NameUsageQueryManager<T>());
    }

    /**
     * Constructs a {@code Taxonaut} using {@code locale}
     *
     * @param locale {@code Locale} to display the GUI
     */
    public Taxonaut(NameUsageExchanger<T> exchanger)
    {
	this(Locale.getDefault(), exchanger);
    }


    /**
     * Constructs a {@code Taxonaut} using {@code locale}
     *
     * @param locale {@code Locale} to display the GUI
     */
    public Taxonaut(Locale locale, NameUsageExchanger<T> exchanger)
    {
	this(locale);
	setNameUsageExchanger(exchanger);
    }

    /**
     * Creates GUI components of given locale to display.
     *
     * @param locale of components
     */
    protected void createComponents(Locale locale)
    {
	components = new ArrayList<JComponent>();

	menu = new MenuBar(locale);
	setLanguageMenu(menu.getLanguageMenu());
	menu.getExportItem().addActionListener(this);
	excelFileChooser = new ExcelFileChooser();
	menu.getOpenItem().setEnabled(false);
	menu.getCloseItem().setEnabled(false);
	menu.getHelpMenu().setEnabled(false);
	components.add(menu);

	statusLayout = new CardLayout(6, 0);
	statusPanel = new JPanel(statusLayout);
	statusLabel = new JLabel();
	statusPanel.add(statusLabel, STATUS_LABEL);
	UIManager.put("ProgressBar.repaintInterval", UIManager.getInt("ProgressBar.repaintInterval")/2);
	UIManager.put("ProgressBar.cycleTime", UIManager.getInt("ProgressBar.cycleTime")/2);

	progress = new PlaceableProgressBar();
	progress.setTextPlacement(SwingConstants.LEFT);
	statusPanel.add(progress, PROGRESS_BAR);
	statusLayout.show(statusPanel, STATUS_LABEL);

	components.add(statusPanel);
	components.add(statusLabel);
	components.add(progress);

	nameUsageQueryPanel = new NameUsageQueryPanel<T>(this);
	components.add(nameUsageQueryPanel);

	localQueryPanel = new RankQueryPanel<T>(this);
	components.add(localQueryPanel);

	/*
	nameUsageQueryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	nameUsageQueryPanel.setNameQuery(nameListPane);
	nameUsageQueryPanel.setNameList(nameListPane.getNameList());
	*/

	queryTabs = createTabbedPane();
	queryTabs.add(nameUsageQueryPanel);
	queryTabs.add(localQueryPanel);
	components.add(queryTabs);

	nameListPane = new NameListPane<T>();
	nameListPane.addQueryListener(this);
	nameList = nameListPane.getNameList();
	nameListPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	nameListPane.getNameList().getSelectionModel().addListSelectionListener(this);
	nameListPane.setAppendable(false);

	/*
	nameListPane.setDataExchanger(new UBio(uBioKey));
	*/

	resultTabs = createTabbedPane();
	resultTabs.add(nameListPane);
	components.add(resultTabs);

	detailTabs = createTabbedPane();
	components.add(detailTabs);

	tree = new NameTree();
	tree.addTreeSelectionListener(this);
	treePane = 
	    new JScrollPane(tree, 
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	treePane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	detailTabs.add(treePane);
	components.add(treePane);

	hierarchiesPane = new HierarchiesPane<T>();
	detailTabs.add(hierarchiesPane);
	components.add(hierarchiesPane);

	detailText = new JTextArea();
	detailText.setEditable(false);
	detailTabs.add(detailText);
	components.add(detailText);

	Publication publication = new Publication();
	Author author = new Author();
	author.addPublication(publication);
	//Appearance appearance = new Appearance(publication);
	Appearance appearance = new Appearance();
	NameUsage<?> nameUsage = new DefaultNameUsageNode(appearance);

	publicationPanel = new PublicationPanel(publication);
	appearancePanel = new AppearancePanel(appearance);
	appearancePanel.setPublicationPanel(publicationPanel);
	nameUsagePanel = new NameUsagePanel(nameUsage);
	nameUsagePanel.setAppearancePanel(appearancePanel);
	nameUsagePanel.addChangeListener(this);
	// authorPanel = new AuthorPanel(author);
	components.add(publicationPanel);
	components.add(appearancePanel);
	components.add(nameUsagePanel);

	// detailTabs.add("Name", nameUsagePanel);
	// detailTabs.add("Appeared as...", appearancePanel);
	// detailTabs.add("in Publication", publicationPanel);
	// detailTabs.add("Author", authorPanel);

	resultSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	resultSplitPane.setOneTouchExpandable(true);
	resultSplitPane.setLeftComponent(resultTabs);
	resultSplitPane.setRightComponent(detailTabs);
	resultSplitPane.setDividerLocation(0.4);
	components.add(resultSplitPane);

	querySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	querySplitPane.setOneTouchExpandable(true);
	querySplitPane.setLeftComponent(queryTabs);
	querySplitPane.setRightComponent(resultSplitPane);
	querySplitPane.setDividerLocation(0.5);
	// querySplitPane.setDividerSize(3);

	components.add(querySplitPane);

	queryMessages = new QueryMessages(locale);

	setLocale(locale);
	// setQueryManager(new UBio(uBioKey));
    }

    protected JTabbedPane createTabbedPane()
    {
	JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
	tabs.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	return tabs;
    }

    /**
     * Layouts GUI components.
     */
    protected void layoutComponents()
    {
	//getContentPane().add(querySplitPane, BorderLayout.NORTH);
	//getContentPane().add(resultSplitPane, BorderLayout.CENTER);
	getContentPane().add(querySplitPane, BorderLayout.CENTER);
	getContentPane().add(statusPanel, BorderLayout.SOUTH);
	//getContentPane().add(progress, BorderLayout.SOUTH);
	getRootPane().setJMenuBar(menu);

    }

    /**
     * Sets size of GUI components.
     */
    protected void setComponentsSize()
    {
	/*
	Dimension d = treePane.getPreferredSize();
	nameListPane.setSize(d.width, d.height);
	*/
    }

    protected QueryManager<T, NameUsageExchanger<T>> prepareQueryMnager()
    {
	NameUsageQueryManager <T> manager = new NameUsageQueryManager<T>();
	// FIXME
	// make it to be compiled
	// org.nomencurator.io.gbif.NubExchanger is incompatible with 
	// org.nomencurator.io.NameUsageExchanger<org.nomencurator.model.NameUsage<?>,org.nomencurator.model.NameUsage<?>>
	// even though org.nomencurator.io.gbif.NubExchanger 
	// extends AbstractNameUsageExchanger<NubNameUsage, NubNameUsage> 
	//manager.addSource(new NubExchanger());

	return manager;
    }

    public void setQueryManager(QueryManager<T, NameUsageExchanger<T>> queryManager)
    {
	this.queryManager = queryManager;
	/*
	if(nameListPane != null)
	    nameListPane.setDataExchanger(queryManager);
	*/
	/*
	if(nameUsageQueryPanel != null)
	    nameUsageQueryPanel.setQueryManager(queryManager);
	*/

    }

    public QueryManager<T, NameUsageExchanger<T>> getQueryManager()
    {
	return queryManager;
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	if(locale == null)
	    locale = Locale.getDefault();
	/*
	if(locale.equals(getLocale()))
	    return;
	*/
	super.setLocale(locale);

	if (queryMessages != null)
	    queryMessages.setLocale(locale);

	if(nameUsageQueryPanel == null)
	    return;

	nameUsageQueryPanel.setLocale(locale);
	// nameList.setLocale(locale);

	String queryBorderText =
	    ResourceKey.AS_IS;

	String nameListBorderText =
	    ResourceKey.RESULTS;
	String treePaneBorderText =
	    ResourceKey.HIERARCHY;

	String tabBorderText =
	    ResourceKey.DETAILS;

	String authorTabText =
	    ResourceKey.AUTHOR_TAB;
	String publicationTabText =
	    ResourceKey.IN_PUBLICATION_TAB;
	String appearanceTabText =
	    ResourceKey.APPEARANCE_TAB;
	String nameUsageTabText =
	    ResourceKey.NAMEUSAGE_TAB;

	    
	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    queryBorderText = resource.getString(queryBorderText);
	    /*
	    nameListBorderText = resource.getString(nameListBorderText);
	    treePaneBorderText = resource.getString(treePaneBorderText);
	    */
	    tabBorderText = resource.getString(tabBorderText);
	    /*
	    authorTabText = resource.getString(authorTabText);
	    publicationTabText = resource.getString(publicationTabText);
	    appearanceTabText = resource.getString(appearanceTabText);
	    nameUsageTabText = resource.getString(nameUsageTabText);
	    */

	}
	catch(MissingResourceException e) {
	}

	// ((TitledBorder)nameUsageQueryPanel.getBorder()).setTitle(queryBorderText);
	//	((TitledBorder)nameListPane.getBorder()).setTitle(nameListBorderText);
	//	((TitledBorder)treePane.getBorder()).setTitle(treePaneBorderText);
	((TitledBorder)detailTabs.getBorder()).setTitle(tabBorderText);

	((JComponent)getContentPane()).getLayout().layoutContainer(this);
	((JComponent)getContentPane()).revalidate();

	int tabCount = detailTabs.getTabCount();
	for(int i = 0; i < tabCount; i++) {
	    detailTabs.getComponentAt(i).setLocale(locale);
	}

	int tabIndex = -1;
	if(authorPanel != null) {
	    tabIndex = detailTabs.indexOfComponent(authorPanel);
	    if (tabIndex > -1) {
		detailTabs.setTitleAt(tabIndex, authorTabText);
	    }
	}

	if(publicationPanel != null) {
	    tabIndex = detailTabs.indexOfComponent(publicationPanel);
	    if (tabIndex > -1) {
		detailTabs.setTitleAt(tabIndex, publicationTabText);
	    }
	}

	if(appearancePanel != null) {
	    tabIndex = detailTabs.indexOfComponent(appearancePanel);
	    if (tabIndex > -1) {
		detailTabs.setTitleAt(tabIndex, appearanceTabText);
	    }
	}

	if(nameUsagePanel != null) {
	    tabIndex = detailTabs.indexOfComponent(nameUsagePanel);
	    if (tabIndex > -1) {
		detailTabs.setTitleAt(tabIndex, nameUsageTabText);
	    }
	}

	setTextProperties(getTextProperties(locale));
    }

    protected String[] getTextProperties(Locale locale)
    {
	String[] textProperties = null;
	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(getClass().getName(), locale, new XMLResourceBundleControl());
	    textProperties = new String[textPropertyKeys.length];
	    for (int i = 0; i < textProperties.length; i++) {
		textProperties[i] = resource.getString(textPropertyKeys[i].name());
	    }
	}
	catch(MissingResourceException e) {
	}

	return textProperties;
    }

    protected void setTextProperties(String[] textProperties)
    {
	if (statusLabel != null) {
	    statusLabel.setText(textProperties[TextPropertyKey.STETUS_TEXT.ordinal()]);
	}

	int tabIndex = -1;

	if (queryTabs != null) {
	    if (nameUsageQueryPanel != null) {
		tabIndex = queryTabs.indexOfComponent(nameUsageQueryPanel);
		if (tabIndex != -1) {
		    queryTabs.setTitleAt(tabIndex, textProperties[TextPropertyKey.NAME_QUERY.ordinal()]);
		}
	    }
	    if (localQueryPanel != null) {
		tabIndex = queryTabs.indexOfComponent(localQueryPanel);
		if (tabIndex != -1) {
		    queryTabs.setTitleAt(tabIndex, textProperties[TextPropertyKey.LOCAL_QUERY.ordinal()]);
		}
	    }
	}

	if (resultTabs != null) {
	    if (nameListPane != null) {
		tabIndex = resultTabs.indexOfComponent(nameListPane);
		if (tabIndex != -1) {
		    resultTabs.setTitleAt(tabIndex, textProperties[TextPropertyKey.NAME_USAGES.ordinal()]);
		}
	    }
	}

	if (detailTabs != null) {
	    if (treePane != null) {
		tabIndex = detailTabs.indexOfComponent(treePane);
		if (tabIndex != -1) {
		    detailTabs.setTitleAt(tabIndex, textProperties[TextPropertyKey.NAME_TREE.ordinal()]);
		}
	    }
	    if (hierarchiesPane != null) {
		tabIndex = detailTabs.indexOfComponent(hierarchiesPane);
		if (tabIndex != -1) {
		    detailTabs.setTitleAt(tabIndex, textProperties[TextPropertyKey.HIERARCHIES.ordinal()]);
		}
	    }
	    if (detailText != null) {
		tabIndex = detailTabs.indexOfComponent(detailText);
		if (tabIndex != -1) {
		    detailTabs.setTitleAt(tabIndex, textProperties[TextPropertyKey.DETAIL.ordinal()]);
		}
	    }
	}
    }

    /**
     * Set {@code menu} as {@code LanguageMenu}
     * of this {@code Taxonaut}
     *
     * @param menu {@code LanguageMenu} for this {@code Taxonaut}
     */
    public void setLanguageMenu(LanguageMenu menu)
    {
	if(languageMenu == menu)
	    return;
	if(languageMenu != null)
	    languageMenu.removeChangeListener(this);
	languageMenu = menu;
	if(languageMenu != null) {
	    setLocale(languageMenu.getLocale());
	    languageMenu.addChangeListener(this);
	}
    }

    protected void showStatus(String message, int value)
    {
    }

    protected void hideStatus()
    {
    }

    protected static <N extends NameUsage<?>> Collection<NameUsage<N>> convertNameUsages(Collection<? extends N> usages)
    {
	Collection<NameUsage<N>> nameUsages = null;
	if (usages != null) {
	    nameUsages = new ArrayList<NameUsage<N>>(usages.size());
	    for (N usage : usages) {
		@SuppressWarnings("unchecked")
		    NameUsage<N> nameUsage = (NameUsage<N>) usage;
		nameUsages.add(nameUsage);
	    }
	}
	return nameUsages;
    }

    class NameUsageQuery extends SwingWorker<Collection<T>,  T> {
	@Getter QueryParameter<T> queryParameter = null;
	NameUsageExchanger<T> nameUsageExchanger = null;
	public NameUsageQuery(NameUsageExchanger<T> nameUsageExchanger, 
			      QueryParameter<T> parameter) {
	    super();
	    this.nameUsageExchanger = nameUsageExchanger;
	    queryParameter =parameter;
	}

	@Override public 	Collection<T> doInBackground() {
	    return nameUsageExchanger.getObjects(queryParameter);
	}

        @Override protected void done() 
	{
	    String queryLiteral = null;
	    QueryParameter<T> parameter = nameUsageQuery.getQueryParameter();
	    if (parameter instanceof NameUsageQueryParameter) {
		NameUsageQueryParameter<T> queryParameter = (NameUsageQueryParameter<T>)parameter;
		queryLiteral = queryParameter.getLiteral();
	    }
	    
	    try {
		Collection<T> results = nameUsageQuery.get();
		Collection<NameUsage<?>> nameUsages = new ArrayList<>(results.size());
		for(T result : results) {
		    nameUsages.add((NameUsage<?>)result);
		}
		statusLabel.setText(queryMessages.getMessage(nameUsages.size(), new Object[]{queryLiteral}));
		((NameTableModel)nameListPane.getNameList().getModel()).set(nameUsages);
	    }
	    catch (InterruptedException e) {
		statusLabel.setText("Interrupted: " + queryLiteral);
	    }
	    catch (ExecutionException e) {
		statusLabel.setText(e.getMessage());
	    }
	    finally {
		progress.setMinimum(progress.getMinimum());
		progress.setIndeterminate(false);
		statusLayout.show(statusPanel, STATUS_LABEL);
		nameUsageQueryPanel.enableButtons(true);
		setCursor(false);
	    }

	}
    }

    NameUsageQuery nameUsageQuery = null;

    class HierarchiesComparison extends SwingWorker<UnitedNameTreeModel,  Object> {
	@Getter ComparisonQueryParameter<NameUsage<?>> comparisonQueryParameter = null;
	NameUsageQueryParameter<T> nameUsageQueryParameter = null;
	NameUsageExchanger<T> nameUsageExchanger = null;
	Set<String> nameLiterals = null;
	String message = null;
	String retrievingMessage = "Retrieving hierarchy from ";
	String analyzingMessage = "Analyzing  hierarchy in ";
	String roughLiteralMessage = "Retrieving usages of ";
	String usageMessage = "Retrieving hierarchy of ";
	int maximum = -1;
	int current = 0;
	public HierarchiesComparison(NameUsageExchanger<T> nameUsageExchanger,
				     ComparisonQueryParameter<NameUsage<?>> comparisonQueryParameter) {
	    super();
	    this.nameUsageExchanger = nameUsageExchanger;
	    this.comparisonQueryParameter = comparisonQueryParameter;
	    nameLiterals = new HashSet<String>();
	}

	@SuppressWarnings("unchecked")
	@Override public UnitedNameTreeModel doInBackground() {
		Rank higher = comparisonQueryParameter.getHigher();
		int height = comparisonQueryParameter.getHeight();
		Rank lower = comparisonQueryParameter.getLower();
		int depth = comparisonQueryParameter.getDepth();
		boolean roughSet = comparisonQueryParameter.isRoughSet();

		nameUsageQueryParameter = new NameUsageQueryParameter<>();
		nameUsageQueryParameter.setQueryMode(QueryMode.PARTIAL_HIERARCHIES);
		nameUsageQueryParameter.setHigher(comparisonQueryParameter.getHigher());
		nameUsageQueryParameter.setHeight(comparisonQueryParameter.getHeight());
		nameUsageQueryParameter.setLower(comparisonQueryParameter.getLower());
		nameUsageQueryParameter.setDepth(comparisonQueryParameter.getDepth());
		nameUsageQueryParameter.setRoughSet(comparisonQueryParameter.isRoughSet());

		if (!comparisonQueryParameter.isAppend()) {
		    nameLiterals.clear();
		}

		Collection<NameUsage<?>> nameUsages = 
		    nameUsageExchanger.integrateHierarchies(comparisonQueryParameter.getNameUsages());
		List<NameUsage<?>> rootNodes = new ArrayList<>();
		Set<String> positiveLiterals = roughSet ? new HashSet<String>() : null;
		message = retrievingMessage;
		maximum = nameUsages.size();
		for (NameUsage<?> nameUsage : nameUsages) {
		    current++;
		    publish(nameUsage);
		    nameUsageQueryParameter.setFilter((NameUsage<T>)nameUsage);
		    rootNodes.addAll(getRoots(nameUsageQueryParameter));
		    nameLiterals.add(nameUsage.getLiteral());
		    if (roughSet) {
			positiveLiterals.addAll(nameUsage.getIncludants().keySet());
		    }
		}

		current = 0;
		maximum = -1;
		if (roughSet) {
		    Collection<NameUsage<T/*?*/>> crossUsages = new HashSet<>();
		    message = roughLiteralMessage;
		    maximum = positiveLiterals.size();
		    for (String literal : positiveLiterals) {
			current++;
			publish(literal);
			crossUsages.addAll(nameUsageExchanger.getNameUsages(literal, null));
		    }
		    positiveLiterals.clear();
		    current = 0;
		    maximum = crossUsages.size();
		    message = usageMessage;
		    for (NameUsage<T/*?*/> crossUsage : crossUsages) {
			current++;
			publish(crossUsage.getLiteral());
			rootNodes.addAll(nameUsageExchanger.getPartialHierarchies(crossUsage, higher, height, lower, depth));
		    }
		    crossUsages.clear();
		}
		current = 0;
		maximum = -1;

		// FIXME to be more general
		Collection<NameUsage<?>> integrated =nameUsageExchanger.integrateHierarchies(rootNodes);
		if (unitedTreeModel == null || !comparisonQueryParameter.isAppend()) {
		    unitedTreeModel = new UnitedNameTreeModel(integrated.size() + 1);
		    nameListPane.setAppendable(true);
		}

		current = 0;
		maximum = integrated.size();
		message = analyzingMessage;
		for(NameUsage<?> rootNode : integrated) { 
		    NameTreeModel nameTreeModel =new NameTreeModel(rootNode);
		    if(nameTreeModel != null) {
			current++;
			publish(rootNode);
			unitedTreeModel.add(nameTreeModel);
		    }
		}

		return unitedTreeModel;
	}

	@Override protected void process(List<Object> chunks) {
	    StringBuffer buffer = new StringBuffer("(");
	    buffer.append(current).append("/").append(maximum).append("): ").append(message);
	    Object lastItem = chunks.get(chunks.size() - 1);
	    buffer.append(lastItem instanceof NameUsage ? ((NameUsage<?>)lastItem).getViewName() : lastItem.toString());
	    progress.setString(buffer.toString());
	}

        @Override protected void done() 
	{
	    try {
		List<NameTreeTable<T>> tables = compare(get());
		for (NameTreeTable<T> nameTreeTable : tables) {
		    NameTreeTableModel nameTreeTableModel = (NameTreeTableModel)nameTreeTable.getModel();
		    List<Integer> rows = new ArrayList<Integer>();
		    for (String literal : nameLiterals) {
			Collection<Integer> selections = nameTreeTableModel.getRows(literal);
			if(selections != null && selections.size() > 0) {
			    rows.addAll(selections);
			}
		    }
		    if (rows.size() > 0) {
			for (Integer row : rows) {
			    nameTreeTable.addRowSelectionInterval(row, row);
			}
		    }
		}
	    }
	    catch (InterruptedException e) {
		statusLabel.setText("Interrupted");
	    }
	    catch (ExecutionException e) {
		statusLabel.setText(e.getMessage());
	    }
	    finally {
		progress.setMinimum(progress.getMinimum());
		progress.setIndeterminate(false);
		progress.setString("");
		statusLabel.setText("Comparison and analysis done");
		statusLayout.show(statusPanel, STATUS_LABEL);
		nameListPane.enableButtons(true);
		setCursor(false);
	    }

	}
    }

    HierarchiesComparison hierarchiesComparison = null;


    public void propertyChange(PropertyChangeEvent event)
    {
	String propertyName = event.getPropertyName();
	Object source = event.getSource();
	if (source == nameUsageQuery) {
	    if ("state".equals(propertyName)) {
		Object value = event.getNewValue();
		if (value.equals(SwingWorker.StateValue.STARTED)) {
		}
		else if (value.equals(SwingWorker.StateValue.DONE)) {
		    progress.setMinimum(progress.getMinimum());
		    progress.setIndeterminate(false);

		    String literal = null;
		    QueryParameter<T> parameter = nameUsageQuery.getQueryParameter();
		    if (parameter instanceof NameUsageQueryParameter) {
			NameUsageQueryParameter<T> queryParameter = (NameUsageQueryParameter<T>)parameter;
			literal = queryParameter.getLiteral();
		    }

		    try {
			Collection<T> results = nameUsageQuery.get();
			Collection<NameUsage<?>> nameUsages = new ArrayList<>(results.size());
			for(T result : results) {
			    nameUsages.add((NameUsage<?>)result);
			}
			statusLabel.setText(queryMessages.getMessage(nameUsages.size(), new Object[]{literal}));
			((NameTableModel)nameListPane.getNameList().getModel()).set(nameUsages);
		    }
		    catch (InterruptedException e) {
			statusLabel.setText("Interrupted: " + literal);
		    }
		    catch (ExecutionException e) {
			statusLabel.setText(e.getMessage());
		    }
		    finally {
			statusLayout.show(statusPanel, STATUS_LABEL);
		    }
		}
		else {
		}
	    }
	}
    }

    protected void indeterminate(JProgressBar progress, String message)
    {
	indeterminate(progress, message, 0, 100);
    }

    protected void indeterminate(JProgressBar progress, String message, int minimum, int maximum)
    {
	progress.setString(message);
	progress.setStringPainted(true);
	progress.setMinimum(minimum);
	progress.setMaximum(maximum);
	progress.setIndeterminate(true);
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    public void query(QueryEvent/*<T>*/ event)
    {
	if(event == null || queryManager == null)
	    return;

	Object source = event.getSource();

	if(source == nameUsageQueryPanel) {
	    nameUsageQueryPanel.enableButtons(false);
	    QueryParameter/*<T>*/ parameter = event.getQueryParameter();
	    NameUsageQueryParameter/*<T>*/ queryParameter = null;
	    if (parameter instanceof NameUsageQueryParameter)
		queryParameter = (NameUsageQueryParameter/*<T>*/)parameter;
	    String queryLiteral = queryParameter == null? null : queryParameter.getLiteral();

	    ((NameTableModel)nameListPane.getNameList().getModel()).clear();
	    indeterminate(progress, "Retrieving " + queryLiteral);
	    statusLayout.show(statusPanel, PROGRESS_BAR);
	    progress.setVisible(true);

	    nameUsageQuery = 
		new NameUsageQuery(nameUsageExchanger, event.getQueryParameter());
	    //nameUsageQuery.addPropertyChangeListener(this);
		setCursor(true);
		nameUsageQuery.execute();
	}
	else if(source == localQueryPanel) {
	    QueryParameter/*<T>*/ p = event.getQueryParameter();
	    if(p instanceof NameUsageQueryParameter && alignerTree != null) {
		NameUsageQueryParameter parameter = (NameUsageQueryParameter)p;
		    String msg = alignerTree.getNames(parameter.getLiteral(), parameter.getRank(), null, null, parameter.getMatchingMode());
		    statusLabel.setText(msg);
		    statusLayout.show(statusPanel, STATUS_LABEL);
	    }
	}
	else if(source == nameListPane) {
	    QueryParameter queryParameter = event.getQueryParameter();
	    ComparisonQueryParameter<NameUsage<?>> comparisonQueryParameter = null;
;	    if(queryParameter instanceof ComparisonQueryParameter) {
    		comparisonQueryParameter = (ComparisonQueryParameter<NameUsage<?>>)queryParameter;
		nameListPane.enableButtons(false);
		if(hierarchiesPane != null) {
		    int tabs = hierarchiesPane.getTabCount();
		    while(tabs > 0) {
			hierarchiesPane.remove(--tabs);
		    }
		}
		clearNameUsage();
		indeterminate(progress, "");
		statusLayout.show(statusPanel, PROGRESS_BAR);
		progress.setVisible(true);
		hierarchiesComparison = new HierarchiesComparison(nameUsageExchanger, comparisonQueryParameter);
		setCursor(true);
		hierarchiesComparison.execute();
	    }
	}
    }

    /**
     * Handle notification from objects
     *
     * @param event {@code ChangeEvent} from an object
     */
    public void stateChanged(ChangeEvent event)
    {
	/*
	if(event.getSource() == languageMenu) {
	    setLocale(languageMenu.getLocale());
	    return;
	}

	if(nameUsagePanel == null ||
	   nameUsagePanel != event.getSource() ||
	   tree == null)
	    return;

	NameUsage current = 
	    (NameUsage)nameUsagePanel.getNamedObject();

	TreePath path =
	    tree.getSelectionPath();

	if(path == null) {
	    //...
	    return;
	}
	
	NameUsage selection =
	    ((NameTreeNode)path.getLastPathComponent()).getNameUsage();

	if(selection == current)
	    return;

	Object[] nodes =
	    path.getPath();
	NameTreeNode node = 
	    (NameTreeNode)nodes[nodes.length - 1];
	if(selection.getHigherTaxon() == current) {
	    tree.scrollPathToVisibleCenter(new TreePath(Array.remove(node, nodes)));
	}
	else if(Array.contains(current, node.getNameUsage().getLowerTaxa())) {
	    Enumeration e = 
		((NameTreeNode)path.getLastPathComponent()).children();
	    while(e.hasMoreElements()) {
		NameTreeNode lowerTaxon =
		    (NameTreeNode)e.nextElement();
		if(lowerTaxon.getNameUsage() != current)
		    continue;
		tree.scrollPathToVisibleCenter(path.pathByAddingChild(lowerTaxon));
		break;
	    }
	}
	else {
	    tree.clearSelection();
	}
	*/
    }

    public void valueChanged(TreeSelectionEvent event)
    {
	TreePath path = null;
	switch(tree.getSelectionCount()) {
	case 0:
	    break;
	case 1:
	    path = tree.getSelectionPath();
	    if(path != null) {
		Object last = path.getLastPathComponent();
		if(last instanceof NameTreeNode) {
		    /*
		    NameUsage n = ((NameTreeNode)last).getNameUsage();
		    if(n != nameUsagePanel.getNamedObject()) {
			nameUsagePanel.setNamedObject(n);
			publicationPanel.setNamedObject(n.getPublication());
		    }
		    */
		}
	    }
	    break;
	default:
	}
    }

    protected MultiplexNameUsageQuery<T> getHierarchy = null;

    protected List<MultiplexNameUsageQuery<T>> getHierarchies = null;

    class NameTreeModelRetrieval extends SwingWorker<NameTreeModel, Object> {
	NameUsage<?> nameUsage;
	public NameTreeModelRetrieval(NameUsage<?> nameUsage) {
	    super();
	    this.nameUsage = nameUsage;
	}

	@Override public NameTreeModel doInBackground() {
	    return getSelectedTreeModel(nameUsage);
	}

        @Override protected void done() {
	    NameTreeModel nameTreeModel = getSelectedTreeModel(nameUsage);
	    if(nameTreeModel == null) {
		clearNameUsage();
	    }
	    else {
		tree.setRootVisible(true);
		tree.setModel(nameTreeModel);
		tree.expandAll();
	    }

	    progress.setMinimum(progress.getMinimum());
	    progress.setIndeterminate(false);
	    progress.setString("");
	    statusLabel.setText("Retrieved the hierarchy of " + nameUsage.getLiteral() + "  in " + nameUsage.getViewName());
	    statusLayout.show(statusPanel, STATUS_LABEL);
	    nameListPane.enableButtons(true);
	}
    }

    public void valueChanged(ListSelectionEvent event)
    {
	if(event.getValueIsAdjusting())
	    return;

	if(event.getSource() == nameList.getSelectionModel()) {
	    NameTable nameList = nameListPane.getNameList();
	    NameTableModel nameListModel = nameList.getNameTableModel();
	    int count = nameList.getSelectedRowCount();
	    if(count == 0) {
		// how can I clear the tree?
		clearNameUsage();
	    }
	    else if (count == 1) {
		int index = nameList.convertRowIndexToModel(nameList.getSelectedRow());
		NameUsage<?> nameUsage = nameListModel.getNamedObject(index);
		nameUsagePanel.setNamedObject(nameUsage);
		detailText.setText(nameUsage.getDetail());

		indeterminate(progress, "Retrieving the hierarchy of " + nameUsage.getLiteral() + "  in " + nameUsage.getViewName());
		statusLayout.show(statusPanel, PROGRESS_BAR);
		progress.setVisible(true);

		(new NameTreeModelRetrieval(nameUsage)).execute();
	    }
	    else {
	    }
	}
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
	protected Collection<NameUsage<?>> getRoots(NameUsageQueryParameter<T> queryParameter)
    {
	if(queryParameter == null)
	    return null;

	return nameUsageExchanger.integrateHierarchies(nameUsageExchanger.getObjects(queryParameter));
    }



    @SuppressWarnings({"rawtypes", "unchecked"})
	protected Collection<NameUsage<?>> getRoots(NameUsage<?> nameUsage)
    {
	if(nameUsage == null || nameListPane == null)
	    return null;

	NameUsage<?> n = nameUsage.getNameUsage();
	if(n != null && n != nameUsage)
	    nameUsage = n;

	NameUsageQueryParameter<?> queryParameter =
	    nameListPane.getQueryParameter(nameUsage);

	//FIXME for generalisation to support other data sources
	Collection</*NamedObject<*/NubNameUsage/*>*/> results =
	    new NubExchanger().getObjects((NameUsageQueryParameter<NubNameUsage>)queryParameter);

	List<NameUsage<?>> toReturn = new ArrayList<>();
	for(/*NamedObject<*/NubNameUsage/*>*/ result : results) {
	    if(result instanceof NameUsage)
		toReturn.add((NameUsage<?>)result);
	}

	return toReturn;
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    protected NameTreeModel getSelectedTreeModel(NameUsage<?> nameUsage)
    {
	if(nameUsage == null || nameListPane == null)
	    return null;

	NameUsage<?> n = nameUsage.getNameUsage();
	if(n != null && n != nameUsage)
	    nameUsage = n;

	NameUsageQueryParameter/*<T>*/ queryParameter = 
	    nameListPane.getQueryParameter(nameUsage);

	//FIXME for generalisation to support other data sources
	NubExchanger exchanger = new NubExchanger();
	Collection<NamedObject<NubNameUsage>> results = 
	    nameUsageExchanger.getObjects(queryParameter);
	if(results == null || results.isEmpty()) {
	    return null;
	}
	NameTreeModel nameTreeModel = 
	    new NameTreeModel((NameUsage<?>)results.iterator().next());
	nameTreeModel.setViewName(nameUsage.getViewName());

	return nameTreeModel;
    }

    protected List<NameTreeTable<T>> compare(UnitedNameTreeModel unitedTreeModel)
    {
	if(hierarchiesPane == null)
	    return null;

	return hierarchiesPane.classify(unitedTreeModel);
    }

    protected void clearNameUsage()
    {
	if(tree != null)
	    tree.setModel(new NameTreeModel((NameTreeNode)null));
	if(detailText != null)
	    detailText.setText("");
	if(nameUsagePanel != null)
	    nameUsagePanel.setNewNamedObject();
    }

    // FIXME
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void queryReturned(QueryResultEvent/*<T>*/ event)
    {
	if(event == null)
	    return;

	synchronized(queryThread) {
	    Collection<NamedObject<?>> results = event.getResults();
	    if(results != null && !results.isEmpty()) {
		Iterator<NamedObject<?>> iterator = results.iterator();
		NamedObject<?> object = iterator.next();
		if(object instanceof Agent) {
		    agentResults(results);
		}
		else if (object instanceof Annotation) {
		    anottationtResults(results);
		}
		else if (object instanceof Publication) {
		    publicationtResults(results);
		}
		else if (object instanceof NameUsage) {
		    nameUsageResults(results);
		}
		else {
		}
	    }
	}
	if(event.getResidue() == 0)
	    queryThread = null;
	/*
	setCursor(DEFAULT);

	if(getHierarchy != null &&
	   getHierarchy.getId() == event.getThreadID()) {
	    NameUsage node = 
		(NameUsage)event.getResult();
	    if(node == null) {
		tree.setModel(null);
		return;
	    }
	    NameTreeModel treeModel = new NameTreeModel(node);
	    tree.setModel(treeModel);
	    NameUsage nameUsage = 
		(NameUsage)nameUsagePanel.getNamedObject();
	    nameUsagePanel.setNamedObject(nameUsage);
	    NameTreeNode treeNode = treeModel.getNode(nameUsage);
	    if(treeNode != null)
		tree.scrollPathToVisibleCenter(new TreePath(treeNode.getPath()));
	    getHierarchy.removeQueryResultListener(this);
	    getHierarchy = null;
	}
	else if(getHierarchies != null) {
	}
	*/
    }    

    protected void agentResults(Collection<NamedObject<?>> results)
    {
    }

    protected void anottationtResults(Collection<NamedObject<?>> results)
    {
    }

    protected void publicationtResults(Collection<NamedObject<?>> results)
    {
    }

    protected void nameUsageResults(Collection<NamedObject<?>> results)
    {
    }


    protected void setCursor(boolean wait)
    {
	if(wait) {
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    for(JComponent component : components) {
		component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    }
	}

	else {
	    for(JComponent component : components) {
		component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }

    class ExportPOI extends SwingWorker<Workbook, Integer> {
	Workbook wb;
	File fileName;
	String pathName;
	Component component;
	ExportPOI(Workbook wb, File fileName, Component component) {
	    super();
	    this.wb = wb;
	    this.fileName = fileName;
	    pathName = fileName.getAbsolutePath();
	    this.component = component;
	}

	@Override public Workbook doInBackground() {
	    int sheets = 0;
	    setCursor(true);
	    indeterminate(progress, "Exporting to " + pathName);
	    statusLayout.show(statusPanel, PROGRESS_BAR);
	    progress.setVisible(true);

	    if (nameListPane != null) {
		JTable table = nameListPane.getNameTable();
		JTabbedPane tabs = resultTabs;
		String tabTitle = null;
		int tabCount =  tabs.getTabCount(); 
		for (int i = 0; i < tabCount; i++) {
		    if(nameListPane == tabs.getComponentAt(i)) {
			tabTitle = tabs.getTitleAt(i);
			break;
		    }
		}
		publish(Integer.valueOf(++sheets));
		POIAdaptor.createSheet(
				       new NameTableStringModel((NameTableModel)table.getModel()),
				       wb, tabTitle, true);
	    }
	    
	    if (hierarchiesPane != null) {
		int tabCount = hierarchiesPane.getTabCount(); 
		for (int i = 0; i < tabCount; i++) {
		    publish(Integer.valueOf(++sheets));
		    POIAdaptor.createSheet(
					   new NameTreeTableStringModel((NameTreeTableModel)hierarchiesPane.getTableAt(i).getModel()),
					   wb, hierarchiesPane.getTitleAt(i), true
					   );
		}
	    }
	    return wb;
	}

	@Override protected void process(List<Integer> chunks) {
	    progress.setString("Processing sheet " + chunks.get(chunks.size() - 1));
	}

        @Override protected void done() {
	    BufferedOutputStream stream = null;
	    String message = null;
	    try {
		stream = new BufferedOutputStream(new FileOutputStream(fileName));
		get().write(stream);
	    }
	    catch (FileNotFoundException fileNotFoundException) {
		message = fileNotFoundException.getMessage();
		message = (message != null && message.length() > 0) ?
		    message : pathName + " is not found or inaccessible.";
		JOptionPane.showMessageDialog(component,
					      message, 
					      "File error",
					      JOptionPane.ERROR_MESSAGE);
	    }
	    catch (InterruptedException interruptedException) {
		message = interruptedException.getMessage();
		message = (message != null && message.length() > 0) ?
		    message : "Interrupted";
	    }
	    catch (ExecutionException executionException) {
		message = executionException.getMessage();
	    }
	    catch (IOException writeException) {
		message = writeException.getMessage();
		message = (message != null && message.length() > 0) ?
		    message : pathName + " is not writable.";
	    }
	    finally {
		try {
		    stream.close();
		    message = "Exported to " + pathName;
		}
		catch (IOException closeException) {
		    message = closeException.getMessage();
		    message = (message != null && message.length() > 0) ?
			message : pathName + " is not closed.";
		}
	    }
	    progress.setMinimum(progress.getMinimum());
	    progress.setIndeterminate(false);
	    progress.setString("");
	    statusLabel.setText(message);
	    statusLayout.show(statusPanel, STATUS_LABEL);
	    setCursor(false);
	}
    }

    public void actionPerformed(ActionEvent e)
    {
	Object source = e.getSource();
	if (menu != null && menu.getExportItem() == source) {
	    if (excelFileChooser != null 
		&& JFileChooser.APPROVE_OPTION == excelFileChooser.showSaveDialog(this)) {
		File fileName = excelFileChooser.getSelectedFile();
		String pathName = fileName.getAbsolutePath().trim();
		FileFilter[] filters = excelFileChooser.getChoosableFileFilters();
		FileFilter fit = null;
		for (FileFilter filter : filters) {
		    if (fit != null)
			break;
		    if (filter instanceof FileNameExtensionFilter) {
			if (fit != null)
			    break;
			String[] extensions = ((FileNameExtensionFilter)filter).getExtensions();
			for (String extension : extensions) {
			    if (fit != null)
				break;
			    if (pathName.endsWith(extension)) {
				fit = filter;
			    }
			}
		    }
		}
		if (fit == null) {
		    fit = excelFileChooser.getFileFilter();
		    fileName = new File(pathName + "." + ((FileNameExtensionFilter)fit).getExtensions()[0]);
		}

		Workbook wb = (fit == excelFileChooser.getXlsFilter()) ?
		    new HSSFWorkbook() : new XSSFWorkbook();

		(new ExportPOI(wb, fileName, this)).execute();
	    }
	}
    }

    public static void main(String[] args)
    {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

	JFrame frame = new JFrame("Taxonaut");
	//Taxonaut<NameUsage<?>> taxonaut = new Taxonaut<NameUsage<?>>();
	Taxonaut<NubNameUsage> taxonaut = new Taxonaut<>();
	taxonaut.setNameUsageExchanger(new NubExchanger());
	frame.getContentPane().add(taxonaut);

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(frame.getPreferredSize());
	frame.pack();
	frame.setVisible(true);
    }

}

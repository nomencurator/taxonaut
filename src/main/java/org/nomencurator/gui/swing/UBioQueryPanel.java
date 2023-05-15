/*
 * QueryPanel.java:  a class provides layouted fields for query to 
 * Nomencurator.
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.net.URL;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//import javax.swing.text.Dcoument;

//import org.nomencurator.io.DataExchanger;
import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;

import org.nomencurator.io.ubio.UBio;

// import org.nomencurator.model.NameUsage;
import org.nomencurator.model.ubio.UBioNameUsage;

import org.nomencurator.resources.ResourceKey;

// import org.nomencurator.swing.table.NameTableModel;

/**
 * {@code UBioQueryPanel} provides layouted fields for query to uBio
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class UBioQueryPanel<T extends UBioNameUsage>
    extends JPanel
    implements ActionListener,
	       ChangeListener,
	       DocumentListener,
	       KeyListener,
	       //	       QueryResultListener,
	       Runnable
{
    private static final long serialVersionUID = -1033282234290796154L;

    public static final int RANK = 1;
    public static final int NAME = 2;
    public static final int SENSU = 4;
    public static final int YEAR = 8;
    public static final int QUERY_TYPE = 16;

    /* label for the rank field*/
    protected JLabel rankLabel;

    /* field to specify rank(s) */
    protected JTextField rankField;

    /* label for the taxon name field */
    protected JLabel nameLabel;

    /* field to specify taxon name(s) */
    protected JTextField nameField;

    /* label for the sensu field */
    protected JLabel sensuLabel;

    /* field to specify user of the name */
    protected JTextField sensuField;

    /* label for the year field */
    protected JLabel yearLabel;

    /* field to specify year of publication */
    protected JTextField yearField;
    
    protected JPanel innerPanel;

    protected JPanel southPanel;

    /* button to search */
    protected JButton searchButton;

    /* button to clear fields */
    protected JButton clearButton;

    /* field to display query result */
    protected JLabel resultLabel;
    
    /*uBio icon label*/
    protected JLabel uBioLabel;

    protected static ImageIcon uBioIcon;

    static {
	//	URL uBioIconURL = QueryPanel.class.getResource("org/ubio/demo/uBio_logo.gif");
	URL uBioIconURL = QueryPanel.class.getResource("uBio_logo.gif");
	if(uBioIconURL != null)
	    uBioIcon = new ImageIcon(uBioIconURL);
    }
    
    protected PlaceableProgressBar progress;

    protected JPanel statusPanel;

    protected CardLayout statusLayout;

    protected LanguageMenu languageMenu;

    //protected NameQuery nameQuery;

    protected boolean incrementalSearch;

    protected String record_message;

    protected String records_message;

    protected String found_for_message;

    protected String search_for_message;

    protected JComboBox<String> queryType;

    //protected DataExchanger queryManager;
    protected UBio queryManager;

    protected QueryMessages messages;

    // protected NameTable targetNameTable;

    // protected NameTreeTable targetNameTreeTable;

    private static final String PROGRESS = "progress";

    private static final String RESULT = "result";

    /**
     * Constract a {@code UBioQueryPanel}
     * using VM default locale
     */
    public UBioQueryPanel()
    {
	this(Locale.getDefault());
    }

    /**
     * Constract a {@code UBioQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public UBioQueryPanel(Locale locale)
    {
	this(locale, 
	     RANK | NAME | SENSU | YEAR | QUERY_TYPE);
	     
    }

    /**
     * Constract a {@code UBioQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public UBioQueryPanel(int components)
    {
	this(Locale.getDefault(), components);
    }


    /**
     * Constract a {@code QueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public UBioQueryPanel(Locale locale, int components)
    {
	super(new BorderLayout());
	((BorderLayout)getLayout()).setHgap(6);
	incrementalSearch = false;
	createComponents(locale, components);
	//	setLocale(locale);
	layoutComponents();
	setComponentsSize();
    }

    /**
     * Creates {@code Components}
     *
     * @param locale {@code Locale} to determine texts in labels and buttons.
     * VM default locale will be used if the value is null.
     */
    protected void createComponents(Locale locale, int components)
    {
	if((components & RANK) != 0) {
	    rankLabel = new JLabel();
	    rankField = new JTextField(8);
	    rankField.addKeyListener(this);
	    rankField.getDocument().addDocumentListener(this);
	}

	if((components & NAME) != 0) {
	    nameLabel = new JLabel();
	    //nameField = new JTextField(25);
	    nameField = new JTextField();
	    nameField.addKeyListener(this);
	    nameField.getDocument().addDocumentListener(this);
	}

	if((components & SENSU) != 0) {
	    sensuLabel = new JLabel();
	    //sensuField = new JTextField(15);
	    sensuField = new JTextField();
	    sensuField.addKeyListener(this);
	    sensuField.getDocument().addDocumentListener(this);
	    sensuLabel.setEnabled(false);
	    sensuField.setEnabled(false);
	}

	if((components & YEAR) != 0) {
	    yearLabel = new JLabel();
	    yearField = new JTextField(4);
	    yearField.addKeyListener(this);
	    yearField.getDocument().addDocumentListener(this);
	    yearLabel.setEnabled(false);
	    yearField.setEnabled(false);
	}

	if((components & QUERY_TYPE) != 0) {
	    queryType = new JComboBox<String>();
	}

	searchButton = new JButton();
	searchButton.addActionListener(this);
	clearButton = new JButton();
	clearButton.addActionListener(this);
	enableButtons(false);
	if(uBioIcon != null)
	    uBioLabel = new JLabel(uBioIcon);


	progress = new PlaceableProgressBar();
	progress.setTextPlacement(SwingConstants.LEFT);
	resultLabel = new JLabel(" ");
	statusLayout = new CardLayout(6, 0);
	statusPanel = new JPanel(statusLayout);

	progress.setBorderPainted(false);

	setLocale(locale);
    }

    /** Layout {@code Components} */
    protected void layoutComponents()
    {
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.insets.top = 3;

	statusPanel.add(progress, PROGRESS);
	statusPanel.add(resultLabel, RESULT);

	southPanel = new JPanel(layout);
	add(southPanel, BorderLayout.SOUTH);
	gbc.gridy=0;
	gbc.gridx=0;
	gbc.anchor = GridBagConstraints.WEST;
	southPanel.add(Box.createHorizontalStrut(6), gbc);

	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	gbc.gridx++;
	southPanel.add(statusPanel, gbc);
	gbc.weightx = 0.0;
	gbc.fill = GridBagConstraints.NONE;
	gbc.gridx++;
	southPanel.add(Box.createHorizontalStrut(3), gbc);

	if(queryType != null) {
	    gbc.gridx++;
	    southPanel.add(queryType, gbc);
	    gbc.gridx++;
	    southPanel.add(Box.createHorizontalStrut(3), gbc);
	}


	gbc.gridx++;
	southPanel.add(searchButton, gbc);
	gbc.gridx++;
	southPanel.add(Box.createHorizontalStrut(3), gbc);
	gbc.gridx++;
	southPanel.add(clearButton, gbc);
	gbc.gridx++;
	southPanel.add(Box.createHorizontalStrut(3), gbc);
	if(uBioLabel != null) {
	    gbc.gridx++;
	    southPanel.add(uBioLabel, gbc);
	    gbc.gridx++;
	    southPanel.add(Box.createHorizontalStrut(3), gbc);
	}
	gbc.gridx++;
	southPanel.add(Box.createHorizontalStrut(6), gbc);



	layout = new GridBagLayout();
	gbc = new GridBagConstraints();

	innerPanel = new JPanel(layout);
	add(innerPanel, BorderLayout.CENTER);

	gbc.gridy=0;
	gbc.gridx=0;
	gbc.anchor = GridBagConstraints.WEST;
	innerPanel.add(Box.createHorizontalStrut(6), gbc);

	if(rankField != null) {
	    gbc.gridx++;
	    innerPanel.add(rankLabel, gbc);
	    gbc.gridx++;
	    innerPanel.add(Box.createHorizontalStrut(3), gbc);
	}

	gbc.fill = GridBagConstraints.NONE;

	if(nameField != null) {
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.weightx = 0.6;
	    gbc.gridx++;
	    innerPanel.add(nameLabel, gbc);
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.weightx = 0;
	    gbc.gridx++;
	    innerPanel.add(Box.createHorizontalStrut(3), gbc);
	}

	if(sensuField != null) {
	    gbc.gridx++;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.weightx = 0.0;//0.4
	    innerPanel.add(sensuLabel, gbc);
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.weightx = 0;
	    gbc.gridx++;
	    innerPanel.add(Box.createHorizontalStrut(3), gbc);
	}

	if(yearField != null) {
	    gbc.gridx++;
	    innerPanel.add(yearLabel, gbc);
	}
	
	gbc.gridy++;
	gbc.gridx=0;

	innerPanel.add(Box.createHorizontalStrut(6), gbc);
	if(rankField != null) {
	    gbc.gridx++;
	    innerPanel.add(rankField, gbc);
	    gbc.gridx++;
	    innerPanel.add(Box.createHorizontalStrut(3), gbc);
	}

	gbc.fill = GridBagConstraints.NONE;

	if(nameField != null) {
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.weightx = 0.6;
	    gbc.gridx++;
	    innerPanel.add(nameField, gbc);
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.weightx = 0;
	    gbc.gridx++;
	    innerPanel.add(Box.createHorizontalStrut(3), gbc);
	}

	if(sensuField != null) {
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.gridx++;
	    gbc.weightx = 0.4;
	    innerPanel.add(sensuField, gbc);

	    gbc.weightx = 0;
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.gridx++;
	    innerPanel.add(Box.createHorizontalStrut(3), gbc);
	}

	if(yearField != null) {
	    gbc.gridx++;
	    innerPanel.add(yearField, gbc);
	    gbc.gridx++;
	    innerPanel.add(Box.createHorizontalStrut(3), gbc);
	}

	/*
	gbc.gridx++;
	innerPanel.add(searchButton, gbc);
	gbc.gridx++;
	innerPanel.add(Box.createHorizontalStrut(3), gbc);
	gbc.gridx++;
	innerPanel.add(clearButton, gbc);
	*/
	gbc.gridx++;
	innerPanel.add(Box.createHorizontalStrut(6), gbc);
    }

    /** Set size of {@code Components} */
    protected void setComponentsSize()
    {
	clearButton.setPreferredSize(null);
	clearButton.setMinimumSize(null);

	searchButton.setPreferredSize(null);
	searchButton.setPreferredSize(null);

	Dimension size = 
	    clearButton.getPreferredSize();
	Dimension searchSize = 
	    searchButton.getPreferredSize();

	if(searchSize.height < size.height)
	    searchSize.height = size.height;
	if(searchSize.width < size.width)
	    searchSize.width = size.width;

	if(rankField != null) {
	    size = setPreferredComponentHeight(rankField, searchSize);
	    rankField.setMinimumSize(size);
	}

	if(yearField != null) {
	    size = setPreferredComponentHeight(yearField, searchSize);
	    yearField.setMinimumSize(size);
	}

	if(nameField != null)
	    setPreferredComponentHeight(nameField, searchSize);

	if(sensuField != null)
	    setPreferredComponentHeight(sensuField, searchSize);

	if(yearField != null)
	    setPreferredComponentHeight(yearField, searchSize);
	
	clearButton.setMinimumSize(searchSize);
	clearButton.setPreferredSize(searchSize);

	searchButton.setMinimumSize(searchSize);
	searchButton.setPreferredSize(searchSize);
    }

    /**
     * Sets preferred size of component to match with reference size.
     * Height of reference size is increased if the component is taller
     * than reference size.  It returens preference size of the component.
     *
     * @param component {@code JComponent} of which size to be modified
     * @param referenceSize {@code Dimension} representing height to which
     * the height of the {@code component} matchs.
     *
     * @return Dimension containing preference size of the component
     */
    protected Dimension setPreferredComponentHeight(JComponent component, Dimension referenceSize)
    {
	Dimension size = component.getPreferredSize();
	if(size.height < referenceSize.height)
	    size.height =  referenceSize.height;
	else
	    referenceSize.height = size.height;

	component.setPreferredSize(size);

	return size;
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	/*
	if(locale == null)
	    locale = Locale.getDefault();
	if(locale.equals(getLocale()))
	    return;
	*/
	super.setLocale(locale);

	String rankText = ResourceKey.RANK;
	String nameText = ResourceKey.TAXON_NAME;
	String sensuText = ResourceKey.SENSU;
	String yearText = ResourceKey.YEAR;
	String searchText = ResourceKey.SEARCH;
	String clearText = ResourceKey.CLEAR;
	record_message = ResourceKey.RECORD;
	records_message = ResourceKey.RECORDS;
	found_for_message = ResourceKey.FOUND_FOR_MESSAGE;
	String exact = ResourceKey.EXACT_QUERY;
	String contains = ResourceKey.CONTAINS_QUERY;
	String fuzzy = ResourceKey.FUZZY_QUERY;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    rankText = resource.getString(rankText);
	    nameText = resource.getString(nameText);
	    sensuText = resource.getString(sensuText);
	    yearText = resource.getString(yearText);
	    searchText = resource.getString(searchText);
	    clearText = resource.getString(clearText);
	    record_message = resource.getString(record_message);
	    records_message = resource.getString(records_message);
	    found_for_message = resource.getString(found_for_message);
	    exact = resource.getString(exact);
	    contains = resource.getString(contains);
	    fuzzy = resource.getString(fuzzy);
	}
	catch(MissingResourceException e) {

	}

	if(rankText == null)
	    rankText = ResourceKey.RANK;
	if(nameText == null)
	    nameText = ResourceKey.TAXON_NAME;
	if(sensuText == null)
	    sensuText = ResourceKey.SENSU;
	if(yearText == null)
	    yearText = ResourceKey.YEAR;
	if(searchText == null)
	    searchText = ResourceKey.SEARCH;
	if(clearText == null)
	    clearText = ResourceKey.CLEAR;
	if(record_message == null)
	    record_message = ResourceKey.RECORD;
	if(records_message == null)
	    records_message = ResourceKey.RECORDS;
	if(found_for_message == null)
	    found_for_message = ResourceKey.FOUND_FOR_MESSAGE;
	if(exact == null)
	    exact = ResourceKey.EXACT_QUERY;
	if(contains == null)
	    contains = ResourceKey.CONTAINS_QUERY;
	if(fuzzy == null)
	    fuzzy = ResourceKey.FUZZY_QUERY;

	if(rankLabel != null)
	    rankLabel.setText(rankText);

	if(nameLabel != null)
	    nameLabel.setText(nameText);

	if(sensuLabel != null)
	    sensuLabel.setText(sensuText);

	if(yearLabel != null)
	    yearLabel.setText(yearText);

	searchButton.setText(searchText);
	clearButton.setText(clearText);

	if(queryType != null) {
	    String[] str = {exact, contains, fuzzy};
	    queryType.setModel(new DefaultComboBoxModel<String>(str));
	}

	if(messages != null)
	    messages.setLocale(locale);

	if(isAncestorOf(searchButton))
	    setComponentsSize();
	revalidate();
    }

    public void actionPerformed(ActionEvent event)
    {
	Object source = event.getSource();
	if(source == searchButton) {
	    /*
	    if(nameQuery == null)
		return;
	    */
	    setEnabled(false);
	    statusLayout.show(statusPanel, PROGRESS);
	    new Thread(this).start();
	    //setEnabled(true);
	}
	else if (source == clearButton) {
	    if(rankField != null)
		rankField.setText("");
	    if(nameField != null)
		nameField.setText("");
	    if(sensuField != null)
		sensuField.setText("");
	    if(yearField != null)
		yearField.setText("");

	}
    }

    //ignore properties
    public void changedUpdate(DocumentEvent event) {}

    public void insertUpdate(DocumentEvent event)
    {
	enableButtons(testText(rankField) || testText(nameField) ||
		      testText(sensuField) || testText(yearField));
    }

    public void removeUpdate(DocumentEvent event) 
    {
	enableButtons(testText(rankField) || testText(nameField) ||
		      testText(sensuField) || testText(yearField));
    }

    //protected boolean testText(JTextComponent text)
    protected boolean testText(JTextField text)
    {
	if(text == null)
	    return false;
	String str = text.getText();
	return (str != null && str.length() > 0);
    }

    protected void processDocumentEvent(DocumentEvent event)
    {
    }

    public void keyTyped(KeyEvent event)
    {
    }

    public void keyReleased(KeyEvent event)
    {
    }

    public void keyPressed(KeyEvent event)
    {
	Object source = event.getSource();
	if(event.getKeyCode() == KeyEvent.VK_ENTER && 
	   (source == rankField || source == nameField ||
	    source == sensuField || source == yearField) /*&&
							   nameQuery != null*/) {
	    new Thread(this).start();
	}
    }	

    /*
    public NameQuery getNameQuery()
    {
	return nameQuery;
    }
    
    public void setNameQuery(NameQuery nameQuery)
    {
	if(this.nameQuery == nameQuery)
	    return;
	this.nameQuery = nameQuery;
    }
    */


    //public DataExchanger getQueryManager()
    public ObjectExchanger<?> getQueryManager()
    {
	return queryManager;
    }

    //public void setQueryManager(DataExchanger queryManager)
    public void setQueryManager(UBio queryManager)
    {
	/*
	if(this.queryManager != null)
	    this.queryManager.removeQueryResultListener(this);

	this.queryManager = queryManager;

	if(queryManager != null)
	    queryManager.addQueryResultListener(this);
	*/
    }

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

    public void stateChanged(ChangeEvent event)
    {
	if(event.getSource() != languageMenu)
	    return;
	setLocale(languageMenu.getLocale());
    }

    public void run()
    {
	if(queryManager == null)
	    return ;

	String rank = null;

	if(rankField != null)
	    rank = rankField.getText();	
	if(rank != null && rank.length() == 0)
	    rank = null;
	
	progress.setStringPainted(true);
	progress.setIndeterminate(true);
	progress.setTextColor(null);
	String name = null;
	if(nameField != null)
	    name = nameField.getText();
	if(name == null)
	    return;
	progress.setString("Searching for " + name);
	/*
	if(queryType != null)
	    queryManager.requestNameUsages(rank, name,
					   queryType.getSelectedIndex());
	else
	    queryManager.requestNameUsages(rank, name);
	*/

	setEnabled(false);
    }

    protected void enableButtons(boolean enable)
    {
	searchButton.setEnabled(enable);
	clearButton.setEnabled(enable);
	
    }

    /*
    public void queryReturned(QueryResultEvent event)
    {
	setEnabled(true);
	progress.setIndeterminate(false);
	progress.setValue(0);
	resultLabel.setText("");
	statusLayout.show(statusPanel, RESULT);

	Object result = event.getResult();
	if(result == null ||
	   !result.getClass().isArray())
	    return;

	String message = null;
	int records = 0;
	if(result != null)
	    records = ((Object[])result).length;
	String name = nameField.getText();
	String rank = rankField.getText();
	if(rank == null)
	    rank = "";
	if(messages == null)
	    messages = new QueryMessages(getLocale());
	resultLabel.setText(messages.getMessage(records, new Object[] {rank, name}));

	if(result instanceof NameUsage[] &&
	   targetNameTable != null) {
	    ((NameTableModel)targetNameTable.getModel()).set((NameUsage[])result);
	}
    }

    public void setNameList(NameTable target)
    {
	targetNameTable = target;
    }

    public NameTable getNameList()
    {
	return targetNameTable;
    }

    public void setNameTreeTable(NameTreeTable target)
    {
	if(targetNameTreeTable != null)
	    targetNameTreeTable.removeQueryResultListener(this);
	targetNameTreeTable = target;
	if(targetNameTreeTable != null)
	    targetNameTreeTable.addQueryResultListener(this);
    }

    public NameTreeTable getNameTreeTable()
    {
	return targetNameTreeTable;
    }
    */
}
    

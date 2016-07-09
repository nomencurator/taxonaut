/*
 * QueryPanel.java:  a class provides layouted fields for query to data sources.
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

package org.nomencurator.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.net.URL;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;

//import javax.swing.text.Dcoument;

import org.nomencurator.gui.swing.model.QueryTypeChooserModel;

import org.nomencurator.gui.vocabulary.QueryPanelElement;

import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.NameUsageQueryEvent;
import org.nomencurator.io.NameUsageQueryParameter;
import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.QueryEvent;
import org.nomencurator.io.QueryListener;
import org.nomencurator.io.QueryMode;
import org.nomencurator.io.QueryManager;
import org.nomencurator.io.QueryParameter;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code QueryPanel} provides components to specify a filter to search data sources.
 * It dispatches a {@code QueryEvent} representing a query filter.
 *
 * @version 	02 Julu 2016
 * @author 	Nozomi `James' Ytow
 */
public class QueryPanel<T extends NamedObject<?>>
    extends JPanel
    implements ActionListener,
	       ChangeListener,
	       DocumentListener,
	       KeyListener,
	       //	       QueryResultListener,
	       Runnable
{
    private static final long serialVersionUID = -2456360279323152682L;

    /* list of {@code EventLIstener}s */
    protected EventListenerList listeners;

    /* a {@code JComboBox} to select target object type */
    protected JComboBox<String> queryType;

    /* a {@code JComboBox} to select rank of name usages */
    protected JComboBox<Rank> rankBox;

    /* field to specify taxon name(s) */
    protected JTextField queryField;

    /* button to search */
    protected JButton searchButton;

    /* a {@code ButtonGroup} orgnaising {@code QueryMode} options */
    protected ButtonGroup buttons;

    /* radio buttons to specify a {@code QueryMode} */
    protected JRadioButton[] radioButtons;

    protected static MatchingMode[] matchingModes = MatchingMode.values();

    protected PlaceableProgressBar progress;

    protected JPanel statusPanel;

    protected CardLayout statusLayout;

    protected boolean incrementalSearch;

    protected String record_message;

    protected String records_message;

    protected String found_for_message;

    protected String search_for_message;

    // FIXME: It should be generalised to accept any NamedObject, or provide queryManagers for each type
    // e.g. NameUsage, Publication, Agent....
    protected QueryManager<T, ObjectExchanger<T>> queryManager;

    protected QueryMessages messages;

    // protected NameTable targetNameTable;

    // protected NameTreeTable targetNameTreeTable;

    private static final String PROGRESS = "progress";

    private static final String RESULT = "result";

    /**
     * Constract a {@code QueryPanel} using VM default locale
     */
    public QueryPanel()
    {
	this(Locale.getDefault());
    }

    /**
     * Constract a {@code QueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public QueryPanel(Locale locale)
    {
	this(Locale.getDefault(), null);
    }
    /**
     * Constract a {@code QueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public QueryPanel(QueryListener<T> listener)
    {
	this(Locale.getDefault(), listener);
    }

    /**
     * Constract a {@code QueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public QueryPanel(Locale locale, QueryListener<T> listener)
    {
	super(new GridBagLayout());
	incrementalSearch = false;
	createComponents(locale);
	layoutComponents();
	setComponentsSize();
	addQueryListener(listener);
    }

    /**
     * Creates {@code Components}
     *
     * @param locale {@code Locale} to determine texts in labels and buttons.
     * VM default locale will be used if the value is null.
     */
    protected void createComponents(Locale locale)
    {
	queryType = new JComboBox<String>(new QueryTypeChooserModel());
	queryType.setEditable(true);
	queryType.setEnabled(false);
	queryField = new JTextField();
	queryField.addKeyListener(this);
	queryField.getDocument().addDocumentListener(this);
	searchButton = new JButton();
	searchButton.addActionListener(this);
	enableButtons(false);

	buttons = new ButtonGroup();
	radioButtons = new JRadioButton[matchingModes.length];
	for(int i = 0; i < radioButtons.length; i++) {
	    radioButtons[i] = new JRadioButton();
	    buttons.add(radioButtons[i]);
	}
	radioButtons[0].setSelected(true);
	
	progress = new PlaceableProgressBar();
	progress.setTextPlacement(SwingConstants.LEFT);
	statusLayout = new CardLayout(6, 0);
	statusPanel = new JPanel(statusLayout);
	
	progress.setBorderPainted(false);
	
	setLocale(locale);
    }

    /** Layout {@code Components} */
    protected void layoutComponents()
    {
	GridBagConstraints gbc = new GridBagConstraints();
	// gbc.insets.top = 3;

	int queryFieldLength = radioButtons. length + 2;

	gbc.gridx=0;
	gbc.gridy=0;
	gbc.gridwidth = queryFieldLength;
	gbc.weightx = 1.0;
	gbc.fill = GridBagConstraints.BOTH;
	add(queryField, gbc);
	gbc.gridy++;
	
	gbc.gridwidth = 1;
	gbc.weightx = 0.0;
	gbc.gridx=0;
	gbc.fill = GridBagConstraints.NONE;
	gbc.insets.right = 3;
	add(queryType, gbc);
	gbc.gridx++;
	for(int i = 0; i < radioButtons. length ; i++) {
	    add(radioButtons[i], gbc);
	    gbc.gridx++;	    
	}
	gbc.anchor = GridBagConstraints.LINE_END;
	add(searchButton, gbc);
    }

    /** Set size of {@code Components} */
    protected void setComponentsSize()
    {
	Dimension searchSize = 
	    searchButton.getPreferredSize();

	if(queryField != null)
	    setPreferredComponentHeight(queryField, searchSize);

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
	super.setLocale(locale);

	String searchText = ResourceKey.SEARCH;
	String[] matchingModeText = new String[matchingModes.length];
	for (int i = 0; i < matchingModes.length; i++) {
	    matchingModeText[i] = matchingModes[i].toString();
	}

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    searchText = resource.getString(searchText);
	    for (int i = 0; i < matchingModeText.length; i++) {
		matchingModeText[i] = resource.getString(matchingModeText[i]);
		if(matchingModeText[i] == null)
		    matchingModeText[i] = matchingModes[i].toString();
	    }
	}
	catch(MissingResourceException e) {

	}

	searchButton.setText(searchText);

	for (int i = 0; i < matchingModeText.length; i++) {
	    radioButtons[i].setText(matchingModeText[i]);
	}

	if(isAncestorOf(searchButton))
	    setComponentsSize();
	revalidate();
    }

    public void addQueryListener(QueryListener<T> listener)
    {
	if(listener == null)
	    return;

	if(listeners == null)
	    listeners = new EventListenerList();

	listeners.add(QueryListener.class, listener);
    }

    public void removeQueryListener(QueryListener<T> listener)
    {
	if(listener == null || listeners == null)
	    return;

	listeners.remove(QueryListener.class, listener);
    }

    @SuppressWarnings("unchecked")
    protected void fireQueryEvent(QueryEvent<T> queryEvent)
    {
	if(queryEvent == null || listeners == null)
	    return;
	
	Object[] listenersArray = listeners.getListenerList();
	for (int i = listenersArray.length-2; i>=0; i-=2) {
	    if (listenersArray[i]==QueryListener.class) {
		((QueryListener<T>)listenersArray[i+1]).query(queryEvent);
	    }
	}
    }
    
    public void actionPerformed(ActionEvent event)
    {
	Object source = event.getSource();
	if(source == searchButton) {
	    setEnabled(false);
	    // statusLayout.show(statusPanel, PROGRESS);
	    fireQueryEvent(composeQuery());
	    setEnabled(true);
	}
    }

    //ignore properties
    public void changedUpdate(DocumentEvent event) {}

    public void insertUpdate(DocumentEvent event)
    {
	enableButtons(testText(queryField));
    }

    public void removeUpdate(DocumentEvent event) 
    {
	enableButtons(testText(queryField));
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
	   (source == queryField)
	   ) {
	    setEnabled(false);
	    fireQueryEvent(composeQuery());
	    setEnabled(true);
	}
    }	

    protected QueryEvent<T> composeQuery()
    {
	Object selection = queryType.getSelectedItem();
	if(selection == null)
	    return null;
	switch(selection.toString()) {
	case "Agent":
	    return composeAgentQuery();
	case "Pbulication":
	    return composePublicationQuery();
	default:
	    return composeNameUsageQuery();
	}
	
    }

    protected QueryEvent<T> composeAgentQuery()
    {
	return null;
    }

    protected QueryEvent<T> composePublicationQuery()
    {
	return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected QueryEvent<T> composeNameUsageQuery()
    {
	String literal = queryField.getText();
	if(literal == null || literal.length() == 0)
	    return null; // or, all records...?

        Rank rank = null;
	Object selection = rankBox.getSelectedItem();
        if(selection instanceof Rank) {
            //....
	}
	
	MatchingMode matchingMode = null;
	for(int i = 0; i < matchingModes.length; i++) {
	    if(radioButtons[i].isSelected())
		matchingMode = matchingModes[i];
	}
	return new NameUsageQueryEvent(this, new NameUsageQueryParameter(literal, rank, matchingMode));
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


    //public ObjectExchanger getQueryManager()
    public QueryManager<T, ObjectExchanger<T>> getQueryManager()
    {
	return queryManager;
    }

    public void setQueryManager(QueryManager<T, ObjectExchanger<T>> queryManager)
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
    }

    public void stateChanged(ChangeEvent event)
    {
    }

    public void run()
    {
	if(queryManager == null)
	    return ;

	progress.setStringPainted(true);
	progress.setIndeterminate(true);
	progress.setTextColor(null);
	String name = null;
	if(queryField != null)
	    name = queryField.getText();
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
    }
}
    

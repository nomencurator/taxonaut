/*
 * AbstractQueryPanel.java:  a class provides layouted fields for query to 
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

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.vocabulary.QueryPanelElement;

/**
 * {@code AbstractQueryPanel} provides components to specify a filter to search data sources.
 * It dispatches a {@code QueryEvent} representing a query filter.
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractQueryPanel<T extends NamedObject<?>>
    extends JPanel
    implements ActionListener,
	       DocumentListener,
	       KeyListener
{
    private static final long serialVersionUID = -3552323994299347004L;

    /* list of {@code EventLIstener}s */
    protected EventListenerList listeners;

    /* field to specify taxon name(s) */
    protected JTextField queryField;

    /* width of {@code queryField} in number of grids */
    protected int queryFieldGridWidth;

    /* button to search */
    protected JButton searchButton;

    protected PlaceableProgressBar progress;

    protected JPanel statusPanel;

    protected CardLayout statusLayout;

    /**
     * Constract a {@code AbstractQueryPanel} using VM default locale
     */
    protected AbstractQueryPanel()
    {
	this(Locale.getDefault());
    }

    /**
     * Constract a {@code AbstractQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    protected AbstractQueryPanel(Locale locale)
    {
	this(Locale.getDefault(), null);
    }
    /**
     * Constract a {@code AbstractQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    protected AbstractQueryPanel(QueryListener<T> listener)
    {
	this(Locale.getDefault(), listener);
    }

    /**
     * Constract a {@code AbstractQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    protected AbstractQueryPanel(Locale locale, QueryListener<T> listener)
    {
	super(new GridBagLayout());
	queryFieldGridWidth = createComponents();
	setLocale(locale);
	setComponentsSize();
	layoutComponents();
	if(listener != null)
	    addQueryListener(listener);
    }

    /**
     * Creates {@code Components}.  It returns number of created componets
     * other than {@code queryFiedl}.  expecting to be used to layout these components.
     *
     * @return number of created componets other than {@code queryFiedl}
     *
     */
    protected int createComponents()
    {
	queryField = new JTextField();
	queryField.addKeyListener(this);
	queryField.getDocument().addDocumentListener(this);

	int gridWidth = 0;

	searchButton = new JButton();
	searchButton.addActionListener(this);
	enableButtons(false);
	gridWidth++;

	return gridWidth;
    }

    /** Layout {@code Components} */
    protected abstract void layoutComponents();

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

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    searchText = resource.getString(searchText);
	}
	catch(MissingResourceException e) {

	}
	searchButton.setText(searchText);

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

    protected abstract QueryEvent<T> composeQuery();

    protected void enableButtons(boolean enable)
    {
	searchButton.setEnabled(enable);
    }

}
    

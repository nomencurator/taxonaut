/*
 * NameUsageQueryPanel.java:  a class provides layouted fields for query to
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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.net.URL;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
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

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.model.RankModel;

import org.nomencurator.gui.vocabulary.QueryPanelElement;

/**
 * {@code NameUsageQueryPanel} provides components to specify a filter to search data sources.
 * It dispatches a {@code QueryEvent} representing a query filter.
 *
 * @version 	27 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameUsageQueryPanel<T extends NameUsage<?>>
    extends RankQueryPanel< T>
    implements ItemListener
{
    private static final long serialVersionUID = -6396795868029423073L;

    /* a {@code ButtonGroup} orgnaising {@code QueryMode} options */
    protected ButtonGroup matchingModeButtons;

    protected static MatchingMode[] matchingModes = MatchingMode.values();

    protected static String matchingModePrefix = MatchingMode.getResourcePrefix();

    /* radio buttons to specify a {@code QueryMode} */
    protected JRadioButton[] matchingModeRadioButtons;

    protected Component matchingModesFiller;

    /* separator for basionym area */
    protected JSeparator basionymSeparator;

    /* label for basionym control area */
    protected JLabel basionymLabel;

    /* checkbox to enable basionym extensions */
    protected JCheckBox basionymCheckBox;

    /* separator for synonym area */
    protected JSeparator synonymSeparator;

    /* label for synonym control area */
    protected JLabel synonymLabel;

    /* checkbox to enable synonym extensions */
    protected JCheckBox synonymCheckBox;

    protected JSeparator vernacularSeparator;

    /* label for vernacular name search */
    protected JLabel vernacularLabel;

    /* checkbox to search also vernacular names */
    protected JCheckBox vernacularCheckBox;

    protected LocaleChooser localeChooser;

    protected Component localeFiller;

    protected JPanel queryPanel;

    protected JPanel filterPanel;

    protected JPanel rangePanel;

    /**
     * Constract a {@code NameUsageQueryPanel} using VM default locale
     */
    public NameUsageQueryPanel()
    {
	this(Locale.getDefault());
    }

    /**
     * Constract a {@code NameUsageQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public NameUsageQueryPanel(Locale locale)
    {
	this(Locale.getDefault(), null);
    }
    /**
     * Constract a {@code NameUsageQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public NameUsageQueryPanel(QueryListener<T> listener)
    {
	this(Locale.getDefault(), listener);
    }

    /**
     * Constract a {@code NameUsageQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public NameUsageQueryPanel(Locale locale, QueryListener<T> listener)
    {
	super(locale, listener);
    }

    @Override
    protected int createComponents()
    {
	int gridWidth = super.createComponents();

	filterPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
	rangePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
	queryPanel = new JPanel();

	int maxGridWidth = gridWidth - 1;
	gridWidth = 0;

	matchingModeButtons = new ButtonGroup();
	matchingModeRadioButtons = new JRadioButton[matchingModes.length];
	for(int i = 0; i < matchingModeRadioButtons.length; i++) {
	    matchingModeRadioButtons[i] = new JRadioButton();
	    matchingModeButtons.add(matchingModeRadioButtons[i]);
	    gridWidth++;
	}
	matchingModeRadioButtons[0].setSelected(true);

	matchingModesFiller = Box.createGlue();
	gridWidth++;

	if (gridWidth > maxGridWidth)
	    maxGridWidth = gridWidth;
	gridWidth = 0;

	basionymCheckBox = new JCheckBox();
	gridWidth++;

	basionymLabel = new JLabel();
	gridWidth++;

	synonymCheckBox = new JCheckBox();
	gridWidth++;

	synonymLabel = new JLabel();
	gridWidth++;

	vernacularCheckBox = new JCheckBox();
	gridWidth++;

	vernacularLabel = new JLabel();
	gridWidth++;

	localeChooser = new LocaleChooser();
	localeChooser.setEnabled(false);
	gridWidth++;

	vernacularCheckBox.addItemListener(this);

	if(basionymCheckBox != null 
	   || synonymCheckBox != null 
	   || vernacularCheckBox != null) {
	    basionymSeparator = new JSeparator(SwingConstants.VERTICAL);
	    //gridWidth++;

	    if (basionymCheckBox != null
		&& (synonymCheckBox != null 
		    || vernacularCheckBox != null)
		) {
		synonymSeparator = new JSeparator(SwingConstants.VERTICAL);
		//		gridWidth++;
	    }

	    if(synonymCheckBox != null
	       && vernacularCheckBox != null) {
		vernacularSeparator = new JSeparator(SwingConstants.VERTICAL);
		//		gridWidth++;
	    }

	    localeFiller = Box.createGlue();
	    gridWidth++;
	}

	// for the search button
	gridWidth++;

	if (gridWidth > maxGridWidth)
	    maxGridWidth = gridWidth;

	return maxGridWidth;
    }

    /** Layout {@code Components} */
    protected void layoutComponents()
    {
	queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.LINE_AXIS));
	filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));
	rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.LINE_AXIS));
	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	add(queryPanel);
	add(new JSeparator(SwingConstants.HORIZONTAL));
	add(filterPanel);
	add(new JSeparator(SwingConstants.HORIZONTAL));
	add(rangePanel);

	queryPanel.add(queryField);
	queryPanel.add(searchButton);

	for(JRadioButton button  : matchingModeRadioButtons) {
	    rangePanel.add(button);
	}
	rangePanel.add(matchingModesFiller);

	filterPanel.add(rankBox);

	if (basionymSeparator != null) {
	    filterPanel.add(Box.createGlue());
	    basionymSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
	    filterPanel.add(basionymSeparator);
	}

	if(basionymCheckBox != null) {
	    filterPanel.add(basionymCheckBox);
	    filterPanel.add(basionymLabel);
	}

	if (synonymSeparator != null) {
	    filterPanel.add(Box.createGlue());
	    synonymSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
	    filterPanel.add(synonymSeparator);
	    // filterPanel.add(Box.createGlue());
	}

	if(synonymCheckBox != null) {
	    filterPanel.add(synonymCheckBox);
	    filterPanel.add(synonymLabel);
	}

	if(vernacularSeparator != null) {
	    filterPanel.add(Box.createGlue());
	    vernacularSeparator.setAlignmentX(Component.CENTER_ALIGNMENT);
	    filterPanel.add(vernacularSeparator);
	    //filterPanel.add(Box.createGlue());
	}

	if(vernacularCheckBox != null) {
	    filterPanel.add(vernacularCheckBox);
	    filterPanel.add(vernacularLabel);
	    filterPanel.add(Box.createGlue());
	    localeChooser.setAlignmentX(Component.RIGHT_ALIGNMENT);
	    filterPanel.add(localeChooser);
	}
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	String[] matchingModeText = new String[matchingModes.length];
	for (int i = 0; i < matchingModes.length; i++) {
	    matchingModeText[i] = matchingModePrefix + matchingModes[i].toString();
	}

	String basionymLabelText = "BASIONYM_LABEL";
	String synonymLabelText = "SYNONYM_LABEL";
	String vernacularLabelText = "VERNACULAR_LABEL";

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    for (int i = 0; i < matchingModeText.length; i++) {
		matchingModeText[i] = resource.getString(matchingModeText[i]);
		if(matchingModeText[i] == null)
		    matchingModeText[i] = matchingModes[i].toString();
	    }

	    basionymLabelText = resource.getString(basionymLabelText);
	    if (basionymLabelText == null)
		basionymLabelText = "basionym";
	    synonymLabelText = resource.getString(synonymLabelText);
	    if (synonymLabelText == null)
		synonymLabelText = "synonym";
	    vernacularLabelText = resource.getString(vernacularLabelText);
	    if (vernacularLabelText == null)
		vernacularLabelText = "vernacular names";
	}
	catch(MissingResourceException e) {

	}



	for (int i = 0; i < matchingModeText.length; i++) {
	    matchingModeRadioButtons[i].setText(matchingModeText[i]);
	}

	if (basionymLabel != null)
	    basionymLabel.setText(basionymLabelText);
	if (synonymLabel != null)
	    synonymLabel.setText(synonymLabelText);
	if (vernacularLabel != null)
	    vernacularLabel.setText(vernacularLabelText);

	localeChooser.setLocale(locale);

	super.setLocale(locale);
    }

    protected synchronized QueryEvent<T> composeQuery()
    {
	QueryEvent<T> event = super.composeQuery();
	if (event.getQueryParameter() != null) {
	    MatchingMode matchingMode = null;
	    for(int i = 0; i < matchingModes.length; i++) {
		if(matchingModeRadioButtons[i].isSelected()) {
		    matchingMode = matchingModes[i];
		    break;
		}
	    }
	    event.getQueryParameter().setMatchingMode(matchingMode);
	    NameUsageQueryParameter<T> parameter = (NameUsageQueryParameter<T>)event.getQueryParameter();
	    if (basionymCheckBox != null)
		parameter.setIncludeBasionyms(basionymCheckBox.isSelected());
	    if (synonymCheckBox != null)
		parameter.setIncludeSynonyms(synonymCheckBox.isSelected());
	    if (vernacularCheckBox != null)
		parameter.setIncludeVernaculars(vernacularCheckBox.isSelected());
	    if (localeChooser != null)
		parameter.setLocale(localeChooser.getSelectedItem());
	}
	return event;
    }

    public void itemStateChanged(ItemEvent event) {
	if(event == null)
	    return;

	if (vernacularCheckBox == event.getSource()) {
	    localeChooser.setEnabled(vernacularCheckBox.isSelected());
	}
    }

    protected void enableButtons(boolean enable)
    {
	super.enableButtons(enable);
    }
}


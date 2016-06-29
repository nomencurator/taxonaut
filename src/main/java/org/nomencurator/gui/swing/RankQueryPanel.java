/*
 * RankQueryPanel.java:  a class provides layouted fields for query to 
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

//import javax.swing.text.Dcoument;

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
 * {@code RankQueryPanel} provides components to specify a filter to search data sources.
 * It dispatches a {@code QueryEvent} representing a query filter.
 *
 * @version 	24 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class RankQueryPanel<N extends NameUsage<?, ?>, T extends N>
    extends AbstractQueryPanel<N, T>
{
    private static final long serialVersionUID = 8532843090203758723L;

    /* a {@code JComboBox} to select target object type or rank of name usages */
    protected JComboBox<Rank> rankBox;

    /**
     * Constract a {@code RankQueryPanel} using VM default locale
     */
    public RankQueryPanel()
    {
	this(Locale.getDefault());
    }

    /**
     * Constract a {@code RankQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public RankQueryPanel(Locale locale)
    {
	this(Locale.getDefault(), null);
    }
    /**
     * Constract a {@code RankQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public RankQueryPanel(QueryListener<N, T> listener)
    {
	this(Locale.getDefault(), listener);
    }

    /**
     * Constract a {@code RankQueryPanel}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    public RankQueryPanel(Locale locale, QueryListener<N, T> listener)
    {
	super(locale, listener);
    }

    @Override
    protected int createComponents()
    {
	int gridWidth = super.createComponents();

	//RankModel rankModel = RankModel.getRankModel();
	RankModel rankModel = new RankModel();
	//rankModel.insertElementAt("", 0);
	//rankModel.setSelectedItem("");
	rankBox = new JComboBox<Rank>(rankModel);
	rankBox.setEditable(true);
	rankBox.setEnabled(true);
	gridWidth++;
	
	return gridWidth;
    }

    /** Layout {@code Components} */
    protected void layoutComponents()
    {
	GridBagConstraints gbc = new GridBagConstraints();
	// gbc.insets.top = 3;

	gbc.gridx=0;
	gbc.gridy=0;
	gbc.gridwidth = queryFieldGridWidth;
	gbc.weightx = 1.0;
	gbc.fill = GridBagConstraints.BOTH;
	add(queryField, gbc);
	gbc.gridy++;
	
	gbc.gridwidth = 1;
	gbc.weightx = 0.0;
	gbc.gridx=0;
	gbc.fill = GridBagConstraints.NONE;
	gbc.anchor = GridBagConstraints.LINE_START;
	gbc.insets.right = 3;
	add(rankBox, gbc);

	gbc.gridx++;
	gbc.anchor = GridBagConstraints.LINE_END;
	add(searchButton, gbc);
    }

    //FIXME: type parameters of NameUsageQueryParameter
    @SuppressWarnings("rawtypes")
    protected QueryEvent<N, T> composeQuery()
    {
	String literal = queryField.getText();
	if(literal == null || literal.length() == 0)
	    return null; // or, all records...?

	Rank rank = null;
	Object selection = rankBox.getSelectedItem();

	if(selection instanceof Rank) {
	    rank = (Rank)selection;
	    if(rank == RankModel.UNSPECIFIED)
		rank = null;
	}
	else if (selection instanceof String) {
	    String str = (String)selection;
	    if(str.length() > 0)
		rank = Rank.get(str);
	}
	else {
	    rank = Rank.get(selection.toString());
	}
	return new NameUsageQueryEvent<N, T>(this, new NameUsageQueryParameter<N, T>(literal, rank, null));
    }

}
    

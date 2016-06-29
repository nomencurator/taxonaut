/*
 * RankChooser.java:  a JCombobox to choose a Rank
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
 * <CODE>RankChooser</CODE> provides JComboBox to choose a <tt>Rank</tt>.
 *
 * @version 	26 June. 2016
 * @author 	Nozomi `James' Ytow
 */
public class RankChooser
    extends JComboBox<Rank>
{
    private static final long serialVersionUID = 387196212884190052L;

    /**
     * Constract a <CODE>RankChooser</CODE> using VM default locale
     */
    public RankChooser()
    {
	super(new RankModel());
    }

    @Override
    public Rank getSelectedItem()
    {
	Rank rank = null;

	Object selection = super.getSelectedItem();

	if(selection instanceof Rank) {
	    rank = (Rank)selection;
	    if(rank == RankModel.UNSPECIFIED)
		rank = null;
	}
	else if (selection instanceof String) {
	    String item = (String)selection;
	    if(item.length() > 0)
		rank = Rank.get(item);
	}
	else {
	    rank = Rank.get(selection.toString());
	}
	return rank;
    }

}
    

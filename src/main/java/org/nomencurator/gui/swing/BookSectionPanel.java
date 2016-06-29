/*
 * BookSectionPanel.java:  a java based GUI for Nomencurator
 *
 * Copyright (c) 2004, 2015, 2016 Nozomi `James' Ytow
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.model.CitationModel;

/**
 * {@code BookSectionPanel} provides {@code CitationPanel}
 * for GUI to access to a {@code Publication} object
 * as an article
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Publication
 * @see org.nomencurator.gui.swing.CitationPanel
 * @see org.nomencurator.gui.swing.model.CitationModel
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class BookSectionPanel
    extends BookPanel
{
    private static final long serialVersionUID = 2978760484717308219L;

    public BookSectionPanel(CitationModel model,
			    Locale locale)
    {
	super(model, locale);
    }

    /**
     * Creates components of this {@code CitationPanel}
     */
    protected void createComponents()
    {
	super.createComponents();
	createContentsTitle();
    }

    /**
     * Layouts components of this {@code CiationPanel}
     */
    protected int layoutComponents(int gridy)
    {
	GridBagLayout layout =
	    (GridBagLayout)getLayout();
	GridBagConstraints constraints =
	    new GridBagConstraints();
	constraints.gridy = gridy;

	layoutComponent(contentsLabel, contentsTitle, layout, constraints);

	return super.layoutComponents(++constraints.gridy);
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

	super.setLocale(locale);

	String bookTitleText =
	    ResourceKey.BOOK_SECTION_CITATION_LABEL;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);

	    bookTitleText = resource.getString(bookTitleText);
	}
	catch(MissingResourceException e) {

	}

	if(citationLabel != null)
	    citationLabel.setText(bookTitleText);

	getLayout().layoutContainer(this);
	revalidate();
    }


}

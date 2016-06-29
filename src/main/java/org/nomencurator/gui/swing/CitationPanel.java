/*
 * CitationPanel.java:  a java based GUI for Nomencurator
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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JButton;
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

import org.nomencurator.gui.swing.NamedObjectPanel;

import org.nomencurator.gui.swing.model.CitationModel;

/**
 * {@code CitationPanel} provides an abstract class
 * for GUI to access to a {@code Publication} object,
 * especially citation related data
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Publication
 * @see org.nomencurator.gui.swing.model.CitationModel
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class CitationPanel
    extends JPanel
{
    private static final long serialVersionUID = -4395457754012981425L;

    public static int proceedingPaddingWidth = 12;

    public static int succeedingPaddingWidth = 6;

    /** {@code CitationModel} behind an instance of {@code CitationPanel} */
    protected CitationModel citation;

    /** {@code JTextField} replesenting varbatim list of authors */
    protected JTextField authors;

    /** {@code JTextField} replesenting varbatim list of authors' affiliation */
    protected JTextField affiliations;

    /** {@code JTextField} replesenting doi string if the Publication has */
    protected JTextField doi;

    /** {@code JTextField} replesenting ISBN or ISSN number if unique to the Publication */
    protected JTextField isxn;

    /** {@code JTextField} replesenting title of Publication to be appeared as citation information, e.g. journal title in case of articles */
    protected JTextField citationTitle;

    /** {@code JTextField} replesenting title of contents, e.g. title of article in a journal */
    protected JTextField contentsTitle;

    /** {@code JTextField} replesenting year of publication */
    protected JTextField year;

    /** {@code JTextField} replesenting volume number or string if the publication or journal has */
    protected JTextField volume;

    /** {@code JTextField} replesenting issue number in the volume, if given */
    protected JTextField issue;

    /** {@code JTextField} replesenting page number where the publication started */
    protected JTextField firstPage;

    /** {@code JTextField} replesenting page number where the publication ended */
    protected JTextField lastPage;

    /** {@code JTextField} replesenting name of publisher if the publication is a book */
    protected JTextField publisher;

    /** {@code JTextField} replesenting place of publication if relevant */
    protected JTextField place;

    /** {@code JTextField} replesenting received date if given */
    protected JTextField received;

    /** {@code JTextField} replesenting revised date if given */
    protected JTextField revised;

    /** {@code JTextField} replesenting accepted date if given */
    protected JTextField accepted;

    /** {@code JTextField} replesenting accepted date if given */
    protected JTextField published;

    protected CitationPanel(CitationModel model,
			    Locale locale)
    {
	super(new GridBagLayout());
	setModel(model);
	createComponents();
	setLocale(locale);
	layoutComponents();
    }

    public void setModel(CitationModel citation)
    {
	if(this.citation == citation)
	    return;

	this.citation = citation;
    }

    public CitationModel getModel()
    {
	return citation;
    }

    /**
     * Creates components of this {@code CitationPanel}
     */
    abstract protected void createComponents();

    /**
     * Layouts components of this {@code CiationPanel}
     */
    abstract protected int layoutComponents(int gridy);

    /**
     * Layouts components of this {@code CiationPanel}
     */
    protected int layoutComponents()
    {
	return layoutComponents(0);
    }

    /**
     * Creates {@code JTextField} using {@code model}
     *
     * @param model {@code Document} to be used by the
     * {@code JTextField}
     *
     * @return {@code JTextField} using {@code model}
     * as its model
     */
    protected JTextField createTextField(Document model, int columns)
    {
	JTextField textField = null;
	if(model != null)  {
	    try { 
		textField = new JTextField(model, model.getText(0, model.getLength()), columns);
	    }
	    catch (BadLocationException e) {
	    }
	}
	if(textField == null)
	    textField = new JTextField(columns);

	return textField;
    }

    /** Creates {@code JTextField} replesenting varbatim list of authors */
    protected void createAuthors()
    {
	authors = createTextField(citation.getAuthors(), 20);
    }

    /** Creates {@code JTextField} replesenting varbatim list of authors' affiliation */
    protected void createAffiliations()
    {
	affiliations = createTextField(citation.getAffiliations(), 20);
    }


    /** Creates {@code JTextField} replesenting doi string */
    protected void createDoi()
    {
	doi = createTextField(citation.getDoi(), 10);
	doiLabel = new JLabel();
    }

    /** Creates {@code JTextField} replesenting ISBN or ISSN number */
    protected void createIsxn()
    {
	isxn = createTextField(citation.getISXN(), 15);
	isxnLabel = new JLabel();
    }

    /**
     * Creates {@code JTextField} replesenting title of Publication
     * to be appeared as citation information,
     * e.g. journal title in case of articles
     */
    protected void createCitationTitle()
    {
	citationTitle = createTextField(citation.getCitationTitle(), 20);
	citationLabel = new JLabel();
    }


    /** Creates {@code JTextField} replesenting title of contents, e.g. title of article in a journal */
    protected void createContentsTitle()
    {
	contentsTitle = createTextField(citation.getContentsTitle(), 20);
	contentsLabel = new JLabel();
    }


    /** Creates {@code JTextField} replesenting year of publication */
    protected void createYear()
    {
	year = createTextField(citation.getYear(), 4);
	yearLabel = new JLabel();
    }


    /** Creates {@code JTextField} replesenting volume number or string if the publication or journal has */
    protected void createVolume()
    {
	volume = createTextField(citation.getVolume(), 4);
	volumeLabel = new JLabel();
    }


    /** Creates {@code JTextField} replesenting issue number in the volume, if given */
    protected void createIssue()
    {
	issue = createTextField(citation.getIssue(), 4);
	issueLabel = new JLabel();
    }


    /** Creates {@code JTextField} replesenting page number where the publication started */
    protected void createFirstPage()
    {
	firstPage = createTextField(citation.getFirstPage(), 5);
	firstPageLabel = new JLabel();
    }


    /** Creates {@code JTextField} replesenting page number where the publication ended */
    protected void createLastPage()
    {
	lastPage = createTextField(citation.getLastPage(), 5);
	lastPageLabel = new JLabel();
    }


    /** Creates {@code JTextField} replesenting name of publisher if the publication is a book */
    protected void createPublisher()
    {
	publisher = createTextField(citation.getPublisher(), 10);
	publisherLabel =  new JLabel();
    }


    /** Creates {@code JTextField} replesenting place of publication if relevant */
    protected void createPlace()
    {
	place = createTextField(citation.getPlace(), 20);
	placeLabel =  new JLabel();
    }


    /** Creates {@code JTextField} replesenting received date if given */
    protected void createReceived()
    {
	received = createTextField(citation.getReceived(), 10);
	receivedLabel =  new JLabel();
    }


    /** Creates {@code JTextField} replesenting revised date if given */
    protected void createRevised()
    {
	revised = createTextField(citation.getRevised(), 10);
	revisedLabel =  new JLabel();
    }


    /** Creates {@code JTextField} replesenting accepted date if given */
    protected void createAccepted()
    {
	accepted = createTextField(citation.getAccepted(), 10);
	acceptedLabel =  new JLabel();
    }


    /** Creates {@code JTextField} replesenting accepted date if given */
    protected void createPublished ()
    {
	published = createTextField(citation.getPublished(), 10);
	publishedLabel =  new JLabel();
    }

    /** {@code JLabel} for varbatim list of authors */
    protected JLabel authorsLabel;

    /** {@code JLabel} for varbatim list of authors' affiliation */
    protected JLabel affiliationsLabel;

    /** {@code JLabel} for doi string if the Publication has */
    protected JLabel doiLabel;

    /** {@code JLabel} for ISBN or ISSN number if unique to the Publication */
    protected JLabel isxnLabel;

    /** {@code JLabel} for title of Publication to be appeared as citation information, e.g. journal title in case of articles */
    protected JLabel citationLabel;

    /** {@code JLabel} for title of contents, e.g. title of article in a journal */
    protected JLabel contentsLabel;

    /** {@code JLabel} for year of publication */
    protected JLabel yearLabel;

    /** {@code JLabel} for volume number or string if the publication or journal has */
    protected JLabel volumeLabel;

    /** {@code JLabel} for issue number in the volume, if given */
    protected JLabel issueLabel;

    /** {@code JLabel} for page number where the publication started */
    protected JLabel firstPageLabel;

    /** {@code JLabel} for page number where the publication ended */
    protected JLabel lastPageLabel;

    /** {@code JLabel} for name of publisher if the publication is a book */
    protected JLabel publisherLabel;

    /** {@code JLabel} for place of publication if relevant */
    protected JLabel placeLabel;

    /** {@code JLabel} for received date if given */
    protected JLabel receivedLabel;

    /** {@code JLabel} for revised date if given */
    protected JLabel revisedLabel;

    /** {@code JLabel} for accepted date if given */
    protected JLabel acceptedLabel;

    /** {@code JLabel} for accepted date if given */
    protected JLabel publishedLabel;

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

	String contentsText =
	    ResourceKey.CONTENT_LABEL;
	String citationText =
	    ResourceKey.CONTENT_LABEL;
	String yearText =
	    ResourceKey.PUBLICATION_YEAR;
	String volumeText =
	    ResourceKey.VOLUME;
	String issueText =
	    ResourceKey.ISSUE;
	String firstPageText =
	    ResourceKey.FIRST_PAGE;
	String lastPageText =
	    ResourceKey.LAST_PAGE;
	String receivedText =
	    ResourceKey.RECEIVED;
	String revisedText =
	    ResourceKey.REVISED;
	String acceptedText =
	    ResourceKey.ACCEPTED;
	String publishedText =
	    ResourceKey.PUBLISHED;
	String doiText =
	    ResourceKey.DOI;
	String issnText =
	    ResourceKey.ISSN;
	String isbnText =
	    ResourceKey.ISBN;
	String publisherText =
	    ResourceKey.PUBLISHER;
	String placeText =
	    ResourceKey.PLACE;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);

	    contentsText = resource.getString(contentsText);
	    citationText = resource.getString(citationText);
	    yearText = resource.getString(yearText);
	    volumeText = resource.getString(volumeText);
	    issueText = resource.getString(issueText);
	    firstPageText = resource.getString(firstPageText);
	    lastPageText = resource.getString(lastPageText);
	    receivedText = resource.getString(receivedText);
	    revisedText = resource.getString(revisedText);
	    acceptedText = resource.getString(acceptedText);
	    publishedText = resource.getString(publishedText);
	    publisherText = resource.getString(publisherText);
	    placeText = resource.getString(placeText);
	    doiText = resource.getString(doiText);
	    issnText = resource.getString(issnText);
	    isbnText = resource.getString(isbnText);
	}
	catch(MissingResourceException e) {

	}

	if(doiLabel != null)
	    doiLabel.setText(doiText);

	if(citationLabel != null)
	    citationLabel.setText(citationText);

	if(contentsLabel != null)
	    contentsLabel.setText(contentsText);

	if(yearLabel != null)
	    yearLabel.setText(yearText);
	if(volumeLabel != null)
	    volumeLabel.setText(volumeText);
	if(issueLabel != null)
	    issueLabel.setText(issueText);
	if(firstPageLabel != null)
	    firstPageLabel.setText(firstPageText);
	if(lastPageLabel != null)
	    lastPageLabel.setText(lastPageText);
	if(publisherLabel != null)
	    publisherLabel.setText(publisherText);
	if(placeLabel != null)
	    placeLabel.setText(placeText);
	if(receivedLabel != null)
	    receivedLabel.setText(receivedText);
	if(revisedLabel != null)
	    revisedLabel.setText(revisedText);
	if(acceptedLabel != null)
	    acceptedLabel.setText(acceptedText);
	if(publishedLabel != null)
	    publishedLabel.setText(publishedText);

	getLayout().layoutContainer(this);
	revalidate();
    }


    /**
     * Returns gridwidth to be used to layout components
     *
     * @return int representing gridwidth
     */
    abstract protected int getGridwidth();

    /**
     * Layouts {@code component} to {@code container}
     * using {@code layout} under {@code constaraints}
     */
    protected void layoutComponent(Component component,
				   GridBagLayout layout,
				   GridBagConstraints constraints)
    {
	/*
	layout.setConstraints(component, constraints);
	add(component);
	*/
	NamedObjectPanel.layoutComponent(this, component, layout, constraints);
    }

    /**
     * Layouts {@code component} to {@code container}
     * with {@code label} using {@code layout}
     * under {@code constaraints}
     */
    protected boolean layoutComponent(JLabel label,
				   Component component,
				   GridBagLayout layout,
				   GridBagConstraints constraints)
    {
	/*
	return layoutComponent(label, component,
			       layout, constraints,
			       getGridwidth(),
			       GridBagConstraints.HORIZONTAL,
			       0, succeedingPaddingWidth);
	*/
	return NamedObjectPanel.layoutComponent(this, label, component,
						layout, constraints,
						getGridwidth());
    }

    /**
     * Layouts {@code component} to {@code container}
     * with {@code label} using {@code layout}
     * under {@code constaraints}
     */
    protected boolean layoutComponent(JLabel label,
				      Component component,
				      GridBagLayout layout,
				      GridBagConstraints constraints,
				      int width,
				      int filling,
				      int proceedingPadding,
				      int succeedingPadding)
    {
	/*
	if(label == null ||
	   component == null)
	    return false;

	int gridwidth = constraints.gridwidth;
	int fill = constraints.fill;
	int anchor = constraints.anchor;

	if(proceedingPadding > 0)
	    layoutComponent(Box.createHorizontalStrut(proceedingPadding), layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_END;
	layoutComponent(label, layout, constraints);
	if(succeedingPadding > 0)
	    layoutComponent(Box.createHorizontalStrut(succeedingPadding), layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_START;
	constraints.gridwidth = width;
	constraints.fill = filling;
	layoutComponent(component, layout, constraints);

	constraints.anchor = anchor;
	constraints.fill = fill;
	constraints.gridwidth = gridwidth;

	return true;
	*/
	return NamedObjectPanel.layoutComponent(this, label, component, layout, 
						constraints, width, filling,
						proceedingPadding,
						succeedingPadding);
						
    }

    /**
     * Layouts {@code Component}s for year, volume, issue, etc.
     */
    /**
     * Layouts {@code Component}s for year, volume, issue, etc.
     */
    protected boolean layoutYearEtc(GridBagLayout layout,
				 GridBagConstraints constraints)
    {
	int gridwidth = constraints.gridwidth;
	int fill = constraints.fill;
	int anchor = constraints.anchor;
	boolean layouted = false;

	if(yearLabel != null && year != null)
	    layouted |= 
		layoutComponent(yearLabel, year,
				layout, constraints,
				constraints.gridwidth,
				constraints.fill,
				0, succeedingPaddingWidth);

	if(volumeLabel != null && volume != null)
	    layouted |= 
		layoutComponent(volumeLabel, volume,
				layout, constraints,
				constraints.gridwidth,
				constraints.fill,
				proceedingPaddingWidth,
				succeedingPaddingWidth);

	if(issueLabel != null && issue != null)
	    layouted |= 
		layoutComponent(issueLabel, issue,
				layout, constraints,
				constraints.gridwidth,
				constraints.fill,
				proceedingPaddingWidth,
				succeedingPaddingWidth);

	if(firstPageLabel != null && firstPage != null)
	    layouted |= 
		layoutComponent(firstPageLabel, firstPage,
				layout, constraints,
				constraints.gridwidth,
				constraints.fill,
				proceedingPaddingWidth,
				succeedingPaddingWidth);

	if(lastPageLabel != null && lastPage != null)
	    layouted |= 
		layoutComponent(lastPageLabel, lastPage,
				layout, constraints,
				constraints.gridwidth,
				constraints.fill,
				0, 0);

	constraints.anchor = anchor;
	constraints.fill = fill;
	constraints.gridwidth = gridwidth;

	return layouted;
    }

}



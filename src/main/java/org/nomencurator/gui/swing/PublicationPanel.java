/*
 * PublicationPanel.java:  a java based GUI for Nomencurator
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

import java.awt.event.MouseEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.EventObject;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.nomencurator.model.Agent;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.Person;
import org.nomencurator.model.Publication;
import org.nomencurator.model.NamedObject;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.model.CitationModel;

import org.nomencurator.gui.swing.table.AuthorTableModel;
import org.nomencurator.gui.swing.table.AppearanceTableModel;
import org.nomencurator.gui.swing.table.NamedObjectTableModel;


/**
 * {@code PublicationPanel} provides a GUI to access to
 * a {@code Publication} object
 *
 * @version 	28 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class PublicationPanel
    extends NamedObjectPanel<Publication>
    implements ItemListener 
{
    private static final long serialVersionUID = 3540750600235814384L;

    protected CitationModel model;

    /** {@code Box} to display components for authors' attributes */
    protected Box authorsBorder;

    /** {@code JTextField} to display verbatim authors string */
    protected JTextArea authors;

    /** {@code JTable} to display a list of authors */
    protected JTable authorsList;

    /** {@code JScrollPane} to scroll a list of authors */
    protected JScrollPane authorsPane;

    protected AuthorPanel authorPanel;

    protected String authorDialogTitle;

    /** {@code JPanel} to indicate {@code affiliations} field */
    protected JPanel affiliationsBorder;

    /** {@code JTextField} to display verbatim affiliations string */
    protected JScrollPane affiliationsPane;

    /** {@code JTextField} to display verbatim affiliations string */
    protected JTextArea affiliations;

    /** {@code JPanel} to put {@code Border} of {@code publications} */
    protected Box citationBorder;

    /** {@code JComboBox} to select type of the publication */
    protected JComboBox<String> publicationType;

    /** {@code JPanel} to manage switching between publication type dependent GUI */
    protected JPanel publicationPane;

    protected JPanel article;

    protected JPanel book;

    /** {@code JTable} of taxa recognised by the {@code Author} */
    protected JTable taxa;

    protected JScrollPane taxaPane;

    /** {@code JPanel} to put  {@code Border} of {@code taxa} */
    protected JPanel taxaBorder;

    protected AppearancePanel appearancePanel;

    protected String taxaDialogTitle;

    public static String[] publicationTypes =
	ResourceKey.getPublicationTypeKeys();

    /**
     * Creates a {@code PublicationPanel} for a new
     * {@code Publication} using default Locale
     */
    public PublicationPanel()
    {
	this(new Publication());
    }

    /**
     * Creates a {@code PublicationPanel} for
     * {@code publication} using default Locale
     *
     * @param publication {@code Publication} to be handled by this 
     * {@code PublicationPanel}
     *
     */
    public PublicationPanel(Publication publication)
    {
	this(publication, Locale.getDefault());
    }

    /**
     * Creates a {@code PublicationPanel} for a new
     * {@code Publication} using {@code locale}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code PublicationPanel}
     */
    public PublicationPanel(Locale locale)
    {
	this(new Publication(), locale);
    }

    /**
     * Creates a {@code PublicationPanel} for
     * {@code publication} using {@code locale}
     *
     * @param publication {@code Publication} to be handled by this 
     * {@code PublicationPanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code PublicationPanel}
     */
    public PublicationPanel(Publication publication,
		       Locale locale)
    {
	this(publication, locale, false);
    }

    /**
     * Creates a {@code PublicationPanel} for
     * {@code publication} using {@code locale}
     *
     * @param publication {@code Publication} to be handled by this 
     * {@code PublicationPanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code PublicationPanel}
     */
    public PublicationPanel(Publication publication,
		       Locale locale, boolean isDialog)
    {
	super(publication, locale, isDialog);
    }


    /**
     * Creates main components of this {@code NamedObjectPanel}
     */
    protected void createContents()
    {
	super.createContents();

	authors = new JTextArea();
	NamedObjectTableModel<?> tableModel = new AuthorTableModel();
	authorsList = new ViewportSizedTable(tableModel);
	int columns = tableModel.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    authorsList.getColumn(tableModel.getColumnName(i)).setCellEditor(this);
	}
	authorsPane = new JScrollPane(authorsList);
	authorsPane.addMouseListener(this);
	authorsBorder = new Box(BoxLayout.Y_AXIS);
	authorsBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	authorsBorder.add(authors);
	authorsBorder.add(authorsPane);
	authorsBorder.addMouseListener(this);

	affiliations = new JTextArea();
	affiliationsPane = new JScrollPane(affiliations,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	affiliationsBorder = new JPanel();
	affiliationsBorder.add(affiliationsPane);
	affiliationsBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));

	publicationType = new JComboBox<String>();
	publicationType.addItemListener(this);

	CardLayout layout = new CardLayout();
	publicationPane = 
	    new JPanel(layout);

	if(model == null)
	    model = new CitationModel();
	publicationPane.add(new ArticlePanel(model, getLocale()), ResourceKey.JOURNAL_ARTICLE);
	publicationPane.add(new BookPanel(model, getLocale()), ResourceKey.BOOK);
	publicationPane.add(new BookSectionPanel(model, getLocale()), ResourceKey.BOOK_SECTION);
	publicationPane.add(new BookPanel(model, getLocale()), ResourceKey.EDITED_BOOK);
	publicationPane.add(new ManuscriptPanel(model, getLocale()), ResourceKey.MANUSCRIPT);
	publicationPane.add(new MagazinePanel(model, getLocale()), ResourceKey.MAGAZINE_ARTICLE);
	publicationPane.add(new ConferenceProceedingsPanel(model, getLocale()), ResourceKey.CONFERENCE_PROCEEDINGS);

	citationBorder = new Box(BoxLayout.Y_AXIS);
	citationBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	citationBorder.add(publicationType);
	citationBorder.add(publicationPane);

	tableModel = new AppearanceTableModel();
	taxa = new ViewportSizedTable(tableModel);
	columns = tableModel.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    taxa.getColumn(tableModel.getColumnName(i)).setCellEditor(this);
	}
	taxaPane = new JScrollPane(taxa);
	taxaPane.addMouseListener(this);
	taxaBorder = new JPanel();
	taxaBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	taxaBorder.add(taxaPane);
	taxaBorder.addMouseListener(this);
    }

    protected void layoutComponents()
    {
	contents.add(authorsBorder);
	//contents.add(affiliationsBorder);
	contents.add(citationBorder);
	contents.add(taxaBorder);

	super.layoutComponents();
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

	String authorsText = ResourceKey.AUTHORS;
	String affiliationsText = ResourceKey.AFFILIATIONS;
	String citationText = ResourceKey.CITATION;
	String taxaText = ResourceKey.TAXA;

	try {

	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    authorsText =
		resource.getString(authorsText);
	    affiliationsText = 
		resource.getString(affiliationsText);
	    citationText = 
		resource.getString(citationText);
	    taxaText =
		resource.getString(taxaText);

	    publicationType.removeAllItems();
	    for(int i = 0; i < publicationTypes.length; i++) {
		String type = 
		    resource.getString(publicationTypes[i]);
		publicationType.addItem(type);
	    }
	}
	catch(MissingResourceException e) {

	}

	TitledBorder border = null;
	if(authorsBorder != null) {
	    if(authorsText == null)
		authorsText = ResourceKey.AUTHORS;
	    border = 
		(TitledBorder)authorsBorder.getBorder();
	    border.setTitle(authorsText);
	}

	if(affiliationsBorder != null) {
	    if(affiliationsText == null)
		affiliationsText = ResourceKey.AFFILIATIONS;
	    border = 
		(TitledBorder)affiliationsBorder.getBorder();
	    border.setTitle(affiliationsText);
	}

	if(citationBorder != null) {
	    if(citationText == null)
		citationText = ResourceKey.CITATION;
	    border = 
		(TitledBorder)citationBorder.getBorder();
	    border.setTitle(citationText);
	}
	if(taxaBorder != null) {
	    if(taxaText == null)
		taxaText = ResourceKey.TAXA;
	    border = 
		(TitledBorder)taxaBorder.getBorder();
	    border.setTitle(taxaText);
	}

    }

    /**
     * Sets specified {@code object} to display
     *
     * @param object {@code NamedObject} to be displaied
     */
    public void setNamedObject(Publication object)
    {
	super.setNamedObject(object);
	if(model == null)
	    model = new CitationModel();
	model.setPublication(object);
    }


    protected Publication duplicate()
    {
	return new Publication(getNamedObject());
    }

    public void itemStateChanged(ItemEvent e)
    {
	if(ItemEvent.SELECTED != e.getStateChange() ||
	   publicationType != e.getSource())
	    return;
	((CardLayout)publicationPane.getLayout()).show(publicationPane, 
						       publicationTypes[publicationType.getSelectedIndex()]);
    }

    /**
     * Creates a new {@code NamedObject}
     *
     * @return NamedObject created
     */
    protected Publication createNamedObject()
    {
	return new Publication();
    }

    /**
     * Sets values in GUI components to the 
     * {@code NamedObject} if the {@code namedObject} is
     * not null and it is modified.
     *
     * @return true if values were set, otherwise false in 
     */
    protected boolean setValues()
    {
	if(!super.setValues())
	    return false;

	return true;
    }

    /**
     * Load values of the {@code NamedObject} to
     * GUI components if the {@code namedObject} is
     * not null.
     *
     * @return true if values were loaded, otherwise false 
     */
    protected boolean loadValues()
    {
	if(!super.setValues())
	    return false;

	return true;
    }

    protected AuthorPanel createAuthorPanel()
    {
	AuthorPanel authorPanel =
	    new AuthorPanel();
	authorPanel.setModified(true);
	return authorPanel;
    }

    protected AppearancePanel createAppearancePanel()
    {
	AppearancePanel appearancePanel =
	    new AppearancePanel();
	appearancePanel.setModified(true);
	return appearancePanel;
    }

    public void mouseClicked(MouseEvent event)
    {
	showEditorPane(event);
    }

    public boolean isCellEditable(EventObject event)
    {
	return showEditorPane(event);
    }

    @SuppressWarnings("unchecked")
    protected boolean showEditorPane(EventObject event)
    {
	JTable table = null;
	String title = null;
	NamedObjectPanel<?> dialog = null;
	Object source = event.getSource();
	if(source == authorsList ||
	   source == authorsPane ||
	   source == authorsBorder) {
	    table = authorsList;
	    title = authorDialogTitle;
	    if(authorPanel == null)
		authorPanel = createAuthorPanel();
	    else if (source != authorsList)
		authorPanel.setNamedObject(new Person());
	    dialog = authorPanel;
	}
	else if (source == taxa ||
		 source == taxaPane ||
		 source == taxaBorder) {
	    table = taxa;
	    title = taxaDialogTitle;
	    if(appearancePanel == null)
		appearancePanel = createAppearancePanel();
	    else if (source != taxa)
		appearancePanel.setNamedObject(new Appearance(getNamedObject()));
	    dialog = appearancePanel;
	}

	if(table != null) {
	    if(showOptionDialog(table, dialog, title, event) &&
	       source != authorsList && source != taxa) {
		((NamedObjectTableModel)table.getModel()).add(dialog.getNamedObject());
	    }
	}
	return false;
    }

}


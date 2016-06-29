/*
 * AuthorPanel.java:  an abstract class providing a GUI to
 * NamedObject of Nomencurator
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.MouseEvent;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.nomencurator.model.Agent;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.NamedObject;
import org.nomencurator.model.Person;
import org.nomencurator.model.Publication;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.table.AffiliationTableModel;
import org.nomencurator.gui.swing.table.NamedObjectTableModel;
import org.nomencurator.gui.swing.table.NameUsageTableModel;
import org.nomencurator.gui.swing.table.ObjectTableModel;
import org.nomencurator.gui.swing.table.PublicationTableModel;

/**
 * {@code AuthorPanel} provides a GUI to access to
 * a {@code Author} object
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Author
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class AuthorPanel
    extends NamedObjectPanel<Person>
{
    private static final long serialVersionUID = -5666489790478304531L;

    /** {@code JPanel} to display components for author's attributes */
    protected JPanel authorAttributes;

    /** {@code JLabel} to indicate surname contents */
    protected JLabel surnameLabel;

    /** {@code JComboBox} to display and edit surname prefix */
    protected JComboBox<String> surnamePrefix;

    /** {@code Border} of {@code surnamePrefix} */
    protected Border surnamePrefixBorder;

    /**  {@code JTextField} to display and edit surname */
    protected JTextField surname;

    /** {@code Border} of {@code surname} */
    protected Border surnameBorder;

    /**  {@code JComboBox} to display and edit surname epithet */
    protected JComboBox<String> surnameEpithet;

    /** {@code Border} of {@code surnameEpithet} */
    protected Border surnameEpithetBorder;

    /** {@code JLabel} to indicate first name contents */
    protected JLabel firstNameLabel;

    /**  {@code JTextField} to display and edit first name */
    protected JTextField firstName;

    /** {@code Border} of {@code firstName} */
    protected Border firstNameBorder;

    /** {@code JLabel} to indicate middle names contents */
    protected JLabel middleNamesLabel;

    /**  {@code JTextField} to display and edit middle names */
    protected JTextField middleNames;

    /** {@code Border} of {@code middleNames} */
    protected Border middleNamesBorder;

    /** {@code JLabel} to indicate title contents */
    protected JLabel titleLabel;

    /**  {@code JTextField} to display and edit title */
    protected JComboBox<String> title;

    /** {@code Border} of {@code title} */
    protected Border titleBorder;

    /** {@code JLabel} to indicate birth contents */
    protected JLabel birthLabel;

    /**  {@code JTextField} to display and edit birthday */
    protected JTextField birth;

    /** {@code Border} of {@code birth} */
    protected Border birthBorder;

    /** {@code JLabel} to indicate death contents */
    protected JLabel deathLabel;

    /**  {@code JTextField} to display and edit date of death */
    protected JTextField death;

    /** {@code Border} of {@code death} */
    protected Border deathBorder;

    /** {@code JTable} of affilations of the {@code Person} */
    protected JTable affiliations;

    /** {@code JScrollPane} of affilations of the {@code Person} */
    protected JScrollPane affiliationsPane;

    /** {@code Box} to put {@code Border} of {@code affiliations} */
    protected Box affiliationsBorder;

    /** {@code JTable} of publications by the {@code Person} */
    protected JTable publications;

    /** {@code JScrollPane} to scroll {@code pulbications} */
    protected JScrollPane publicationsPane;

    /** {@code JPanel} to put {@code Border} of {@code publications} */
    protected Box publicationsBorder;

    protected PublicationPanel publicationPanel;

    protected String publicationDialogTitle;

    /** {@code JTable} of taxa recognised by the {@code Person} */
    protected JTable taxa;

    /** {@code JScrollPane} to scroll {@code taxa} */
    protected JScrollPane taxaPane;

    /** {@code Box} to put  {@code Border} of {@code taxa} */
    protected Box taxaBorder;

    protected NameUsagePanel nameUsagePanel;

    protected String nameUsageDialogTitle;

    /**
     * Creates a {@code AuthorPanel} for a new
     * {@code Person} using default Locale
     */
    public AuthorPanel()
    {
	this(new Person());
    }

    /**
     * Creates a {@code AuthorPanel} for
     * {@code author} using default Locale
     *
     * @param author {@code Person} to be handled by this 
     * {@code AuthorPanel}
     *
     */
    public AuthorPanel(Person author)
    {
	this(author, Locale.getDefault());
    }

    /**
     * Creates a {@code AuthorPanel} for a new
     * {@code Person} using {@code locale}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AuthorPanel}
     */
    public AuthorPanel(Locale locale)
    {
	this(new Person(), locale);
    }

    /**
     * Creates a {@code AuthorPanel} for
     * {@code author} using {@code locale}
     *
     * @param author {@code Person} to be handled by this 
     * {@code AuthorPanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AuthorPanel}
     */
    public AuthorPanel(Person author,
		       Locale locale)
    {
	super(author, locale, false);
    }

    /**
     * Creates a {@code AuthorPanel} for
     * {@code author} using {@code locale}
     *
     * @param author {@code Person} to be handled by this 
     * {@code AuthorPanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AuthorPanel}
     */
    public AuthorPanel(Person author,
		       Locale locale,
		       boolean isDialog)
    {
	super(author, locale, isDialog);
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	ComponentOrientation previousOrientation = 
	    getComponentOrientation();

	super.setLocale(locale);

	String surnameText = ResourceKey.SURNAME;
	String firstNameText = ResourceKey.FIRST_NAME;
	String middleNamesText = ResourceKey.MIDDLE_NAMES;
	String titleText = ResourceKey.TITLE;
	String birthText = ResourceKey.BIRTH;
	String deathText = ResourceKey.DEATH;
	String affiliationsText = ResourceKey.AFFILIATIONS;
	String publicationsText = ResourceKey.PUBLICATIONS;
	String taxaText = ResourceKey.TAXA;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    
	    surnameText =
		resource.getString(surnameText);
	    firstNameText = 
		resource.getString(firstNameText);
	    middleNamesText = 
		resource.getString(middleNamesText);
	    titleText = 
		resource.getString(titleText);
	    birthText = 
		resource.getString(birthText);
	    deathText = 
		resource.getString(deathText);
	    affiliationsText = 
		resource.getString(affiliationsText);
	    publicationsText =
		resource.getString(publicationsText);
	    taxaText =
		resource.getString(taxaText);
	}
	catch(MissingResourceException e) {

	}

	if(surnameLabel != null) {
	    if(surnameText == null)
		surnameText = ResourceKey.SURNAME;
	    surnameLabel.setText(surnameText);
	}

	if(firstNameLabel != null) {
	    if(firstNameText == null)
		firstNameText = ResourceKey.FIRST_NAME;
	    firstNameLabel.setText(firstNameText);
	}

	if(middleNamesLabel != null) {
	    if(middleNamesText == null)
		middleNamesText = ResourceKey.MIDDLE_NAMES;
	    middleNamesLabel.setText(middleNamesText);
	}

	if(titleLabel != null) {
	    if(titleText == null)
		titleText = ResourceKey.TITLE;
	    titleLabel.setText(titleText);
	}

	if(birthLabel != null) {
	    if(birthText == null)
		birthText = ResourceKey.BIRTH;
	    birthLabel.setText(birthText);
	}

	if(deathLabel != null) {
	    if(deathText == null)
		deathText = ResourceKey.DEATH;
	    deathLabel.setText(deathText);
	}

	TitledBorder border = null;
	if(affiliationsBorder != null) {
	    if(affiliationsText == null)
		affiliationsText = ResourceKey.AFFILIATIONS;
	    border = 
		(TitledBorder)affiliationsBorder.getBorder();
	    border.setTitle(affiliationsText);
	}

	if(publicationsBorder != null) {
	    if(publicationsText == null)
		publicationsText = ResourceKey.PUBLICATIONS;
	    border = 
		(TitledBorder)publicationsBorder.getBorder();
	    border.setTitle(publicationsText);
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
     * Creates main components of this {@code NamedObjectPanel}
     */
    protected void createContents()
    {
	super.createContents();

	authorAttributes = 
	    new JPanel(new GridBagLayout());
	surnameLabel = new JLabel();
	surnameLabel.setBorder(labelBorder);
	surnameEpithet = new JComboBox<String>();
	surnameEpithet.addItem("");
	surnameEpithet.addItem("Jr");
	surnameEpithet.addItem("Sen");
	surname = new JTextField(15);
	surnamePrefix = new JComboBox<String>();

	firstNameLabel = new JLabel();
	firstNameLabel.setBorder(labelBorder);
	firstName = new JTextField(15);

	middleNamesLabel = new JLabel();
	middleNamesLabel.setBorder(labelBorder);
	middleNames = new JTextField(15);

	titleLabel = new JLabel();
	titleLabel.setBorder(labelBorder);
	title = new JComboBox<String>();
	title.addItem("");
	title.addItem("Sir");
	title.addItem("Lord");

	birthLabel = new JLabel();
	birthLabel.setBorder(labelBorder);
	birth = new JTextField(10);

	deathLabel = new JLabel();
	deathLabel.setBorder(labelBorder);
	death = new JTextField(10);

	ObjectTableModel<?> objectModel = new AffiliationTableModel();
	affiliations = new ViewportSizedTable(objectModel);
	int columns = objectModel.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    affiliations.getColumn(objectModel.getColumnName(i)).setCellEditor(this);
	}
	affiliationsPane = new JScrollPane(affiliations);
	affiliationsPane.addMouseListener(this);
	/*
	affiliationsBorder = new Box(BoxLayout.Y_AXIS);
	affiliationsBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	affiliationsBorder.add(affiliationsPane);
	affiliationsBorder.addMouseListener(this);
	*/

	NamedObjectTableModel<?> model = new PublicationTableModel();
	publications = new ViewportSizedTable(model);
	columns = model.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    publications.getColumn(model.getColumnName(i)).setCellEditor(this);
	}
	publicationsPane = new JScrollPane(publications);
	/*
	publicationsBorder = new Box(BoxLayout.Y_AXIS);
	publicationsBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	publicationsBorder.add(publicationsPane);
	*/

	model = new NameUsageTableModel();
	taxa = new ViewportSizedTable(model);
	columns = model.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    taxa.getColumn(model.getColumnName(i)).setCellEditor(this);
	}
	taxaPane = new JScrollPane(taxa);
	taxaPane.addMouseListener(this);
	/*
	taxaBorder = new Box(BoxLayout.Y_AXIS);
	taxaBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	taxaBorder.add(taxaPane);
	taxaBorder.addMouseListener(this);
	*/
	createRelevantTabbedPane();
	//relevantTabs.append(taxaPane);
	relevantTabs.add(publicationsPane);
	relevantTabs.add(affiliationsPane);
    }

    /**
     * Layouts components of this {@code NamedObjectPanel}
     */
    protected void layoutComponents()
    {
	GridBagLayout layout =
	    (GridBagLayout)authorAttributes.getLayout();
	GridBagConstraints constraints =
	    new GridBagConstraints();
	constraints.gridy = 0;
	constraints.anchor = GridBagConstraints.LINE_END;
	layoutComponent(authorAttributes, titleLabel, layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_START;
	layoutComponent(authorAttributes, title, layout, constraints);
	constraints.gridy++;
	constraints.anchor = GridBagConstraints.LINE_END;
	layoutComponent(authorAttributes, firstNameLabel, layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_START;
	layoutComponent(authorAttributes, firstName, layout, constraints);
	constraints.gridy++;
	constraints.anchor = GridBagConstraints.LINE_END;
	layoutComponent(authorAttributes, middleNamesLabel, layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_START;
	layoutComponent(authorAttributes, middleNames, layout, constraints);
	constraints.gridy++;
	constraints.anchor = GridBagConstraints.LINE_END;
	layoutComponent(authorAttributes, surnameLabel, layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_START;
	//layoutComponent(authorAttributes, surnamePrefix, layout, constraints);
	layoutComponent(authorAttributes, surname, layout, constraints);
	layoutComponent(authorAttributes, surnameEpithet, layout, constraints);
	constraints.gridy++;
	constraints.anchor = GridBagConstraints.LINE_END;
	layoutComponent(authorAttributes, birthLabel, layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_START;
	layoutComponent(authorAttributes, birth, layout, constraints);
	constraints.gridy++;
	constraints.anchor = GridBagConstraints.LINE_END;
	layoutComponent(authorAttributes, deathLabel, layout, constraints);
	constraints.anchor = GridBagConstraints.LINE_START;
	layoutComponent(authorAttributes, death, layout, constraints);
	/*
	constraints.gridy++;
	constraints.gridwidth = GridBagConstraints.REMAINDER;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	layoutComponent(authorAttributes, affiliations, layout, constraints);
	constraints.gridy++;
	layoutComponent(authorAttributes, publications, layout, constraints);
	constraints.gridy++;
	layoutComponent(authorAttributes, taxa, layout, constraints);
	*/

	contents.add(authorAttributes);

	//contents.add(affiliationsBorder);
	//contents.add(publicationsBorder);
	contents.add(relevantTabsPane);
	super.layoutComponents();
    }

    protected Person duplicate()
    {
	return new Person(getNamedObject());
    }

    /**
     * Sets editable state to {@code editable}
     * and takes care of components
     *
     * @param editable true to be editable
     */
    protected void setEditable(boolean editable)
    {
	Border b;
	super.setEditable(editable);

	if(titleBorder == null)
	    titleBorder = BorderFactory.createEmptyBorder();
	b = title.getBorder();
	title.setBorder(titleBorder);
	titleBorder = b;
	title.setEditable(editable);
	title.setEnabled(editable);

	if(firstNameBorder == null)
	    firstNameBorder = BorderFactory.createEmptyBorder();
	b = firstName.getBorder();
	firstName.setBorder(firstNameBorder);
	firstNameBorder = b;
	firstName.setEditable(editable);

	if(middleNamesBorder == null)
	    middleNamesBorder = BorderFactory.createEmptyBorder();
	b = middleNames.getBorder();
	middleNames.setBorder(middleNamesBorder);
	middleNamesBorder = b;
	middleNames.setEditable(editable);

	if(surnameBorder == null)
	    surnameBorder = BorderFactory.createEmptyBorder();
	b = surname.getBorder();
	surname.setBorder(surnameBorder);
	surnameBorder = b;
	surname.setEditable(editable);

	if(birthBorder == null)
	    birthBorder = BorderFactory.createEmptyBorder();
	b = birth.getBorder();
	birth.setBorder(birthBorder);
	birthBorder = b;
	birth.setEditable(editable);

	if(deathBorder == null)
	    deathBorder = BorderFactory.createEmptyBorder();
	b = death.getBorder();
	death.setBorder(deathBorder);
	deathBorder = b;
	death.setEditable(editable);
	
    }

    /**
     * Creates a new {@code NamedObject}
     *
     * @return NamedObject created
     */
    protected Person createNamedObject()
    {
	return new Person();
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

    protected PublicationPanel createPublicationPanel()
    {
	PublicationPanel publicationPanel =
	    new PublicationPanel();
	publicationPanel.setModified(true);
	return publicationPanel;
    }

    public void mouseClicked(MouseEvent event)
    {
	showEditorPane(event);
    }

    public boolean isCellEditable(EventObject event)
    {
	Object source = event.getSource();
	if(source == affiliations) {
	    return false;
	}
	return showEditorPane(event);
    }

    @SuppressWarnings("unchecked")
    protected boolean showEditorPane(EventObject event)
    {
	JTable table = null;
	String title = null;
	NamedObjectPanel<?> dialog = null;
	Object source = event.getSource();
	if (source == publications ||
	    source == publicationsPane ||
	    source == publicationsBorder) {
	    table = publications;
	    title = publicationDialogTitle;
	    if(publicationPanel == null)
		publicationPanel = createPublicationPanel();
	    else if (source != publications) {
		publicationPanel.setNamedObject(new Publication());
	    }
	    dialog = publicationPanel;
	}
	else if (source == taxa ||
		 source == taxaPane ||
		 source == taxaBorder) {
	    table = taxa;
	    title = nameUsageDialogTitle;
	    if(nameUsagePanel == null) {
		nameUsagePanel = new NameUsagePanel();
		nameUsagePanel.setModified(true);
	    }
	    else if (source != taxa) {
		nameUsagePanel.setNamedObject(new DefaultNameUsageNode());
	    }
	    dialog = nameUsagePanel;
	}

	if(table != null) {
	    if(showOptionDialog(table, dialog, title, event) &&
	       source != publications && source != taxa) {
		((NamedObjectTableModel)table.getModel()).add(dialog.getNamedObject());
	    }
	}

	return false;
    }
}

/*
 * AppearancePanel.java:  an abstract class providing a GUI to
 * NamedObject of Nomencurator
 *
 * Copyright (c) 2004, 2006, 2015, 2016 Nozomi `James' Ytow
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
import java.awt.event.MouseListener;

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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.Publication;

import org.nomencurator.gui.swing.table.AnnotationTableModel;
import org.nomencurator.gui.swing.table.NamedObjectTableModel;
import org.nomencurator.gui.swing.table.NameUsageTableModel;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code AppearancePanel} provides a GUI to access to
 * a {@code Author} object
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Author
 *
 * @version 	28 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class AppearancePanel
    extends NamedObjectPanel<Appearance>
    implements MouseListener
{
    private static final long serialVersionUID = 7964297286210274985L;

    /** {@code JPanel} to display citation information */
    protected JPanel citationBorder;

    protected JTextField citation;

    protected JTextField page;

    protected JLabel pageLabel;

    protected JTextField lines;

    protected JLabel linesLabel;

    protected JTextField figure;

    protected JLabel figureLabel;

    /** {@code JTextField} to see and edit appearance */
    protected JTextArea appearance;

    /** {@code JScrollPane} to show {@code appearance} */
    protected JScrollPane appearancePane;

    /** {@code Box} to display appearance */
    protected Box appearanceBorder;

    /** {@code JTable} of {@code NameUsage}s encoded in the {@code Appearance} */
    protected JTable taxa;

    /** {@code JScrollPane} to show {@code taxa} */
    protected JScrollPane taxaPane;

    /** {@code Box} to display {@code NameUsage}s */
    protected Box taxaBorder;

    protected String nameUsageText;

    protected NameUsagePanel nameUsagePanel;

    /** {@code JTable} of {@code Annotation}s encoded in the {@code Appearance} */
    protected JTable annotations;

    /** {@code JScrollPane} to show {@code annotations} */
    protected JScrollPane annotationPane;

    /** {@code Box} to display {@code Annotation}s */
    protected Box annotationBorder;

    protected AnnotationPanel annotationPanel;

    protected String annotationText;

    protected PublicationPanel publicationPanel;

    /**
     * Creates a {@code AppearancePanel} for a new
     * {@code Appearance} using default Locale
     */
    public AppearancePanel()
    {
	this(new Appearance());
    }

    /**
     * Creates a {@code AppearancePanel} for
     * {@code appearance} using default Locale
     *
     * @param appearance {@code Appearance} to be handled by this 
     * {@code AppearancePanel}
     *
     */
    public AppearancePanel(Appearance appearance)
    {
	this(appearance, Locale.getDefault());
    }

    /**
     * Creates a {@code AppearancePanel} for a new
     * {@code Appearance} using {@code locale}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AppearancePanel}
     */
    public AppearancePanel(Locale locale)
    {
	this(new Appearance(), locale);
    }

    /**
     * Creates a {@code AppearancePanel} for
     * {@code appearance} using {@code locale}
     *
     * @param appearance {@code Appearance} to be handled by this 
     * {@code AppearancePanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AppearancePanel}
     */
    public AppearancePanel(Appearance appearance,
		       Locale locale)
    {
	this(appearance, locale, false);
    }

    /**
     * Creates a {@code AppearancePanel} for
     * {@code appearance} using {@code locale}
     *
     * @param appearance {@code Appearance} to be handled by this 
     * {@code AppearancePanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AppearancePanel}
     */
    public AppearancePanel(Appearance appearance,
		       Locale locale, boolean isDialog)
    {
	super(appearance, locale, isDialog);
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

	String citationText = ResourceKey.CITATION;
	String pageText = ResourceKey.PAGE;
	String linesText = ResourceKey.LINES;
	String appearanceText = ResourceKey.APPEARANCE;
	String midleNamesText = ResourceKey.MIDDLE_NAMES;
	String taxaText = ResourceKey.TAXA;
	annotationText = ResourceKey.ANNOTATION;
	String annotationsText = ResourceKey.ANNOTATIONS;
	nameUsageText = ResourceKey.NAME_USAGE;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    
	    citationText =
		resource.getString(citationText);
	    pageText =
		resource.getString(pageText);
	    linesText =
		resource.getString(linesText);
	    appearanceText =
		resource.getString(appearanceText);
	    annotationText =
		resource.getString(annotationText);
	    annotationsText =
		resource.getString(annotationsText);
	    taxaText =
		resource.getString(taxaText);
	    nameUsageText =
		resource.getString(nameUsageText);
	}
	catch(MissingResourceException e) {

	}

	if(pageLabel != null)
	    pageLabel.setText(pageText);
	if(linesLabel != null)
	    linesLabel.setText(linesText);

	TitledBorder border = null;
	if(citationBorder != null) {
	    if(citationText == null)
		citationText = ResourceKey.CITATION;
	    border = 
		(TitledBorder)citationBorder.getBorder();
	    border.setTitle(citationText);
	}

	if(appearanceBorder != null) {
	    if(appearanceText == null)
		appearanceText = ResourceKey.APPEARANCE;
	    border = 
		(TitledBorder)appearanceBorder.getBorder();
	    border.setTitle(appearanceText);
	}

	
	if(relevantTabs != null) {
	    if(taxaText == null)
		taxaText = ResourceKey.TAXA;
	    if(taxaPane != null)
	    relevantTabs.setTitleAt(relevantTabs.indexOfComponent(taxaPane),
				    taxaText);
	    if(annotationPane != null)
	    relevantTabs.setTitleAt(relevantTabs.indexOfComponent(annotationPane),
				    annotationText);

	    
	}

	/*
	if(taxaBorder != null) {
	    if(taxaText == null)
		taxaText = ResourceKey.TAXA;
	    border = 
		(TitledBorder)taxaBorder.getBorder();
	    border.setTitle(taxaText);
	}

	if(annotationBorder != null) {
	    if(annotationsText == null)
		annotationsText = ResourceKey.ANNOTATIONS;
	    border = 
		(TitledBorder)annotationBorder.getBorder();
	    border.setTitle(annotationsText);
	}

	if(annotationPanel != null)
	    annotationPanel.setLocale(locale);
	*/
    }

    /**
     * Creates main components of this {@code NamedObjectPanel}
     */
    protected void createContents()
    {
	super.createContents();

	GridBagLayout layout = new GridBagLayout();
	citationBorder = new JPanel(layout);
	citationBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	citation = new JTextField();
	page = new JTextField(6);
	pageLabel = new JLabel();
	lines = new JTextField(7);
	linesLabel = new JLabel();
	/*
	figure = new JTextField();
	figureLabel = new JLabel();
	*/
	GridBagConstraints constraints =
	    new GridBagConstraints();
	constraints.gridy = 0;
	layoutComponent(citationBorder, null,
			citation, layout, constraints, 7);
	constraints.gridy++;
	layoutComponent(citationBorder, pageLabel,
			page, layout, constraints, constraints.gridwidth);
	layoutComponent(citationBorder, linesLabel,
			lines, layout, constraints, constraints.gridwidth,
			GridBagConstraints.HORIZONTAL,
			proceedingPaddingWidth,
			succeedingPaddingWidth);
    
	appearance = new JTextArea();
	appearancePane = 
	    new JScrollPane(appearance,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	appearanceBorder = new Box(BoxLayout.Y_AXIS);
	appearanceBorder.add(appearancePane);
	appearanceBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));

	AnnotationTableModel annotationModel = new AnnotationTableModel();
	annotations = new ViewportSizedTable(annotationModel);
	annotations.addMouseListener(this);
	//annotationPanel = new AnnotationPanel();
	//annotationPanel.setDialogSource(annotations);
	int columns = annotationModel.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    annotations.getColumn(annotationModel.getColumnName(i)).setCellEditor(this);
	}
	annotationBorder = new Box(BoxLayout.Y_AXIS);
	annotationBorder.addMouseListener(this);
	annotationPane = new JScrollPane(annotations);
	annotationPane.addMouseListener(this);
	/*
	annotationBorder.add(annotationPane);
	annotationBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	*/
	
	NameUsageTableModel model = new NameUsageTableModel();
	taxa = new ViewportSizedTable(model, 10);
	taxa.addMouseListener(this);
	//nameUsagePanel.setDialogSource(annotations);
	columns = model.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    taxa.getColumn(model.getColumnName(i)).setCellEditor(this);
	}
	
	taxaBorder = new Box(BoxLayout.Y_AXIS);
	taxaBorder.addMouseListener(this);
	taxaPane = new JScrollPane(taxa);
	taxaPane.addMouseListener(this);

	createRelevantTabbedPane();
	relevantTabs.add(taxaPane);
	relevantTabs.add(annotationPane);
	/*
	taxaBorder.add(taxaPane);
	taxaBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	*/
    }

    /**
     * Layouts components of this {@code NamedObjectPanel}
     */
    protected void layoutComponents()
    {
	contents.add(citationBorder);
	contents.add(appearanceBorder);
	/*
	contents.add(taxaBorder);
	contents.add(annotationBorder);
	*/
	contents.add(relevantTabsPane);

	super.layoutComponents();
    }

    protected Appearance duplicate()
    {
	return new Appearance(getNamedObject());
    }

    /**
     * Creates a new {@code NamedObject}
     *
     * @return NamedObject created
     */
    protected Appearance createNamedObject()
    {
	return new Appearance();
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
	
	Appearance appearance = getNamedObject();

	if(appearance == null)
	    return true;

	String text = null;
	try {
	    text = page.getText();
	}
	catch (NullPointerException e) {
	    text = null;
	}
	appearance.setPages(text);

	try {
	    text = lines.getText();
	}
	catch (NullPointerException e) {
	    text = null;
	}
	appearance.setLines(text);

	if(publicationPanel != null)
	    appearance.setPublication(publicationPanel.getNamedObject());

	appearance.setNameUsages(((NameUsageTableModel)taxa.getModel()).enumeration());
	appearance.setAnnotations(((AnnotationTableModel)annotations.getModel()).enumeration());

	return true;
    }

    /**
     * Load values of the {@code NamedObject} to
     * GUI components if the {@code namedObject} is
     * not null.
     *
     * @return true if values were loaded, otherwise false 
     */
    @SuppressWarnings("unchecked")
    protected boolean loadValues()
    {
	if(!super.loadValues())
	    return false;

	Appearance appearance = getNamedObject();

	if(appearance == null)
	    return true;

	Publication publication = appearance.getPublication();
	if(publicationPanel != null)
	    publicationPanel.setNamedObject(publication);
	if(publication != null)
	    citation.setText(publication.getSummary());
	else
	    citation.setText(null);

	page.setText(appearance.getPages());
	lines.setText(appearance.getLines());

	((NameUsageTableModel)taxa.getModel()).setObjects(appearance.getNameUsages());
	((AnnotationTableModel)annotations.getModel()).setObjects(appearance.getAnnotations());

	return true;
    }

    /**
     * Sets editable state to {@code editable}
     * and takes care of components
     *
     * @param editable true to be editable
     */
    protected void setEditable(boolean editable)
    {
	/*
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
	*/	
    }

    public void setPublicationPanel(PublicationPanel panel)
    {
	if(publicationPanel == panel)
	    return;

	publicationPanel = panel;
    }

    public PublicationPanel getPublicationPanel()
    {
	return publicationPanel;
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
	if (source == annotations ||
	    source == annotationBorder ||
	    source == annotationPane) {
	    table = annotations;
	    table = annotations;
	    title = annotationText;
	    if(annotationPanel == null)
		annotationPanel = createAnnotationPanel();
	    else if (source != annotations)
		annotationPanel.setNamedObject(new Annotation());
	    dialog = annotationPanel;
	}
	else if (source == taxa ||
		 source == taxaBorder || 
		 source == taxaPane) {
	    table = taxa;
	    title = nameUsageText;
	    if(nameUsagePanel == null) {
		nameUsagePanel = new NameUsagePanel();
		nameUsagePanel.setModified(true);
	    }
	    else if (source != taxa)
		nameUsagePanel.setNamedObject(new DefaultNameUsageNode());
	    dialog = nameUsagePanel;
	}

	if(table != null) {
	    if(showOptionDialog(table, dialog, title, event) &&
	       source != annotations && source != taxa) {
		((NamedObjectTableModel)table.getModel()).add(dialog.getNamedObject());
	    }
	}

	return false;
    }

    public void setTabbedPane(JTabbedPane tabbedPane, PublicationPanel publicationPanel)
    {
	setTabbedPane(tabbedPane);
	setPublicationPanel(publicationPanel);
    }

}

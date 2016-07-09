/*
 * AnnotationPanel.java:  an abstract class providing a GUI to
 * NamedObject of Nomencurator
 *
 * Copyright (c) 2004, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.CellEditor;
import javax.swing.AbstractCellEditor;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.LinkType;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NamedObject;

import org.nomencurator.gui.swing.model.LinkTypeModel;

import org.nomencurator.gui.swing.table.NamedObjectTableModel;
import org.nomencurator.gui.swing.table.AnnotationTableModel;
import org.nomencurator.gui.swing.table.NameUsageTableModel;
import org.nomencurator.gui.swing.table.LinkTypeTableModel;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code AnnotationPanel} provides a GUI to access to
 * a {@code Author} object
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Author
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class AnnotationPanel
    extends NamedObjectPanel<Annotation>
    implements ActionListener,
	       ItemListener,
	       MouseListener,
	       PopupMenuListener
{
    private static final long serialVersionUID = 8202943688805312114L;

    /**
     */
    protected static LinkTypeModel linkTypeModel = new LinkTypeModel();
    
    protected JPanel annotationBorder;
    protected JComboBox<LinkType/*String*/> linkType;
    protected JLabel linkTypeLabel;

    protected JTextField sensu;
    protected JLabel sensuLabel;

    protected JLabel citationLabel;
    protected JTextField citation;

    protected Box annotatorTaxaBorder;
    protected JTable annotatorTaxa;
    protected JScrollPane annotatorPane;

    protected Box annotatedTaxaBorder;
    protected JTable annotatedTaxa;
    protected JScrollPane annotatedPane;

    protected String annotationText;

    protected JButton closeButton;

    protected JScrollPane linkTypePane;

    protected String linkTypeText;
    
    protected String closeText;

    protected Object[] linkTypeButtons;

    protected JTable linkTypeTable;
    protected LinkTypeTableModel linkTypeTableModel;
    protected Timer linkTypeEditTimer;
    protected boolean linkTypeEditorShown;

    protected NameUsagePanel nameUsagePanel;
    protected String annotatorTaxonTitle;
    protected String annotatedTaxonTitle;

    protected AppearancePanel appearancePanel;

    /**
     * Creates a {@code AnnotationPanel} for a new
     * {@code NameUsage} using default Locale
     */
    public AnnotationPanel()
    {
	this(new Annotation());
    }

    /**
     * Creates a {@code AnnotationPanel} for
     * {@code nameusage} using default Locale
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code AnnotationPanel}
     *
     */
    public AnnotationPanel(Annotation annotation)
    {
	this(annotation, Locale.getDefault());
    }

    /**
     * Creates a {@code AnnotationPanel} for a new
     * {@code NameUsage} using {@code locale}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AnnotationPanel}
     */
    public AnnotationPanel(Locale locale)
    {
	this(new Annotation(), locale);
    }

    /**
     * Creates a {@code AnnotationPanel} for
     * {@code nameusage} using {@code locale}
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code AnnotationPanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AnnotationPanel}
     */
    public AnnotationPanel(Annotation annotation,
		       Locale locale)
    {
	this(annotation, locale, false);
    }

    /**
     * Creates a {@code AnnotationPanel} for
     * {@code nameusage} using {@code locale}
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code AnnotationPanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AnnotationPanel}
     */
    public AnnotationPanel(Annotation annotation,
		       Locale locale, boolean isDialog)
    {
	super(annotation, locale, isDialog);
    }

    /**
     * Creates components of this {@code NamedObjectPanel}
     */
    protected void createComponents()
    {
	super.createComponents();
	//closeButton = new JButton();
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

	String citationText = ResourceKey.USED_IN;
	String sensuText = ResourceKey.SENSU;
	linkTypeText = ResourceKey.LINK_TYPE;
	String annotatorTaxaText = ResourceKey.ANNOTATOR_TAXA;
	String annotatedTaxaText = ResourceKey.ANNOTATED_TAXA;
	closeText = ResourceKey.CLOSE;
	annotationText = ResourceKey.ANNOTATION;
	annotatorTaxonTitle = ResourceKey.ANNOTATOR_TAXON;
	annotatedTaxonTitle = ResourceKey.ANNOTATED_TAXON;

	try {
	    ResourceBundle resource = 
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    
	    sensuText =
		resource.getString(sensuText);
	    citationText =
		resource.getString(citationText);
	    linkTypeText =
		resource.getString(linkTypeText);
	    annotatorTaxaText =
		resource.getString(annotatorTaxaText);
	    annotatedTaxaText =
		resource.getString(annotatedTaxaText);
	    closeText =
		resource.getString(closeText);
	    annotationText =
		resource.getString(annotationText);
	    annotatorTaxonTitle =
		resource.getString(annotatorTaxonTitle);
	    annotatedTaxonTitle =
		resource.getString(annotatedTaxonTitle);

	}
	catch(MissingResourceException e) {

	}

	if(linkTypeLabel != null)
	    linkTypeLabel.setText(linkTypeText);
	if(sensuLabel != null)
	    sensuLabel.setText(sensuText);
	if(citationLabel != null)
	    citationLabel.setText(citationText);

	if(closeButton != null) {
	    closeButton.setText(closeText);
	}

	TitledBorder border = null;
	if(annotatorTaxaBorder != null) {
	    if(annotatorTaxaText == null)
		annotatorTaxaText = ResourceKey.ANNOTATOR_TAXA;
	    border = 
		(TitledBorder)annotatorTaxaBorder.getBorder();
	    border.setTitle(annotatorTaxaText);
	}

	if(annotatedTaxaBorder != null) {
	    if(annotatedTaxaText == null)
		annotatedTaxaText = ResourceKey.ANNOTATED_TAXA;
	    border = 
		(TitledBorder)annotatedTaxaBorder.getBorder();
	    border.setTitle(annotatedTaxaText);
	}
    }

    /**
     * Creates main components of this {@code NamedObjectPanel}
     */
    protected void createContents()
    {
	super.createContents();

    	GridBagConstraints constraints =
	    new GridBagConstraints();
	constraints.gridy = 0;

	GridBagLayout layout = new GridBagLayout();
	annotationBorder = new JPanel(layout);

	linkType = new JComboBox<LinkType>(linkTypeModel);
	linkType.setEditable(true);
	linkType.addActionListener(this);
	linkType.addPopupMenuListener(this);
	linkType.addItemListener(this);

	linkTypeEditTimer = new Timer(4500, this);
	linkTypeEditTimer.setRepeats(false);
	linkTypeEditTimer.setDelay(360000);
	linkTypeEditTimer.setCoalesce(true);
	//linkTypeEditTimer.setLogTimers(true);
	linkTypeEditTimer.addActionListener(this);
	linkTypeEditorShown = false;

	linkTypeLabel = new JLabel();
	layoutComponent(annotationBorder, linkTypeLabel,
			linkType, layout, constraints);
	constraints.gridy++;


	/*
	sensu = new JTextField();
	sensuLabel = new JLabel();
	layoutComponent(annotationBorder, sensuLabel,
			sensu, layout, constraints);
	constraints.gridy++;
	*/

	citation = new JTextField();
	citationLabel = new JLabel();
	layoutComponent(annotationBorder, citationLabel,
			citation, layout, constraints);

	annotatorTaxaBorder = new Box(BoxLayout.Y_AXIS);
	annotatorTaxaBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	annotatorTaxaBorder.addMouseListener(this);
	NameUsageTableModel model =new NameUsageTableModel();
	annotatorTaxa = new ViewportSizedTable(model);
	int columns = model.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    annotatorTaxa.getColumn(model.getColumnName(i)).setCellEditor(this);
	}
	annotatorPane = new JScrollPane(annotatorTaxa);
	annotatorPane.addMouseListener(this);
	annotatorTaxaBorder.add(annotatorPane);

	annotatedTaxaBorder = new Box(BoxLayout.Y_AXIS);
	annotatedTaxaBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	annotatedTaxaBorder.addMouseListener(this);
	model = new NameUsageTableModel();
	annotatedTaxa = new ViewportSizedTable(model);
	columns = model.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    annotatedTaxa.getColumn(model.getColumnName(i)).setCellEditor(this);
	}
	annotatedPane = new JScrollPane(annotatedTaxa);
	annotatedPane.addMouseListener(this);
	annotatedTaxaBorder.add(annotatedPane);

	nameUsagePanel = new NameUsagePanel();
	nameUsagePanel.setDialogSource(annotatorTaxa);
    }

    /**
     * Layouts components of this {@code NamedObjectPanel}
     */
    protected void layoutComponents()
    {
	contents.add(annotationBorder);
	contents.add(annotatorTaxaBorder);
	contents.add(annotatedTaxaBorder);

	super.layoutComponents();

	if(buttonsPanel != null &&
	   closeButton != null) {
	    buttonsPanel.add(closeButton);
	}
    }

    protected Annotation duplicate()
    {
	return new Annotation(getNamedObject());
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

    /**
     * Creates a new {@code NamedObject}
     *
     * @return NamedObject created
     */
    protected Annotation createNamedObject()
    {
	return new Annotation();
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

	Annotation annotation = getNamedObject();

	if(annotation == null)
	    return true;

	annotation.setLinkType(((LinkType)linkType.getSelectedItem()).getLinkType());

	if(appearancePanel != null)
	    annotation.setAppearance(appearancePanel.getNamedObject());

	NameUsageTableModel model = 
	    (NameUsageTableModel)annotatorTaxa.getModel();
	model.clear();
	Iterator<NameUsage<?>> nameUsages = annotation.getAnnotators();
	while (nameUsages.hasNext()) {
	    model.add(nameUsages.next());
	}
	
	model = 
	    (NameUsageTableModel)annotatedTaxa.getModel();
	model.clear();
	nameUsages = annotation.getAnnotatants();
	while (nameUsages.hasNext()) {
	    model.add(nameUsages.next());
	}

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
	if(!super.loadValues())
	    return false;

	Annotation annotation = getNamedObject();

	if(annotation == null)
	    return true;

	Appearance appearance = annotation.getAppearance();
	if(appearancePanel != null)
	    appearancePanel.setNamedObject(appearance);
	if(appearance != null)
	    sensu.setText(appearance.getSummary());
	
	String linkTypeName = annotation.getLinkType();
	LinkType linkTypeToSelect = 
	    LinkTypeModel.get(linkTypeName);
	if(linkTypeToSelect == null) {
	    linkTypeToSelect = new LinkType(linkTypeName, "", "");
	    LinkTypeModel.add(linkTypeToSelect);
	}
	linkType.setSelectedItem(linkTypeToSelect);
	annotation.clearAnnotators();
	Iterator<NameUsage<?>> iterator = ((NameUsageTableModel)annotatorTaxa.getModel()).iterator();
	if(iterator != null) {
	    while(iterator.hasNext()) {
		annotation.addAnnotator(iterator.next());
	    }
	}
	
	annotation.clearAnnotatants();
	iterator = ((NameUsageTableModel)annotatedTaxa.getModel()).iterator();
	if(iterator != null) {
	    while(iterator.hasNext()) {
		annotation.addAnnotatant(iterator.next());
	    }
	}

	return true;
    }

    public void setAppearancePanel(AppearancePanel panel)
    {
	if(appearancePanel == panel)
	    return;

	appearancePanel = panel;
    }

    public AppearancePanel getAppearancePanel()
    {
	return appearancePanel;
    }

    public void actionPerformed(ActionEvent event)
    {
	Object source = event.getSource();
	
	if(source == linkType) {
	    if(isShowing() && !linkTypeEditorShown &&
	       linkType.getSelectedIndex() == -1) {
		Object selectedItem = linkType.getSelectedItem();
		if(selectedItem == null)
		    return ;
		LinkType linkTypeToAdd = null;
		if(selectedItem instanceof LinkType)
		    linkTypeToAdd = (LinkType)selectedItem;
		else
		    linkTypeToAdd = new LinkType((String)selectedItem, "", "");
		if(linkTypePane == null)
		    createLinkTypePane();
		linkTypeTableModel.add(linkTypeToAdd);
		if(showLinkTypeDialog() == JOptionPane.OK_OPTION) {
		    linkType.addItem(linkTypeToAdd/*.getLinkType()*/);
		}
	    }
	}
	else if(!linkTypeEditorShown &&
		source == linkTypeEditTimer) {
	    linkTypeEditTimer.stop();
	    if(linkTypePane == null)
		createLinkTypePane();
	    LinkType linkTypeToAdd = new LinkType();
	    linkTypeTableModel.add(linkTypeToAdd);
	    linkTypeEditorShown = true;
	    if(showLinkTypeDialog() == JOptionPane.OK_OPTION) {
		linkType.addItem(linkTypeToAdd/*.getLinkType()*/);
		linkType.setSelectedItem(linkTypeToAdd/*.getLinkType()*/);
	    }
	}
    }

    protected void createLinkTypePane()
    {
	if(linkTypeTableModel == null)
	    linkTypeTableModel = new LinkTypeTableModel();
	linkTypeTable = new ViewportSizedTable(linkTypeTableModel, 5);
	linkTypePane = 
	    new JScrollPane(linkTypeTable);
    }

    protected int showLinkTypeDialog()
    {
	if(linkTypePane == null)
	    createLinkTypePane();
	if(linkTypeButtons == null) {
	}
	return JOptionPane.showOptionDialog(linkType,
					    linkTypePane,
					    linkTypeText,
					    JOptionPane.OK_CANCEL_OPTION,
					    JOptionPane.PLAIN_MESSAGE,
					    null,
					    linkTypeButtons,
					    closeText);
    }

    public void popupMenuCanceled(PopupMenuEvent event)
    {
	linkTypeEditTimer.stop();
	linkTypeEditorShown = false;
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent event)
    {
	linkTypeEditTimer.stop();
	linkTypeEditorShown = false;
    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent event)
    {
	if(!linkTypeEditorShown)
	    linkTypeEditTimer.restart();
    }

    public int getDelay()
    {
	return linkTypeEditTimer.getDelay();
    }

    public void setDelay(int delay)
    {
	linkTypeEditTimer.setDelay(delay);
    }

    public int getInitialDelay()
    {
	return linkTypeEditTimer.getInitialDelay();
    }

    public void setInitialDelay(int delay)
    {
	linkTypeEditTimer.setInitialDelay(delay);
    }

    public void itemStateChanged(ItemEvent event)
    {
	Object source = event.getSource();
	if(source == linkType) {
	    if(linkType.getSelectedIndex() == -1) {
		setModified(true);
	    }
	    else {
		setModified(true);
	    }
	}
    }

    public void mouseClicked(MouseEvent event)
    {
	showEditorPane(event);
    }

    public boolean isCellEditable(EventObject event)
    {
	return showEditorPane(event);
    }

    protected NameUsagePanel createNameUsagePanel()
    {
	NameUsagePanel nameUsagePanel = new NameUsagePanel();
	nameUsagePanel.setModified(true);
	return nameUsagePanel;
    }

    @SuppressWarnings("unchecked")
    protected boolean showEditorPane(EventObject event)
    {
	JTable table = null;
	String title = null;
	Object source = event.getSource();
	if(source == annotatorTaxa) {
	    table = annotatorTaxa;
	    title = annotatorTaxonTitle;
	}
	else if (source == annotatedTaxa) {
	    table = annotatedTaxa;
	    title = annotatedTaxonTitle;
	}
	if(table != null) {
	    if(nameUsagePanel == null)
		nameUsagePanel = createNameUsagePanel();
	    else if(source != annotatorTaxa &&
		    source != annotatedTaxa) {
		nameUsagePanel.setNamedObject(new DefaultNameUsageNode());
	    }
	    if(showOptionDialog(table, nameUsagePanel, title, event) &&
	       source != annotatorTaxa && source != annotatedTaxa) {
		((NamedObjectTableModel)table.getModel()).add(nameUsagePanel.getNamedObject());
	    }
	}
	return false;
    }
}

/*
 * AbstractNameUsagePanel.java:  an abstract class providing a GUI to
 * NamedObject of Nomencurator
 *
 * Copyright (c) 2004, 2006, 2014, 2015 Nozomi `James' Ytow
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

import java.util.Collection;
import java.util.EventObject;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import javax.swing.table.TableModel;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.TableCellEditor;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.NamedObject;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.model.RankModel;

import org.nomencurator.gui.swing.table.NameTableModel;
import org.nomencurator.gui.swing.table.NamedObjectTableModel;
import org.nomencurator.gui.swing.table.NameUsageTableModel;
import org.nomencurator.gui.swing.table.RelevantNameUsageTableModel;

/**
 * {@code AbstractNameUsagePanel} provides a GUI to access to
 * a {@code NameUsage} instance
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.NameUsage
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameUsagePanel<T extends NameUsage<?>>
    extends NamedObjectPanel<T>
    implements ListSelectionListener
{
    private static final long serialVersionUID = -7062028044711547579L;

    /**
     */
    protected JPanel nameBorder;

    protected JComboBox<Locale> locale;
    protected JLabel localeLabel;

    protected JComboBox<Rank> rank;
    protected JLabel rankLabel;

    protected JTextField literal;
    protected JLabel literalLabel;

    protected JTextField sensu;
    protected JLabel sensuLabel;

    protected JLabel citationLabel;
    protected JTextField citation;

    protected JPanel higherTaxonBorder;
    protected JLabel higherTaxonLabel;
    protected JTextField higherTaxon;

    protected JCheckBox incertaeSedis;
    protected JLabel incertaeSedisLabel;

    protected JCheckBox type;
    protected JLabel typeLabel;

    protected JComboBox<String> typeOfType;

    protected Box lowerTaxaBorder;
    protected JScrollPane lowerTaxaPane;
    protected JTable lowerTaxa;

    protected AbstractNameUsagePanel<T> nameUsagePanel;
    protected String higherTaxonTitle;
    protected String lowerTaxonTitle;


    protected Box nameUsagesBorder;
    protected JScrollPane nameUsagesPane;	
    protected JTable nameUsages;

    protected AnnotationPanel annotationPanel;
    protected String annotationTitle;
    
    protected AppearancePanel appearancePanel;

    /**
     * Constructs a {@code AbstractNameUsagePanel} for
     * {@code nameusage} using default Locale
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code AbstractNameUsagePanel}
     *
     */
    protected AbstractNameUsagePanel(T nameUsage)
    {
	this(nameUsage, Locale.getDefault());
    }

    /**
     * Constructs a {@code AbstractNameUsagePanel} for
     * {@code nameusage} using {@code locale}
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code AbstractNameUsagePanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AbstractNameUsagePanel}
     */
    protected AbstractNameUsagePanel(T nameUsage,
		       Locale locale)
    {
	this(nameUsage, locale, false);
    }

    /**
     * Constructs a {@code AbstractNameUsagePanel} for
     * {@code nameusage} using {@code locale}
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code AbstractNameUsagePanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code AbstractNameUsagePanel}
     */
    protected AbstractNameUsagePanel(T nameUsage,
		       Locale locale, boolean isDialog)
    {
	super(nameUsage, locale, isDialog);
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

	String nameBorderText = ResourceKey.NAME;
	String citationText = ResourceKey.USED_IN;
	String localeText = ResourceKey.LOCALE;
	String rankText = ResourceKey.RANK;
	String literalText = ResourceKey.NAME_FIELD;
	String sensuText = ResourceKey.SENSU;
	String higherTaxonText = ResourceKey.HIGHER_TAXON;
	String lowerTaxaText = ResourceKey.LOWER_TAXA;
	String typeText = ResourceKey.TYPE;
	String incertaeSedisText = ResourceKey.INCERTAE_SEDIS;
	String nameUsagesText = ResourceKey.NAME_USAGES;
	annotationTitle = ResourceKey.RELEVANT_TAXA_DIALOG;
	lowerTaxonTitle = ResourceKey.LOWER_TAXON_DIALOG;
	higherTaxonTitle = ResourceKey.HIGHER_TAXON_DIALOG;

	try {
	    ResourceBundle resource = 
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    
	    nameBorderText =
		resource.getString(nameBorderText);
	    citationText =
		resource.getString(citationText);
	    localeText =
		resource.getString(localeText);
	    rankText =
		resource.getString(rankText);
	    literalText =
		resource.getString(literalText);
	    sensuText =
		resource.getString(sensuText);
	    higherTaxonText =
		resource.getString(higherTaxonText);
	    lowerTaxaText =
		resource.getString(lowerTaxaText);
	    typeText =
		resource.getString(typeText);
	    incertaeSedisText =
		resource.getString(incertaeSedisText);
	    nameUsagesText =
		resource.getString(nameUsagesText);
	    annotationTitle =
		resource.getString(annotationTitle);
	    higherTaxonTitle=
		resource.getString(higherTaxonTitle);
	    lowerTaxonTitle =
		resource.getString(lowerTaxonTitle);
	}
	catch(MissingResourceException e) {

	}

	if(localeLabel != null)
	    localeLabel.setText(localeText);
	if(rankLabel != null)
	    rankLabel.setText(rankText);
	if(literalLabel != null)
	    literalLabel.setText(literalText);
	if(sensuLabel != null)
	    sensuLabel.setText(sensuText);
	if(citationLabel != null)
	    citationLabel.setText(citationText);
	if(typeLabel != null)
	    typeLabel.setText(typeText);
	if(incertaeSedisLabel != null)
	    incertaeSedisLabel.setText(incertaeSedisText);


	TitledBorder border = null;

	if(nameBorder != null) {
	    if(nameBorderText == null)
		nameBorderText = ResourceKey.NAMEUSAGE_TAB;
	    border = 
		(TitledBorder)nameBorder.getBorder();
	    border.setTitle(nameBorderText);
	}


	if(nameUsagesBorder != null) {
	    if(nameUsagesText == null)
		nameUsagesText = ResourceKey.NAME_USAGES;
	    border = 
		(TitledBorder)nameUsagesBorder.getBorder();
	    border.setTitle(nameUsagesText);
	}

	if(higherTaxonLabel != null) {
	    higherTaxonLabel.setText(higherTaxonText);
	}

	if(lowerTaxaBorder != null) {
	    if(lowerTaxaText == null)
		lowerTaxaText = ResourceKey.LOWER_TAXA;
	    border = 
		(TitledBorder)lowerTaxaBorder.getBorder();
	    border.setTitle(lowerTaxaText);
	}

	if(relevantTabs != null) {
	    if(lowerTaxaText == null)
		lowerTaxaText = ResourceKey.LOWER_TAXA;
	    if(lowerTaxaPane != null)
	    relevantTabs.setTitleAt(relevantTabs.indexOfComponent(lowerTaxaPane),
				    lowerTaxaText);
	    if(nameUsagesPane != null)
	    relevantTabs.setTitleAt(relevantTabs.indexOfComponent(nameUsagesPane),
				    annotationTitle);

	    
	}
    }

    /**
     * Creates main components of this {@code NamedObjectPanel}
     */
    protected void createContents()
    {
	//super.createContents();

    	GridBagConstraints constraints =
	    new GridBagConstraints();
	constraints.gridy = 0;

	GridBagLayout layout = new GridBagLayout();
	nameBorder = new JPanel(layout);
	nameBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));

	locale = new JComboBox<Locale>();
	localeLabel = new JLabel();
	layoutComponent(nameBorder, localeLabel,
			locale, layout, constraints, 7);
	constraints.gridy++;

	rank = new JComboBox<Rank>(new RankModel());
	rank.setEditable(true);
	rankLabel = new JLabel();
	layoutComponent(nameBorder, rankLabel,
			rank, layout, constraints,
			7/*2*//*constraints.gridwidth*/);

	//layoutComponent(nameBorder, Box.createHorizontalStrut(proceedingPaddingWidth), layout, constraints);

	constraints.gridy++;
	//literal = new JTextField(20);
	literal = new JTextField();
	literalLabel = new JLabel();
	int fill = constraints.fill;
	//constraints.fill = GridBagConstraints.HORIZONTAL;
	/*
	layoutComponent(nameBorder, literalLabel,
			literal, layout, constraints,
			constraints.gridwidth);
	*/
	layoutComponent(nameBorder, literalLabel,
			literal, layout, constraints,
			7/*constraints.gridwidth*/);
	constraints.fill = fill;
	constraints.gridy++;

	sensu = new JTextField();
	sensuLabel = new JLabel();
	layoutComponent(nameBorder, sensuLabel,
			sensu, layout, constraints, 7);
	constraints.gridy++;

	citation = new JTextField();
	citationLabel = new JLabel();
	layoutComponent(nameBorder, citationLabel,
			citation, layout, constraints, 7);

	constraints.gridy++;
	/*
    	constraints =
	    new GridBagConstraints();
	constraints.gridy = 0;

	layout = new GridBagLayout();
	higherTaxonBorder = new JPanel(layout);
	higherTaxonBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	*/
	higherTaxon = new JTextField();
	higherTaxonLabel = new JLabel();
	higherTaxon.addMouseListener(this);


	layoutComponent(nameBorder, higherTaxonLabel,
			higherTaxon, layout, constraints, 7);
	constraints.gridy++;

	type = new JCheckBox();
	typeLabel = new JLabel();
	typeOfType = new JComboBox<String>();
	String[] str = {"","holotyp", "syntype","lectotype", "neotype"};
	typeOfType.setModel(new DefaultComboBoxModel<String>(str));
	int gridx = constraints.gridx;
	int anchor = constraints.anchor;
	constraints.anchor = GridBagConstraints.LINE_START;
	constraints.gridx = 2;
	layoutComponent(nameBorder, type, layout, constraints);
	constraints.gridx++;
	layoutComponent(nameBorder, typeLabel, layout, constraints);
	constraints.gridx++;
	layoutComponent(nameBorder, Box.createHorizontalStrut(proceedingPaddingWidth), layout, constraints);
	//constraints.fill = GridBagConstraints.HORIZONTAL;
	int gridwidth = constraints.gridwidth;
	constraints.gridx++;
	fill = constraints.fill;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	//constraints.gridwidth = 5;
	layoutComponent(nameBorder, typeOfType, layout, constraints);
	constraints.gridwidth = gridwidth;
	constraints.fill = fill;

	//constraints.gridy++;
	incertaeSedis = new JCheckBox();
	incertaeSedisLabel = new JLabel();
	//constraints.gridx = 2;
	constraints.gridx++;
	layoutComponent(nameBorder, Box.createHorizontalStrut(proceedingPaddingWidth), layout, constraints);
	constraints.gridx++;
	layoutComponent(nameBorder, incertaeSedis, layout, constraints);
	constraints.gridx++;
	layoutComponent(nameBorder, incertaeSedisLabel, layout, constraints);
	constraints.gridx = gridx;

	createRelevantTabbedPane();

	NamedObjectTableModel<?> model = new NameUsageTableModel();
	lowerTaxa = new ViewportSizedTable(model, 10);
	lowerTaxa.getSelectionModel().addListSelectionListener(this);
	//lowerTaxa = new JTable(model);
	int columns = model.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    lowerTaxa.getColumn(model.getColumnName(i)).setCellEditor(this);
	}
	lowerTaxaPane = new JScrollPane(lowerTaxa);
	lowerTaxaBorder = new Box(BoxLayout.Y_AXIS);
	lowerTaxaBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	//lowerTaxaBorder.add(lowerTaxaPane);
	relevantTabs.add(lowerTaxaPane);

	model = new RelevantNameUsageTableModel();
	nameUsages = new ViewportSizedTable(model, 10);
	//nameUsages = new JTable(model);
	//annotationPanel = new AnnotationPanel();
	//annotationPanel.setDialogSource(nameUsages);
	columns = model.getColumnCount();
	for (int i = 0; i < columns; i++) {
	    nameUsages.getColumn(model.getColumnName(i)).setCellEditor(this);
	}
	nameUsagesPane = new JScrollPane(nameUsages);
	nameUsagesPane.addMouseListener(this);
	nameUsagesBorder = new Box(BoxLayout.Y_AXIS);
	nameUsagesBorder.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
	//nameUsagesBorder.add(nameUsagesPane);
	nameUsagesBorder.addMouseListener(this);
	relevantTabs.add(nameUsagesPane);
    }

    /**
     * Layouts components of this {@code NamedObjectPanel}
     */
    protected void layoutComponents()
    {
	JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	splitPane.setRightComponent(relevantTabsPane);
	splitPane.setLeftComponent(nameBorder);
	splitPane.setDividerSize(3);
	contents = splitPane;

	/*
	contents.add(nameBorder);
	//contents.add(higherTaxonBorder);
	//contents.add(lowerTaxaBorder);
	//contents.add(nameUsagesBorder);
	contents.add(relevantTabsPane);
	*/
	super.layoutComponents();
    }

    /**
     * Sets editable state to {@code editable}
     * and takes care of components
     *
     * @param editable true to be editable
     */
    protected void setEditable(boolean editable)
    {
	super.setEditable(editable);
	if(locale != null)
	    locale.setEnabled(editable);

	if(rank != null)
	    rank.setEnabled(editable);

	if(literal != null)
	    literal.setEditable(editable);

	if(sensu != null)
	    sensu.setEditable(editable);

	if(citation != null)
	    citation.setEditable(editable);

	if(higherTaxon != null)
	    higherTaxon.setEditable(editable);

	if(incertaeSedis != null)
	    incertaeSedis.setEnabled(editable);

	if(type != null)
	    type.setEnabled(editable);
	
	if(typeOfType != null)
	    typeOfType.setEnabled(editable);

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

	T nameUsage = getNamedObject();

	if(appearancePanel != null)
	    nameUsage.setAppearance(appearancePanel.getNamedObject());

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

	T nameUsage = getNamedObject();

	if(nameUsage == null)
	    return true;

	Appearance appearance = nameUsage.getAppearance();
	if(appearancePanel != null)
	    appearancePanel.setNamedObject(appearance);
	if(appearance != null)
	    sensu.setText(appearance.getSummary());

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
   
    protected abstract AbstractNameUsagePanel<T> createNameUsagePanel();

    @SuppressWarnings("unchecked")
    public void mouseClicked(MouseEvent event)
    {
	if (event.getClickCount() <= 1)
	    return;

	Object source = event.getSource();

	if (source == higherTaxon) {
	    if(nameUsagePanel == null) {
		nameUsagePanel = createNameUsagePanel();
	    }
	    T nameUsage = getNamedObject();
	    boolean created = false;
	    if(nameUsage != null) {
		nameUsage = (T)nameUsage.getHigherNameUsage();
		if(nameUsage == this)
		    nameUsage = null;
	    }
	    /*
	    else {
		setNamedObject(createNamedObject());
	    }
	    */
	    if(nameUsage == null) {
		nameUsage = createNamedObject();
		created = true;
	    }

	    nameUsagePanel.setNamedObject(nameUsage);
	    if(showOptionDialog(higherTaxon, nameUsagePanel, higherTaxonTitle, event) &&
	       created) {
		((NameUsage)getNamedObject()).setHigherNameUsage(nameUsagePanel.getNamedObject());
	    }
	    return;
	}

	//showEditorPane(event);
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
	if(source == nameUsages ||
	   source == nameUsagesBorder || 
	   source == nameUsagesPane) {
	    table = nameUsages;
	    title = annotationTitle;
	    if(annotationPanel == null)
		annotationPanel = createAnnotationPanel();
	    else if(source != nameUsages) {
		annotationPanel.setNamedObject(new Annotation());
	    }
	    dialog = annotationPanel;
	}
	else if (source == lowerTaxa ||
		 source == lowerTaxaBorder || 
		 source == lowerTaxaPane) {
	    table = lowerTaxa;
	    title = lowerTaxonTitle;
	    if(nameUsagePanel == null)
		nameUsagePanel = createNameUsagePanel();
	    else if (source != lowerTaxa) {
		nameUsagePanel.setNamedObject(createNamedObject());
	    }
	    dialog = nameUsagePanel;
	}

	if(table != null) {
	    if(showOptionDialog(table, dialog, title, event) &&
	       source != nameUsages && source != lowerTaxa) {
		((NamedObjectTableModel)table.getModel()).add(dialog.getNamedObject());
	    }
	}

	return false;
    }

    public void setTabbedPane(JTabbedPane tabbedPane, AppearancePanel appearancePanel)
    {
	setTabbedPane(tabbedPane);
	setAppearancePanel(appearancePanel);
    }

    /**
     * Sets specified {@code object} to display
     *
     * @param object {@code NamedObject} to be displaied
     */
    @SuppressWarnings("unchecked")
    public void setNamedObject(T object)
    {
	super.setNamedObject(object);
	if(!(object instanceof NameUsage))
	    return;

	NameUsage<?> nameUsage =object;
	literal.setText(nameUsage.getLiteral());
	sensu.setText(nameUsage.getAuthority());

	Rank theRank= nameUsage.getRank();
	if(theRank == null)
	    theRank = RankModel.UNSPECIFIED;
	rank.setSelectedItem(theRank);
	if(rank.getSelectedItem() != theRank) {
	    rank.addItem(theRank);
	    rank.setSelectedItem(theRank);
	}

	((NameUsageTableModel)lowerTaxa.getModel()).set(nameUsage.getLowerNameUsages());
	if(nameUsage instanceof NameUsageNode) {
	    ((RelevantNameUsageTableModel)nameUsages.getModel()).set(((NameUsageNode)nameUsage).getRelevantAnnotations());
	}
	else {
	    ((RelevantNameUsageTableModel)nameUsages.getModel()).setObjects(nameUsage.getAnnotations());
	}

	lowerTaxa.revalidate();

	StringBuffer buffer = new StringBuffer();
	nameUsage = nameUsage.getHigherNameUsage();
	if(nameUsage != null) {
	    String str = Rank.getRank(nameUsage);
	    if(str != null)
		buffer.append(str);
	    str = nameUsage.getLiteral();
	    if(str != null) {
		if(buffer.length() > 0)
		    buffer.append(' ');
		buffer.append(str);
	    }
	    str = nameUsage.getAuthority();
	    if(str != null) {

		if(buffer.length() > 0)
		    buffer.append(' ');
		buffer.append(str);
	    }
	}
	higherTaxon.setText(buffer.toString());

    }

    public void valueChanged(ListSelectionEvent event)
    {
	if(event.getValueIsAdjusting() ||
	   lowerTaxa == null ||
	   event.getSource() != lowerTaxa.getSelectionModel() ||
	   lowerTaxa.getSelectedRowCount() > 1)
	    return;

	showEditorPane(event);
    }

    public void selectLowerTaxa(T[] nameUsages)
    {
	selectNameUsages(nameUsages, lowerTaxa);
	relevantTabs.setSelectedComponent(lowerTaxaPane);
    }

    public void selectRelevantTaxa(T[] nameUsages)
    {
	selectNameUsages(nameUsages, this.nameUsages);
	relevantTabs.setSelectedComponent(nameUsagesPane);	
    }

    protected void selectNameUsages(T[] nameUsages,
				    JTable table)
    {
	TableModel model = table.getModel();
	int[] indices = null;
	if((model instanceof RelevantNameUsageTableModel)) {
	    indices = 
		((RelevantNameUsageTableModel)table.getModel()).getIndicesOf(nameUsages);
	}
	else if((model instanceof NameTableModel)) {
	    indices = 
		((NameTableModel)table.getModel()).getIndicesOf(nameUsages);
	}

	if(indices == null  || indices.length == 0)
	    return;

	ListSelectionModel selection =
	    table.getSelectionModel();
	selection.clearSelection();
	for (int index : indices) {
	    selection.addSelectionInterval(index, index);
	}
    }
}


/*
 * NameListPane.java:  a JPanel wrapping a NameUsage list
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.nomencurator.io.ComparisonQueryParameter;
import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.NameUsageQueryParameter;
import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.QueryEvent;
import org.nomencurator.io.QueryManager;
import org.nomencurator.io.QueryMode;
import org.nomencurator.io.MultiplexNameUsageQuery;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.ComponentEnabler;
import org.nomencurator.gui.ComponentSizeAdjuster;

import org.nomencurator.gui.swing.table.NameTableModel;

import org.nomencurator.gui.swing.tree.NameTreeNode;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code NameListPane} provides a list of name usages and
 * methods to filter them.
 *
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
class NameListPane<T extends NameUsage<?>>
    extends AbstractNameListPane<T>
{
    private static final long serialVersionUID = 505307850078236355L;

    protected JLabel heightLabel;
    protected JComboBox<String> heightComboBox;
    protected RankChooser higherRank;

    protected JLabel depthLabel;
    protected JComboBox<String> depthComboBox;
    protected RankChooser lowerRank;

    protected JCheckBox pivot;
    protected JLabel pivotLabel;

    protected JCheckBox synonym;
    protected JLabel synonymLabel;

    protected JCheckBox roughSet;
    protected JLabel roughSetLabel;

    protected JButton compareButton;
    protected JButton appendButton;


    /** true if <tt>appendButton</tt> to be enabled */
    @Getter
    @Setter
    protected boolean appendable = true;

    protected JSeparator depthHeightSeparator;

    protected LanguageMenu languageMenu;

    protected Hierarchies<T> currentHierarchies;
    
    protected List<Hierarchies<T>> hierarchies;

    protected HierarchiesFrame<T> currentFrame;

    protected List<HierarchiesFrame<T>> frames;

    protected List<MultiplexNameUsageQuery<T>> getHierarchies = null;

    protected static String[] heightValues = {
	"", "0", "1", "2", "3" };

    protected static String[] heightDepthValues = {
	"", "0", "1", "2", "3" };

    protected static String[] depthValues = {
	"", "0", "1", "2", "3" };


    /**
     * Construct a <tt>JPanel</tt> using the default Locale,
     * to display a table of name usages,
     * two combo boxes to specify depth and height, and buttons
     * to compare hierarchies based on specified name usages.
     */
    protected NameListPane()
    {
	super();
    }

    /**
     * Construct a <tt>JPanel</tt> using specified Locale,
     * to display a table of name usages,
     * two combo boxes to specify depth and height, and buttons
     * to compare hierarchies based on specified name usages.
     *
     * @param locale to specify
     */
    protected NameListPane(Locale locale)
    {
	super(locale);
    }

    /**
     * Creates GUI components of given locale to display.
     *
     * @param locale of components
     */
    protected JPanel createButtonsPanel()
    {
	JPanel panel = super.createButtonsPanel();

	heightLabel = new JLabel("height");
	depthLabel = new JLabel("depth");
	heightComboBox = 
	    new JComboBox<String>(new DefaultComboBoxModel<String>(heightValues));
	heightComboBox.setEditable(true);
	Component columnResizer =
	    heightComboBox.getEditor().getEditorComponent();
	if(columnResizer instanceof JTextField) {
	    ((JTextField)columnResizer).setColumns(0);
	}
	higherRank = new RankChooser();

	depthComboBox =
	    new JComboBox<String>(new DefaultComboBoxModel<String>(depthValues));
	depthComboBox.setEditable(true);
	columnResizer =
	    depthComboBox.getEditor().getEditorComponent();
	if(columnResizer instanceof JTextField) {
	    ((JTextField)columnResizer).setColumns(0);
	}
	lowerRank = new RankChooser();

	depthHeightSeparator = new JSeparator(SwingConstants.VERTICAL);

	pivotLabel = new JLabel("pivot");
	pivotLabel.setEnabled(false);
	pivot = new JCheckBox();
	pivot.setEnabled(false);

	synonymLabel = new JLabel("syonoym");
	synonymLabel.setEnabled(false);
	synonym = new JCheckBox();
	synonym.setEnabled(false);
	    
	roughSetLabel = new JLabel("rough set");
	roughSetLabel.setEnabled(false);
	roughSet = new JCheckBox();
	roughSet.setEnabled(false);

	//depthComboBox.setPreferredSize(null);
	compareButton = new JButton();
	compareButton.addActionListener(this);
	compareButton.setEnabled(false);
	appendButton  =  new JButton();
	appendButton.addActionListener(this);
	appendButton.setEnabled(false);

	panel.add(heightLabel);
	panel.add(heightComboBox);
	panel.add(higherRank);
	//panel.add(new JSeparator(SwingConstants.VERTICAL));
	panel.add(depthLabel);
	panel.add(depthComboBox);
	panel.add(lowerRank);
	panel.add(depthHeightSeparator);

	/*
	panel.add(new JSeparator(SwingConstants.VERTICAL));
	panel.add(roughSet);
	panel.add(roughSetLabel);
	*/
	/*
	panel.add(synonym);
	panel.add(synonymLabel);
	panel.add(new JSeparator(SwingConstants.VERTICAL));
	*/
	/*
	panel.add(pivot);
	panel.add(pivotLabel);
	panel.add(new JSeparator(SwingConstants.VERTICAL));
	*/
	//panel.add(new JSeparator(SwingConstants.VERTICAL));
	panel.add(appendButton);
	panel.add(compareButton);

	return panel;
    }

    /**
     * Sets size of GUI components.
     */
    protected void setComponentsSize()
    {
	// ComponentSizeAdjuster.adjustComponentsWidth(appendButton, compareButton);
	ComponentSizeAdjuster.adjustComponentsHeight(appendButton, compareButton, depthComboBox, heightComboBox, depthHeightSeparator);
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    protected void setButtonsLocale(Locale locale)
    {
	if(locale == null)
	    locale = Locale.getDefault();

	String compareButtonText = 
	    ResourceKey.COMPARE;
	String appendButtonText =
	    ResourceKey.ADD_TO_COMPARATOR;
	String depthLabelText =
	    ResourceKey.DEPTH_LABEL;
	String fullDepthText =
	    ResourceKey.FULL_DEPTH;
	String heightLabelText =
	    ResourceKey.HEIGHT_LABEL;
	String fullHeightText =
	    ResourceKey.FULL_HEIGHT;

	String synonymLabelText = ResourceKey.SYNONYM_LABEL;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    compareButtonText = 
		resource.getString(compareButtonText);
	    appendButtonText = 
		resource.getString(appendButtonText);
	    depthLabelText =
		resource.getString(depthLabelText);
	    fullDepthText =
		resource.getString(fullDepthText);
	    heightLabelText =
		resource.getString(heightLabelText);
	    fullHeightText =
		resource.getString(fullHeightText);
	    /*
	    synonymLabelText = resource.getString(synonymLabelText);
	    if (synonymLabelText == null)
		synonymLabelText = "synonym";
	    */
	}
	catch(MissingResourceException e) {
	    e.printStackTrace();
	}
	if(compareButton != null)
	    compareButton.setText(compareButtonText);
	if(appendButton != null)
	    appendButton.setText(appendButtonText);
	if (synonymLabel != null)
	    synonymLabel.setText(synonymLabelText);
	heightLabel.setText(heightLabelText);
	depthLabel.setText(depthLabelText);

	if(buttonsPanel != null) {
	    /*
	    ComponentOrientation orientation = 
		getComponentOrientation();
	    FlowLayout f = 
		(FlowLayout)buttonsPanel.getLayout();
	    if(orientation.isHorizontal() &&
	       !orientation.isLeftToRight())
		f.setAlignment(FlowLayout.LEFT);
	    else
		f.setAlignment(FlowLayout.RIGHT);
	    */
	}

	//replaceElement(heightComboBox, fullHeightText);
	replaceElement(heightComboBox, "");
	//replaceElement(depthComboBox, fullDepthText);
	replaceElement(depthComboBox, "");
	
	//nameTable.setLocale(locale);

	// revalidate();
    }

    protected void replaceElement(JComboBox<String> comboBox, String fullValue)
    {
	if(comboBox == null || fullValue == null)
	    return;
	
	ComboBoxModel<String> model = comboBox.getModel();
	
	if(model != null &&
	   model instanceof DefaultComboBoxModel) {
	    
	    DefaultComboBoxModel<String> m =
		(DefaultComboBoxModel<String>)model;
	    
	    int selected = -1;
	    Object s = m.getSelectedItem();
	    if(s != null) {
		selected = m.getIndexOf(s);
	    }
	    
	    if(!fullValue.equals(m.getElementAt(0))) {
		m.removeElementAt(0);
		m.insertElementAt(fullValue, 0);
		if(selected == 0) {
		    m.setSelectedItem(fullValue);
		}
	    }
	}
    }

    public void setLanguageMenu(LanguageMenu menu)
    {
	if(languageMenu == menu)
	    return;
	if(languageMenu != null)
	    languageMenu.removeChangeListener(this);
	languageMenu = menu;
	if(languageMenu != null) {
	    setLocale(languageMenu.getLocale());
	    languageMenu.addChangeListener(this);
	}
    }

    public void stateChanged(ChangeEvent event)
    {
	if(event.getSource() != languageMenu)
	    return;

	setLocale(languageMenu.getLocale());
    }

    protected void enableButtons(boolean enable) {
	if(enable) {
		switch(nameTable.getSelectedRowCount()) {
		case 0:
		    ComponentEnabler.setEnabled(false, appendButton, compareButton, pivot, pivotLabel, roughSet, roughSetLabel);
		    break;
		case 1:
		    ComponentEnabler.setEnabled(false, appendButton, compareButton, pivot, pivotLabel, roughSet, roughSetLabel);
		    if(appendable)
			ComponentEnabler.setEnabled(true, appendButton, pivot, pivotLabel, roughSet, roughSetLabel);
		    break;
		default:
		    ComponentEnabler.setEnabled(true, compareButton, pivot, pivotLabel, roughSet, roughSetLabel);
		    if(appendable)
			ComponentEnabler.setEnabled(true, appendButton);
		    break;
		}
	} else {
	    ComponentEnabler.setEnabled(enable, appendButton, compareButton, pivot, pivotLabel);
	}
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void actionPerformed(ActionEvent event)
    {
	Object source = event.getSource();
	if(source == compareButton || source == appendButton) {
	    ComparisonQueryParameter parameter = createComparisonQueryParameter();
	    parameter.setAppend(source == appendButton);
	    fireQueryEvent(new QueryEvent(this, parameter));
	    return;
	}
    }

    protected ComparisonQueryParameter<NameUsage<?>> createComparisonQueryParameter() {
	String value = (String)heightComboBox.getSelectedItem();
	int height = NameUsageExchanger.FULL_HEIGHT;
	if(!heightDepthValues[0].equals(value))
	    height = Integer.valueOf(value).intValue();
	value = (String)depthComboBox.getSelectedItem();
	int depth = NameUsageExchanger.FULL_DEPTH;
	if(!heightDepthValues[0].equals(value))
	    depth = Integer.valueOf(value).intValue();

	ComparisonQueryParameter<NameUsage<?>> parameter = new ComparisonQueryParameter<>();
	parameter.setHeight(height);
	parameter.setDepth(depth);
	parameter.setQueryMode(QueryMode.HIERARCHIES);
	parameter.setPivot(pivot.isSelected());
	parameter.setHigher(higherRank.getSelectedItem());
	parameter.setLower(lowerRank.getSelectedItem());

	NameTableModel model = (NameTableModel)nameTable.getModel();
	int[] selections = getSelectedModelRows();
	for(int i = 0; i < selections.length; i++) {
	    //parameter.add(model.getNamedObject(selections[i]));
	    parameter.add(model.getNamedObject(nameTable.convertRowIndexToModel(selections[i])));
	}

	return parameter;
    }

    @SuppressWarnings("unchecked")
    public NameUsageQueryParameter<T> getQueryParameter(NameUsage<?> nameUsage)
    {
	NameUsageQueryParameter<T> parameter = getQueryParameter();
	parameter.setFilter((NameUsage<T>)nameUsage);
	return parameter;
    }


    public NameUsageQueryParameter<T> getQueryParameter()
    {
	String value = (String)heightComboBox.getSelectedItem();
	int height = NameUsageExchanger.FULL_HEIGHT;
	if(value.length() > 0 /* !heightDepthValues[0].equals(value) */)
	    height = Integer.valueOf(value).intValue();
	value = (String)depthComboBox.getSelectedItem();
	int depth = NameUsageExchanger.FULL_DEPTH;
	if(value.length() > 0 /* !heightDepthValues[0].equals(value) */)
	    depth = Integer.valueOf(value).intValue();

	NameUsageQueryParameter<T> parameter =
	    new NameUsageQueryParameter<T>();
	parameter.setHeight(height);
	parameter.setDepth(depth);
	parameter.setQueryMode(QueryMode.PARTIAL_HIERARCHIES);
	parameter.setHigher(higherRank.getSelectedItem());
	parameter.setLower(lowerRank.getSelectedItem());

	return parameter;
    }

    public int getHierarchyHeight()
    {
	return getHierarchyVerticalSize(heightComboBox, NameUsageExchanger.FULL_HEIGHT);
    }
    
    public int getHierarchyDepth()
    {
	return getHierarchyVerticalSize(depthComboBox, NameUsageExchanger.FULL_DEPTH);
    }

    protected int getHierarchyVerticalSize(JComboBox<String> selector, int fullValue)
    {
	String selection = (String)selector.getSelectedItem();
	int value = fullValue;
	if(!heightDepthValues[0].equals(selection))
	    value = Integer.valueOf(selection).intValue();
	return value;
    }

    @SuppressWarnings("unchecked")
    public void windowGainedFocus(WindowEvent event)
    {
	Window window = event.getWindow();
	if(!(window instanceof HierarchiesFrame))
	    return;

	currentHierarchies = 
	    ((HierarchiesFrame<T>)window).getHierarchies();
    }

    public void windowLostFocus(WindowEvent event)
    {
    }

    public void setObjectExchanger(ObjectExchanger<NameUsage<?>> manager)
    {
	nameTable.setObjectExchanger(manager);
    }

    public ObjectExchanger<NameUsage<?>> getObjectExchanger()
    {
	return nameTable.getObjectExchanger();
    }


    public String getNames(String name, Rank rank, String authority, String year, MatchingMode queryType)
	throws IOException

    {
	return nameTable.getNames(name, rank, authority, year, queryType);
    }

    public String getNames(Collection<? extends NameUsage<?>> nameUsages, String name, Rank rank, String authority, String year, MatchingMode queryType)
    {
	return nameTable.getNames(nameUsages, name, rank, authority, year, queryType);
    }

    public NameTableModel getNameTableModel()
    {
	return getNameList().getNameTableModel();

    }
}

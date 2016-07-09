/*
 * AbsractNameListPane.java:  a JPanel wrapping a NameUsage list
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
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.NameUsageQueryParameter;
import org.nomencurator.io.ObjectExchanger;
//import org.nomencurator.io.QueryResultEvent;
//import org.nomencurator.io.QueryResultListener;
import org.nomencurator.io.QueryEvent;
import org.nomencurator.io.QueryListener;
import org.nomencurator.io.QueryManager;
import org.nomencurator.io.QueryMode;
import org.nomencurator.io.MultiplexNameUsageQuery;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.table.NameTableModel;

import org.nomencurator.gui.swing.tree.NameTreeNode;

/**
 * {@code AbstractNameListPane} provides a list of name usages and
 * methods to filter them.
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameListPane<T extends NameUsage<?>>
    extends JPanel
    implements ActionListener,
	       ChangeListener,
	       ListSelectionListener,
	       NameQuery,
	       WindowFocusListener
{
    private static final long serialVersionUID = -2133224927427201961L;

    protected NameTable/*<T>*/  nameTable;
    protected JScrollPane listPane;
    protected JPanel buttonsPanel;

    /* list of {@code EventLIstener}s */
    protected EventListenerList listeners;

    protected LanguageMenu languageMenu;

   /**
     * Construct a {@code JPanel} using the default Locale,
     * to display a table of name usages,
     * two combo boxes to specify depth and height, and buttons
     * to compare hierarchies based on specified name usages.
     */
    protected AbstractNameListPane()
    {
	this(Locale.getDefault());
    }

    /**
     * Construct a {@code JPanel} using specified Locale,
     * to display a table of name usages,
     * two combo boxes to specify depth and height, and buttons
     * to compare hierarchies based on specified name usages.
     *
     * @param locale to specify
     */
    protected AbstractNameListPane(Locale locale)
    {
	super(new BorderLayout());
	createComponents();
	setLocale(locale);
	layoutComponents();
	setComponentsSize();
    }

    /**
     * Creates GUI components of given locale to display.
     *
     * @param locale of components
     */
    protected void createComponents()
    {
	nameTable = new NameTable/*<T>*/();
	nameTable.setAutoscrolls(true);
	nameTable.getSelectionModel().addListSelectionListener(this);

	listPane = new JScrollPane(nameTable,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	buttonsPanel = createButtonsPanel();
    }

    /**
     * Create and returns a {@code JPanel} to contain componens to operate, such as buttons, combo boxes, etc.
     * It is expeced to override in subclasses.
     *
     * @return JPanel to contain operation components.
     */
    protected JPanel createButtonsPanel()
    {
	JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	return panel;
    }

    /**
     * Layouts GUI components.
     */
    protected void layoutComponents()
    {
	add(listPane, BorderLayout.CENTER);
	add(buttonsPanel, BorderLayout.SOUTH);
    }

    /**
     * Sets size of GUI components.
     */
    protected void setComponentsSize()
    {
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale <CODE>Locale</CODE> to be used for localization
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

	nameTable.setLocale(locale);

	setButtonsLocale(locale);

	revalidate();
    }

    /**
     * Localizes components in {@code buttonsPanel} according to specified locale.
     *
     * @param locale <CODE>Locale</CODE> to be used for localization
     */
    protected abstract void setButtonsLocale(Locale locale);

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

    public void valueChanged(ListSelectionEvent event)
    {
	if(event.getValueIsAdjusting())
	    return;

	Object source = event.getSource();
	if(source != nameTable.getSelectionModel())
	    return;

	int selectedRowCount = nameTable.getSelectedRowCount();
 	if(selectedRowCount > 1) {
	    enableButtons(true);
	}
	else {
	    enableButtons(false);
	}
    }

    /**
     * Enables or disables compoents in {@code buttonsPanel} according to the parameter.
     *
     * @param enable true to enable components.
     */
    protected abstract void enableButtons(boolean enable);

    public void actionPerformed(ActionEvent event)
    {
	fireQueryEvent(composeQuery());
    }

    @SuppressWarnings("unchecked")
    protected QueryEvent<T> composeQuery()
    {
	int index = getSelectedModelRow();
	if(index == -1)
	    return null;
	NameUsageQueryParameter<T> parameter = new NameUsageQueryParameter<T>();
	// FIXME
	// re-design NameTableModel to use generics
	parameter.setFilter((NameUsage<T>)getNameTableModel().getNamedObject(index));
	return new QueryEvent<T>(this, parameter);
    }

    /**
     * Returns {@code NameTable} implementing a list of name usages
     *
     *  @return list of name usages as {@code NameTable}
     */
    public NameTable/*<T>*/ getNameList()
    {
	return nameTable;
    }

    /**
     * Returns {@code NameTableModel} implementing a model of name usage list
     *
     *  @return list of name usages as {@code NameTableModel}
     */
    public NameTableModel getNameTableModel()
    {
	return getNameList().getNameTableModel();
    }

    /**
     * Returns view index of first selected row, or -1 if no row is selected.
     * 
     * @return view index of first selected row
     */
    public int getSelectedRow() {
	return nameTable.getSelectedRow();
    }

    /**
     * Returns model index of first selected row in the view, or -1 if no row is selected.
     * 
     * @return model index of first selected row
     */
    public int getSelectedModelRow() {
	return nameTable.convertRowIndexToModel(nameTable.getSelectedRow());
    }

    /**
     * Returns number of rows selected.
     *
     * @return number of rows selected
     */
    public int getSelectedRowCount() {
	return nameTable.getSelectedRowCount();
    }

    /**
     * Returns view indices of all selected rows.
     *
     * @return an array of int containing view indices of all selected rows,
     * or empty array if no row is selected.
     */
    public int[] getSelectedRows() {
	return nameTable.getSelectedRows();
    }

    /**
     * Returns model indices of all selected rows.
     *
     * @return an array of int containing model indices of all selected rows,
     * or empty array if no row is selected.
     */
    public int[] getSelectedModelRows() {
	int[] selections = nameTable.getSelectedRows();
	int[] models = new int[selections.length];
	for(int i = 0; i < selections.length; i++) {
	    models[i] = nameTable.convertRowIndexToModel(selections[i]);
	}
	return models;
    }

    public void addQueryListener(QueryListener<T> listener)
    {
	if(listener == null)
	    return;

	if(listeners == null)
	    listeners = new EventListenerList();

	listeners.add(QueryListener.class, listener);
    }

    public void removeQueryListener(QueryListener<T> listener)
    {
	if(listener == null || listeners == null)
	    return;

	listeners.remove(QueryListener.class, listener);
    }

    /**
     * Dispatches queryEvent to {@code QueryListenr}.
     */
    @SuppressWarnings("unchecked")
    protected void fireQueryEvent(QueryEvent<T> queryEvent)
    {
	if(queryEvent == null || listeners == null)
	    return;

	Object[] listenersArray = listeners.getListenerList();
	for (int i = listenersArray.length-2; i>=0; i-=2) {
	    if (listenersArray[i]==QueryListener.class) {
		((QueryListener<T>)listenersArray[i+1]).query(queryEvent);
	    }
	}
    }
}

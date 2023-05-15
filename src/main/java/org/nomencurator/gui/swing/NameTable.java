/*
 * NameTable.java:  a JTable to display NameUsage list
 *
 * Copyright (c) 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.gui.swing;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.NameUsageExchanger;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.model.util.NameUsageAttribute;
import org.nomencurator.model.util.NameUsageAttributeComparator;

import org.nomencurator.gui.swing.table.NameTableCellRenderer;
import org.nomencurator.gui.swing.table.NameTableModel;

import org.nomencurator.gui.swing.tree.NameTreeNode;

/**
 * {@code NameTable} provides a JTable to display a list of NameUsages
 * using {@code NameTreeTableModel}
 *
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTable
    extends GenericNameTable<NameUsage<?>>
{
    private static final long serialVersionUID = 416287578673174387L;

    public NameTable()
    {
	super();
    }

    public NameTable(Locale locale)
    {
	super(locale);
    }

    public NameTable(final NameUsage<?>[] names)
    {
	this(names, Locale.getDefault());
    }

    public NameTable(final NameUsage<?>[] names,
		     Locale locale)
    {
	super(names, locale);
    }

    public NameTable(final NameTreeNode[] nodes)
    {
	super(nodes);
    }

    public NameTable(final NameTreeNode[] nodes,
		     Locale locale)
    {
	super(nodes, locale);
    }

    public NameTable(final List<? extends NameUsage<?>> nodes)
    {
	super(nodes);
    }

    public NameTable(final List<? extends NameUsage<?>> nodes,
		     Locale locale)
    {
	super(nodes, locale);
    }

    public NameTable(final List<? extends NameUsage<?>> nodes,
		     Locale locale, int rows)
    {
	super(nodes, locale, rows);
    }
}

/*public*/ class GenericNameTable<T extends NameUsage<?>>
    extends ViewportSizedTable
    implements ChangeListener, NameQuery
{
    //private static final long serialVersionUID = -2434207703661539827L;
    private static final long serialVersionUID = 5372155683262353891L;

    protected LanguageMenu languageMenu;

    protected NameUsageExchanger<T> queryManager;
    
    protected QueryMessages messages;

    public static final NameUsageAttribute[] attributes = NameUsageAttribute.values();

    public GenericNameTable()
    {
	this(Locale.getDefault());
    }

    public GenericNameTable(Locale locale)
    {
	this((List<T>)null, locale);
    }

    public GenericNameTable(final T[] names)
    {
	this(names, Locale.getDefault());
    }

    public GenericNameTable(final T[] names,
		     Locale locale)
    {
	super();
	setModel(createModel(names, locale));
	// setAutoCreateRowSorter(true);
	setDragEnabled(true);
	setDropMode(DropMode.ON);
    }

    public GenericNameTable(final NameTreeNode[] nodes)
    {
	this(nodes, Locale.getDefault());
    }

    public GenericNameTable(final NameTreeNode[] nodes,
		     Locale locale)
    {
	super(createModel(nodes, locale));
	// setAutoCreateRowSorter(true);
	setDragEnabled(true);
	setDropMode(DropMode.ON);
    }

    public GenericNameTable(final List<? extends T> nodes)
    {
	this(nodes, Locale.getDefault());
    }

    public GenericNameTable(final List<? extends T> nodes,
		     Locale locale)
    {
	this(nodes, locale, DEFAULT_ROWS);
    }

    public GenericNameTable(final List<? extends T> nodes,
		     Locale locale, int rows)
    {
	super(createModel(nodes, locale), rows);
    }

    protected void initializeLocalVars()
    {
	super.initializeLocalVars();
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	int i = 0;
	while(e.hasMoreElements() && i < attributes.length) {
	    e.nextElement().setCellRenderer(new NameTableCellRenderer(attributes[i++]));
	}
	getColumnModel().getColumn(0).setMinWidth(0);
	getColumnModel().getColumn(0).setPreferredWidth(3);

	TableRowSorter<NameTableModel/*<T>*/> sorter = 
	    new TableRowSorter<NameTableModel/*<T>*/>((NameTableModel/*<T>*/)getModel());
	for (NameUsageAttribute attribute : attributes) {
	    sorter.setComparator(attribute.ordinal(), new NameUsageAttributeComparator(attribute));
	}
	setRowSorter(sorter);

	setDragEnabled(true);
    }

    public void updateUI()
    {
	super.updateUI();
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	while(e.hasMoreElements()) {
	    TableCellRenderer renderer = e.nextElement().getCellRenderer();
	    if (renderer instanceof NameTableCellRenderer)
		((NameTableCellRenderer)renderer).updateUI();
	}
    }

    public static TableModel createModel(final NameTreeNode[] nodes)
    {
	return createModel(nodes, Locale.getDefault());
    }

    public static TableModel createModel(final NameTreeNode[] nodes,
					 Locale locale)
    {
	return new NameTableModel/*<T>*/(nodes, locale);
    }

    public static TableModel createModel(final NameUsage<?>[] names)
    {
	return createModel(names, Locale.getDefault());
    }

    public static TableModel createModel(final NameUsage<?>[] names,
					 Locale locale)
    {
	return new NameTableModel(names, locale);
    }

    public static TableModel createModel(final List<? extends NameUsage<?>> names)
    {
	return createModel(names, Locale.getDefault());
    }

    public static TableModel createModel(final List<? extends NameUsage<?>> names,
					 Locale locale)
    {
	if(names != null)
	    return new NameTableModel/*<T>*/(names.iterator(), locale);
	else
	    return new NameTableModel/*<T>*/(locale);
    }


    //    protected TableModel createDefaultDataModel()
    //    {
    //	return new NameTableModel/*<T>*/();
    //    }

    public void setModel(TableModel model)
    {
	if(model instanceof NameTableModel)
	    super.setModel(model);
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
	if(locale.equals(getLocale()))
	   return;
	
	super.setLocale(locale);

	if(messages == null)
	    messages = new QueryMessages(locale);
	else
	    messages.setLocale(locale);

	((NameTableModel/*<T>*/)getModel()).setLocale(locale);
	revalidate();
    }

    public void setLanguageMenu(LanguageMenu menu)
    {
	if(languageMenu == menu)
	    return;
	if(languageMenu != null)
	    languageMenu.removeChangeListener(this);
	languageMenu = menu;
	if(languageMenu != null)
	    languageMenu.addChangeListener(this);
    }

    public void stateChanged(ChangeEvent event)
    {
	if(event.getSource() != languageMenu)
	    return;
	setLocale(languageMenu.getLocale());
    }

    public void setObjectExchanger(ObjectExchanger<T> exchanger)
    {
	//how can we narrow acceptable subtype of a generic?
	if(exchanger instanceof NameUsageExchanger)
	    queryManager = (NameUsageExchanger<T>)exchanger;
    }

    public ObjectExchanger<T> getObjectExchanger()
    {
	return queryManager;
    }

    public String getNames(String name, Rank rank, String authority, String year, MatchingMode queryType)
	throws IOException
    {
	return getNames(queryManager.getNameUsages(name, rank, queryType, false, false, false, null), name, rank, authority, year, queryType);
    }

    public String getNames(Collection<? extends NameUsage<?>> names, String name, Rank rank, String authority, String year, MatchingMode queryType)
    {
	if(messages == null)
	    messages = new QueryMessages(getLocale());
	String rankName = (rank == null)? "" : rank.getName();

	if(names == null) {
	    return messages.getMessage(0, new Object[] {rankName, name});
	}

	//authority, year restriction code must be here,
	//or, ObjectExchanger must manage them

	((NameTableModel)getModel()).set(names);

	return messages.getMessage(names.size(), new Object[] {rankName, name});
    }

    public void selectNameUsages(NameUsage<?>[] nameUsages)
    {
	int[] indices = 
	    ((NameTableModel)getModel()).getIndicesOf(nameUsages);
	if(indices == null  || indices.length == 0)
	    return;

	ListSelectionModel selection = getSelectionModel();
	selection.clearSelection();
	for (int index : indices) {
	    selection.addSelectionInterval(index, index);
	}
    }

    public NameTableModel getNameTableModel()
    {
	return (NameTableModel)getModel();
    }

}

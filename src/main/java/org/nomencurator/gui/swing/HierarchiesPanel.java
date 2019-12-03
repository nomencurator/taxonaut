/*
 * HierarchiesPanel.java:  a class provaides table to compare hierarchies
 * using Nomencurator.
 *
 * Copyright (c) 2003, 2006, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;
import org.nomencurator.io.MultiplexNameUsageQuery;

import org.nomencurator.io.ubio.UBio;

import org.nomencurator.model.NameUsage;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.table.NameTreeTableMode;
import org.nomencurator.gui.swing.table.NameTreeTableModel;

import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code HierarchiesPanel} provides a table to compare hierarchies
 * and a text box to search these hierarchies
 *
 * @version 	03 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class HierarchiesPanel<T extends NameUsage<?>>
    extends JPanel
    implements ChangeListener,
	       QueryResultListener<T>,
	       Runnable
{
    private static final long serialVersionUID = 8396001721380574975L;

    protected LanguageMenu languageMenu;

    /** Panel to display components for query */
    protected QueryPanel<NameUsage<?>> query;

    @Getter
    /** Panel to show two tables representing hierarchies */
    protected HierarchiesPane<T> hierarchiesPane;

    @Getter
    /** <tt>StatusPanel</tt> to show status */
    protected StatusPanel statusPanel;

    protected NameTreeTable<?>[] tables;

    protected UnitedNameTreeModel unifiedTreeModel;

    public HierarchiesPanel()
    {
	this(new UnitedNameTreeModel());
    }

    public HierarchiesPanel(UnitedNameTreeModel model)
    {
	this(model, Locale.getDefault());
    }

    /**
     * Creates a {@code HierarchiesPanel}
     * using VM default locale
     */
    public HierarchiesPanel(Locale locale)
    {
	this(new UnitedNameTreeModel(), locale);
    }

    /**
     * Creates a {@code HierarchiesPanel}
     * using VM default locale
     */
    public HierarchiesPanel(UnitedNameTreeModel model, Locale locale)
    {
	super(new BorderLayout());
	//setJMenuBar(createMenuBar());
	unifiedTreeModel = model;
	createComponents();
	layoutComponents();
	setComponentsSize();
	setLocale(locale);
	//query.setQueryManager(model);
    }

    /*
    protected JMenuBar createMenuBar()
    {
	menu = new MenuBar();
	languageMenu = menu.getLanguageMenu();
	languageMenu.addChangeListener(this);
	return menu;
    }
    */

    /**
     * Create {@code Components}
     * of specified locale
     *
     * @param locale {@code Locale} to determine texts in labels and buttons
     */
    protected void createComponents()
    {
	//query = new QueryPanel(QueryPanel.RANK | QueryPanel.NAME);
	query = new QueryPanel<NameUsage<?>>();
	hierarchiesPane = new HierarchiesPane<T>();
	final List<NameTreeTableModel> tableModels = NameTreeTableModel.createTableModels(unifiedTreeModel);
	//FIXME 20141201
	//query.setQueryManager(table.getAlignerTree());
	hierarchiesPane.addTab("Search result", new NameTreeTable<NameUsage<?>>(tableModels.get(NameTreeTableMode.ASSIGNMENTS.ordinal())));
	hierarchiesPane.addTab("Inconsistent taxa", new NameTreeTable<NameUsage<?>>(tableModels.get(NameTreeTableMode.INCONSISTENCY.ordinal())));
	hierarchiesPane.addTab("Synonyms", new NameTreeTable<NameUsage<?>>(tableModels.get(NameTreeTableMode.SYNONYMS.ordinal())));
	hierarchiesPane.addTab("Different taxa",  new NameTreeTable<NameUsage<?>>(tableModels.get(NameTreeTableMode.DIFFERENCE.ordinal())));
	hierarchiesPane.addTab("Missing taxa", new NameTreeTable<NameUsage<?>>(tableModels.get(NameTreeTableMode.MISSINGS.ordinal())));
	hierarchiesPane.addTab("Common taxa",  new NameTreeTable<NameUsage<?>>(tableModels.get(NameTreeTableMode.COMMONS.ordinal())));

	statusPanel = new StatusPanel();
    }

    /** Layout {@code Components} */
    protected void layoutComponents()
    {
	add(query, BorderLayout.NORTH);
	add(hierarchiesPane, BorderLayout.CENTER);
	add(statusPanel, BorderLayout.SOUTH);
    }

    public HierarchiesPane<T> removeHierarchies() {
	HierarchiesPane<T> toRemove = null;
	synchronized(getTreeLock()) {
	    toRemove = getHierarchiesPane();
	    remove(toRemove);
	    this.hierarchiesPane = null;
	}
	return toRemove;
    }

    public HierarchiesPane<T> setHierarchiesPane(HierarchiesPane<T> hierarchiesPane) {
	if(this.hierarchiesPane == hierarchiesPane)
	    return this.hierarchiesPane;

	HierarchiesPane<T> currentHierarchiesPane = null;
	synchronized(getTreeLock()) {
	    currentHierarchiesPane = getHierarchiesPane();
	    if(currentHierarchiesPane != null) {
		removeHierarchies();
	    }
	    this.hierarchiesPane = hierarchiesPane;
	    add(hierarchiesPane, BorderLayout.CENTER);
	}
	return currentHierarchiesPane;
    }

    /** Set size of {@code Components} */
    protected void setComponentsSize()
    {
    }

    public QueryPanel<NameUsage<?>> getQueryPanel()
    {
	return query;
    }

    public UnitedNameTreeModel getUnitedNameTreeModel()
    {
	return unifiedTreeModel;
    }

    /**
     * Adds {@code table</CDODE> to {@code nameTab}
     * with tab {@code rank}
     *
     * @param rank name of rank as {@code String}
     * @param table 
     */
    public void addTab(String rank, Component table)
    {
	hierarchiesPane.addTab(rank, table);
    }

    public void remove(Component table)
    {
	if(hierarchiesPane != null)
	    hierarchiesPane.remove(table);
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	/*
	if(query == null) {
	    super.setLocale(locale);
	    return;
	}

	if(locale == null)
	    locale = Locale.getDefault();
	if(locale.equals(getLocale()))
	   return;
	*/
	super.setLocale(locale);

	if(query == null)
	    return;
	//menu.setLocale(locale);
	query.setLocale(locale);
	hierarchiesPane.setLocale(locale);
	statusPanel.setLocale(locale);
	//menu.revalidate();
    }

    public void stateChanged(ChangeEvent event)
    {
	if(event.getSource() != languageMenu)
	    return;
	setLocale(languageMenu.getLocale());
    }

    protected List<MultiplexNameUsageQuery<T>> threads = null;

    public void add(MultiplexNameUsageQuery<T> thread)
    {
	if(thread == null)
	    return;

	if(threads == null)
	    threads = new ArrayList<MultiplexNameUsageQuery<T>>();
	thread.addQueryResultListener(this);
	threads.add(thread);
    }

    public void remove(MultiplexNameUsageQuery<T> thread)
    {
	if(thread == null)
	    return;

	if(threads != null)
	    threads.remove(thread);
	thread.removeQueryResultListener(this);
	if(threads.isEmpty())
	    threads = null;
    }

    public void run()
    {
	/*
	if(threads == null ||
	   threads.size() == 0)
	    return;
	synchronized (threads) {
	    for(Thread query : threads) {
		if(query.getState() == Thread.State.NEW)
		    query.start();
	    }
	}
	*/
    }


    public synchronized  void queryReturned(QueryResultEvent<T> event)
    {
	//remove((MultiplexNameUageQuery)event.getSource());
	Collection<T> nodes = event.getResults();
	if(nodes == null)
	    return;
	Iterator<T> iterator = nodes.iterator();
	T node = iterator.next();
	NameTreeModel model = new NameTreeModel(node);
	model.setViewName(node.getViewName());

	unifiedTreeModel.add(model);
	//threads.remove(event.getThread());
	/*
	for(NameTreeTable<?> table : nameTreeTables) {
	    ((NameTreeTableModel)table.getModel()).getUnitedNameTreeModel().add(model);
	}
	*/
	//revalidate();
    }
}

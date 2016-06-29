/*
 * TableColumnModelSynchronizer.java:  synchronizes TableColumnModels
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import javax.swing.ListSelectionModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.nomencurator.gui.swing.NameTreeTable;

/**
 * <tt>TableColumnModelSynchronizer</tt> synchronizes <tt>TableColumnModel</tt>s in their
 * width etc.
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class TableColumnModelSynchronizer
    extends HashMap<NameTreeTable<?>, TableColumnModel>
    implements TableColumnModelListener
{
    private static final long serialVersionUID = 4014722380610055704L;

    protected List<TableColumnSynchronizer> columns;

    protected Object eventSource;

    public TableColumnModelSynchronizer()
    {
	super();
    }

    protected void initializeLocalVars()
    {
	eventSource = null;
    }

    public TableColumnModel put(NameTreeTable<?> key, TableColumnModel model)
    {
	if(columns == null) {
	    columns = new ArrayList<TableColumnSynchronizer>();
	}

	TableColumnModel object = super.get(key);
	if(object == model)
	    return object;

	object = super.put(key, model);
	if (object != null)
	    object.removeColumnModelListener(this);
	model.addColumnModelListener(this);

	int columnCount = model.getColumnCount();
	for(int i = columns.size(); i < columnCount; i++)
	    columns.add(new TableColumnSynchronizer());

	for(int i = 0; i < columnCount; i++) {
	    columns.get(i).put(key, model.getColumn(i));
	}

	return object;
    }

    public TableColumnModel getTableColumnModel(Object key)
    {
	return super.get(key);
    }

    public TableColumnModel remove(Object key)
    {
	TableColumnModel model = super.get(key);
	if(model == null)
	    return null;

	int columnCount = model.getColumnCount();
	for(int i = 0; i < columnCount; i++) {
	    columns.get(i).remove(key);
	}

	model.removeColumnModelListener(this);
	model = super.remove(key);

	if(isEmpty()) {
	    columns.clear();
	    columns = null;
	}

	return model;
    }

    public void clear()
    {
	new Throwable().printStackTrace(System.out);
	Set<NameTreeTable<?>> keys = keySet();
	for (NameTreeTable<?> key : keys) {
	    TableColumnModel model = get(key);
	    int columnCount = model.getColumnCount();
	    for(int i = 0; i < columnCount; i++) {
		columns.get(i).remove(key);
	    }
	    model.removeColumnModelListener(this);
	}
	columns.clear();
	super.clear();
    }



    public synchronized void setEventSource(Object source)
    {
	eventSource = source;
    }

    public Object getEventSource()
    {
	return eventSource;
    }

    public void columnAdded(TableColumnModelEvent event)
    {
	if(isEmpty())
	    return;

	if(getEventSource() != null)
	    return;

	setEventSource(event.getSource());


	setEventSource(null);
    }

    public void columnMoved(TableColumnModelEvent event)
    {
	if(isEmpty() || getEventSource() != null) {
	    return;
	}

	setEventSource(event.getSource());

	int columnIndex = event.getFromIndex();
	int newIndex = event.getToIndex();

	Collection<TableColumnModel> models = values();
	for (TableColumnModel model : models) {
	    if(model == getEventSource())
		continue;
	    model.moveColumn(columnIndex, newIndex);
	}

	if(columnIndex != newIndex) {
	    TableColumnSynchronizer column = columns.get(columnIndex);
	    columns.remove(columnIndex);
	    columns.add(newIndex, column);
	}

	setEventSource(null);
    }
     
    public void columnRemoved(TableColumnModelEvent e)
    {
    }

    public void columnMarginChanged(ChangeEvent e)
    {
    }

    public void columnSelectionChanged(ListSelectionEvent e) 
    {
    }

}

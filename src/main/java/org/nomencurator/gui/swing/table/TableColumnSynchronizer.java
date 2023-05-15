/*
 * TableColumnSynchronizer.java:  synchronize width etc. of 
 * TableColumn
 *
 * Copyright (c) 2003, 2005, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Enumeration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.swing.table.TableColumn;

import org.nomencurator.gui.swing.NameTreeTable;

/**
 * {@code TableColumnSynchronizer} provides a mechanism to make
 * {@code TableColumn}s having the same width and resizability.
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class TableColumnSynchronizer
    extends HashMap<NameTreeTable<?>, TableColumn>
    implements PropertyChangeListener
{
    private static final long serialVersionUID = -235397184005457132L;

    protected TableColumn eventSource;

    protected boolean sync;

    public TableColumnSynchronizer()
    {
	super();
	initializeLocalVars();
    }

    protected void initializeLocalVars()
    {
	eventSource = null;
	sync = true;
    }

    public TableColumn put(NameTreeTable<?> key, TableColumn column)

    {
	TableColumn previous = super.get(key);
	if(previous == column)
	    return previous;

	if(previous != null)
	    previous.removePropertyChangeListener(this);
	column.addPropertyChangeListener(this);

	return super.put(key, column);
    }

    public TableColumn remove(Object key)
    {
	TableColumn column = super.get(key);
	if(column == null)
	    return null;

	column.removePropertyChangeListener(this);
	super.remove(key);
	return column;
    }


    public TableColumn getTableColumn(Object key)
    {
	return super.get(key);
    }

    public synchronized void setEventSource(TableColumn column)
    {
	eventSource = column;
    }

    public TableColumn getEventSource()
    {
	return eventSource;
    }

    public void propertyChange(PropertyChangeEvent event)
    {

	if(!isSynchronized())
	    return;

	if(isEmpty())
	    return;

	if(getEventSource() != null)
	    return;

	setEventSource((TableColumn)event.getSource());

	String propertyName = event.getPropertyName();
	Object value = event.getNewValue();

	if (propertyName.equals("modelIndex"))
	    setModelIndex(((Integer)value).intValue());
	else if(propertyName.equals("width"))
	    setWidth(((Integer)value).intValue());
	else if (propertyName.equals("preferredWidth"))
	    setPreferredWidth(((Integer)value).intValue());
	else if (propertyName.equals("maxWidth"))
	    setMaxWidth(((Integer)value).intValue());
	else if (propertyName.equals("minWidth"))
	    setMaxWidth(((Integer)value).intValue());
	else if (propertyName.equals("isResizable"))
	    setResizable(((Boolean)value).booleanValue());
	setEventSource(null);
    }

    public void setModelIndex(int index)
    {
	TableColumn skip = getEventSource();
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    if(column == skip)
		continue;
	    column.setModelIndex(index);
	}
    }

    public void setWidth(int width)
    {
	TableColumn skip = getEventSource();
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    if(column == skip)
		continue;
	    column.setWidth(width);
	}
    }

    
    public void setPreferredWidth(int width)
    {
	TableColumn skip = getEventSource();
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    if(column == skip)
		continue;
	    column.setPreferredWidth(width);
	}
    }

    public int getPreferredMaxWidth()
    {
	if(isEmpty())
	    return -1;

	int width = Integer.MIN_VALUE;

	int w = width;
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    w = column.getPreferredWidth();
	    if(w > width)
		width = w;
	}
	return width;
    }

    public int getPreferredMinWidth()
    {
	if(isEmpty())
	    return -1;

	int width = Integer.MAX_VALUE;
	int w = width;
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    w = column.getPreferredWidth();
	    if(w < width)
		width = w;
	}
	return width;
    }

    public void setMinWidth(int width)
    { 
	TableColumn skip = getEventSource();
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    if(column == skip)
		continue;
	    column.setMinWidth(width);
	}
    }

    public int getMinWidth()
    { 
	if(isEmpty())
	    return -1;

	int width = Integer.MAX_VALUE;

	int w = width;
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    w = column.getMinWidth();
	    if(w < width)
		width = w;
	}
	return width;
    }

    public void setMaxWidth(int width)
    {
	TableColumn skip = getEventSource();
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    if(column == skip)
		continue;
	    column.setMaxWidth(width);
	}
    }

    public int getMaxWidth()
    { 
	if(isEmpty())
	    return -1;

	int width = Integer.MIN_VALUE;
	int w = width;
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    w = column.getMinWidth();
	    if(w > width)
		width = w;
	}
	return width;
    }

    public void setResizable(boolean isResizable)
    {
	TableColumn skip = getEventSource();
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    if(column == skip)
		continue;
	    column.setResizable(isResizable);
	}
    }

    public void sizeWidthToFit()
    {
	Collection<TableColumn> columns = super.values();
	for (TableColumn column : columns) {
	    column.sizeWidthToFit();
	}
    }

    public synchronized void setSynchronized(boolean sync)
    {
	this.sync = sync;
    }

    public boolean isSynchronized()
    {
	return sync;
    }
}

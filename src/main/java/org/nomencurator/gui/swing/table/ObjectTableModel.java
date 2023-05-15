/*
 * ObjectTableModel.java:  a TableModel to manage Objects
 *
 * Copyright (c) 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.nomencurator.gui.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.event.TableModelEvent;

import javax.swing.table.AbstractTableModel;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;

/**
 * {@code ObjectTableModel} is an abstract {@code TableModel}
 * to manage a table representation of a list of {@code Object}s
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class ObjectTableModel<E>
    extends AbstractTableModel
{
    private static final long serialVersionUID = 7704860335229753046L;

    protected List<E> objects;

    protected String[] columnNames;

    protected Locale locale;

    public ObjectTableModel()
    {
	this(Locale.getDefault());
    }

    public ObjectTableModel(Locale locale)
    {
	objects = Collections.synchronizedList(new ArrayList<E>());
	prepareColumnNames();
	setLocale(locale);
    }

    protected abstract void prepareColumnNames();

    public int getColumnCount()
    {
	return columnNames.length;
    }


    public int getRowCount()
    {
	return objects.size();
    }

    public Object getValueAt(int row, int column)
    {
	if(row < 0 || row > getRowCount())
	    return null;

	E namedObject = objects.get(row);

	if(namedObject == null)
	    return null;

	if(column < 0 || column > getColumnCount() - 1)
	    return namedObject;
	
	return null;
    }

    public String getColumnName(int column)
    {
	if(columnNames == null ||
	   column < 0 || column > columnNames.length - 1)
	    return null;

	return columnNames[column];
    }

    /**
     * Returns {@code Locale} localizing this {@code Component}
     *
     * @return Locale localizing this
     */
    public Locale getLocale()
    {
	return locale;
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public abstract void setLocale(Locale locale);

    public boolean isCellEditable(int row, int column)
    {

	//return false;
	if(row > -1 && row < getRowCount() &&
	   column > -1 && column < columnNames.length)
	    return true;

	return false;
    }

    public void add(E object)
    {
	if(object == null)
	    return ;
	int row = objects.size();
	objects.add(object);
	fireTableRowsInserted(row, row);
    }

    public void add(Collection<? extends E> e)
    {
	if(e == null || e.isEmpty())
	    return;
	
	objects.addAll(e);
	fireTableDataChanged();
    }

    public void add(Enumeration<? extends E> e)
    {
	if(e == null || !e.hasMoreElements())
	    return;

	while(e.hasMoreElements()) {
	    objects.add(e.nextElement());
	}
	fireTableDataChanged();
    }

    public void add(Iterator<? extends E> e)
    {
	if(e == null || !e.hasNext())
	    return;

	while(e.hasNext()) {
	    objects.add(e.next());
	}
	fireTableDataChanged();
    }

    public void add(E[] objects)
    {
	if(objects == null || objects.length == 0)
	    return;

	for(E object: objects) {
	    this.objects.add(object);
	}
	fireTableDataChanged();
    }

    public boolean remove(int index)
    {
	if(index >= objects.size())
	    return false;

	Object o = objects.remove(index);

	if(o == null)
	    return false;

	fireTableRowsDeleted(index, index);
	return true;
    }

    public boolean remove(E object)
    {
	int index = objects.indexOf(object);
	if(index < 0)
	    return false;
	boolean result = 
	    objects.remove(object);
	if(result)
	    fireTableRowsDeleted(index, index);
	return result;
    }

    public boolean remove(E[] objects)
    {
	if(objects == null ||
	   objects.length == 0)
	    return false;

	boolean result = true;
	int size = this.objects.size();
	for(E object: objects) {
	    result &= 
		this.objects.remove(object);
	}
	if(this.objects.size() < size)
	    fireTableDataChanged();
	return result;
    }

    public void clear()
    {
	objects.clear();
	fireTableDataChanged();
    }

    public void set(Collection<? extends E> e)
    {
	objects.clear();
	if(e == null) {
	    fireTableDataChanged();
	}
	else {
	    add(e);
	}
    }

    public void set(Enumeration<? extends E> e)
    {
	objects.clear();
	if(e == null) {
	    fireTableDataChanged();
	}
	else {
	    add(e);
	}
    }

    public void set(E[] objects)
    {
	this.objects.clear();
	if(objects == null) {
	    fireTableDataChanged();
	}
	else {
	    add(objects);
	}
    }

    public E getObject(int row)
    {
	return objects.get(row);
    }

    public int indexOf(E object)
    {
	return objects.indexOf(object);
    }

    public void setValueAt(Object value, int row, int column)
    {
    }

    protected List<E> getObjects()
    {
	return objects;
    }

    public Enumeration<E> enumeration()
    {
	return Collections.enumeration(objects);
    }

    public Iterator<E> iterator()
    {
	return objects.iterator();
    }

    public int setObjects(Collection<E> objects)
    {
	int rows = this.objects.size();
	this.objects.clear();
	if(objects != null) {
	    this.objects.addAll(objects);
	}

	int size = this.objects.size();

	if(size > 0)
	    fireTableRowsUpdated(0, size - 1);
	else if(rows > 1)
	    fireTableRowsDeleted(0, rows-1);

	return size;
    }

    public int setObjects(Enumeration<E> objects)
    {
	int rows = this.objects.size();
	this.objects.clear();
	int i = 0;
	if(objects != null) {
	    while(objects.hasMoreElements()){
		this.objects.add(objects.nextElement());
		i++;
	    }
	}

	if(i > 0)
	    fireTableRowsUpdated(0, i-1);
	else if(rows > 1)
	    fireTableRowsDeleted(0, rows-1);

	return i;
    }

    public int setObjects(E[] objects)
    {
	int rows = this.objects.size();
	this.objects.clear();
	int i = 0;
	if(objects != null) {
	    for(E object : objects) {
		this.objects.add(object);
	    }
	}

	if(i > 0)
	    fireTableRowsUpdated(0, i-1);
	else if(rows > 1)
	    fireTableRowsDeleted(0, rows-1);

	return i;
    }

    public int getIndexOf(E object)
    {
	synchronized (objects) {
	    if(objects == null ||
	       objects.size() == 0)
		return -1;
	    
	    return objects.indexOf(object);
	}
    }

    public int[] getIndicesOf(E[] objects)
    {
	if(this.objects == null ||
	   this.objects.size() == 0)
	    return null;

	int[] indices = new int[objects.length];
	int i = 0;
	synchronized (this.objects) {
	    for(E object: objects) {
		indices[i++] = 
		    getIndexOf(object);
	    }
	}

	return indices;
    }

}



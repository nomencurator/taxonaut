/*
 * NamedObjectTableModel.java:  a TableModel to manage NameUsage
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
 * {@code NamedObjectTableModel} is an abstract {@code TableModel}
 * to manage a table representation of a list of {@code NamedObject}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.NamedObject
 *
 * @version 	07 July 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class NamedObjectTableModel<T extends NamedObject<?>>
    extends ObjectTableModel<T>
{
    private static final long serialVersionUID = -3638313284499918045L;

    public NamedObjectTableModel()
    {
	super();
    }

    public NamedObjectTableModel(Locale locale)
    {
	super(locale);
    }

    public T getNamedObject(int row)
    {
	return getObject(row);
    }

    public static <E extends NamedObject<?>> String getNames(Enumeration<E> e)
    {
	if(e == null || !e.hasMoreElements())
	    return null;
	StringBuffer buffer = new StringBuffer();
	while(e.hasMoreElements()) {
	    E namedObject = e.nextElement();
	    if(buffer.length() > 0)
		buffer.append(", ");
	    buffer.append(namedObject.getLiteral());
	}
	return buffer.toString();
    }

    public static <E extends NamedObject<?>> String getNames(Iterator<E> e)
    {
	if(e == null || !e.hasNext())
	    return null;
	StringBuffer buffer = new StringBuffer();
	while(e.hasNext()) {
	    E namedObject = e.next();
	    if(buffer.length() > 0)
		buffer.append(", ");
	    buffer.append(namedObject.getLiteral());
	}
	return buffer.toString();
    }

    public static <E extends NamedObject<?>> String getNames(E[] namedObjects)
    {
	if(namedObjects == null ||
	   namedObjects.length == 0)
	    return null;

	StringBuffer buffer = new StringBuffer();
	for(E namedObject: namedObjects) {
	    if(namedObject == null)
		continue;
	    if(buffer.length() > 0)
		buffer.append(", ");
	    buffer.append(namedObject.getLiteral());
	}
	return buffer.toString();
    }

    public static <E extends NamedObject<?>> String getNames(Collection<E> namedObjects)
    {
	if(namedObjects == null ||
	   namedObjects.size() == 0)
	    return null;

	StringBuffer buffer = new StringBuffer();
	for(E namedObject: namedObjects) {
	    if(namedObject == null)
		continue;
	    if(buffer.length() > 0)
		buffer.append(", ");
	    buffer.append(namedObject.getLiteral());
	}
	return buffer.toString();
    }
}



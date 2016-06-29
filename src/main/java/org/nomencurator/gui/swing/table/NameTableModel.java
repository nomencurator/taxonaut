/*
 * NameTableModel.java:  a TableModel to list NameUsages
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

package org.nomencurator.gui.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Publication;

import org.nomencurator.model.util.NameUsageAttribute;

import org.nomencurator.gui.swing.tree.NameTreeNode;

import org.nomencurator.resources.ResourceKey;

import lombok.Getter;

/**
 * {@code NameTableModel} is a {@code TableModel} to
 * manage {@code NameUsage}s.
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTableModel/*<N extends NameUsage<?, ?>>*/
    extends NamedObjectTableModel<NameUsage<?, ?>>
{
    private static final long serialVersionUID = 8280031941034886624L;

    protected static NameUsageAttribute[] contents = NameUsageAttribute.values();

    public static final int RANK = NameUsageAttribute.RANK.ordinal();
    public static final int NAME =  NameUsageAttribute.NAME.ordinal();
    public static final int AUTHORITY =  NameUsageAttribute.AUTHORITY.ordinal();
    public static final int YEAR =  NameUsageAttribute.YEAR.ordinal();
    public static final int SENSU =  NameUsageAttribute.SENSU.ordinal();
    public static final int DATASET =  NameUsageAttribute.DATASET.ordinal();
    public static final int DESCENDANTS_COUNT =  NameUsageAttribute.DESCENDANTS_COUNT.ordinal();

    public NameTableModel()
    {
	this(Locale.getDefault());
    }

    public NameTableModel(Locale locale)
    {
	super(locale);
    }

    public NameTableModel(Iterator<? extends NameUsage<?, ?>> names)
    {
	this(names, Locale.getDefault());
    }

    public NameTableModel(Iterator<? extends NameUsage<?, ?>> names,
			  Locale locale)
    {
	this(locale);

	if(names == null)
	    return;

	add(names);

	setLocale(locale);
    }

    public NameTableModel(NameUsage<?, ?>[] names,
			  Locale locale)
    {
	this(locale);

	if(names == null)
	    return;

	add(names);
    }

    public NameTableModel(NameTreeNode[] names,
			  Locale locale)
    {
	this(locale);

	if(names == null)
	    return;

	for(int i = 0; i < names.length; i++) {
	    add(/*(N)*/names[i].getNameUsage());
	}
    }

    protected void prepareColumnNames()
    {
	columnNames = new String[contents.length];
    }

    public int getColumnCount()
    {
	return contents.length;
    }

    public Class<?> getColumnClass(int column)
    {
	return NameUsage.class;
    }

    public Object getValueAt(int row, int column)
    {
	if(column < 0 || column > contents.length ||
	   row < 0 || row > getObjects().size())
	    return null;

	Object o = getObjects().get(row);
	NameUsage<?, ?> nameUsage = null;
	if(o instanceof NameUsage)
	    nameUsage = (NameUsage<?, ?>)o;
	else if(o instanceof NameTreeNode)
	    nameUsage = 
		((NameTreeNode)o).getNameUsage();

	return nameUsage;
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
	if(locale.equals(this.locale))
	    return;

	this.locale = locale;
	if(columnNames == null)
	    return;

	columnNames[DESCENDANTS_COUNT] = ResourceKey.DESCENDANTS_COUNT_COLUMN;
	columnNames[RANK] = ResourceKey.RANK_COLUMN;
	columnNames[NAME] = ResourceKey.TAXON_NAME_COLUMN;
	columnNames[AUTHORITY] = ResourceKey.AUTHORITY_COLUMN;
	columnNames[YEAR] = ResourceKey.YEAR_COLUMN;
	columnNames[SENSU] = ResourceKey.SENSU_COLUMN;
	columnNames[DATASET] = ResourceKey.DATASET_COLUMN;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[DESCENDANTS_COUNT] = 
		resource.getString(columnNames[DESCENDANTS_COUNT]);
	    columnNames[RANK] = 
		resource.getString(columnNames[RANK]);
	    columnNames[NAME] = 
		resource.getString(columnNames[NAME]);
	    columnNames[AUTHORITY] = 
		resource.getString(columnNames[AUTHORITY]);
	    columnNames[YEAR] = 
		resource.getString(columnNames[YEAR]);
	    columnNames[SENSU] = 
		resource.getString(columnNames[SENSU]);
	    columnNames[DATASET] = 
		resource.getString(columnNames[DATASET]);
	}
	catch(MissingResourceException e) {

	}
    }

}

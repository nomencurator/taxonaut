/*
 * LinkTypeTableModel.java:  a TableModel to manage LinkTypes
 *
 * Copyright (c) 2003, 2004, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing.table;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.event.TableModelEvent;

import javax.swing.table.AbstractTableModel;

import org.nomencurator.model.LinkType;
import org.nomencurator.model.LinkTypes;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code LinkTypeTableModel} is a {@code TableModel} to
 * manage {@code LinkType}.
 *
 * @version 	28 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class LinkTypeTableModel
    extends AbstractTableModel
{
    private static final long serialVersionUID = 876767513099922276L;

    public static final int LINK_TYPE = 0;
    public static final int ANNOTATOR = LINK_TYPE + 1;
    public static final int ANNOTATANT = ANNOTATOR + 1;
    public static final int LAST_COLUMN = ANNOTATANT + 1;

    protected LinkTypes linkTypes;

    protected String[] columnNames;

    protected Locale locale;

    public LinkTypeTableModel()
    {
	this(Locale.getDefault());
    }

    public LinkTypeTableModel(Locale locale)
    {
	this(new LinkTypes(), locale);
    }

    public LinkTypeTableModel(LinkTypes linkTypes)
    {
	this(linkTypes, Locale.getDefault());
    }

    public LinkTypeTableModel(LinkTypes linkTypes,
			  Locale locale)
    {
	this.linkTypes = linkTypes;

	columnNames = new String[LAST_COLUMN];
	setLocale(locale);
    }

    public int getColumnCount()
    {
	return LAST_COLUMN;
    }


    public int getRowCount()
    {
	return linkTypes.getSize();
    }

    public Object getValueAt(int row, int column)
    {
	if(column < 0 || column > LAST_COLUMN - 1 ||
	   row < 0 || row > getRowCount())
	    return null;

	LinkType linkType = linkTypes.get(row);
	if(linkType == null)
	    return null;

	switch(column) {
	case LINK_TYPE:
	    return linkType.getLinkType();
	case ANNOTATOR:
	    return linkType.getAnnotatorCardinality();
	case ANNOTATANT:
	    return linkType.getAnnotatantCardinality();
	}
	
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
    public void setLocale(Locale locale)
    {
	if(locale == null)
	    locale = Locale.getDefault();
	if(locale.equals(this.locale))
	    return;

	this.locale = locale;
	if(columnNames == null)
	    return;

	columnNames[LINK_TYPE] = ResourceKey.LINK_TYPE;
	columnNames[ANNOTATOR] = ResourceKey.ANNOTATOR_CARDINALITY;
	columnNames[ANNOTATANT] = ResourceKey.ANNOTATANT_CARDINALITY;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[LINK_TYPE] = 
		resource.getString(columnNames[LINK_TYPE]);
	    columnNames[ANNOTATOR] = 
		resource.getString(columnNames[ANNOTATOR]);
	    columnNames[ANNOTATANT] = 
		resource.getString(columnNames[ANNOTATANT]);
	}
	catch(MissingResourceException e) {

	}
    }

    public boolean isCellEditable(int row, int column)
    {
	if(row < LinkTypes.getDefaultLinkTypeCount())
	    return false;

	return true;
    }

    public void add(LinkType linkType)
    {
	int row = linkTypes.getSize();
	linkTypes.add(linkType);
	fireTableRowsInserted(row, row);
    }

    public void setValueAt(Object value, int row, int column)
    {
	if(row < LinkTypes.getDefaultLinkTypeCount())
	    return;

	LinkType linkType = linkTypes.get(row);
	switch(column) {
	case 0:
	    linkType.setLinkType((String)value);
	    break;
	case 1:
	    linkType.setAnnotatorCardinality((String)value);
	    break;
	case 2:
	    linkType.setAnnotatantCardinality((String)value);
	    break;
	}
    }
}

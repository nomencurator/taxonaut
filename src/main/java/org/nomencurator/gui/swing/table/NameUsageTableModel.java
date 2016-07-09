/*
 * NameUsageTableModel.java:  a TableModel to manage NameUsage
 *
 * Copyright (c) 2004, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;


/**
 * {@code NameUsageTableModel} is a {@code TableModel}
 * to manage a table representation of a list of {@code NameUsage}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.NameUsage
 *
 * @version 	02 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameUsageTableModel
    extends NamedObjectTableModel<NameUsage<?>>
{
    private static final long serialVersionUID = 8754198090017932443L;

    public static final int LOCALE = 0;
    public static final int RANK = LOCALE+1;
    public static final int LITERAL = RANK + 1;
    public static final int SENSU = LITERAL + 1;
    public static final int LAST_COLUMN = SENSU + 1;
    //public static final int LAST_COLUMN = SENSU;

    public NameUsageTableModel()
    {
	this(Locale.getDefault());
    }

    public NameUsageTableModel(Locale locale)
    {
	super(locale);
    }

    protected void prepareColumnNames()
    {
	columnNames = new String[LAST_COLUMN];
    }

    public Object getValueAt(int row, int column)
    {
	NameUsage<?> nameUsage = 
	    (NameUsage<?>)super.getValueAt(row, -1);

	if(nameUsage == null)
	    return null;

	if(column < 0 || column > LAST_COLUMN - 1)
	    return nameUsage;

	String toReturn = null;
	switch(column) {
	case LOCALE:
	    Locale locale = nameUsage.getLocale();
	    if(locale != null)
		toReturn = locale.toString();
	    else
		toReturn = "";
	    break;
	case RANK:
	    //return nameUsage.getRank();
	    toReturn = Rank.getRank(nameUsage);
	    break;
	case LITERAL:
	    //return nameUsage.getLiteral();
	    toReturn = nameUsage.getLiteral();
	    break;
	case SENSU:
	    NameUsage<?> sensu = nameUsage.getSensu();
	    if(sensu == null)
		return "not specified";
	    if(sensu == nameUsage)
		return null;
	    StringBuffer buffer = new StringBuffer();
	    buffer.append(Rank.getRank(sensu));
	    if(buffer.length() > 0)
		buffer.append(' ');
	    buffer.append(sensu.getLiteral());
	    
	    //return buffer.toString();
	    toReturn = buffer.toString();
	}
	
	//return null;

	if(toReturn == null)
	    toReturn = "";

	return toReturn;
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

	columnNames[LOCALE] = ResourceKey.LOCALE_COLUMN;
	columnNames[RANK] = ResourceKey.RANK_COLUMN;
	columnNames[LITERAL] = ResourceKey.TAXON_NAME_COLUMN;
	columnNames[SENSU] = ResourceKey.SENSU_COLUMN;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[LOCALE] = 
		resource.getString(columnNames[LOCALE]);
	    columnNames[RANK] = 
		resource.getString(columnNames[RANK]);
	    columnNames[LITERAL] = 
		resource.getString(columnNames[LITERAL]);
	    columnNames[SENSU] = 
		resource.getString(columnNames[SENSU]);
	}
	catch(MissingResourceException e) {

	}
    }

}



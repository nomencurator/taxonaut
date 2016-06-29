/*
 * AffiliationTableModel.java:  a TableModel to manage Appearance
 *
 * Copyright (c) 2004, 2015, 2016 Nozomi `James' Ytow
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

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.nomencurator.model.Affiliation;

import org.nomencurator.resources.ResourceKey;


/**
 * {@code AffiliationTableModel} is a {@code TableModel}
 * to manage a table representation of a list of {@code Affiliation}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Affiliation
 *
 * @version 	27 June 2016
 * @affiliation 	Nozomi `James' Ytow
 */
public class AffiliationTableModel
    extends ObjectTableModel<Affiliation>
{
    private static final long serialVersionUID = -1301598452929856149L;

    public static final int INSTITUTE = 0;
    public static final int SINCE = INSTITUTE + 1;
    public static final int UNTIL = SINCE + 1;
    public static final int LAST_COLUMN = UNTIL + 1;

    public AffiliationTableModel()
    {
	this(Locale.getDefault());
    }

    public AffiliationTableModel(Locale locale)
    {
	super(locale);
    }

    protected void prepareColumnNames()
    {
	columnNames = new String[LAST_COLUMN];
    }

    public Object getValueAt(int row, int column)
    {
	Affiliation affiliation = 
	    (Affiliation)super.getValueAt(row, -1);

	if(affiliation == null)
	    return null;

	if(column < 0 || column > LAST_COLUMN - 1)
	    return affiliation;

	switch(column) {
	case INSTITUTE:
	    return affiliation.getInstitute();
	case SINCE:
	    return affiliation.getFrom();
	case UNTIL:
	    return affiliation.getUntil();
	}
	
	return null;
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

	columnNames[INSTITUTE] = ResourceKey.INSTITUTE_COLUMN;
	columnNames[SINCE] = ResourceKey.SINCE_COLUMN;
	columnNames[UNTIL] = ResourceKey.UNTIL_COLUMN;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[INSTITUTE] = 
		resource.getString(columnNames[INSTITUTE]);
	    columnNames[SINCE] = 
		resource.getString(columnNames[SINCE]);
	    columnNames[UNTIL] = 
		resource.getString(columnNames[UNTIL]);
	}
	catch(MissingResourceException e) {

	}
    }
}

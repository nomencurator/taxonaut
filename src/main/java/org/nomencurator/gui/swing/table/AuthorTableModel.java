/*
 * AuthorTableModel.java:  a TableModel to manage Authors
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

import org.nomencurator.model.Agent;
import org.nomencurator.model.Person;

import org.nomencurator.resources.ResourceKey;


/**
 * {@code AuthorTableModel} is a {@code TableModel}
 * to manage a table representation of a list of {@code Person}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Person
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class AuthorTableModel
    extends NamedObjectTableModel<Agent> 
{
    private static final long serialVersionUID = 2951999725595983778L;

    public static final int NAME = 0;
    public static final int LAST_COLUMN = NAME + 1;

    public AuthorTableModel()
    {
	this(Locale.getDefault());
    }

    public AuthorTableModel(Locale locale)
    {
	super(locale);
    }

    protected void prepareColumnNames()
    {
	columnNames = new String[LAST_COLUMN];
    }

    public Object getValueAt(int row, int column)
    {
	Person author = 
	    (Person)super.getValueAt(row, -1);

	if(author == null)
	    return null;

	if(column < 0 || column > LAST_COLUMN - 1)
	    return author;

	switch(column) {
	case NAME:
	    return author.getFullname();
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

	columnNames[NAME] = ResourceKey.NAME_COLUMN;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[NAME] = 
		resource.getString(columnNames[NAME]);
	}
	catch(MissingResourceException e) {

	}
    }
}

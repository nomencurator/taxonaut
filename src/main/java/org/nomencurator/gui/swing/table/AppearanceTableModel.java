/*
 * AppearanceTableModel.java: a TableModel to manage Appearance
 *
 * Copyright (c) 2004, 2006, 2015, 2016 Nozomi `James' Ytow
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

import org.nomencurator.model.Appearance;
import org.nomencurator.model.NameUsage;

import org.nomencurator.resources.ResourceKey;


/**
 * {@code AppearanceTableModel} is a {@code TableModel}
 * to manage a table representation of a list of {@code Appearances}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Appearance
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class AppearanceTableModel
    extends NamedObjectTableModel<Appearance>
{
    private static final long serialVersionUID = 5033771391939174781L;

    public static final int PAGE = 0;
    public static final int LINES = PAGE + 1;
    public static final int NAMES = LINES + 1;
    public static final int LAST_COLUMN = NAMES + 1;

    public AppearanceTableModel()
    {
	this(Locale.getDefault());
    }

    public AppearanceTableModel(Locale locale)
    {
	super(locale);
    }

    protected void prepareColumnNames()
    {
	columnNames = new String[LAST_COLUMN];
    }

    public Object getValueAt(int row, int column)
    {
	Appearance appearance = 
	    (Appearance)super.getValueAt(row, -1);

	if(appearance == null)
	    return null;

	if(column < 0 || column > LAST_COLUMN - 1)
	    return appearance;

	switch(column) {
	case PAGE:
	    return appearance.getPages();
	case LINES:
	    return appearance.getLines();
	case NAMES:
	    return getNames(appearance.getNameUsages());
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

	columnNames[PAGE] = ResourceKey.PAGE_COLUMN;
	columnNames[LINES] = ResourceKey.LINES_COLUMN;
	columnNames[NAMES] = ResourceKey.NAMES_COLUMN;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[PAGE] = 
		resource.getString(columnNames[PAGE]);
	    columnNames[LINES] = 
		resource.getString(columnNames[LINES]);
	    columnNames[NAMES] = 
		resource.getString(columnNames[NAMES]);
	}
	catch(MissingResourceException e) {

	}
    }
}

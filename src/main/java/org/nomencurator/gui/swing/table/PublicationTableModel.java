/*
 * PublicationTableModel.java:  a TableModel to manage Appearance
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

import org.nomencurator.model.Publication;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code PublicationTableModel} is a {@code TableModel}
 * to manage a table representation of a list of {@code Appearances}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Publication
 *
 * @version 	29 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class PublicationTableModel
    extends NamedObjectTableModel<Publication>
{
    private static final long serialVersionUID = -2905995985993660541L;

    public static final int AUTHOR = 0;
    public static final int YEAR = AUTHOR + 1;
    public static final int CITATION = YEAR + 1;
    public static final int LAST_COLUMN = CITATION + 1;

    public PublicationTableModel()
    {
	this(Locale.getDefault());
    }

    public PublicationTableModel(Locale locale)
    {
	super(locale);
    }

    protected void prepareColumnNames()
    {
	columnNames = new String[LAST_COLUMN];
    }

    public Object getValueAt(int row, int column)
    {
	Publication publication = 
	    (Publication)super.getValueAt(row, -1);

	if(publication == null)
	    return null;

	if(column < 0 || column > LAST_COLUMN - 1)
	    return publication;

	switch(column) {
	case AUTHOR:
	    return publication.getCanonicalAuthorName();
	case YEAR:
	    return publication.getYear();
	case CITATION:
	    return publication.getCanonicalCitation();
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

	columnNames[AUTHOR] = ResourceKey.AUTHOR_COLUMN;
	columnNames[YEAR] = ResourceKey.YEAR_COLUMN;
	columnNames[CITATION] = ResourceKey.CITATION_COLUMN;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[AUTHOR] = 
		resource.getString(columnNames[AUTHOR]);
	    columnNames[YEAR] = 
		resource.getString(columnNames[YEAR]);
	    columnNames[CITATION] = 
		resource.getString(columnNames[CITATION]);
	}
	catch(MissingResourceException e) {

	}
    }
}

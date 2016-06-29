/*
 * AnnotationTableModel.java:  a TableModel to manage Annotation
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

import org.nomencurator.model.Annotation;

import org.nomencurator.resources.ResourceKey;


/**
 * {@code AnnotationTableModel} is a {@code TableModel}
 * to manage a table representation of a list of {@code Annotations}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Annotation
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class AnnotationTableModel
    extends NamedObjectTableModel<Annotation>
{
    private static final long serialVersionUID = -2450911567954015770L;

    public static final int LINK_TYPE = 0;
    public static final int ANNOTATOR = LINK_TYPE + 1;
    public static final int ANNOTATANT = ANNOTATOR + 1;
    public static final int LAST_COLUMN = ANNOTATANT + 1;

    public AnnotationTableModel()
    {
	this(Locale.getDefault());
    }

    public AnnotationTableModel(Locale locale)
    {
	super(locale);
    }

    protected void prepareColumnNames()
    {
	columnNames = new String[LAST_COLUMN];
    }

    public Object getValueAt(int row, int column)
    {
	Annotation annotation = 
	    (Annotation)super.getValueAt(row, -1);

	if(annotation == null)
	    return null;

	if(column < 0 || column > LAST_COLUMN - 1)
	    return annotation;

	switch(column) {
	case LINK_TYPE:
	    return annotation.getLinkType();
	case ANNOTATOR:
	    return getNames(annotation.getAnnotators());
	case ANNOTATANT:
	    return getNames(annotation.getAnnotatants());
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

	columnNames[LINK_TYPE] = ResourceKey.ANNOTATION_TYPE;
	columnNames[ANNOTATOR] = ResourceKey.ANNOTATOR_TAXA;
	columnNames[ANNOTATANT] = ResourceKey.ANNOTATED_TAXA;

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
}

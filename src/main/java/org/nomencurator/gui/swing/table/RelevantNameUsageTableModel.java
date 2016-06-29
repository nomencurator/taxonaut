/*
 * RelevantNameUsageTableModel.java:  a TableModel to manage Annotation
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.NameUsage;

import org.nomencurator.resources.ResourceKey;


/**
 * {@code RelevantNameUsageTableModel} is a {@code TableModel}
 * to manage a table representation of a list of {@code Annotations}s
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Annotation
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class RelevantNameUsageTableModel
    extends NamedObjectTableModel<Annotation>
{
    private static final long serialVersionUID = -3768372277546723480L;

    public static final int LINK_TYPE = 0;
    public static final int NAMEUSAGES = LINK_TYPE + 1;
    public static final int LAST_COLUMN = NAMEUSAGES + 1;

    public RelevantNameUsageTableModel()
    {
	this(Locale.getDefault());
    }

    public RelevantNameUsageTableModel(Locale locale)
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
	case NAMEUSAGES:
	    return getNames(annotation.getAnnotatants());
	    //return getNames(annotation.getAnnotators());
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
	columnNames[NAMEUSAGES] = ResourceKey.RELEVANT_TAXA;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, this.locale);
	    columnNames[LINK_TYPE] = 
		resource.getString(columnNames[LINK_TYPE]);
	    columnNames[NAMEUSAGES] = 
		resource.getString(columnNames[NAMEUSAGES]);
	}
	catch(MissingResourceException e) {

	}
    }

    protected static final int[] emptyIndices = new int[0];

    public int[] getIndicesOf(NameUsage<?, ?> nameUsage)
    {
	if(nameUsage == null)
	    return emptyIndices;
	
	
	List<Annotation> namedObjects = getObjects();
	synchronized (namedObjects) {
	    if(namedObjects == null ||
	       namedObjects.size() == 0) {
		return emptyIndices;
	    }
	    List<Annotation>list = addAnnotationsOn(nameUsage, null);
	    int[] indices = getIndicesOf(list);
	    list.clear();
	    return indices;
	}
    }

    public int[] getIndicesOf(NameUsage<?, ?>[] nameUsages)
    {
	if(nameUsages == null ||
	   nameUsages.length == 0)
	    return emptyIndices;

	List<Annotation>list = new ArrayList<Annotation>();
	List<Annotation> namedObjects = getObjects();
	synchronized (namedObjects) {
	    for(NameUsage<?, ?> nameUsage: nameUsages) {
		addAnnotationsOn(nameUsage, list);
	    }
	    int[] indices = getIndicesOf(list);
	    list.clear();
	    return indices;
	}
    }

    protected int[] getIndicesOf(List<Annotation> annotations)
    {
	int[] indices = new int[annotations.size()];
	int i = 0;
	List<Annotation> namedObjects = getObjects();
	for(Annotation annotation : annotations) {
	    indices[i++] = namedObjects.indexOf(annotation);
	}
	return indices;
    }


    protected List<Annotation> addAnnotationsOn(NameUsage<?, ?> nameUsage, List<Annotation>annotations)
    {
	if(annotations == null)
	    annotations = new ArrayList<Annotation>();
	List<Annotation> namedObjects = getObjects();
	synchronized (namedObjects) {
	    String pid = nameUsage.getPersistentID();
	    for(Annotation annotation: namedObjects) {
		Iterator<NameUsage<?, ?>> annotatants = annotation.getAnnotatants();
		if(annotatants == null)
		    continue;
		boolean found = false;
		while(annotatants.hasNext()) {
		    NameUsage<?, ?> annotatant = annotatants.next();
		    if(annotatant != null &&(
		       annotatant == nameUsage ||
		       (pid != null &&
			pid.equals(annotatant.getPersistentID())))) {
			found = true;
			break;
		    }
		}
		if(found && !annotations.contains(annotation))
		    annotations.add(annotation);
	    }
	}
	return annotations;
    }
}

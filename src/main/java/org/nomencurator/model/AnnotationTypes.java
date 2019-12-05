/*
 * AnnotationTypes.java:  managing link type of Annotation
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2004, 2014, 2015, 2019 Nozomi `James' Ytow
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

package org.nomencurator.model;

import java.beans.PropertyChangeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.nomencurator.beans.PropertyChanger;

import org.nomencurator.util.ArrayUtility;

/**
 * <code>AnnotationType</code> represents link types of Annotation data structure in Nomencurator data model
 *
 * @see org.nomencurator.model.Annotation
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class AnnotationTypes
    extends PropertyChanger
{
    static Map<String, AnnotationType> map;
    static Set<AnnotationType> set;

    static String[][] defaultAnnotationTypeData = {
	{"synonym", "1", "n"}
	, {"homonym", "n", "m"}
	, {"nec", "1", "n"}
	, {"refer", "1", "n"}
	, {"equiv", "n", "0"}
	, {"vernacular", "1", "n"}
    };
    
    static {
	AnnotationType annotationType = null;
	map = Collections.synchronizedMap(new HashMap<String, AnnotationType>(defaultAnnotationTypeData.length));
	set = Collections.synchronizedSet(new HashSet<AnnotationType>(defaultAnnotationTypeData.length));
	for(int i = 0; i < defaultAnnotationTypeData.length; i++) {
	    annotationType = new AnnotationType(defaultAnnotationTypeData[i][0],
				defaultAnnotationTypeData[i][1],
				defaultAnnotationTypeData[i][2]);

	    map.put(annotationType.getAnnotationType(), annotationType);
	    set.add(annotationType);
	}
    }

    public static int getDefaultAnnotationTypeCount()
    {
	return defaultAnnotationTypeData.length;
    }

    public AnnotationType[] getAnnoationTypes()
    {
	AnnotationType[] types = null;
	synchronized(set) {
	    types = new AnnotationType[set.size()];
	    types = set.toArray(types);
	}
	return types;
    }

    public String[] getAnnotationTypeNames()
    {
	String[] names = null;
	synchronized (map) {
	    names = new String[map.size()];
	    map.keySet().toArray(names);
	}

	return names;
    }

    public void add(AnnotationType annotationType)
    {
	if(annotationType == null)
	    return;

	String annotationTypeName = annotationType.getAnnotationType();
	if(annotationTypeName == null ||
	   get(annotationTypeName) != null)
	    return;

	map.put(annotationTypeName, annotationType); 
	set.add(annotationType);
	firePropertyChange(new PropertyChangeEvent(this, "add", null, annotationType));
    }

    public AnnotationType get(String typeName)
    {
	return map.get(typeName);
    }

    public void remove(String typeName)
    {
	if(typeName != null) {
	    AnnotationType annotationType = map.get(typeName);
	    if(annotationType != null) {
		map.remove(typeName);
		set.remove(annotationType);
	    }
	}
    }

    public AnnotationTypes()
    {
    }

    public int getSize()
    {
	if(set == null)
	    return -1;
	return set.size();
    }
}

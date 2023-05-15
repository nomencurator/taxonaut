/*
 * LinkType.java:  managing link type of Annotation
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2004, 2015, 2016, 2019 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.model;

import java.beans.PropertyChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nomencurator.beans.PropertyChanger;

/**
 * {@code LinkType} represents link types of Annotation data structure in Nomencurator data model
 *
 * @see org.nomencurator.model.Annotation
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class LinkTypes
    extends PropertyChanger
{
    static Map<String, LinkType> linkTypesMap;
    static List<LinkType> linkTypeList;

    static String[][] defaultLinkTypeData = {
	{"synonym", "1", "n"},
	{"homonym", "n", "m"},
	{"nec", "1", "n"},
	{"refer", "1", "n"},
	{"equiv", "n", "0"}
    };
    
    static {
	LinkType linkType = null;
	linkTypesMap = new HashMap<String, LinkType>(defaultLinkTypeData.length);
	linkTypeList = new ArrayList<LinkType>(defaultLinkTypeData.length);
	for(int i = 0; i < defaultLinkTypeData.length; i++) {
	    linkType = new LinkType(defaultLinkTypeData[i][0],
				defaultLinkTypeData[i][1],
				defaultLinkTypeData[i][2]);

	    linkTypesMap.put(defaultLinkTypeData[i][0], linkType);
	    linkTypeList.add(linkType);
	}
    }

    public static int getDefaultLinkTypeCount()
    {
	return defaultLinkTypeData.length;
    }

    public LinkType[] getLinkTypes()
    {
	return linkTypeList.toArray(new LinkType[linkTypeList.size()]);
    }

    public String[] getLinkTypeNames()
    {
	String[] names = null;
	synchronized (linkTypeList) {
	    List<String> linkTypeNames = new ArrayList<String>(linkTypeList.size());
	    for (LinkType linkType :  linkTypeList) {
		linkTypeNames.add(linkType.getLinkType());
	    }
	    names = linkTypeNames.toArray(new String[linkTypeNames.size()]);
	}

	return names;
    }

    public void add(LinkType linkType)
    {
	if(linkType == null)
	    return;

	String linkTypeName = linkType.getLinkType();
	if(linkTypeName == null ||
	   get(linkTypeName) != null)
	    return;

	linkTypesMap.put(linkTypeName, linkType);
	linkTypeList.add(linkType);
	firePropertyChange(new PropertyChangeEvent(this, "add", null, linkType));
    }

    public LinkType get(String linkType)
    {
	return linkTypesMap.get(linkType);
    }

    public LinkType get(int index)
    {
	return linkTypeList.get(index);
    }

    public void remove(String linkType)
    {
	if(linkType != null)
	    linkTypesMap.remove(linkType);
    }

    public LinkTypes()
    {
    }

    public int getSize()
    {
	if(linkTypeList == null)
	    return -1;
	return linkTypeList.size();
    }
}

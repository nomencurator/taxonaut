/*
 * NubResolver.java:  a GBIF CheklistBank NameUsage resolover with cache
 *
 * Copyright (c) 2014, 2016 Nozomi `James' Ytow
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

package org.nomencurator.io.gbif;

import org.gbif.api.model.checklistbank.VernacularName;

import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.gbif.api.vocabulary.Rank;

import org.nomencurator.io.AbstractNameUsageExchanger;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.gbif.NubNameUsage;

import org.nomencurator.model.AbstractNameUsage;
import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.Author;
import org.nomencurator.model.Name;

import org.nomencurator.model.Publication;

import org.nomencurator.api.gbif.SpeciesAPIClient;

import org.nomencurator.util.Locales;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;

/**
 * {@code NubResolver} resolved NameUsages in GBIF chcklistBank.
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NubResolver
//    extends AbstractNameUsageExchanger<NubNameUsage>
{
    @Getter
    //private static List<SpeciesAPIClient> dataSources;
    private static List<NubExchanger> dataSources;
    
    protected Map<Integer, org.gbif.api.model.checklistbank.NameUsage> scientificNameUsage;

    protected Map<Integer, NubNameUsage> scientificNameNubs;

    protected Map<Integer, VernacularName> vernacularNames;

    protected Map<Integer, NubNameUsage> vernacularNameNubs;

    public NubResolver() {};

    // need to redesing to implement resolver....
    public NubNameUsage getObject(String id)
    {
	//FIXME
	return null;
    }

    public Collection<NubNameUsage> getObjects(String id)
    {
	//FIXME
	return null;
    }

    public NubNameUsage getPartialHierarchy(NubNameUsage nameUsage, int height, int depth)
    {
	return nameUsage;
    }
    /*
    public Collection<NubNameUsage> getLowerTaxa(NubNameUsage nameUsage, boolean recursive)
    {
	if(!recursive)
	    return getLowerTaxa(nameUsage, 1);

	Collection<NameUsage<? extends NubNameUsage>> lowerTaxa = nameUsage.getLowerTaxa();
	List<NubNameUsage> list = new ArrayList<NubNameUsage>(lowerTaxa.size());
	for(NameUsage<? extends NubNameUsage> n: lowerTaxa) {
	    NubNameUsage nub = null;
	    if(n instanceof NubNameUsage) {
		nub = (NubNameUsage)n;
	    }
	    else {
		n = n.getEntity();
		if(n instanceof NubNameUsage) {
		    nub = (NubNameUsage)n;
		}
	    }
	    if(nub != null) {
		getLowerTaxa(nub, recursive);
		list.add(nub);
	    }
	}
	return list;
    }
    */

    public NubNameUsage getRoot(NubNameUsage nameUsage)
    {
	NameUsage<NubNameUsage> root = nameUsage.getRoot();
	if(root instanceof NubNameUsage)
	    return (NubNameUsage)root;
	else if (root.getEntity() instanceof NubNameUsage)
	    return root.getEntity();
	return 
	    null;
    }

    public NubNameUsage getNameUsage(String name)
    {
	//FIXME
	return null;
    }

    public Collection<NubNameUsage> getNameUsages(String name, int queryType)
    {
	//FIXME
	return null;
    }

    public Collection<NubNameUsage> getNameUsages(String rank, String name, int queryType)
    {
	//FIXME
	return null;
    }

}

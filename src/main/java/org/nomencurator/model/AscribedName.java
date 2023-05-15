/*
 * AscribedName.java: an implementation of a vertex in a name graph
 * with an ascribed name as its value.  It also retains 
 * pointers to correspoinding NameUsages, and other jointed
 * AscribedNames.
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * An {@code AscribedName} provides a vertex in a name graph.
 * It has an ascribed name as its value, lists of {@code NameUsageNode}s
 * used the ascribed name, higher and lower {@code AscribedName}s
 * of which names are used {@code NameUsage}s represented by
 * {@code NameUsageNode}s in the list.  The name graph enables
 * detection of inconsistency in name usages.
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class AscribedName
    extends AbstractName <AscribedName>
{
    private static final long serialVersionUID = -2541306348846950537L;

    protected Rank rank;  //do we need?

    protected AscribedName higherName;

    protected Map<String, AscribedName> lowerNames;

    // protected Map<AscribedName, Set<? extends NameUsage>> nameUsages;

    public AscribedName getHigherName()
    {
	return higherName;
    }

    public void setHigherName(AscribedName higher)
    {
	if(higher == higherName) 
	    return;

	if(higherName != null)
	    higherName.removeLowerName(this);

	higherName = higher;

	if(higherName != null)
	    higherName.addLowerName(this);

    }

    public void addLowerName(AscribedName lower)
    {
	if(lower == null)
	    return;

	if(lowerNames == null)
	    lowerNames = Collections.synchronizedMap(new HashMap<String, AscribedName>());

	lowerNames.put(lower.getLiteral(), lower);
	lower.setHigherName(this);
    }

    public void removeLowerName(AscribedName lower)
    {
	if(lower == null)
	    return;

	lowerNames.remove(lower.getLiteral());
	if(lowerNames.isEmpty())
	    lowerNames = null;

	lower.setHigherName(null);
    }

    public Set<String> getLowerNames()
    {
	if(lowerNames == null)
	    return null;

	return lowerNames.keySet();
    }

    public Collection<AscribedName> getLowerAscribedNames()
    {
	if(lowerNames == null)
	    return null;

	return lowerNames.values();
    }
}


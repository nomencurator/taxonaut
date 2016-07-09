/*
 * NameUsageAttributeComparator.java:  a TableCellRenerer for NameTreeNode
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model.util;

import java.util.Comparator;

import java.text.Collator;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.model.gbif.NubNameUsage;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code NameUsageAttributeComparator|} is a {@code TableCellRenderer} to render a {@code NameTree}.
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameUsageAttributeComparator
    implements Comparator<NameUsage<?>>
{
    @Getter protected boolean nullFirst;

    @Getter protected NameUsageAttribute attribute;

    protected RankComparator ranker;

    public NameUsageAttributeComparator(NameUsageAttribute attribute)
    {
	this(attribute, true);
    }

    public NameUsageAttributeComparator(NameUsageAttribute attribute, boolean nullFirst)
    {
	setNullFirst(nullFirst);
	setAttribute(attribute);
    }

    public void setNullFirst(boolean nullFirst)
    {
	if (this.nullFirst == nullFirst)
	    return;

	this.nullFirst = nullFirst;	
	if (ranker != null)
	    ranker.setNullFirst(nullFirst);
    }

    public void setAttribute(NameUsageAttribute attribute)
    {
	if (this.attribute != null && this.attribute.equals(attribute))
	    return;

	this.attribute = attribute;
	if (NameUsageAttribute.RANK.equals(attribute)) {
	    if (ranker == null)
		ranker = new RankComparator(isNullFirst());
	}
	else
	    ranker = null;
    }

    protected int cmpr(Integer i1, Integer i2)
    {
	if (i1 == null) {
	    if (i2 == null)
		return 0;
	    return isNullFirst() ? -1 : 1;
	} 
	else if (i2 == null)
	    return isNullFirst() ? 1 : -1;

	return i1.compareTo(i2);
    }

    protected int cmpr(String s1, String s2)
    {
	return isNullFirst() ? 
	    Comparator.nullsFirst(Collator.getInstance()).compare(s1, s2) :
	    Comparator.nullsLast(Collator.getInstance()).compare(s1, s2) ;
    }

    protected String getAuthority(NameUsage<?> nameUsage)
    {
	String authority = nameUsage.getAuthority();
	if (authority == null) {
	    nameUsage = nameUsage.getSensu();
	    if(nameUsage != null)
		authority = nameUsage.getViewName();
	}
	return authority;
    }

    public int compare(NameUsage<?> n1, NameUsage<?> n2)
    {
	switch (attribute) {
	case YEAR:
	    return cmpr(n1.getAuthorityYear(), n2.getAuthorityYear());
	case DESCENDANTS_COUNT:
	    return cmpr(n1.getDescendantCount(), n2.getDescendantCount()); 
	case RANK:
	    return ranker.compare(n1.getRank(), n2.getRank());
	case NAME:
	    return cmpr(n1.getLiteral(), n2.getLiteral());
	case  AUTHORITY:
	    return cmpr(getAuthority(n1), getAuthority(n2));
	case SENSU:
	    return cmpr(n1.getViewName(), n2.getViewName());
	case DATASET:
	    if (n1 == null || !(n1 instanceof NubNameUsage)
		|| n2 == null || !(n2 instanceof NubNameUsage))
		return 0;
	    return cmpr(((NubNameUsage)n1).getDatasetTitle(), 
			((NubNameUsage)n2).getDatasetTitle());
	default:
	    return 0;
	}
    }
}


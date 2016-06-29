/*
 * Roughset.java:  a class to provide a rough set
 * for TaxoNote based on Nomencurator data model
 *
 * Copyright (c) 2002, 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.util;

import java.util.Collection;
import java.util.Collections;
// import java.util.Map;
// import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.nomencurator.util.HashMap;
import org.nomencurator.util.Map;

/**
 * <code>RoughSet</code> is a class to compare
 * multiple hierarchies of names
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
//public class RoughSet<T>
public class RoughSet
{
    protected Set<?> core;
    protected Set<?> boundary;
    protected Set<?> excludants;

    public RoughSet()
    {
	this(null);
    }

    public RoughSet(Set<?> core)
    {
	this(core, null);
    }

    public RoughSet(Set<?> core, Set<?> excludants)
    {
	this(core, excludants, null);
    }

    public RoughSet(Set<?> core,
		    Set<?> excludants,
		    Set<?> boundary)
    {
	setCore(core);
	setExcludants(excludants);
	setBoundary(boundary);
    }

    public Set<?> getCore()
    {
	return core;
    }

    public void setCore(Set<?> core)
    {
	this.core = core;
    }

    public Set<?> getBoundary()
    {
	return boundary;
    }

    public void setBoundary(Set<?> boundary)
    {
	this.boundary = boundary;
    }

    public Set<?> getExcludants()
    {
	return excludants;
    }

    public Set<?> getUpper()
    {
	return SetUtility.union(getCore(), getBoundary());
    }

    public void setExcludants(Set<?> excludants)
    {
	this.excludants = excludants;
    }

    public RoughSet upperIntersection(RoughSet a)
    {
	if(a == null)
	    throw new IllegalArgumentException("RoughSet#upperIntersection");

	RoughSet intersection = new RoughSet(SetUtility.intersection(a.getCore(), getCore()));

	return crossSection(intersection, a, true);
    }

    public RoughSet loewrIntersection(RoughSet a)
    {
	if(a == null)
	    throw new IllegalArgumentException("RoughSet#loewrIntersection");

	RoughSet intersection = new RoughSet(SetUtility.intersection(a.getCore(), getCore()));

	return crossSection(intersection, a, false);
    }

    protected RoughSet crossSection(RoughSet crossSection, RoughSet a, boolean isUpper)
    {
	if(a == null)
	    throw new IllegalArgumentException("RoughSet#loewrIntersection");

	RoughSet intersection = new RoughSet(SetUtility.intersection(a.getCore(), getCore()));
	Set<?> upper = null;
	Set<?> thisUpper = getUpper();
	Set<?> thatUpper = a.getUpper();
	if(isUpper) 
	    upper = SetUtility.union(thisUpper, thatUpper);
	else
	    upper = SetUtility.union(SetUtility.intersection(thisUpper, thatUpper), SetUtility.union(SetUtility.sub(thisUpper, a.getExcludants()), SetUtility.sub(thatUpper, getExcludants())));
	crossSection.setBoundary(SetUtility.sub(upper, crossSection.getCore()));
	upper = SetUtility.union(upper, crossSection.getCore());
	crossSection.setExcludants(SetUtility.sub(SetUtility.union(a.getExcludants(), getExcludants()), upper));
	upper.clear();
	return crossSection;
    }

    public RoughSet union(RoughSet a)
    {
	if(a == null)
	    throw new IllegalArgumentException("RoughSet#union");

	RoughSet union = new RoughSet(SetUtility.union(a.getCore(), getCore()));
	return crossSection(union, a, true);
    }
}

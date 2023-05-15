/*
 * DefaultNameUsage.java:  a Java implementation of NameUsage class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.model;

import org.w3c.dom.Element;

/**
 * An implementation of {@code NameUsage}  in Nomencurator data model.
 * It was referred to as NameRecord in the original publication.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class DefaultNameUsage
    extends AbstractNameUsage<DefaultNameUsage>
{
    private static final long serialVersionUID = 6159012881992744269L;

    /** Constructs an "empty" <CODE>NameUsage</CODE> */
    public DefaultNameUsage()
    {
	super();
    }

    /**
     * Constructs an "empty" <CODE>NameUsage</CODE>
     * appeared in <CODE>appearance</CODE>
     *
     * @param appearance <CODE>Appearance</CODE> where the name used
     */
    public DefaultNameUsage(Appearance apperance)
    {
	super(apperance);
    }

    /**
     * Constructs a <CODE>NameUsage</CODE> object having
     * <CODE>persistentID</CODE> as its representation,
     */
    public DefaultNameUsage(String persistentID)
    {
	super(persistentID);
    }

    /**
     * Constructs a <CODE>NameUsage</CODE> based on
     * <CODE>nameUsage</CODE>
     */
    public DefaultNameUsage(Name<? extends DefaultNameUsage> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs a deep copy of <CODE>nameUsage</CODE>
     *
     * @param nameUsage <CODE>NameUsage</CODE> to be copied deeply
     */
    public DefaultNameUsage(NameUsage<?> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs a <CODE>NameUsage</CODE> by giving
     * its attributes.
     *
     * @param rank <CODE>String</CODE> indicating name of rank
     * @param name <CODE>String</CODE> indicating ascribed name
     * @param auth <CODE>Name</CODE> of authoritative name usage
     * @param rec  <CODE>Name</CODE> of recording name usage
     * @param type boolean, true if the <CODE>NameUsage</CODE> is name bearing type
     * @param higher <CODE>Name</CODE> of higher taxon
     * @param lower array of lower taxa's <CODE>Name</CODE>s
     */
    public DefaultNameUsage(String rankLiteral, String name,
		     Name<DefaultNameUsage> auth, Name<DefaultNameUsage> rec,
		     boolean type,
		     Name<DefaultNameUsage> higher, Name<DefaultNameUsage> [] lower)
    {
	super(rankLiteral, name, auth, rec, type, higher, lower);
    }

    /**
     * Constructs an <CODE>NameUage</CODE> object using XML data
     * given by <CODE>xml</CODE>
     *
     * @param xml <CODE>Element</CODE> specifying a <CODE>NameUsage</CODE>
     *
     */
    public DefaultNameUsage(Element xml)
    {
	super(xml);
    }

    /**
     * Constructs an <CODE>NameUage</CODE> object using XML data
     * given by <CODE>xml</CODE>
     *
     * @param xml <CODE>Element</CODE> specifying a <CODE>NameUsage</CODE>
     * @param appearance <CODE>Appearance</CODE> where the name used
     */
    public DefaultNameUsage(Element xml, Appearance ap)
    {
	super(xml, ap);
    }

    protected DefaultNameUsage createNameUsage()
    {
	return new DefaultNameUsage();
    }

    @SuppressWarnings("unchecked")
    protected DefaultNameUsage createNameUsage(Name<?> nameUsage)
    {
	if(nameUsage instanceof DefaultNameUsage)
	    return new DefaultNameUsage((Name<? extends DefaultNameUsage>)nameUsage);
	else
	    return new DefaultNameUsage();
    }

    protected DefaultNameUsage createNameUsage(NameUsage<?> nameUsage)
    {
	return new DefaultNameUsage(nameUsage);
    }

    protected DefaultNameUsage createNameUsage(String persistentID)
    {
	return new DefaultNameUsage(persistentID);
    }


}

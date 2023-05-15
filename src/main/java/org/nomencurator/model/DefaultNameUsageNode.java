/*
 * DefaultNameUsageNode.java:  a Java implementation of NameUsageNode class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * An implementation of {@code NameUsageNode} in Nomencurator
 * data model.  It was referred to as NRnode in the original publication.
 * It wraps a {@code NameUsage} for efficient navigation over 
 * annotated {@code NameUsage}s.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	07 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class DefaultNameUsageNode
    extends AbstractNameUsageNode <DefaultNameUsageNode>
{
    private static final long serialVersionUID = 2180614523366443021L;

    public DefaultNameUsageNode create()
    {
	return new DefaultNameUsageNode();
    }

    /** Constructs an "empty" {@code NameUsageNode} */
    public DefaultNameUsageNode()
    {
	super();
    }

    /**
     * Constructs a <COCE>NameUsageNode} using
     * existing {@code NameUsage}
     *
     * @param nameUsage existing {@code NameUsage}
     * to be pointed by this object
     */
    public DefaultNameUsageNode(NameUsage<?> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs an "empty" {@code NameUsageNode}
     * appeared in {@code appearance}
     *
     * @param appearance {@code Appearance} where the name used
     */
    public DefaultNameUsageNode(Appearance apperance)
    {
	super(apperance);
    }

    /**
     * Constructs a {@code NameUsageNode} object having
     * {@code persistentID} as its representation,
     */
    public DefaultNameUsageNode(String persistentID)
    {
	super(persistentID);
    }

    /**
     * Constructs a {@code NameUsageNode} based on
     * {@code nameUsage}
     */
    public DefaultNameUsageNode(Name<? extends DefaultNameUsageNode> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs a {@code NameUsage} by giving
     * its attributes.
     *
     * @param rank {@code String} indicating name of rank
     * @param name {@code String} indicating ascribed name
     * @param auth {@code Name} of authoritative name usage
     * @param rec  {@code Name} of recording name usage
     * @param type boolean, true if the {@code NameUsage} is name bearing type
     * @param higher {@code Name} of higher taxon
     * @param lower array of lower taxa's {@code Name}s
     */
    public <N extends DefaultNameUsageNode> DefaultNameUsageNode(String rank, String name,
			 Name<N> auth, Name<N> rec,
			 boolean type,
			 Name<N> higher, Name<N> [] lower)
    {
	super(rank, name, auth, rec, type, higher, lower);
    }

    /**
     * Constructs an {@code NameUage} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying a {@code NameUsage}
     *
     */
    public DefaultNameUsageNode(Element xml)
    {
	super(xml);
    }

    /**
     * Constructs an {@code NameUage} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying a {@code NameUsage}
     * @param appearance {@code Appearance} where the name used
     */
    public DefaultNameUsageNode(Element xml, Appearance ap)
    {
	super(xml, ap);
    }

    protected DefaultNameUsageNode createNameUsageNode()
    {
	return new DefaultNameUsageNode();
    }

    protected DefaultNameUsageNode createNameUsageNode(NameUsage<?> nameUsage)
    {
	return new DefaultNameUsageNode(nameUsage);
    }

    protected DefaultNameUsageNode createNameUsageNode(String persistentID)
    {
	return new DefaultNameUsageNode(persistentID);
    }

    protected AbstractNameUsage<DefaultNameUsageNode> createNameUsage()
    {
	return createNameUsageNode();
    }

}

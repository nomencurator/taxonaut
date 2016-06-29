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

package org.nomencurator.model;

import org.w3c.dom.Element;

/**
 * An implementation of <code>NameUsageNode</code> in Nomencurator
 * data model.  It was referred to as NRnode in the original publication.
 * It wraps a <code>NameUsage</code> for efficient navigation over 
 * annotated <code>NameUsage</code>s.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	23 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class DefaultNameUsageNode
//    extends AbstractNameUsageNode <NameUsageNode>
    extends AbstractNameUsageNode <DefaultNameUsageNode, DefaultNameUsageNode>
{
    private static final long serialVersionUID = 2180614523366443021L;

    public DefaultNameUsageNode create()
    {
	return new DefaultNameUsageNode();
    }

    /** Constructs an "empty" <code>NameUsageNode</code> */
    public DefaultNameUsageNode()
    {
	super();
    }

    /**
     * Constructs a <COCE>NameUsageNode</CODE> using
     * existing <CODE>NameUsage</CODE>
     *
     * @param nameUsage existing <CODE>NameUsage</CODE>
     * to be pointed by this object
     */
    public DefaultNameUsageNode(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs an "empty" <code>NameUsageNode</code>
     * appeared in <CODE>appearance</CODE>
     *
     * @param appearance <CODE>Appearance</CODE> where the name used
     */
    public DefaultNameUsageNode(Appearance apperance)
    {
	super(apperance);
    }

    /**
     * Constructs a <code>NameUsageNode</code> object having
     * <code>persistentID</code> as its representation,
     */
    public DefaultNameUsageNode(String persistentID)
    {
	super(persistentID);
    }

    /**
     * Constructs a <code>NameUsageNode</code> based on
     * <code>nameUsage</code>
     */
    public DefaultNameUsageNode(Name<DefaultNameUsageNode, ? extends DefaultNameUsageNode> nameUsage)
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
    public DefaultNameUsageNode(String rank, String name,
			 Name<DefaultNameUsageNode, DefaultNameUsageNode> auth, Name<DefaultNameUsageNode, DefaultNameUsageNode> rec,
			 boolean type,
			 Name<DefaultNameUsageNode, DefaultNameUsageNode> higher, Name<DefaultNameUsageNode, DefaultNameUsageNode> [] lower)
    {
	super(rank, name, auth, rec, type, higher, lower);
    }

    /**
     * Constructs an <CODE>NameUage</CODE> object using XML data
     * given by <CODE>xml</CODE>
     *
     * @param xml <CODE>Element</CODE> specifying a <CODE>NameUsage</CODE>
     *
     */
    public DefaultNameUsageNode(Element xml)
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
    public DefaultNameUsageNode(Element xml, Appearance ap)
    {
	super(xml, ap);
    }

    protected DefaultNameUsageNode createNameUsageNode()
    //protected NameUsageNode<DefaultNameUsageNode, DefaultNameUsageNode> createNameUsageNode()
    {
	return new DefaultNameUsageNode();
    }

    /*
    protected NameUsage<DefaultNameUsageNode, DefaultNameUsageNode> createNameUsage(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	return createNameUsageNode(nameUsage);
    }
    */

    //protected DefaultNameUsageNode createNameUsageNode(NameUsage<DefaultNameUsageNode, DefaultNameUsageNode> nameUsage)
    protected DefaultNameUsageNode createNameUsageNode(NameUsage<?, ?> nameUsage)
    //protected NameUsageNode<DefaultNameUsageNode, DefaultNameUsageNode> createNameUsageNode(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	return new DefaultNameUsageNode(nameUsage);
    }

    protected DefaultNameUsageNode createNameUsageNode(String persistentID)
    //protected NameUsageNode<DefaultNameUsageNode, DefaultNameUsageNode> createNameUsageNode(String persistentID)
    {
	return new DefaultNameUsageNode(persistentID);
    }

    protected AbstractNameUsage<DefaultNameUsageNode, DefaultNameUsageNode> createNameUsage()
    {
	//	return (NameUsage<DefaultNameUsageNode, DefaultNameUsageNode>)createNameUsageNode();
	return createNameUsageNode();
    }

    /*
    protected NameUsage<DefaultNameUsageNode, DefaultNameUsageNode> createNameUsage(Name<?, ?> nameUsage)
    {
	if(nameUsage instanceof DefaultNameUsageNode)
	    return (NameUsage<DefaultNameUsageNode, DefaultNameUsageNode>)createNameUsageNode(getNameUsage(nameUsage));
	else
	    return createNameUsageNode();
    }
    */

    /*
    protected NameUsage<DefaultNameUsageNode, DefaultNameUsageNode> createNameUsage(String persistentID)
    {
	return (NameUsage<DefaultNameUsageNode, DefaultNameUsageNode>)createNameUsageNode(persistentID);
    }
    */
}

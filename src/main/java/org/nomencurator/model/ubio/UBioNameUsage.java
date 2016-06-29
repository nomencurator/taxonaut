/*
 * UBioNameUsage.java:  a Java implementation of NameUsage class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2005, 2006, 2014 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model.ubio;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.model.Name;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.AbstractNameUsageNode;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

import org.ubio.model.NamebankObject;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of {@code NameUsage} in Nomencurator
 * data model to handle uBio NamebankObject.
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class UBioNameUsage
    extends UBioNameUsageNode
{
    private static final long serialVersionUID = 3639279186351987706L;

    /** Constructs an "empty" {@code NameUsage} */
    public UBioNameUsage()
    {
	super();
    }

    /**
     * Constructs a {@code NameUsage} object having
     * {@code persistentID} as its representation,
     */
    public UBioNameUsage(String persistentID)
    {
	super(persistentID);
    }

    /**
     * Constructs an "empty" <code>NameUsageNode</code> representing
     * a uBio Namebank object of {@code namebankID}
     */
    public UBioNameUsage(int namebankID)
    {
	super(namebankID);
    }

    /**
     * Constructs a deep copy of {@code nameUsage}
     *
     * @param nameUsage {@code NameUsage} to be copied deeply
     */
    public UBioNameUsage(NameUsage<?, ?> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs a deep copy of {@code nameUsage}
     *
     * @param nameUsage {@code NameUsage} to be copied deeply
     */
    public UBioNameUsage(NamebankObject namebankObject)
    {
	super(namebankObject);
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
    /*
    public UBioNameUsage(String rank, String name,
		     Name auth, Name rec,
		     boolean type,
		     Name higher, Name [] lower)
    {
	super(rank, name, auth, rec, type, higher, lower);
    }
    */

    public UBioNameUsage clone()
    {
	UBioNameUsage n = 
	    (UBioNameUsage)super.clone();
	return n;
    }
    /*
    protected NameUsage createNameUsage()
    {
	return new UBioNameUsage();
    }

    protected NameUsage createNameUsage(Name<?, ?> nameUsage)
    {
	return new UBioNameUsage((NameUsage<?, ?>)nameUsage);
    }

    protected NameUsage createNameUsage(String persistentID)
    {
	return new UBioNameUsage(persistentID);
    }
    */
}

/*
 * NamedObject.java: an abstract class provaides a "name-commutable"
 * mechanism for Java implementation of the Nomencurator, a
 * Nomenclature Heuristic Model.  For restriction of "final class
 * String" in Java, it uses Name instead of String.  It
 * spoiles simplicity and conceptual importance of name commutability.
 *
 * The same thing can be implemented using nameing mechanism of CORBA.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.text.Document;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * {@code NamedObject} provides an {@code Object}
 * commutable with its name represented not by {@code String}
 * but by {@code Name} because {@code java.lang.String}
 * is final.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Name
 * @see java.lang.String
 *
 * @version 	08 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NamedObject <T extends NamedObject<?>>
    extends Name <T>
{
    /*
     * Object class of Java has toString() method, so why do we need NamedObject?
     * If you mean concrete object, Object class is sufficient.  A name is only
     * a handle of a concrete object, i.e. phiysical entity. 
     * It is insufficient, however, if you mean abstract object because abstract 
     * object must be commutable with its name.  You can store String to place for Object, 
     * but you can't store Object to place for String.  Extending String class is therefore 
     * streightforward solution.  However, String class is final class in Java,
     * so we can't extend String class.  Sigh.
     */

    /**
     * Returns data source specific ID representing this {@code NamedObject}
     *
     * @return String representing this {@code NamedObject}
     */
    public String getLocalKey();

    /**
     * Returns a persistent ID representing this {@code NamedObject}
     *
     * @return String representing this {@code NamedObject}
     */
    public String getPersistentID();

    /**
     * Returns a persistent ID representing this {@code NamedObject}.
     * It contains class name header if {@code withClassName} true.
     *
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code NamedObject}
     */
    public String getPersistentID(boolean withClassName);
    
    /**
     * Returns a persistent ID representing this {@code NamedObject}
     * with specified  {@code separator}
     *
     * @param separator {@code String} to be used as the field separator
     *
     * @return String representing this {@code NamedObject}
     */
    public String getPersistentID(String separator);
    
    /**
     * Returns a persistent ID representing this {@code NamedObject}
     * with specified  {@code separator}.  It contains class name header
     * if {@code withClassName} true.
     * The subclasses must provide this method.
     *
     * @param separator {@code String} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code NamedObject}
     */
    public String getPersistentID(String separator, boolean withClassName);

    /**
     * Returns persistent ID representing this {@code NamedObject}.
     *
     * @return String representing persistent ID of this {@code NamedObject}.
     */
    public void setPersistentID(String pid);

    /**
     * Returns persistent ID representing an empty {@code NamedObject}.
     * The subclasses must provide this method.
     *
     * @return String representing persistent ID of an empty {@code NamedObject}.
     */
    public String getEmptyPersistentID();

    /**
     * Returns {@code String} containing class name and
     * class name separator, i.e. prefix of persistent ID.
     *
     * @return String persistentID prefix
     */
    public String getClassNameHeader();

    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    public int getFieldSepartorsCount();
    
    /**
     * Parse {@code line} and set values of this object
     * if the {@code line} represents them appropriately.
     *
     * @param line {@code String} representing values of this object
     */
    public void parseLine(String line);
    
    /**
     * Returns true if this object is resolved, i.e. has contents
     *
     * @return true if this object is resolved, i.e. has contents
     */
    public boolean isSovled();
    
    /**
     * Puts this object to {@code nomencurator}, an object repository.
     * Returns this if equivalent object is not in {@code nomencurator},
     * or the equivalent object found in {@code nomencurator}.
     *
     * @param nomencurator an object repository
     *
     * @return this if no equivalent object is not in {@code nomencurator},
     * or the equivalent object found in {@code nomencurator}
     */
    public NamedObject<?> putTo(Nomencurator nomencurator);

    /**
     * Returns {@code NamedObject} having no entity object or an eitity instance
     * of other than {@code NamedObject}.  If the entity of this object
     * is an instance of {@code NamedObject}, the method returns result
     * of recursive application of this method.
     * 
     * @return NamedObject have no enitity or non-{@code NamedObject} entity
     */
    public NamedObject<T> getNamedObject();

    /**
     * Merge the given {@code namedObejct} to this, with returning true if successful.
     *
     * @paramn namedObject {@code NamedObject} to be merged.
     * @return true if mergerd successfully.
     */
    public boolean merge(NamedObject<?> namedObject);
    
    public String trim();

    public String getPersistentIDContents();

    /**
     * Establishes mutual linkage between {@code NamedObject}s.
     *
     * @param o1 a {@code NamedObject} to be linked
     * @param v1 a {@code List} of {@code NamedObject}s
     * to contain {@code o1}.  It is assumed to owned by {@code o2}
     * @param o2 a {@code NamedObject} to be linked
     * @param v2 a {@code List} of {@code NamedObject}s
     * to contain {@code o2}.  It is assumed to owned by {@code o1}
     *
     * @return int result code where 0 indicates both {@code NamedObject}s
     * were put to {@code List}s, 1 or 2 indicates eithor {@code o1}
     * or {@code o2} was not addetd to the {@code List}, 3 indicates
     * no {@code NamedObject} was added, -1 indicates both {@code List}s
     * are null.
     */
    //int mutualAdd(NamedObject o1, List v1, NamedObject o2, List v2) 

    /**
     * Releases mutual linkage between {@code NamedObject}s.
     *
     * @param o1 a {@code NamedObject} to be unlinked
     * @param v1 a {@code List} of {@code NamedObject}s
     * to remove {@code o1} from it.  It is assumed to owned by {@code o2}
     * @param o2 a {@code NamedObject} to be unlinked
     * @param v2 a {@code List} of {@code NamedObject}s
     * to remove {@code o2} from it.  It is assumed to owned by {@code o1}
     *
     * @return int result code where 0 indicates both {@code NamedObject}s
     * were removed from {@code List}s, 1 or 2 indicates eithor {@code o1}
     * or {@code o2} was not removed from the {@code List}, 3 indicates
     * no {@code NamedObject} was removed, -1 indicates both {@code List}s
     * are null.
     */
    //int mutualRemove(NamedObject o1, List v1, NamedObject o2, List v2) 

    /**
     * Returns the title of dataset, or null if unavailable.
     *
     * @return String representing the dataset title
     */
    public String getDatasetTitle();

    /**
     * Returns source of this object which gives a context
     * where this NamedObject appeared, or null if the source
     * is unknown.  It may return this object itself when the
     * object is self-evident.
     *
     * @return NamedObject source of this object
     */
    public NamedObject<?> getSource();

    /**
     * Sets {@code source} of this object which gives a context
     * where this NamedObject appeared.
     *
     * @param source where this object appeared
     */
    public void setSource(NamedObject<?> source);

    /**
     * Returns name of whom contributed this object
     *
     * @return String name of contributor
     */
    public String getContributor();

    /**
     * Sets {@code contributor} as name of whom contributed this object
     *
     * @param contributor who contributed this object
     */
    public void setContributor(String contributor);

    /**
     * Returns {@code Timestamp} when the data last updated
     *
     * @return Timestamp when the data last updated
     */
    public Timestamp getLastUpdated();

    /**
     * Sets {@code timpstamp} when the data last updated
     *
     * @param timestamp when the data last updated
     */
    public void setLastUpdated(Timestamp timestamp);

    /**
     * Sets {@code date} when the data last updated
     *
     * @param date when the data last updated
     */
    public void setLastUpdated(Date date);

    /** 
     * Examines whether the object is editable or not
     *
     * @return true if the object is editable
     */
    public boolean isEditable();

    /** 
     * Make the object editable
     *
     * @param editable true to make the object is editable
     */
    public void setEditable(boolean editable);

    /**
     * Returns copyright applied to this object
     *
     * @return copyright applied to this object
     */
    public String getCopyright();

    /**
     * Sets {@code copyright} applied to this object
     *
     * @param copyright to be applied to this object
     */
    public void setCopyright(String copyright);

    /**
     * Returns verbatim repsentation of this object
     * supporting "variable atomization"
     *
     * @return verbatim rpresentation of this object
     */
    public String getVerbatim();

    /**
     * Sets {@code verbatim} as a verbatim
     * repsentation of this object
     * supporting "variable atomization"
     *
     * @param verbatim rpresentation of this object in {@code String}
     */
    public void setVerbatim(String verbatim);

    /**
     * Returns scope of this object as a {@code String}
     *
     * @return String rpresentating scope of the object
     */
    public String getScope();

    /**
     * Sets {@code scope} as scope of this object
     *
     * @param verbatim rpresentation of this object in {@code String}
     */
    public void setScope(String verbatim);


    /**
     * Returns notes on this object
     *
     * @return notes on this object
     */
    public String getNotes();

    /**
     * Sets {@code notes} on this object
     *
     * @param notes on this object
     */
    public void setNotes(String notes);

    /**
     * Returns true if this object is a parsed object,
     * i.e. a representative of equivalent {@code NamedObject}s
     * 
     * @return true if this object is a parsed object
     */
    public boolean isParsed();

    /**
     * Returns an array of {@code NamedObject}s
     * represented by this object
     * 
     * @return an array of {@code NamedObject}s
     * represented by this object
     */
    public Collection<NamedObject<?>> getSourceObjects();

    /**
     * Set {@code NamedObject}s in {@code sources}
     * as objects represented by this object
     * 
     * @param sources an array of {@code NamedObject}s
     * to be represented by this object
     */
    public void setSourceObjects(NamedObject<?>[] sources);

    /**
     * Set {@code NamedObject}s in {@code sources}
     * as objects represented by this object
     * 
     * @param sources a {@code List} of {@code NamedObject}s
     * to be represented by this object
     */
    public void setSourceObjects(Collection<? extends NamedObject<?>>  sources);

    /**
     * Adds {@code source} to the list of {@code NamedObject}s
     * represented by this object
     * 
     * @param source a {@code NamedObject} to be 
     * represented by this object
     */
    public void addSourceObject(NamedObject<?> source);

    /**
     * Removes {@code source} from the list of {@code NamedObject}s
     * represented by this object
     * 
     * @param source a {@code NamedObject} to be 
     * removed from list of {@code NamedObject} represented by this object
     */
    public void removeSourceObject(NamedObject<?> source);

    /**
     * Returns {@code NamedObject} overridden by this
     * {@code NamedObject} or null if none
     *
     * @return {@code NamedObject} overridden by this
     */
    //public NamedObject<?> getOverrides();

    /**
     * Sets {@code namedObject} as the {@code NamedObject}
     * to be overriden by this {@code NamedObject},
     * or null to remove the relationship
     *
     * @param namedObject {@code NamedObject} to be overriden
     */
    //public void setOverrides(NamedObject<?> namedObject);

    /**
     * Returns XML {@code String} representing this object
     *
     * @return XML {@code String} representing this object
     */
    public String toXML();

    /**
     * Appends an XML representing the object at the end of
     * {@code buffer}.  If {@code buffer} is null,
     * it creates a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * XML to be appended, or null to create a new
     * {@code StringBuffer}
     *
     * @return StringBuffer containing the XML at the end
     */
    public StringBuffer toXML(StringBuffer buffer);

    /**
     * Returns XML {@code String} of the all relevant {@code NamedObject}s
     *
     * @return XML {@code String} representing all {@code NamedObject}s 
     * relationg to the {@code NamedObject}
     */
    public String toRelevantXML();

    /**
     * Appends an XML representing all objects relevant to
     * the object at the end of {@code buffer}.
     * If {@code buffer} is null, it creates 
     * a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * XML to be appended, or null to create a new
     * CODE>StringBuffer}
     *
     * @return StringBuffer containing the XML at the end
     */
    public StringBuffer toRelevantXML(StringBuffer buffer);

    /**
     * Sets valuse of this {@code NamedObject} to
     * {@code statement} using {@code connection}.
     * from specified {@code index} of the {@code statement}
     *
     * @param statement {@code PraredStatement} to which
     * value of the this {@code NamedObject} to be set
     * @param connection {@code NamedObjectConnection}
     * to be used to set values
     * @param index {@code int} from where values to be set
     * into the {@code statement}
     *
     * @return int index of the next parameter to be set if there is
     *
     * @exception SQLException
     */
    public int setValues(PreparedStatement statement,
			 NamedObjectConnection<?> connection,
			 int index)
	throws SQLException;

    /**
     * Returns a summarized expression of this {@code NamedObject}
     *
     * @return String representing summary of this {@code NamedObject}
     */
    public String getSummary();

    /**
     * Returns a detailed expression of this {@code NamedObject}
     * in a single {@code String} object.
     *
     * @return String representing detail of this {@code NamedObject}
     */
    public String getDetail();

    /**
     * Returns a detailed expression of this {@code NamedObject}
     * in a single {@code Document} object.
     *
     * @return Docuemnt representing detail of this {@code NamedObject}
     */
    public Document getDetailDocument();

    public NamedObject<T> getNamedObject(Object object);

}

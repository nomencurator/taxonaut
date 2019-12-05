/*
 * AbstractNamedObject.java: an abstract class provaides a "name-commutable"
 * mechanism for Java implementation of the Nomencurator, a
 * Nomenclature Heuristic Model.  For restriction of "final class
 * String" in Java, it uses Name instead of String.  It
 * spoiles simplicity and conceptual importance of name commutability.
 *
 * The same thing can be implemented using nameing mechanism of CORBA.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2005, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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
import java.sql.Types;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;

import org.nomencurator.io.ObjectExchanger;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.util.ArrayUtility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * {@code NamedObject} provides an interface to handle {@code Object}
 * compativle with its name represented not by {@code String}
 * but by {@code Name}, because final class {@code java.lang.String}
 * can not be extended.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Name
 * @see java.lang.String
 *
 * @version 	05 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNamedObject <T extends NamedObject<?>>
    extends AbstractName <T>
    implements NamedObject <T>,
	       SQLSerializable<T>,
	       XMLSerializable,
	       Serializable
{
    private static final long serialVersionUID = 5449537513995459880L;

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

    //protected static String classNameSeparator = "::";
    protected static String classNameSeparator = "#";
    
    //    protected char fieldSeparator = '_';
    protected static String fieldSeparator = "_";
    
    protected static char[] openers = {'{', '>'};
    protected static char[] closers = {'}', '<'};

    protected final static int BIBTEX = 0;
    protected final static int XML    = BIBTEX + 1;
    protected final static int SQL2  = XML + 1;

    protected static char contentsOpen  = openers[BIBTEX];
    protected static char contentsClose = closers[BIBTEX];

    /* The key to identify the data object at its data source.  */
    protected String localKey;

    /** Source of this data. */
    protected NamedObject<?> source;

    /** Appropriate identifier of whom contributed this data. */
    protected String contributor;

    /** {@code Timestamp} when the data last updated */
    protected Timestamp lastUpdated;

    /** Flag indicating editable or not */
    protected boolean editable;

    /** Flag indicating modified or not */
    protected boolean modified;

    /**
     * Name (could be an acronym) of copyright, or copyright itslef 
     * that should be applied to this object
     */
    protected String copyright;

    /**
     * Verbatim representation of this object
     */
    protected String verbatim;

    /**
     * Scope of this object
     */
    protected String scope;

    /**
     * Any notes on this object; may be used as alternative
     * text for ABCD schema
     */
    protected String notes;

    
    //protected NamedObject overrides;

    protected Set<String> objectIDs;

    protected Collection<NamedObject<?>> sources;

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	
	if(getClass() != object.getClass()) return false;

	final AbstractNamedObject<T> that = (AbstractNamedObject<T>) object;
	return super.equals(object)
	    && Objects.equals(this.localKey, that.localKey)
	    && Objects.equals(this.source, that.source)
	    && Objects.equals(this.contributor, that.contributor)
	    && Objects.equals(this.lastUpdated, that.lastUpdated)
	    && Objects.equals(this.editable, that.editable)
	    && Objects.equals(this.modified, that.modified)
	    && Objects.equals(this.copyright, that.copyright)
	    && Objects.equals(this.verbatim, that.verbatim)
	    && Objects.equals(this.scope, that.scope)
	    && Objects.equals(this.notes, that.notes)
	    && Objects.equals(this.objectIDs, that.objectIDs)
	    && Objects.equals(this.sources, that.sources)
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
	    localKey,
	    source,
	    contributor,
	    lastUpdated,
	    editable,
	    modified,
	    copyright,
	    verbatim,
	    scope,
	    notes,
	    objectIDs,
	    sources
			    );
    }

    protected static List<List<String>> getNameLists(final Collection<? extends NamedObject<?>> a, final Collection<? extends NamedObject<?>> b)
    {
	List<List<String>> list = new ArrayList<List<String>>(2);
	list.add(getSortedNameList(a));
	list.add(getSortedNameList(b));
	return list;
    }

    protected static List<String> getSortedNameList(final Collection<? extends NamedObject<?>> collection) {
	if(collection == null)
	    return new ArrayList<String>(0);

	List<String> list = new ArrayList<String>(collection.size());

	for(NamedObject<?> object : collection) {
	    list.add(object.getPersistentID());
	}

	Collections.sort(list);

	return list;
    }

    /**
     * Constructs an empty {@code NamedObject}
     * Only the subclasses may call this constructor.
     */
    protected AbstractNamedObject()
    {
	this("", true);
    }
    
    /**
     * Constructs an {@code NamedObject} having
     * given {@code name} as {@code String} representation.
     */
    protected AbstractNamedObject(String name)
    {
	this(name, false);
    }

    /**
     * Constructs an {@code NamedObject} having
     * given {@code name} as {@code String} representation.
     */
    protected AbstractNamedObject(String name, boolean editable)
    {
	super();
	setPersistentID(name);
	setEditable(editable);
    }
    

    /**
     * Constructs an {@code NamedObject} having
     * given {@code name} as {@code String} representation.
     */
    protected AbstractNamedObject(ResultSet result)
	throws SQLException
    {
	super();
	if(result == null)
	    return;

	// discard objectID BIGINT PRIMARY KEY
	// use reference to this object instead

	// persistentID TEXT
	setPersistentID(result.getString("persistent_id"));

	// contributor TEXT
	setContributor(result.getString("contributor"));

	// discard objectType TEXT which should be determined
	// by the subclass of this object

	// editable INTEGER
	setEditable(result.getInt("editable") != 0);
	
	// copyright TEXT
	setCopyright(result.getString("copyright"));

	// verbatim TEXT
	setVerbatim(result.getString("verbatim"));

	// notes TEXT
	setNotes(result.getString("notes"));

	// createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
	setLastUpdated(result.getTimestamp("last_updated"));
    }

    @SuppressWarnings({"unchecked"})
    private NamedObject<T> castNamedObject(NamedObject<? extends T> namedObject)
    {
	return (NamedObject<T>)namedObject;
    }

    public String getLocalKey()
    {
	T namedObject = getEntity();
	//Object namedObject = getEntity();
	if(namedObject != this && namedObject instanceof NamedObject)
		return ((NamedObject)namedObject).getLocalKey();

	if(localKey != null)
	    return localKey;

	return pidToLocalKey();
    }

    /**
     * Converts the persistent ID to 
     */
    protected String pidToLocalKey()
    {
	return null;
    }
    
    /**
     * Returns a persistent ID representing this {@code NamedObject}
     *
     * @return String representing this {@code NamedObject}
     */
    public String getPersistentID()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getPersistentID();

	if(isNominal())
	    return literal;

	return getPersistentID(fieldSeparator);
    }

    /**
     * Returns a persistent ID representing this {@code NamedObject}.
     * It contains class name header if {@code withClassName} true.
     *
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code NamedObject}
     */
    public String getPersistentID(boolean withClassName)
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getPersistentID(withClassName);

	if(!isNominal())
	    return getPersistentID(fieldSeparator, withClassName);

	if(withClassName)
	    return literal;

	return literal.substring(1 + literal.indexOf(classNameSeparator));
    }
    
    /**
     * Returns a persistent ID representing this {@code NamedObject}
     * with specified  {@code separator}
     *
     * @param separator {@code String} to be used as the field separator
     *
     * @return String representing this {@code NamedObject}
     */
    public String getPersistentID(String separator)
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getPersistentID(separator);

	return getPersistentID(separator, true);
    }
    
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
    public abstract String getPersistentID(String separator, boolean withClassName);

    /**
     * Returns persistent ID representing an empty {@code NamedObject}.
     * The subclasses must provide this method.
     *
     * @return String representing persistent ID of an empty {@code NamedObject}.
     */
    public String getEmptyPersistentID()
    {
	return getEmptyPersistentID(getFieldSepartorsCount());
    }

    /**
     * Returns persistent ID having {@code seprators} underscores
     * after class name header.
     *
     * @param separator {@code String} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing persistent ID of an empty {@code NamedObject}.
     */
    protected String getEmptyPersistentID(String separator, boolean withClassName)
    {
	return getEmptyPersistentID(getFieldSepartorsCount(), separator, withClassName);
    }


    /**
     * Returns persistent ID having {@code seprators} underscores
     * after class name header.
     *
     * @param separators number of field separators appear in the persistent ID
     *
     * @return String representing persistent ID of an empty {@code NamedObject}.
     */
    protected String getEmptyPersistentID(int separators)
    {
	return getEmptyPersistentID(separators, fieldSeparator);
    }

    /**
     * Returns persistent ID having {@code seprators} underscores
     * after class name header.
     *
     * @param separators number of field separators appear in the persistent ID
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing persistent ID of an empty {@code NamedObject}.
     */
    protected String getEmptyPersistentID(int separators, boolean withClassName)
    {
	return getEmptyPersistentID(separators, fieldSeparator, withClassName);
    }

    /**
     * Returns persistent ID having {@code seprators} underscores
     * after class name header.
     *
     * @param separators number of field separators appear in the persistent ID
     * @param separator {@code String} to be used as the field separator
     *
     * @return String representing persistent ID of an empty {@code NamedObject}.
     */
    protected String getEmptyPersistentID(int separators, String separator)
    {
	return getEmptyPersistentID(separators, separator, true);
    }

    /**
     * Returns persistent ID having {@code seprators} underscores
     * after class name header.
     *
     * @param separators number of field separators appear in the persistent ID
     * @param separator {@code String} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing persistent ID of an empty {@code NamedObject}.
     */
    protected String getEmptyPersistentID(int separators, String separator, boolean withClassName)
    {
	StringBuffer pid = null;
	if(withClassName)
	    pid = getClassNameHeaderBuffer();
	else
	    pid = new StringBuffer();

	for(int i = 0; i < separators; i++)
	    pid.append(separator);
	return pid.toString();
    }

    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    //public abstract int getFieldSepartorsCount();
    
    /**
     * Returns persistent ID representing this {@code NamedObject}.
     *
     * @return String representing persistent ID of this {@code NamedObject}.
     */
    public void setPersistentID(String pid)
    {
	if(isA(pid))
	    literal = pid;
	else
	    literal = null;
    }

    /**
     * Returns {@code String} containing class name and
     * class name separator, i.e. header of persistent ID.
     *
     * @return String persistentID header
     */
    public String getClassNameHeader()
    {
	return getClassNameHeaderBuffer().toString();
    }

    /**
     * Returns {@code StringBuffer} containing class name and
     * class name separator as a seed of persistent ID.
     *
     * @return StringBuffer containing persistentID header
     */
    protected StringBuffer getClassNameHeaderBuffer()
    {
	StringBuffer buffer = new StringBuffer(getClassName());
	buffer.append(classNameSeparator);
	return buffer;
    }
    
    /**
     * Returns {@code String} representation of this {@code Object}
     * 
     * @return {@code String} representing this {@code Object}
     */
    public String toString()
    {
	return getPersistentID();
    }
    
    /**
     * Returns true if {@code ojbectName} rpresents an instance of
     * the class, or false if it isn't, including null. 
     *
     * @param objectName "name" {@code String} representation of
     * a {@code NamedObject}
     *
     * @return true if {@code ojbectName} rpresents an instance of
     * the class, or false if it isn't, including null. 
     */  
    protected boolean isA(String objectName)
    {
	if(objectName == null)
	    return false;

	return objectName.startsWith(getClassNameHeader());
    }
    
    /**
     * Returns true if {@code ojbectName} rpresents an instance of
     * the class, or false if it isn't, including null. 
     *
     * @param objectName "name" {@code DelegateString} representation of
     * a {@code NamedObject}
     *
     * @return true if {@code ojbectName} rpresents an instance of
     * the class, or false if it isn't, including null. 
     */  
    protected boolean isA(Name<?> objectName)
    {
	if(objectName == null)
	    return false;

	return isA(objectName.getName());
	//return (objectName instanceof NamedObject); //?
    }
    
    /**
     * Parse {@code line} and set values of this object
     * if the {@code line} represents them appropriately.
     *
     * @param line {@code String} representing values of this object
     */
    //public abstract void parseLine(String line);
    
    /**
     * Returns contents of a {@code line}
     *
     * @param line {@code String} containing values of this object
     *
     * @return {@code String} representing values of this object
     */
    protected String peal(String line)
    {
	return line.substring(line.indexOf(contentsOpen) + 1, line.lastIndexOf(contentsClose));
    }
    
    /**
     * Returns true if this object is resolved, i.e. has contents
     *
     * @return true if this object is resolved, i.e. has contents
     */
    public boolean isSovled()
    {
	return (literal == null || literal.length() == 0);
    }

    @Override
    public NamedObject<T> getNamedObject()
    {
	if(entity == null || !isAssignableFrom(entity))
	    //!(entity instanceof NamedObject))
	    return this;

	//return ((NamedObject<T>)entity).getNamedObject();
	return getNamedObject(entity).getNamedObject();
    }
    
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
    public NamedObject<?> putTo(Nomencurator nomencurator)
    {
	
	String pid = getPersistentID();
	
	//the name is not recorded in the Nomencurator
	if(!nomencurator.containsKey(pid)){
	    nomencurator.put(pid, this);
	    return this;
	}
	
	NamedObject<T> tnr = null;
	Name<?> name = nomencurator.get(pid);
	if(isAssignableFrom(name)) {
	    // safe cast because it is assignable
	    @SuppressWarnings({"unchecked"})
		NamedObject<T> tmp = (NamedObject<T>)name;
	    tnr = tmp;
	}
	if(tnr == this)
	    return this;
	
	//it is named
	if(pid.length() > getEmptyPersistentID().length()){
	    
	    // but it knows nothing better than its name
	    if(literal.length() > 0)
		return tnr;
	    
	    // or recorded one knows nothing better than its name
	    else if(tnr.getLiteral().length() > 0){
		nomencurator.remove(pid);
		nomencurator.put(pid, this);
		return this;
	    }
	    
	    // or neithr...needs a negciation
	    else if(tnr.merge(this))
		return tnr;
	}
	return this;  //?
    }

    public char charAt(int index) { return getPersistentID().charAt(index); }
    
    public int compareTo(Object o) { return getPersistentID().compareTo(o.toString()); }
    
    public int compareTo(String anotherString) { return getPersistentID().compareTo(anotherString); }
    
    public int compareToIgnoreCase(String str) { return getPersistentID().compareToIgnoreCase(str); }
    
    public String concat(String str) { return getPersistentID(). concat(str); }
    
    public boolean endsWith(String suffix) { return getPersistentID().endsWith(suffix); }
    
    public boolean equalsIgnoreCase(String anotherString) { return getPersistentID().equalsIgnoreCase(anotherString); }
    
    public byte[] getBytes() { return getPersistentID().getBytes(); }
    
    @SuppressWarnings(value = {"deprecation"})
    public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) { getPersistentID().getBytes(srcBegin, srcEnd, dst, dstBegin); }
    
    public byte[] getBytes(String enc) throws java.io.UnsupportedEncodingException { return getPersistentID().getBytes(enc); }
    
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) { getPersistentID().getChars(srcBegin, srcEnd, dst, dstBegin); }
    
    //public int hashCode() { return getPersistentID().hashCode(); }
    
    public int indexOf(int ch) { return getPersistentID().indexOf(ch); }
    
    public int indexOf(int ch, int fromIndex) { return getPersistentID().indexOf(ch, fromIndex); }
    
    public int indexOf(String str) { return getPersistentID().indexOf(str); }
    
    public int indexOf(String str, int fromIndex) { return getPersistentID().indexOf(str, fromIndex); }
    
    public String intern() { return getPersistentID().intern(); }
    
    public int lastIndexOf(int ch) { return getPersistentID().lastIndexOf(ch); }
    
    public int lastIndexOf(int ch, int fromIndex) { return getPersistentID().lastIndexOf(ch, fromIndex); }
    
    public int lastIndexOf(String str) { return getPersistentID().lastIndexOf(str); }
    
    public int lastIndexOf(String str, int fromIndex) { return getPersistentID().lastIndexOf(str, fromIndex); }
    
    public int length() { return getPersistentID().length(); }
    
    public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
	return getPersistentID().regionMatches(ignoreCase, toffset, other, ooffset, len); }
    
    public boolean regionMatches(int toffset, String other, int ooffset, int len) {
	return getPersistentID().regionMatches(toffset, other, ooffset, len); }
    
    public String replace(char oldChar, char newChar) { return getPersistentID().replace(oldChar, newChar); }
    
    public boolean startsWith(String prefix) { return getPersistentID().startsWith(prefix); }
    
    public boolean startsWith(String prefix, int toffset) { return getPersistentID().startsWith(prefix, toffset); }
    
    public String substring(int beginIndex) { return getPersistentID().substring(beginIndex); }
    
    public String substring(int beginIndex, int endIndex) { return getPersistentID().substring(beginIndex, endIndex); }
    
    public char[] toCharArray() { return getPersistentID().toCharArray(); }
    
    public String toLowerCase() { return getPersistentID().toLowerCase(); }
    
    public String toLowerCase(Locale locale) { return getPersistentID().toLowerCase(locale); }
    
    public String toUpperCase() { return getPersistentID().toUpperCase(); }
    
    public String toUpperCase(Locale locale) { return getPersistentID().toUpperCase(locale); }
    
    public String trim() { return getPersistentID().trim(); }

    public String getPersistentIDContents()
    {
	return getPersistentID(false);
    }

    /**
     * Returns contents of persisnte ID {@code pid},
     * i.e. returns substring after the first {@code classNameSeparator}.
     * If {@code pid} does not contain {@code classNameSeparator},
     * it returns {@code pid} itself.
     *
     * @param pid persistent ID from where contents extracted.
     *
     * @return contents of given persistent ID, or {@code pid} itself
     * if it is not a persistent ID.
     */
    static public String getPersistentIDContents(String pid)
    {
	if(pid == null)
	    return null;

	int offset = pid.indexOf(classNameSeparator);
	if(offset < 0)
	    return pid;

	return pid.substring(offset += classNameSeparator.length());
    }

    /**
     * Gets name in {@code String}
     *
     * @return String representing a name
     */
    public String getLiteral(){ return getPersistentID();}

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
    int mutualAdd(AbstractNamedObject<?> o1, List<AbstractNamedObject<?>> v1, AbstractNamedObject<?> o2, List<AbstractNamedObject<?>> v2) 
    {
	if(v1 == null && v2 == null)
	    return -1;

	int resultCode = 0;

	if(v1 == null || v1.contains(o1))
	    resultCode |= 1;
	else {
	    v1.add(o1);
	    //	    o2.setChanged();
	}

	if(v2 == null | v2.contains(o2))
	    resultCode |= 2;
	else {
	    v2.add(o2);
	    //	    o1.setChanged();
	}

	return resultCode;
    }

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
    int mutualRemove(AbstractNamedObject<?> o1, List<AbstractNamedObject<?>> v1, AbstractNamedObject<?> o2, List<AbstractNamedObject<?>> v2) 
    {
	if(v1 == null && v2 == null)
	    return -1;

	int resultCode = 0;

	if(v1 == null || !v1.contains(o1))
	    resultCode |= 1;
	else {
	    v1.remove(o1);
	    // o2.setChanged();
	}

	if(v2 == null | !v2.contains(o2))
	    resultCode |= 2;
	else {
	    v2.remove(o2);
	    // o1.setChanged();
	}

	return resultCode;
    }

    /**
     * get String Data
     * @param  xmlElement an {@code Element} of XML document
     * @return String representing contents of {@code xmlElement}
     */
    protected String getString(Element xmlElement)
    {
	Node node = xmlElement.getFirstChild();
	if(node == null)
	    return "";
	if(node instanceof Text) {
	    Text text = (Text)node;
	    if(text.getData() == null)
		return "";
	    else
		return text.getData().trim();
        }
	return getPseudoHTML(xmlElement);
    }

    protected String getPseudoHTML(Element xmlElement)
    {
	StringBuffer html = new StringBuffer();
	if(xmlElement.hasChildNodes()) {
	    NodeList nodes = xmlElement.getChildNodes();
	    int children = nodes.getLength();
	    for(int i = 0; i < children; i++)
		parsePseudoHTML(nodes.item(i), html);
	}
	return html.toString();
    }

    protected void parsePseudoHTML(Node e, StringBuffer html)
    {
	String nodeName = e.getNodeName();
	boolean toAppendTag = !(e instanceof Text);
	boolean isBR = nodeName.equals("BR");
	if(toAppendTag & !isBR){
	    html.append('<');
	    html.append(nodeName);
	    html.append('>');
	}
	if(!e.hasChildNodes() && !isBR) {
	    html.append(e.getNodeValue());
	}
	else {
	    NodeList nodes = e.getChildNodes();
	    int children = nodes.getLength();
	    for(int i = 0; i < children; i++)
		parsePseudoHTML(nodes.item(i), html);
	}
	if(toAppendTag){
	    if(isBR){
		html.append("<BR>");
	    }
	    else {
		html.append("</");
		html.append(nodeName);
		html.append(">\n");
	    }
	}
    }

    /**
     * get Date Data
     * @param  s String
     * @return Date
     */
    protected Date getDate(String s)
    {
	return new SimpleDateFormat().parse(s, new ParsePosition(0));
    }

    /*
    public String getPersistentIDComponent(int counts)
    {
	String pid = getPersistentIDContents();

	int index = 0;
	for(int i = 0; index < pid.length() && i < counts; i++)
	    index = pid.indexOf(fieldSeparator, index);

	if(index < pid.length()) {
	    index++;
	    int next = pid.indexOf(fieldSeparator, index);
	    if(next == -1)
		next = pid.length();
	    pid = pid.substring(index, next);
	}

	return pid;
    }
    */    

    /**
     * Returns the component of the persistentID at {@code index},
     * or null if {@code index} is out or range.
     *
     * @param index position of the component in the persistentID.
     *
     * @return String representing the component 
     */
    public String getPersistentIDComponent(int index)
    {
	return getPersistentIDComponent(getLiteral(), index);
    }

    /**
     * Returns the component of the persistentID at {@code index},
     * or null if {@code index} is out or range.
     *
     */
    public static String getPersistentIDComponent(String pid, int index)
    {
	StringTokenizer tokens = 
	    new StringTokenizer(pid.substring(classNameSeparator.length() + pid.indexOf(classNameSeparator)),
				fieldSeparator);
	if(index > tokens.countTokens())
	    return null;
	int i = -1;
	String token = null;
	while(i < index && tokens.hasMoreTokens()) {
	    token = tokens.nextToken();
	    i++;
	}
	if(i < index)
	    token = null;

	return token;
    }

    protected void appendCalendar(StringBuffer buffer, Calendar c, String separator)
    {
	buffer.append(c.get(Calendar.YEAR)).append(separator);
	buffer.append(c.get(Calendar.MONTH)).append(separator);
	buffer.append(c.get(Calendar.DAY_OF_MONTH));
    }

    public String getDatasetTitle()
    {
	return null;
    }

    /**
     * Returns source of this object which gives a context
     * where this NamedObject appeared, or null if the source
     * is unknown.  It may return this object itself when the
     * object is self-evident.
     *
     * @return NamedObject source of this object
     */
    public NamedObject<?> getSource()
    {
	if(entity != null) 
	    //return ((NamedObject<T>)getEntity()).getSource();
	    return getEntity().getSource();

	return source;
    }

    /**
     * Sets {@code source} of this object which gives a context
     * where this NamedObject appeared.
     *
     * @param source where this object appeared
     */
    public void setSource(NamedObject<?> source)
    {
	if(entity != null) {
	    ((NamedObject<?>)getEntity()).setSource(source);
	    return;
	}

	this.source = source;
    }

    /**
     * Returns name of whom contributed this object
     *
     * @return String name of contributor
     */
    public String getContributor()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getContributor();

	return contributor;
    }

    /**
     * Sets {@code contributor} as name of whom contributed this object
     *
     * @param contributor who contributed this object
     */
    public void setContributor(String contributor)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setContributor(contributor);
	    return;
	}

	this.contributor = contributor;
    }

    /**
     * Returns {@code Timestamp} when the data last updated
     *
     * @return Timestamp when the data last updated
     */
    public Timestamp getLastUpdated()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getLastUpdated();

	return lastUpdated;
    }

    /**
     * Sets {@code timestamp} when the data last updated
     *
     * @param thimestamp when the data last updated
     */
    public void setLastUpdated(Timestamp timestamp)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setLastUpdated(timestamp);
	    return;
	}
	lastUpdated = timestamp;
    }

    /**
     * Sets {@code date} when the data last updated
     *
     * @param date when the data last updated
     */
    public void setLastUpdated(Date date)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setLastUpdated(date);
	    return;
	}
	if(date == null)
	    setLastUpdated(null);
	else
	    setLastUpdated(new Timestamp(date.getTime()));
    }

    /** 
     * Examines whether the object is editable or not
     *
     * @return true if the object is editable
     */
    public boolean isEditable()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).isEditable();

	return editable;
    }

    /** 
     * Make the object editable
     *
     * @param editable true to make the object is editable
     */
    public void setEditable(boolean editable)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setEditable(editable);
	    return;
	}

	this.editable = editable;
    }

    /**
     * Returns copyright applied to this object
     *
     * @return copyright applied to this object
     */
    public String getCopyright()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getCopyright();

	return copyright;
    }

    /**
     * Sets {@code copyright} applied to this object
     *
     * @param copyright to be applied to this object
     */
    public void setCopyright(String copyright)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setCopyright(copyright);
	    return;
	}

	this.copyright = copyright;
    }


    /**
     * Returns verbatim repsentation of this object
     * supporting "variable atomization"
     *
     * @return verbatim rpresentation of this object
     */
    public String getVerbatim()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getVerbatim();

	return verbatim;
    }

    /**
     * Returns scope of this object as a {@code String}
     *
     * @return String rpresentating scope of the object
     */
    public String getScope()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getScope();

	return scope;
    }

    /**
     * Sets {@code scope} as scope of this object
     *
     * @param verbatim rpresentation of this object in {@code String}
     */
    public void setScope(String scope)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setScope(scope);
	    return;
	}

	this.scope = scope;
    }

    /**
     * Sets {@code verbatim} as a verbatim
     * repsentation of this object
     * supporting "variable atomization"
     *
     * @param verbatim rpresentation of this object in {@code String}
     */
    public void setVerbatim(String verbatim)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setVerbatim(verbatim);
	    return;
	}

	this.verbatim = verbatim;
    }

    /**
     * Returns notes on this object
     *
     * @return notes on this object
     */
    public String getNotes()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getNotes();

	return notes;
    }

    /**
     * Sets {@code notes} on this object
     *
     * @param notes on this object
     */
    public void setNotes(String notes)
    {
	if(entity != null) {
	    ((NamedObject)getEntity()).setNotes(notes);
	    return;
	}

	this.notes = notes;
    }

    public boolean isParsed()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).isParsed();

	return (sources != null && sources.size() > 0);
    }

    /**
     * Returns an array of {@code NamedObject}s
     * represented by this object
     * 
     * @return an array of {@code NamedObject}s
     * represented by this object
     */
    public Collection<NamedObject<?>> getSourceObjects()
    {
	if(entity != null)
	    return ((NamedObject<?>)getEntity()).getSourceObjects();

	return sources;
    }

    /**
     * Set {@code NamedObject}s in {@code sources}
     * as objects represented by this object
     * 
     * @param sources an array of {@code NamedObject}s
     * to be represented by this object
     */
    public void setSourceObjects(NamedObject<?>[] sources)
    {
	if(entity != null) {
	    castNamedObject(getNamedObject()).setSourceObjects(sources);
	}
	else if (sources == null) {
	    this.sources = null;
	}
	else if (sources.length == 0) {
	    this.sources = Collections.emptyList();
	}
	else {
	    if (this.sources == null)
		this.sources = new ArrayList<NamedObject<?>>(sources.length);
	    for (int i = 0; i < sources.length; i++) {
		this.sources.add(sources[i]);
	    }
	}
    }

    /**
     * Set {@code NamedObject}s in {@code sources}
     * as objects represented by this object
     * 
     * @param sources a {@code List} of {@code NamedObject}s
     * to be represented by this object
     */
    public void setSourceObjects(Collection<? extends NamedObject<?>>  sources)
    {
	if(entity != null) {
	    getNamedObject().setSourceObjects(sources);
	}
	else {
	    if (sources == null) {
		this.sources = null;
	    }
	    else if (sources.size() == 0) {
		this.sources = Collections.emptyList();
	    }
	    else {
		if (this.sources == null) {
		    this.sources = new ArrayList<NamedObject<?>>(sources);
		}
		else {
		    this.sources.clear();
		    this.sources.addAll(sources);
		}
	    }
	}
    }

    /**
     * Adds {@code source} to the list of {@code NamedObject}s
     * represented by this object
     * 
     * @param source a {@code NamedObject} to be 
     * represented by this object
     */
    public void addSourceObject(NamedObject<?> source)
    {
	if(entity != null) {
	    getNamedObject().addSourceObject(source);
	}
	else {
	    if (this.sources == null)
		this.sources = new ArrayList<NamedObject<?>>(1);
	    this.sources.add(source);
	}
    }

    /**
     * Removes {@code source} from the list of {@code NamedObject}s
     * represented by this object
     * 
     * @param source a {@code NamedObject} to be 
     * removed from list of {@code NamedObject} represented by this object
     */
    public void removeSourceObject(NamedObject<?> source)
    {
	if(entity != null) {
	    getNamedObject().removeSourceObject(source);
	}
	else if (sources != null) {
	    sources.remove(source);
	}
    }

    /**
     * Returns an <code>Iterator</code> of {@code list}'s
     * contents, or null if {@code vector} is null or empty
     *
     * @param list {@code List} to get its elements as an
     * {@code Iterator}
     *
     * @return Iterator of {@code vector}'s contents
     * or null if {@code vector} is null or empty
     */ 
    protected static <E> Iterator<E> elements(List<E> list)
    {
	if(list == null || list.isEmpty())
	    return null;

	return list.iterator();
    }

    /**
     * Returns an <code>Collectcion</code> of {@code map}'s
     * contents, or null if {@code map} is null or empty
     *
     * @param map {@code Map} to get its elements as an
     * {@code Collection}
     *
     * @return Collection of {@code map}'s contents
     * or null if {@code map} is null or empty
     */ 
    static protected <K, V> Collection<V> elements(Map<K, V> map)
    {
	if(map == null || map.isEmpty())
	    return null;

	return map.values();
    }

    /**
     * Returns an <code>Enumeration</code> of {@code map}'s
     * keys, or null if {@code map} is null or empty
     *
     * @param map {@code Map} to get its keys as an
     * {@code Enumeration}
     *
     * @return Enumeration of {@code map}'s contents
     * or null if {@code map} is null or empty
     */ 
    static protected <K, V> Set<K> keySet(Map<K, V> map)
    {
	if(map == null || map.isEmpty())
	    return null;

	return map.keySet();
    }

    /**
     * Returns XML {@code String} representing this object
     *
     * @return XML {@code String} representing this object
     */
    public String toXML()
    {
	return toXML(null).toString();
    }

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
    public StringBuffer toXML(StringBuffer buffer)
    {
	if(buffer == null)
	    buffer = new StringBuffer();
	return buffer;
    }


    /**
     * Returns XML {@code String} of the all related {@code NamedObject}s
     *
     * @return XML {@code String} representing all {@code NamedObject}s 
     * relationg to the {@code NamedObject}
     */
    public String toRelevantXML()
    {
        return toRelevantXML(null).toString();
    }

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
    public StringBuffer toRelevantXML(StringBuffer buffer)
    {
	if(buffer == null)
	    buffer = new StringBuffer();
	return toXML(buffer);
    }

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
	throws SQLException
    {
	return -1;
    }

    /**
     * Returns a SQL to create a relevant table in given
     * {@code sqlType}
     *
     * @param sqlType {@code Locale} representing the target SQL subset
     *
     * @return String representing a SQL to create relevant table
     */
    public String getCreateTableSQL(Locale sqlType)
    {
	return getCreateTableSQL(null, sqlType).toString();
    }

    /**
     * Appends a SQL to create a relevant table at the end of
     * {@code buffer}.  If {@code buffer} is null,
     * it creates a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * SQL to be appended, or null to create a new
     * CODE>StringBuffer}
     * @param sqlType {@code Locale} representing the target SQL subset
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer getCreateTableSQL(StringBuffer buffer,
					  Locale sqlType)
    {
	return getCreateTableSQL(buffer,
				 ResourceKey.NOMENCURATOR,
				 sqlType);
    }

    /**
     * Appends a SQL to create a relevant table at the end of
     * {@code buffer}.  If {@code buffer} is null,
     * it creates a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * SQL to be appended, or null to create a new
     * CODE>StringBuffer}
     * @param resourceBaseName {@code String} basename of property files
     * @param sqlType {@code Locale} representing the target SQL subset
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer getCreateTableSQL(StringBuffer buffer,
					  String resourceBaseName,
					  Locale sqlType)
    {
	if(buffer == null)
	    buffer = new StringBuffer();
	String[][] keys = 
	    ResourceKey.getCreateTablesSequenceKeys();
	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, sqlType);
	    for(int i = 0; i < keys[0].length; i++) {
		String sql = 
		    resource.getString(keys[0][i]);
		String param =
		    resource.getString(keys[1][i]);
		if(param.length() == 0)
		    buffer.append(sql);
		else {
		    StringTokenizer token = 
			new StringTokenizer(param);
		    int tokens = token.countTokens();
		    Object[] params = new Object[tokens];
		    for(int j = 0; j < tokens; j++)
			params[j] = token.nextToken();
		    buffer.append(MessageFormat.format(sql, params));
		    for(int j = 0; j < tokens; j++)
			params[j] = null;
		    params = null;
		    token = null;
		}
	    }
	}
	catch(MissingResourceException e) {
	}
	return buffer;
    }

    /**
     * Inserts the object to the database specified by the
     * {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return object ID of inserted object
     */
    public int insert(Connection connection)
    	throws SQLException
    {
	return 0;
    }


    /**
     * Inserts the object to the database specified by the
     * {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return object ID of inserted object
     */
    public int update(Connection connection)
    	throws SQLException
    {
	return 0;
    }


    /**
     * Inserts the object to the database specified by the
     * {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return object ID of inserted object
     */
    public int delete(Connection connection)
    	throws SQLException
    {
	return 0;
    }


    /**
     * Returns object ID of the named object, or zero if the
     * object doesn't exist in the database behind
     * given {@code connection}
     *
     * @param connection {@code NamedObjectConnection} to the database
     *
     * @return the object ID of the NamedObject in the {@code connection}
     * or zero if it is no in the database.
     */
    public int getObjetID(NamedObjectConnection<? extends T> connection)
    	throws SQLException
    {
	return connection.getObjectID(this);
    }

    /**
     * Sets {@code objectID} of the NamedObject for the
     * object in the database behind {@code connection}.
     *
     * @param objectID {@code int} of object ID in the database
     * @param connection {@code NamedObjectConnection} to the database
     */
    public void setObjetID(int objectID, NamedObjectConnection<? extends T> connection)
    {
	connection.setObjectID(objectID, this);
    }


    /**
     * Returns a SQL to insert the object to a relevant table
     *
     * @return String representing a SQL to insert the object
     * to a relevant table
     */
    public String toSQL(int objectID)
    {
	return toSQL(null, objectID).toString();
    }

    /**
     * Appends a SQL to insert to a relevant table at the end of
     * {@code buffer}.  If {@code buffer} is null,
     * it creates a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * SQL to be appended, or null to create a new
     * CODE>StringBuffer}
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer toSQL(StringBuffer buffer,
			      int objectID)
    {
	if(buffer == null)
	    buffer = new StringBuffer();

	buffer.append("INSERT INTO NamedObject VALUES(");
	// objectID BIGINT PRIMARY KEY
	buffer.append(objectID).append(", ");

	// persistentID TEXT
	buffer.append(getPersistentID()).append(", ");

	// contributor TEXT
	if(contributor == null)
	    buffer.append("NULL, ");
	else
	    buffer.append(contributor).append(", ");

	// objectType TEXT
	buffer.append(getClassName()).append(", ");

	// editable INTEGER
	if(editable)
	    buffer.append("1, ");
	else
	    buffer.append("0, ");

	// copyright TEXT
	if(copyright == null)
	    buffer.append("NULL, ");
	else
	    buffer.append(copyright).append("NULL, ");

	if(verbatim == null)
	    buffer.append("NULL, ");
	else
	    buffer.append(verbatim).append("NULL, ");


	// notes TEXT
	if(notes == null)
	    buffer.append("NULL");
	else
	    buffer.append(notes).append("NULL");

	// lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
	// so unnecessary to specify from applications

	buffer.append(");");
	return buffer;
    }

    /**
     * Sets paremeters to {@code statement} to INSERT, UPDATE 
     * or DELETE
     *
     * @param statement {@code PreparedStatement} to which 
     * paramters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public int setUpdateParamters(PreparedStatement statement,
				    int objectID)
	throws SQLException
    {
	/*
	if(!editable && !modified)
	    return 0;
	*/

	// objectID BIGINT PRIMARY KEY
	statement.setInt(1, objectID);

	// persistentID TEXT
	statement.setString(2, getPersistentID());

	// contributor TEXT
	if(contributor == null)
	    statement.setNull(3, Types.VARCHAR);
	else
	    statement.setString(3, contributor);

	// objectType TEXT
	statement.setString(4, getClassName());

	// editable INTEGER
	if(editable)
	    statement.setInt(5, 1);
	else
	    statement.setInt(5, 0);

	// copyright TEXT
	if(copyright == null)
	    statement.setNull(6, Types.VARCHAR);
	else
	    statement.setString(6, copyright);

	// verbatim TEXT
	if(verbatim == null)
	    statement.setNull(7, Types.VARCHAR);
	else
	    statement.setString(7, verbatim);

	// notes TEXT
	if(notes == null)
	    statement.setNull(8, Types.VARCHAR);
	else
	    statement.setString(8, notes);

	// lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
	// so unnecessary to specify from applications

	return 8;
    }

    /**
     * Inserts the object to the database specified by the
     * {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return object ID of inserted object
     */
    public int insertTo(NamedObjectConnection<?> connection)
    	throws SQLException
    {
	if(connection.isReadOnly())
	    return -1;
	// need to lock....
	PreparedStatement statement =
	    connection.prepareStatement("INSERT INTO NamedObject VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
	int objectID = connection.getNextObjectID();
	setUpdateParamters(statement, objectID);
	statement.executeUpdate();
	return objectID;
    }

    /**
     * Returns a precompiled statement to insert an object
     * of this class to {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return List containing precompiled SQL statement to insert
     */
    public List<PreparedStatement> prepareInsertStatement(Connection connection)
    	throws SQLException
    {
	if(connection.isReadOnly())
	    return null;

	List<PreparedStatement> statements = Collections.synchronizedList(new ArrayList<PreparedStatement>());
	statements.add(connection.prepareStatement("INSERT named_object VALUES(?, ?, ?, ?, ?, ?, ?, ?);"));
	return statements;
    }

    /**
     * Returns a precompiled statement to insert an object
     * of this class to {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return List containing precompiled SQL statement to update
     */
    public List<PreparedStatement> prepareUpdateStatement(Connection connection)
    	throws SQLException
    {
	if(connection.isReadOnly())
	    return null;

	List<PreparedStatement> statements = Collections.synchronizedList(new ArrayList<PreparedStatement>());
	StringBuffer statement = new StringBuffer("UPDATE named_object SET ");
	statement.append("persistent_id = ?, "); //1
	statement.append("contributor = ?, "); //2
	statement.append("object_type = ?, "); //3
	statement.append("editable = ?, ");  //4
	statement.append("coyright = ?, ");  //5
	statement.append("notes = ?");       //6
	statement.append(" WHERE object_id = ?;"); //7
	statements.add(connection.prepareStatement(statement.toString()));
	return statements;
    }

    /**
     * Returns a precompiled statement to delete an object
     * of this class in {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return List containing precompiled SQL statement to delete
     */
    public List<PreparedStatement> prepareDeleteStatement(Connection connection)
    	throws SQLException
    {
	if(connection.isReadOnly())
	    return null;

	List<PreparedStatement> statements = Collections.synchronizedList(new ArrayList<PreparedStatement>());
	StringBuffer statement = new StringBuffer("DELERE FROM named_object WHERE persistent_id = ?;");
	statements.add(connection.prepareStatement(statement.toString()));
	return statements;
    }

    /**
     * Sets paremeters to {@code PreparedStatement} in 
     * {@code statements} to INSERT to the database
     *
     * @param statements {@code Iterator} of 
     * {@code PreparedStatement} to which 
     * parameters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setInsertParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException
    {
	PreparedStatement statement = statements.next();
	// objectID BIGINT PRIMARY KEY
	statement.setInt(1, objectID);

	// persistentID TEXT
	statement.setString(2, getPersistentID());

	// contributor TEXT
	String s = getContributor();
	if(s == null)
	    statement.setNull(3, Types.VARCHAR);
	else
	    statement.setString(3, s);

	// objectType TEXT
	statement.setString(4, getClassName());

	// editable INTEGER
	if(editable)
	    statement.setInt(5, 1);
	else
	    statement.setInt(5, 0);
    
	// copyright TEXT
	s = getCopyright();
	if(s == null)
	    statement.setNull(6, Types.VARCHAR);
	else
	    statement.setString(6, s);

	// copyright TEXT
	s = getVerbatim();
	if(s == null)
	    statement.setNull(7, Types.VARCHAR);
	else
	    statement.setString(7, s);

	// notes TEXT
	s = getNotes();
	if(s == null)
	    statement.setNull(8, Types.VARCHAR);
	else
	    statement.setString(8, s);

	// lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
	// so unnecessary to specify from applications
    }

    /**
     * Sets paremeters to {@code PreparedStatement} in 
     * {@code statements} to UPDATE data in the database
     *
     * @param statements {@code Iterator} of 
     * {@code PreparedStatement} to which 
     * parameters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setUpdateParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException
    {
	PreparedStatement statement = statements.next();
	statement.setString(1, getPersistentID());
	statement.setString(2, getContributor());
	statement.setString(3, getClassName());
	if(isEditable())
	    statement.setInt(4, 1);
	else
	    statement.setInt(4, 0);
	statement.setString(5, getCopyright());
	statement.setString(6, getVerbatim());
	statement.setString(7, getNotes());
	statement.setInt(8, objectID);
    }

    /**
     * Sets paremeters to {@code PreparedStatement} in 
     * {@code statements} to DELETE data in the database
     *
     * @param statements {@code Iterator} of 
     * {@code PreparedStatement} to which 
     * parameters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setDeleteParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException
    {
	PreparedStatement statement = statements.next();
	statement.setInt(1, objectID);	
    }

    /**
     * Returns a summarized expression of this {@code NamedObject}
     *
     * @return String representing summary of this {@code NamedObject}
     */
    public String getSummary()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getSummary();

	return getPersistentIDContents();
    }

    /*
    protected synchronized <T extends Name> int find(T target, T[] array)
    {
	if(array == null || target == null)
	    return -1;

	Object entity = (T)target.getEntity();

	synchronized (array) {
	    for(int i = 0; i < array.length; i++) {
		if(array[i].getEntity() == entity)
		    return i;
	    }
	}
	return -1;
    }

    protected <T extends Name> T[] add(T target, T[] source)
    {
	if(target == null)
	    return source;

	T[] toReturn = source;
	if(source == null) {
	    //	    toReturn = (T[])java.lang.reflect.ArrayUtility.newInstance(target.getClass().getComponentType(), 1);
	    toReturn = (T[])java.lang.reflect.ArrayUtility.newInstance(target.getClass(), 1);
	    toReturn[0] = target;
	}
	else {
	    synchronized (source) {
		if(find(target, source) == -1) {
		    //toReturn = (T[])java.lang.reflect.ArrayUtility.newInstance(target.getClass().getComponentType(), source.length + 1);
		    toReturn = (T[])java.lang.reflect.ArrayUtility.newInstance(target.getClass(), source.length + 1);
		    System.arraycopy(source, 0, toReturn, 0, source.length);
		    toReturn[source.length] = target;
		}
	    }
	}

	return toReturn;
    }

    protected <T extends Name> T[] remove(T target, T[] source)
    {
	if(source == null)
	    return null;

	if(target == null)
	    return source;

	T[] toReturn = source;
	synchronized (source) {
	    int t = find(target, source);
	    if(t != -1) {
		if(source.length == 1) {
		    toReturn = null;
		}
		else {
		    toReturn = (T[])java.lang.reflect.ArrayUtility.newInstance(target.getClass().getComponentType(), source.length - 1);
		    System.arraycopy(source, 0, toReturn, 0, t);
		    System.arraycopy(source, t + 1, toReturn, t, source.length - t - 1);
		}
	    }
	}

	return toReturn;
    }
    */

    /*
    protected <T> void clear(T[] source)
    {
	if(source == null)
	    return;

	for(int i = 0; i < source.length; i++) {
	    source[i] = null;
	}
    }
    */

    /*
    protected <T> T[] copy(T[] source)
    {
	T[] toReturn = null;
	if(source != null) {
	    synchronized (source) {
		//toReturn = (T[])java.lang.reflect.Array.newInstance(source[0].getClass().getComponentType(), source.length - 1);
		toReturn = (T[])java.lang.reflect.Array.newInstance(source[0].getClass(), source.length);
	    }
	}

	return toReturn;
    }
    */

    @SuppressWarnings({"unchecked"})
    public NamedObject<T> getNamedObject(Object object) {
	if(isAssignableFrom(object))
	    return (NamedObject<T>)getClass().cast(object);
	else
	    return null;
    }

    @SuppressWarnings({"unchecked"})
    public AbstractNamedObject<T> getAbstractNamedObject(Object object) {
	if(isAssignableFrom(object))
	    return getClass().cast(object);
	else
	    return null;
    }

    protected ObjectExchanger<T> exchanger;

    public ObjectExchanger<T> getObjectExchanger() { return exchanger; }
    
    public void setObjectExchanger(ObjectExchanger<T> exchanger)
    {
	    this.exchanger = exchanger;
    }

    public String getDetail() {
	Document document = getDetailDocument();
	String detail = null;
	try {
	    detail = document.getText(0, document.getLength());
	} catch (BadLocationException e) {
	    detail = getSummary();
	}
	return detail;
    }

    public Document getDetailDocument() {
	Document document = new DefaultStyledDocument();
	try {
	    document.insertString(0, getSummary(), null);
	} catch (BadLocationException e) {
	}
	return document;
    }
}

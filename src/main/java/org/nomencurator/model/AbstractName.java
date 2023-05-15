/*
 * AbstractName.java:  a class provaides a extendable string
 * for Java implementation of the Nomencurator,
 * a Nomenclature Heuristic Model.  For restriction of "final class String"
 * in Java, it uses Name instead of String.  It spoiles simplicity
 * and conceptual importance of name commutability.
 *
 * The same thing can be implemented using nameing mechanism of CORBA.
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2006, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071, JP19K12711
 */

package org.nomencurator.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.String;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

import org.nomencurator.beans.PropertyChanger;

/**
 * A {@code Name} provides a name, a tagged indirect reference.
 * It "extends" the final {@code String} class, which makes an
 * {@code Object} "compatible" with {@code String},
 * i.e. make namable.
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractName<T extends Name<?>>
    extends PropertyChanger
    implements Name<T>, Serializable
{
    private static final long serialVersionUID = -6225343190498874223L;

    /** string entitiy */
    protected String literal;

    /** object entitiy */
    protected Object entity;

    protected boolean resolved;

    protected boolean implicit;


    /**
     * Returns true if {@code object} equals to this {@code Name}
     *
     * @param object {@code Object} to be compared
     *
     * @return true if {@code object} equals to this {@code Name}
     */
    public boolean equals(Object object)
    {
	if(this == object) return true;
	if(object == null) return false;
	if(!(object instanceof Name)
	     && literal != null 
	     && !literal.equals(object))
		return false;

	if(getClass() != object.getClass()) return false;

	if (object instanceof Name) {
	    final Name<?> n = (Name<?>)object;

	    if(getEntity() == n.getEntity())
		return true;

	    return Objects.equals(getLiteral(), n.getLiteral());
	}
	return false;
    }

    @Override
    public int hashCode() {
	return Objects.hash(literal, entity, resolved);
    }

    protected static Nomencurator curator = Nomencurator.getInstance();

    public AbstractName()
    {
	this(ANONYMOUS);
    }

    public AbstractName(String literal)
    {
	this(literal, null);
    }

    public AbstractName(String literal, T entity)
    {
	this.literal = literal;
	this.entity = entity;
	resolved = true;
    }

    /**
     * Gets name in {@code String}
     *
     * @return String representing a name
     */
    public String getLiteral()
    {
	if(entity != null && entity instanceof Name)
	    return ((Name)entity).getLiteral();
	return literal;
    }

    /**
     * Gives a name as a {@code String}
     *
     * @param String representing a name
     */
    public void setLiteral(String name)
    {
	String oldLiteral = literal;
	literal = name;
	firePropertyChange(new PropertyChangeEvent(this, "literal", oldLiteral, literal));
    }

    /**
     * Gets synonym in {@code String},
     * or null if it is not a synonym
     *
     * @return String representing a name
     */
    public String getSynonym()
    {
	if(isSynonym())
	    return literal;
	return null;
    }

    /**
     * Gets entity of this {@code Name}.
     * Returns itself if it doesn't have entity separately,
     * or entity if the entity non-null.
     * If the entity is an instance of {@code Name},
     * the method returns the result of recursive apply of
     * this method
     *
     * @return Object enitity representied by the name.  It is never null.
     */
    @SuppressWarnings("unchecked")
    public T getEntity()
    {
	Name<T> name = getName();
	if(name != this)
	    return name.getEntity();

	if(entity != null)
	    return (T)entity;

	return (T)this;
    }

    /**
     * Sets a {@code object} as the entity of the name
     *
     * @param object representing the entity
     * @exception IllegalArgumentException if {@code object} is this object
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setEntity(Object entity)
    {
	try {
	    setEntity((T)entity);
	}
	catch (IllegalArgumentException e) {
	    throw e;
	}
	catch (Throwable t) {	
	}
    }

    /**
     * Sets a {@code object} as the entity of the name
     *
     * @param object representing the entity
     * @exception IllegalArgumentException if {@code object} is this object
     */
    public void setEntity(T entity)
    {
	List<Object> entityPath = getEntityPath();
	if(entity == this || entityPath != null &&  (entityPath.contains(entity)))
	    throw new IllegalArgumentException(getClass().getName() +
					       "#setEntity(): self reference is prohibited");

	if(this.entity != entity) {
	    Object oldEntity = this.entity;
	    this.entity = entity;
	    firePropertyChange(new PropertyChangeEvent(this, "entity", oldEntity, this.entity));
	}
    }

    public List<Object> getEntityPath() 
    {
	Name<T> n = getName();
	if(n != this)
	    return n.getEntityPath();

	Stack<Object> stack = new Stack<Object>();
	Object entity = getEntity();
	while (entity != null && stack.search(entity) == -1) {
	    stack.push(entity);
	    if(entity instanceof Name)
		entity = getName(entity).getEntity();
	    else
		entity = null;
	}

	List<Object> entityPath = null;
	int size = stack.size();
	if(size > 0) {
	    entityPath = new ArrayList<Object>(size);
	    while(!stack.empty()){
		entityPath.add(stack.pop());
	    }
	}
	return entityPath;
    }

    @Override
    public Name<T> getName()
    {
	if(entity == null || !isAssignableFrom(entity))
	    return this;

	return getName(entity).getName();
    }

    /**
     * Returns true if this name is a tautology.
     * A pedantic method.
     *
     * @return true if the name refers to itself
     */
    public boolean isTautology()
    {
	T name = getEntity();
	if(name != this)
	    return name.isTautology();

	if(literal != null)
	    return literal.equals(getLiteral());

	return true;
    }

    /**
     * Returns true if the entity is unnamed
     * A pedantic method.
     *
     * @return true if the entity is not named
     */
    public boolean isAnonymous()
    {
	return ((literal == null || literal.equals(ANONYMOUS)) && getEntity() != this);
    }

    /**
     * Returns true if this name is nominal, i.e. no entity
     * A pedantic method.
     *
     * @return true if the entity is null
     */
    public boolean isNominal()
    {
	return (literal != null && literal.length() != 0 &&  (getEntity() == this));
    }

    /**
     * Returns true if this name is a synonym, i.e. both string
     * and entity are non-null.
     * A pedantic method?  Could be...
     *
     * @return true if both literal and entity are non-null
     */
    public Boolean isSynonym()
    {
	return (literal != null && !literal.equals(ANONYMOUS) &&  (getEntity() != this)) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Returns true if {@code name} is a synonym of this
     * {@code Name}, i.e. both have the same entity
     * but different in spelling.
     *
     * @param name {@code Name} to be examined
     *
     * @return true if both literal and entity are non-null
     */
    public Boolean synonym(Name<?> name)
    {
	if(name == null || name == this ||
	   getLiteral().equals(name.getLiteral()))
	    return Boolean.FALSE;

	return (getEntity() == name.getEntity()) ? Boolean.TRUE: Boolean.FALSE;
    }

    /**
     * Returns true if {@code name} is a homonym of this
     * {@code Name}, i.e. they have the same spelling
     * but different entity
     *
     * @param name {@code Name} to be examined
     *
     * @return true if {@code name} is a homonym of this {@code Name}
     */
    public Boolean homonym(Name<?> name)
    {
	if(name == null || name == this ||
	   getEntity() != name.getEntity())
	    return Boolean.FALSE;

	return (getLiteral().equals(name.getLiteral())) ? Boolean.TRUE: Boolean.FALSE;
    }

    /**
     * Returns true if the entity is unnamed
     * A pedantic method.
     *
     * @return true if it does not have independent entity nor named
     */
    public boolean isSelf()
    {
	return ((literal == null || literal.equals(ANONYMOUS)) && getEntity() == this);
    }

    /**
     * Returns true if the entity is resolved, i.e. additional
     * access to data source doesn't improve its contents
     *
     * @return true if it contents is already satureted
     * as in the data source
     */
    public boolean isResolved()
    {
	if(entity != null && entity instanceof Name)
	    return ((Name)entity).isResolved();

	return resolved;
    }

    public boolean isImplicit()
    {
	if(entity != null && entity instanceof Name)
	    return ((Name)entity).isImplicit();

	return implicit;
    }

    /**
     * Set whether the object is {@code implicit} or not.
     *
     * @param implicit bollean value to be set.
     */
    public void setImplicit(boolean implicit)
    {
	if(entity != null && entity instanceof AbstractName<?>)
	    ((AbstractName)entity).setImplicit(implicit);

	this.implicit = implicit;
    }

    /**
     * Reteruns class name without package part
     *
     * @return class name without package part
     */
    public String getClassName()
    {
	String className = getClass().getName();
	return className.substring(className.lastIndexOf(".") + 1);
    }


    /*
     * interfaces to String class
     */
    public char charAt(int index) { return literal.charAt(index); }

    public int compareTo(Object o) { return literal.compareTo(o.toString()); }

    public int compareTo(String anotherString) { return literal.compareTo(anotherString); }

    public int compareToIgnoreCase(String str) { return literal.compareToIgnoreCase(str); }

    public String concat(String str) { return literal.concat(str); }

    public static String copyValueOf(char[] data) { return String.copyValueOf(data); }

    public static String copyValueOf(char[] data, int offset, int count) { return String.copyValueOf(data, offset, count); }

    public boolean endsWith(String suffix) { return literal.endsWith(suffix); }

    //    public boolean equals(Object anObject) { return literal.equals(anObject); }

    public boolean equalsIgnoreCase(String anotherString) { return literal.equalsIgnoreCase(anotherString); }

    public byte[] getBytes() { return literal.getBytes(); }

    @SuppressWarnings(value = {"deprecation"})
    public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin) { literal.getBytes(srcBegin, srcEnd, dst, dstBegin); }

    public byte[] getBytes(String enc) throws java.io.UnsupportedEncodingException { return literal.getBytes(enc); }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) { literal.getChars(srcBegin, srcEnd, dst, dstBegin); }

    //public int hashCode() { return literal.hashCode(); }

    public int indexOf(int ch) { return literal.indexOf(ch); }

    public int indexOf(int ch, int fromIndex) { return literal.indexOf(ch, fromIndex); }

    public int indexOf(String str) { return literal.indexOf(str); }

    public int indexOf(String str, int fromIndex) { return literal.indexOf(str, fromIndex); }

    public String intern() { return literal.intern(); }

    public int lastIndexOf(int ch) { return literal.lastIndexOf(ch); }

    public int lastIndexOf(int ch, int fromIndex) { return literal.lastIndexOf(ch, fromIndex); }

    public int lastIndexOf(String str) { return literal.lastIndexOf(str); }

    public int lastIndexOf(String str, int fromIndex) { return literal.lastIndexOf(str, fromIndex); }

    public int length() { return literal.length(); }

    public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len) {
	return literal.regionMatches(ignoreCase, toffset, other, ooffset, len); }

    public boolean regionMatches(int toffset, String other, int ooffset, int len) {
	return literal.regionMatches(toffset, other, ooffset, len); }

    public String replace(char oldChar, char newChar) { return literal.replace(oldChar, newChar); }

    public boolean startsWith(String prefix) { return literal.startsWith(prefix); }

    public boolean startsWith(String prefix, int toffset) { return literal.startsWith(prefix, toffset); }

    public String substring(int beginIndex) { return literal.substring(beginIndex); }

    public String substring(int beginIndex, int endIndex) { return literal.substring(beginIndex, endIndex); }

    public char[] toCharArray() { return literal.toCharArray(); }

    public String toLowerCase() { return literal.toLowerCase(); }

    public String toLowerCase(Locale locale) { return literal.toLowerCase(locale); }

    public String toString() { return literal.toString(); }

    public String toUpperCase() { return literal.toUpperCase(); }

    public String toUpperCase(Locale locale) { return literal.toUpperCase(locale); }

    public String trim() { return literal.trim(); }

    public static String valueOf(boolean b) { return String.valueOf(b); }

    public static String valueOf(char c) { return String.valueOf(c); }

    public static String valueOf(char[] data) { return String.valueOf(data); }

    public static String valueOf(char[] data, int offset, int count) {
	return String.valueOf(data, offset, count); }

    public static String valueOf(double d) { return String.valueOf(d); }

    public static String valueOf(float f) { return String.valueOf(f); }

    public static String valueOf(int i) { return String.valueOf(i); }

    public static String valueOf(long l) { return String.valueOf(l); }

    public static String valueOf(Object obj) { return String.valueOf(obj); }

    public boolean isInstance(Object object) {
	return this.getClass().isInstance(object);
    }

    public boolean isAssignableFrom(Object object) {
	if(object == null)
	    return false;
	return getClass().isAssignableFrom(object.getClass());
    }

    // casts only when castable
    @SuppressWarnings({"unchecked"})
    public Name<T> cast(Object object) {
	if(isAssignableFrom(object))
	    return (Name<T>)getClass().cast(object);
	return null;
    }

    @SuppressWarnings({"unchecked"})
    public Name<T> getName(Object object) {
	if(isAssignableFrom(object))
	    return (Name<T>)getClass().cast(object);
	return null;
    }

    @SuppressWarnings({"unchecked"})
    public AbstractName<T> getAbstractName(Object object) {
	if(isAssignableFrom(object))
	   return getClass().cast(object);
	return null;
    }

}

/*
 * Name.java: an interface provaides nameing
 * for Java implementation of the Nomencurator, 
 * a Nomenclature Hierarchy-history Model.
 *
 * Copyright (c) 2002, 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.List;
import java.util.Locale;

/**
 * Name is a interface to provide pseudo extension of java.lang.String represnting nameing actions,
 * i.e. relationshp between a literal and an object designated by the literal.
 *
 * @version 	22 June 2016
 * @author 	Nozomi `James' Ytow
 */
public interface Name <N extends Name<?,  ?>, T extends N>
//public interface Name <T>
{
    /** Empty string */
    public static final String EMPTY_STRING = "";
    public static final String ANONYMOUS = EMPTY_STRING;

    /**
     * Gets name in <CODE>String</CODE>
     *
     * @return String representing a name
     */
    public String getLiteral();

    /**
     * Gives a name as a <CODE>String</CODE>
     *
     * @param name String representing the name
     */
    public void setLiteral(String literal);

    /**
     * Gets entity of this <CODE>Name</CODE>.
     * Returns itself if it doesn't have entity separately,
     * or entity if the entity non-null.
     * If the entity is an instance of <CODE>Name</CODE>,
     * the method returns the result of recursive application of 
     * this method
     *
     * @return Object enitity representied by the name.  It is never null.
     */
    public T getEntity();

    /**
     * Sets a <CODE>object</CODE> as the entity of the name
     *
     * @param object representing the entity
     * @exception IllegalArgumentException if <CODE>object</CODE> is this object
     */
    //public void setEntity(Name<T> entity);
    public void setEntity(T entity);
    //public void setEntity(Object entity);

    /**
     * Sets a <CODE>object</CODE> as the entity of the name if it is an instance of type <tt>T</tt>,
     * or do nothing.
     *
     * @param object representing the entity
     * @exception IllegalArgumentException if <CODE>object</CODE> is this object
     */
    public void setEntity(Object entity);

    /**
     * Returns the path of entities designated this <tt>Name</tt>
     *
     * @return <tt>List</tt> representing path of entities designated this <tt>Name</tt>
     */
    public List<Object> getEntityPath() ;

    /**
     * Returns <CODE>Name</CODE> having no entity object or an eitity instance
     * of other than <CODE>Name</CODE>.  If the entity of this object
     * is an instance of <CODE>Name</CODE>, the method returns result
     * of recursive application of this method.
     * 
     * @return Name have no enitity or non-<CODE>Name</CODE> entity
     */
    public Name<N, T> getName();

    /**
     * Gets synonym in <CODE>String</CODE>,
     * or null if it is not a synonym
     *
     * @return String representing a name
     */
    public String getSynonym();

    /**
     * Returns true if this name is a tautology.
     * A pedantic method.
     *
     * @return true if the name refers to itself
     */
    public boolean isTautology();

    /**
     * Returns true if the entity is unnamed
     * A pedantic method.
     *
     * @return true if the entity is not named
     */
    public boolean isAnonymous();

    /**
     * Returns true if this name is nominal, i.e. the
     * object is not an entity.
     * A pedantic method.
     *
     * @return true if the entity is null
     */
    public boolean isNominal();

    /**
     * Returns true if this name is a synonym, i.e. both string
     * and entity are non-null.
     * A pedantic method?  Could be...
     *
     * @return true if both string and entity are non-null
     */
    public Boolean isSynonym();

    /**
     * Returns true if <CODE>name</CODE> is a synonym of this
     * <CODE>Name</CODE>, or null if unknown.
     *
     * @param name <CODE>Name</CODE> to be examined
     *
     * @return true if <tt>name</tt> is synonym of this.
     */
    //public Boolean isSynonym(Name<? extends N, ? extends T> name);
    public Boolean synonym(Name<?, ?> name);

    /**
     * Returns true if <CODE>name</CODE> is a homonym of this
     * <CODE>Name</CODE>, i.e. they have the same spelling
     * but different entity
     *
     * @param name <CODE>Name</CODE> to be examined
     *
     * @return true if <CODE>name</CODE> is a homonym of this <CODE>Name</CODE>
     */
    //public Boolean isHomonym(Name<? extends N, ? extends T> name);
    //public Boolean isHomonym(Name<N, ? extends T> name);
    public Boolean homonym(Name<?, ?> name);

    /**
     * Returns true if the entity is unnamed
     * A pedantic method.
     *
     * @return true if it does not have independent entity nor named
     */
    public boolean isSelf();

    /**
     * Returns true if the entity is resolved, i.e. additional
     * access to data source doesn't improve its contents
     *
     * @return true if it contents is already satureted
     * as in the data source
     */
    public boolean isResolved();

    /**
     * Returns true if the object is implicit in some sense.
     *
     * @return true if the object is an implict one.
     */
    public boolean isImplicit();

    /**
     * Reteruns class name without package part
     *
     * @return class name without package part
     */
    public String getClassName();

    /**
     * Gets a lexicon representing this object
     *
     * @return Object representing this object
     */
    //    public Object getLexicon();

    /**
     * Gives <CODE>lexicon</CODE> as the name
     *
     * @param lexicon Object representing a name
     */
    //    public void getLexicon(Object lexicon);

    public char charAt(int index);
    
    public int compareTo(Object o);
    
    public int compareTo(String anotherString);
    
    public int compareToIgnoreCase(String str);
    
    public String concat(String str);
    
    public boolean endsWith(String suffix);
    
    public boolean equalsIgnoreCase(String anotherString);
    
    public byte[] getBytes();
    
    public void getBytes(int srcBegin, int srcEnd, byte[] dst, int dstBegin);
    
    public byte[] getBytes(String enc) throws java.io.UnsupportedEncodingException;
    
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin);
    
    public int indexOf(int ch);
    
    public int indexOf(int ch, int fromIndex);
    
    public int indexOf(String str);
    
    public int indexOf(String str, int fromIndex);
    
    public String intern();
    
    public int lastIndexOf(int ch);
    
    public int lastIndexOf(int ch, int fromIndex);
    
    public int lastIndexOf(String str);
    
    public int lastIndexOf(String str, int fromIndex);
    
    public int length();
    
    public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len);
    
    public boolean regionMatches(int toffset, String other, int ooffset, int len);
    
    public String replace(char oldChar, char newChar);
    
    public boolean startsWith(String prefix);
    
    public boolean startsWith(String prefix, int toffset);
    
    public String substring(int beginIndex);
    
    public String substring(int beginIndex, int endIndex);
    
    public char[] toCharArray();
    
    public String toLowerCase();
    
    public String toLowerCase(Locale locale);
    
    public String toUpperCase();
    
    public String toUpperCase(Locale locale);

    public boolean isInstance(Object object);

    public Name<N, T> cast(Object object);

    public Name<N, T> getName(Object object);

    public boolean isAssignableFrom(Object object);
}


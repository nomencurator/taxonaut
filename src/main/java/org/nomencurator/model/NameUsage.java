/*
 * NameUsage.java:  a Java interface of NameUsage class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 1999, 2002, 2003, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

// it is incompatible with Collections.synchronizedMap().
//import org.nomencurator.util.Map;

import org.nomencurator.model.vocabulary.TypeStatus;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of <tt>NameUsage</tt> in Nomencurator
 * data model.
 * It was referred to as NameRecord in the original publication.
 *
 * @param <T> the runtime type of the object designated by the name usage
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	28 June 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NameUsage <N extends NameUsage<?, ?>, T extends N>
				      //public interface NameUsage <T>
    extends NamedObject <N, T>, Cloneable
{


    /**
     * Returns true if <tt>nameUsage</tt> is a senior synonym of this
     * <tt>NameUsage</tt>, or null if unknown.
     *
     * @param nameUsage <tt>NameUsage</tt> to be examined
     *
     * @return true if  <tt>nameUsage</tt> is a senior synonym of this.
     */
    public Boolean isSynonymOf(final NameUsage<?, ?> nameUsage);


    /**
     * Returns a persistent ID representing this <tt>NameUsage</tt>
     * with specified  <tt>separator</tt>.  It contains class name header
     * if <tt>withClassName</tt> true.
     *
     * @param separator <tt>char</tt> to be used as the field separator
     * @param withClassName <tt>boolean</tt> specifying with or without
     * class name header
     *
     * @return String representing this <tt>NameUsage</tt>
     */
    public String getPersistentID(String separator, boolean withClassName);

    /**
     * Returns true if this object's contents are saturated
     *
     * @return true if this object's contents are saturated
     */
    public boolean isContentsResolved();

    public void setContentsResolved(boolean resolved);

    /**
     * Returns true if the lower taxa list is saturated
     *
     * @return true if the lower taxa list is saturated
     */
    public boolean isLowerNameUsagesResolved();

    // public void setLowerNameUsagesResolved(boolean resolved);

    /**
     * Returns true if the hierarchy is saturated
     *
     * @return true if the hierarchy is saturated
     */
    public boolean isHierarchyResolved();

    /**
     * Returns true if the lower taxa list is saturated in full depth
     *
     * @return true if the lower taxa list is saturated in full depth
     */
    public boolean isResolvedInFullDepth();

    /**
     * Returns <tt>Rank</tt>
     *
     * @return <tt>Rank</tt>
     */ 
    public Rank getRank();

    /**
     * Sets <tt>rank</tt> as rank
     *
     * @param Rank of the <tt>NameUsage</tt>
     */ 
    public void setRank(Rank rank);

    /**
     * Returns <tt>String</tt> representing rank name
     *
     * @return <tt>String</tt> representing rank name
     */ 
    public String getRankLiteral();

    /**
     * Sets <tt>rank</tt> as rank name
     *
     * @param rank <tt>String</tt> representing rank name
     */ 
    public void setRankLiteral(String rankLiteral);
    
    /**
     * Returns <tt>String</tt> representing the name used
     *
     * @return <tt>String</tt> representing the name used
     */ 
    public String getLiteral();
    
    /**
     * Sets <tt>name</tt> as the name used
     *
     * @param name <tt>String</tt> representing the name used
     */ 
    public void setLiteral(String name);
    
    /**
     * Returns <tt>Locale</tt> representing locale
     *
     * @return <tt>Locale</tt> representing locale
     */ 
    public Locale getLocale();
    //public String getLocale();
    
    /**
     * Sets <tt>locale</tt> as locale of this <tt>NameUsage</tt>
     *
     * @param locale <tt>Locale</tt> to be set
     */ 
    public void setLocale(Locale locale);
    //public void setLocale(String locale);

    
    /*
     * Returns <tt>Publication</tt> containing this <tt>NameUsage</tt>,
     * or null if unknown.
     *
     * @return <tt>Publication</tt> containint this <tt>NameUsage</tt>
     */
    public Publication getPublication();
    
    /*
     * Sets <tt>publication</tt> as <tt>Publication</tt> contaiing this <tt>NameUsage</tt>
     *
     * @param publication <tt>Publication</tt> containing this <tt>NameUsage</tt>
     */
    public void setPublication(Publication publication);
    
    /*
     * Returns <tt>Appearance</tt> encoding this <tt>NameUsage</tt>
     *
     * @return <tt>Appearance</tt> encoding this <tt>NameUsage</tt>
     */
    public Appearance getAppearance();
    
    /*
     * Sets <tt>appearance</tt> as <tt>Apperance</tt> encoding this <tt>NameUsage</tt>
     *
     * @param appearance <tt>Appearance</tt> encoding this <tt>NameUsage</tt>
     */
    public void setAppearance(Appearance appearance);
    
    /**
     * Returns higher name usage.
     * It may be this object itself if it is the root of the hierarchy,
     * or null if unknown.
     *
     * @return <tt>NameUsage</tt> representing the higher taxon
     */
    public NameUsage<N, T> getHigherNameUsage();

    public List<NameUsage<N, T>> getNameUsagePath();
    
    /**
     * Sets higherTaxon as the higher taxon of this NameUsage.
     *
     * @param higherNameUsage <tt>NameUsage</tt> representing higher taxon
     */
    public boolean setHigherNameUsage(NameUsage<N, T> higherNameUsage);
    
    /**
     * Sets <tt>higherTaxon</tt> as higher taxon where this
     * <tt>NameUsage</tt> as the <tt>index</tt>th 
     * lower taxon of the <tt>higherTaxon</tt>
     *
     * @param higherTaxon <tt>NameUsage</tt> representing higher taxon
     * @param index <tt>int</tt> representing postion of this
     * <tt>NameUsgae</tt>
     */
    public boolean setHigherNameUsage(NameUsage<N, T> higherNameUsage, int index);

    /**
     * Returns position of this <tt>NameUsage</tt> in its siblings,
     * or -1 if it does not have siblings
     *
     * @return <tt>int</tt> representing postion of this
     * <tt>NameUsage</tt> in its siblings,
     * or -1 if it does not have siblings
     */
    public int getIndex();

    /**
     * Sets <tt>index</tt> as position of this <tt>NameUsage</tt> in its siblings,
     * or -1 if it does not have siblings
     *
     * @param <tt>index</tt> representing postion of this
     * <tt>NameUsage</tt> in its siblings,
     * or -1 if it does not have siblings
     */
    public void setIndex(int index);

    /**
     * Returns true if the assignment to the higher taxon
     * should be considerd as incertaes sedis
     *
     * @return true if the assignment to the higher taxon
     * should be considerd as incertae sedis
     */
    public boolean isIncertaeSedis();

    /**
     * Sets incertaeSedis to indicate whether the assignment to the higher taxon should be considerd as incertae sedis
     *
     * @param incertaeCeis <tt>boolean</tt> true if the assignment to the higher taxon
     * should be considerd as incertae sedis
     */
    public void setIncertaeSedis(boolean incertaeSedis);

    /**
     * Returns number of lower taxa
     *
     * @return number of lower taxa
     */
    public int getLowerNameUsagesCount();

    /**
     * Sets maximum number of lower taxa and allocate
     * slots to store them, enables automatic
     * management by giving -1 as <tt>count</tt>,
     * or prohibits to have lower taxa by giving 0
     *
     * @param capacity maximum number of lower taxa, 
     * -1 to enable automatic management, 0 to 
     * prohibit to have lower taxa
     */
    //public void setLowerNameUsagesCapacity(int capacity);

    /**
     * Returns maximum number of lower taxa,
     * -1 if expandable automatically,
     * or 0 if undefined
     *
     * @return maximum number of lower taxa
     * -1 if expandable automatically,
     * or 0 if undefined
     */
    //public int getLowerNameUsagesCapacity();

    /**
     * Makes the object to use an array instead of List as container of
     * lower taxa
     *
     * @see setLowerNameUsagesCapacity(int);
     */
    public void toArray();

    /**
     * Tests if <tt>nameUsage</tt> is a direct
     * lower taxon of this object
     *
     * @param nameUsage <tt>NameUsage</tt> to be tested
     * @return true if this object contains <tt>nameUsage</tt>
     * as a lower taxon just below
     */
    public boolean contains(NameUsage<? extends N, ? extends N> nameUsage);

    /**
     * Tests if <tt>nameUsage</tt> is a 
     * lower taxon of this object, recursively if
     * <tt>recursive</tt> is true
     *
     * @param nameUsage <tt>NameUsage</tt> to be tested
     * @param recursive true to recursive search
     * @return true if this object contains <tt>nameUsage</tt>
     * as a lower taxon
     */
    public boolean contains(NameUsage<? extends N, ? extends N> nameUsage, boolean recursive);

    /**
     * Returns <tt>Enumeration</tt> of lower taxa
     * or null if none
     *
     * @return an array of lower taxa
     * or null if none
     */
    public List<NameUsage<N, T>> getLowerNameUsages();

    /**
     * Sets <tt>lowerNameUsages</tt> as the list of lower taxa
     *
     * @param lowerNameUsages <tt>List</tt> representing the list of lower taxa
     */
    public void setLowerNameUsages(List<? extends NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> lowerNameUsages);

    /**
     * Sets <tt>lowerNameUsages</tt> as the list of lower taxa
     *
     * @param lowerNameUsages an array of <tt>NameUsage</tt>s
     * representing the list of lower taxa
     */
    //public void setLowerNameUsages(NameUsage<? extends N, ? extends N>[] lowerNameUsagesArray);

    /**
     * Adds <tt>nameUsage</tt> to the list of lower taxa
     * It returns true if the <tt>nameUsage</tt> added to the
     * list successfuly, or false if the <tt>nameUsage</tt> is
     * already in the list.
     *
     * @param nameUsage <tt>NameUsage</tt> to be added to the list of lower taxa
     *
     * @return true if <tt>nameUsage</tt> was appended to the list of lower taxa
     * successfully, or false if <tt>nameUsage</tt> is already in the list
     */
    public boolean addLowerNameUsage(NameUsage<N, T> nameUsage);
    //public boolean addLowerNameUsage(NameUsage<? extends N, ? extends N> nameUsage);
    //public boolean addLowerNameUsage(NameUsage<N, ?> nameUsage);

    /**
     * Removes <tt>nameUsage</tt> from the list of lower taxa
     *
     * @param nameUsage <tt>NameUsage</tt> to be removed fromthe list of lower taxa
     * @return true if <tt>nameUsage</tt> is a lower taxon of the object,
     * or false if else
     */
    //public boolean removeLowerNameUsage(NameUsage<? extends N, ? extends N> nameUsage);
    public boolean removeLowerNameUsage(NameUsage<N, T> nameUsage);

    /**
     * Removes all lower taxa
     */
    public void removeLowerNameUsages();

    /**
     * Returns <tt>NameUsage</tt> representing type taxon of this
     * <tt>NameUsage</tt>, or null if no type is designated
     *
     * @return NamedObject representing type taxon of this
     * <tt>NameUsage</tt>, or null if no type is designated
     */
    public NameUsage<? extends N, ? extends N> getTypeUsage();

    /**
     * Returns type of  type taxon of this NameUsage, or null if no type is designated
     *
     * @return String representing type of type taxon of this NameUsage,
     * or null if no type is designated
     */
    public String getTypeOfType();

    /**
     * Sets type of type taxon of this NameUsage.
     *
     * @param typeOfType String representing type of type taxon of this
     * NameUsage
     */
    public void setTypeOfType(String typeOfType);

    /**
     * Returns true if this <tt>NamedObject</tt> is a type taxon
     *
     * @return true if this <tt>NamedObject</tt> is a type
     */
    public boolean isType();
    
    /**
     * Sets type as type of taxon
     *
     * @param type boolean true if this taxon is a nominal type
     */
    public void setType(boolean type);

    /**
     * Sets type as type of taxon
     *
     * @param type String representing type of its type taxon
     */
    public void setType(String typeOfType);

    /**
     * Sets type as type taxon of this NameUsage.
     *
     * @param type <tt>NameUsage</tt> to be designated as the type of
     * this <tt>NameUsage</tt>
     */
    public void setTypeUsage(NameUsage<? extends N, ? extends N> type);

    /**
     * Sets <tt>type</tt> as <tt>typeOfType</tt> of this <tt>NameUsage</tt>
     * The  <tt>type</tt> may be null.  If <tt>type</tt> is null,
     * <tt>typeOfType</tt> is ignored.
     *
     * @param type <tt>NameUsage</tt> to be designated as the type of
     * this <tt>NameUsage</tt>
     * @param typeOfType <tt>String</tt> represents type of type, e.g.
     * holotype.
     * 
     */
    public void setTypeUsage(NameUsage<? extends N, ? extends N> type, String typeOfType);

    /**
     * Adds <tt>designator</tt> as one of <tt>NameUsage</tt>s
     * designating this <tt>NameUsage</tt> as its type.
     *
     * @param designator <tt>NameUsage</tt> designating this <tt>NameUsage</tt>
     * as its type
     */
    // public void addTypeDesignator(NameUsage<? extends N, ? extends N> designator, TypeStatus typeStatus);

    /**
     * Removes <tt>designator</tt> from the list of <tt>NameUsage</tt>s
     * designating this <tt>NameUsage</tt> as its type.
     *
     * @param designator <tt>NameUsage</tt> to be remvoed from designator list
     * of this <tt>NameUsage</tt>
     */
    // public void removeTypeDesignator(NameUsage<? extends N, ? extends N> designator);

    /**
     * Returns <tt>Collection</tt> of <tt>Annotatoin</tt>s made by this <tt>NameUsage</tt>, or null if none
     *
     * @return Enumeration of <tt>Annotation</tt>s or null if none
     */
    public Collection<Annotation> getAnnotations();

    /**
     * Returns <tt>Collection</tt> of <tt>Annotations</tt> having specified <tt>linkType</tt> made by this <tt>NameUsage</tt>, or null
     * if there is no <tt>Annotations</tt> having <tt>linkType</tt>.
     *
     * @param linkType link type of <tt>Annotation</tt> to be returned
     *
     * @return Collection of <tt>Annotation</tt>s having <tt>linkType</tt>
     */
    public Collection<Annotation> getAnnotations(String linkType);

    /**
     * Sets <Tt>annotations</tt> as the list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     *
     * @param annotations <tt>Vector</tt> representing the list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     */
    public void setAnnotations(Collection<? extends Annotation> annotations);

    /**
     * Adds <tt>Annotation</tt> to the list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     * It returns true if the <tt>annotation</tt> added to the
     * list successfuly, or false if the <tt>annotation</tt> is
     * already in the list.
     *
     * @param annotation <tt>Annotation</tt> to be added to the list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     *
     * @return true if <tt>annotation</tt> was appended to the list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     * successfully, or false if <tt>annotation</tt> is already in the list
     */
    public boolean addAnnotation(Annotation annotation);

    /**
     * Removes <tt>annotation</tt> from the list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     *
     * @param annotation <tt>NameUsage</tt> to be removed fromthe list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     */
    public boolean removeAnnotation(Annotation annotation);


    /**
     * Removes all <tt>Annotation</tt> from the list of <tt>Annotation</tt>s made by this <tt>NameUsage</tt>
     */
    public void removeAnnotations();

    /**
     * Returns <tt>NameUsage</tt> representing in which sensu the name
     * is used by this <tt>NameUsage</tt>.
     * It may designate the autoritative usage of the name.
     *
     * @return <tt>NameUsage</tt> representing <tt>NameUsage</tt> in which sens
     * it was used.  It may desiganate the authoritative usage of the name as <tt>NameUsage</tt>.
     */
    public NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> getSensu();
    //public NameUsage<? extends N, ? extends N> getSensu();

    /**
     * Sets <tt>sensu</tt> in which the <tt>NameUsage</tt> is used.
     * It may designate the autoritative usage of the name.
     *
     * @param sensu <tt>NameUsage</tt> representing the sens of this <tt>NameUsage</tt>
     */
    public void setSensu(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> sensu);
    //public void setSensu(NameUsage<? extends N, ? extends N> sensu);

    /**
     * Returns <tt>Enumeration</tt> of author of the name,
     * or null if none.
     * It shall be null if authors of the name match to authors of autority
     * <tt>NameUsage</tt>.
     *
     * @return Enumertion of author of the name
     * or null if none.
     */
    public Collection<Author> getAuthors();

    /**
     * Sets <tt>authors</tt> as the list of <tt>Author</tt>s of the name.
     * Is shall not be called if authors  of thename matche to authors of autority
     * <tt>NameUsage</tt>.
     *
     * @param authors <tt>Collection</tt> representing the list of <tt>Author</tt>s of the name
     */
    public void setAuthors(Collection<? extends Author> authors);

    /**
     * Adds <tt>Author</tt> to the list of <tt>Author</tt>s of the name.
     * It returns true if the <tt>author</tt> added to the
     * list successfuly, or false if the <tt>author</tt> is
     * already in the list.
     * <P>
     * Is shall not be called if authors  of thename matche to authors of autority
     * <tt>NameUsage</tt>.
     *
     * @param author <tt>Author</tt> to be added to the list of <tt>Author</tt>s of the name
     *
     * @return true if <tt>author</tt> was appended to the list
     * successfully, or false if <tt>author</tt> is already in the list
     */
    public boolean addAuthor(Author author);

    /**
     * Removes <tt>author</tt> from the list of <tt>Author</tt>s of the name
     *
     * @param author <tt>NameUsage</tt> to be removed fromthe list of <tt>Author</tt>s of the name
     */
    public boolean removeAuthor(Author author);

    /**
     * Returns authority or null if unspecified.
     *
     * @return String representing authority
     */
    public String getAuthority();

    /**
     * Sets <tt>authority</tt>.
     *
     * @param authority of the <tt>NameUsage</tt>
     */
    public void setAuthority(String authority);

    /**
     * Returns authority year in <tt>int</tt>.
     * It must be zero if authors of authority <tt>NameUsage</tt>
     * are nominal authority of the name.
     *
     * @return int representing authority year.
     * i.e. first usage record of the name
     */
    public Integer getAuthorityYear();

    /**
     * Sets year as authority year.
     * It shall be used only if authority <tt>NameUsage</tt>
     * gives aurthority author names difference from authors
     * of the authority <tt>NameUsage</tt>, or if
     * the authority <tt>NameUsage</tt> is unknown but
     * author names and year are known.
     *
     * @param year <tt>int</tt> representing authority year.
     */
    public void setAuthorityYear(Integer year);

    /**
     * Returns name of taxonomic view where this <tt>NameUsage</tt>
     * belongs to.
     *
     * @return String representing name of the taxonomic view
     */
    public String getViewName();

    /**
     * Returns name of taxonomic view where this <tt>NameUsage</tt>
     * belongs to.  The parameter <tt>toSort</tt> determines
     * words order in the name.  If <tt>toSort</tt> is true,
     * the first word represents the year of name publciation.
     *
     * @param toSort boolean to determine words order
     *
     * @return String representing name of the taxonomic view
     */
    public String getViewName(boolean toSort);

    /**
     * Returns yaer of publication as <tt>String</tt>
     *
     * @return String representing the year of name publication
     */
    public String getYear();

    /**
     * Returns root <tt>NameUsage</tt> of the name hierarchy
     * where this <tt>NameUsage</tt> belongs to.
     *
     * @return root <tt>NameUsage</tt> of the name hierarhcy.
     */
    public NameUsage<N, T> getRoot();

    /**
     * Returns true if lower taxa are assigned to this
     *
     * @return whether lower taxa are assigned
     */
    public boolean hasLowerNameUsages();

    /**
     * Returns <tt>NameUsage</tt> proxied by this <tt>NameUsage</tt>
     *
     * @return NameUsage proxied by this <tt>NameUsage</tt>
     */
    public NameUsage<N, T> getNameUsage();

    /**
     * Returns leaf taxa, i.e. taxa without children, under this <tt>NameUsage</tt>
     * as <tt>Map</tt> keyed by their persistent ID.
     *
     * @return <tt>Map</tt> containing <tt>Set</tt>s of leaf taxa under this <tt>NameUsage</tt>
     * keyed by their name literal, or null if none keyed by their persisitent ID
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLeafNameUsages();

    public Map<String, NameUsage<? extends N, ? extends N>> putLeafNameUsagesTo(Map<String, NameUsage<? extends N, ? extends N>> leafNameUsagesMap);

    /**
     * Returns <tt>Map</tt> containing lower taxa at <tt>rank</tt> under this <tt>NameUsage</tt>
     * keyed by their persistent ID.
     *
     * @param rank <tt>Rank</tt> of <tt>NameUsage</tt> to be included in return set of
     *
     * @return <tt>Map</tt> containing lower taxa at <tt>rank</tt> under this
     * <tt>NameUsage</tt> or null if none.
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLowerNameUsagesAt(Rank rank);

    public void putLowerNameUsagesAt(Rank rank, Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsagesMap);

    /**
     * Returns <tt>Map</tt> containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt> or null if none
     *
     * @param rank lowest <tt>Rank</tt> of <tt>NameUsage</tt>
     * to be included in return set
     *
     * @return <tt>Map<String, NameUsage<N, T>></tt> containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt> or null if none
     * keyed by its persistent ID.
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLeafNameUsagesUntil(String rank);

    /**
     * Returns <tt>Map</tt> containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt> or null if none
     *
     * @param rank lowest <tt>Rank</tt> of <tt>NameUsage</tt>
     * to be included in return set
     *
     * @return <tt>Map</tt> containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt> or null if none
     * keyed by its persistent ID
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getLeafNameUsagesUntil(Rank rank);

    public void putLeafNameUsagesUntil(Rank rank, Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsages);

    /**
     * Returns all lower taxa under this NameUsage.
     *
     * @return <tt>Map</tt> containing all
     * lower taxa under this <tt>NameUsage</tt>
     * or null if none, keyed by its persistent ID
     */
    public Map<String, NameUsage<? extends N, ? extends N>> getAllLowerNameUsages();

    public void putAllLowerNameUsagesTo(Map<String, NameUsage<? extends N, ? extends N>> lowerNameUsages);

    /**
     * Returns <tt>Map</tt> of  <tt>Set</tt>s, containing leaf taxa, i.e. taxa without children,
     * under this <tt>NameUsage</tt>, indexed by their ascribed names.   It is <tt>Set</tt> because single name literal
     * can be used different name usages under multilingual or multiple code hierarchies.
     *
     * <p> Note that a single literal may have multiple <tt>NameUsage</tt>s even under a
     * name hierarchy because of it can be multilingual.
     *
     * @return <tt>Map</tt> of <tt>Set</tt>s
     * containing leaf taxa, under this <tt>NameUsage</tt>,
     * keyed by their ascribed names, or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLeafNames();

    /**
     * Returns <tt>Hashtable</tt> of 
     * <tt>Hashtable</tt>s containing
     * lower taxa at <tt>rank</tt> under this
     * <tt>NameUsage</tt>, indexed by
     * their ascribed names, or null if none
     *
     * @param rank <tt>Rank</tt> of <tt>NameUsage</tt>
     * to be included in return set of
     *
     * @return <tt>Hashtable</tt> of <tt>Hashtable</tt>s containing
     * lower taxa at <tt>rank</tt> under this
     * <tt>NameUsage</tt>, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLowerNamesAt(Rank rank);

    /**
     * Returns <tt>Hashtable</tt> of 
     * <tt>Hashtable</tt>s containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt>, indexed by
     * their ascribed names, or null if none
     *
     * @param rank lowest <tt>Rank</tt> of <tt>NameUsage</tt>
     * to be included in return set
     *
     * @return <tt>Hashtable</tt> of <tt>Hashtable</tt>s containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt>, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLeafNamesUntil(String rank);

    /**
     * Returns <tt>Hashtable</tt> of <tt>Hashtable</tt>s containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt> or null if none
     *
     * @param rank lowest <tt>Rank</tt> of <tt>NameUsage</tt>
     * to be included in return set
     *
     * @return <tt>Hashtable</tt> of <tt>Hashtable</tt>s containing
     * leaf taxa at, or higher than <tt>rank</tt>
     * under this <tt>NameUsage</tt>, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getLeafNamesUntil(Rank rank);

    /**
     * Returns <tt>Hashtable</tt> of <tt>Hashtable</tt>s containing all
     * lower taxa under this <tt>NameUsage</tt>
     * or null if none
     *
     * @return <tt>Hashtable</tt> of <tt>Hashtable</tt>s containing all
     * lower taxa under this <tt>NameUsage</tt>, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<? extends N, ? extends N>>> getAllLowerNames();

    public Map<String, NameUsage<? extends N, ? extends N>> getSiblings();

    public Map<String, NameUsage<? extends N, ? extends N>> getLowerNameUsagesSet();

    public Map<String, NameUsage<? extends N, ? extends N>> getIncludants();

    public void clearIncludants();

    public Map<String, NameUsage<? extends N, ? extends N>> getExcludants();

    public void clearExcludants();

    public String getRankedName();

    public String getRankedName(boolean abbreviate);

    /**
     * Returns base name literal of the name literal by removing name ending
     * specific to the rank, zero length <tt>String</tt> if base name
     * is unapplicable to the name, or null if not implemented.
     *
     * @return <tt>String</tt> representing base name literal of the name
     * literal, or zero length  <tt>String</tt> if base name
     * is unapplicable to the name, or null if not implemented.
     */
    //public String getBaseName();

    public NameUsage<N, T> clone();

    public NameUsage<N, T> create();

    public NameUsage<N, T> getNameUsage(Object object);

    public Integer getDescendantCount();
}

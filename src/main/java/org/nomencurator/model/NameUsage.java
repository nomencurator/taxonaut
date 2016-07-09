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

import org.nomencurator.model.vocabulary.TypeStatus;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of {@code NameUsage} in Nomencurator
 * data model.
 * It was referred to as NameRecord in the original publication.
 *
 * @param <T> the runtime type of the object designated by the name usage
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	06 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NameUsage <T extends NameUsage<?>>
    extends NamedObject <T>, Cloneable
{
    /**
     * Returns true if {@code nameUsage} is a senior synonym of this
     * {@code NameUsage}, or null if unknown.
     *
     * @param nameUsage {@code NameUsage} to be examined
     *
     * @return true if  {@code nameUsage} is a senior synonym of this.
     */
    public Boolean isSynonymOf(final NameUsage<?> nameUsage);


    /**
     * Returns a persistent ID representing this {@code NameUsage}
     * with specified  {@code separator}.  It contains class name header
     * if {@code withClassName} true.
     *
     * @param separator {@code char} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code NameUsage}
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
     * Returns {@code Rank}
     *
     * @return {@code Rank}
     */ 
    public Rank getRank();

    /**
     * Sets {@code rank} as rank
     *
     * @param Rank of the {@code NameUsage}
     */ 
    public void setRank(Rank rank);

    /**
     * Returns {@code String} representing rank name
     *
     * @return {@code String} representing rank name
     */ 
    public String getRankLiteral();

    /**
     * Sets {@code rank} as rank name
     *
     * @param rank {@code String} representing rank name
     */ 
    public void setRankLiteral(String rankLiteral);
    
    /**
     * Returns {@code String} representing the name used
     *
     * @return {@code String} representing the name used
     */ 
    public String getLiteral();
    
    /**
     * Sets {@code name} as the name used
     *
     * @param name {@code String} representing the name used
     */ 
    public void setLiteral(String name);
    
    /**
     * Returns {@code Locale} representing locale
     *
     * @return {@code Locale} representing locale
     */ 
    public Locale getLocale();
    
    /**
     * Sets {@code locale} as locale of this {@code NameUsage}
     *
     * @param locale {@code Locale} to be set
     */ 
    public void setLocale(Locale locale);

    
    /**
     * Returns {@code Publication} containing this {@code NameUsage},
     * or null if unknown.
     *
     * @return {@code Publication} containint this {@code NameUsage}
     */
    public Publication getPublication();
    
    /**
     * Sets {@code publication} as {@code Publication} contaiing this {@code NameUsage}
     *
     * @param publication {@code Publication} containing this {@code NameUsage}
     */
    public void setPublication(Publication publication);
    
    /**
     * Returns {@code Appearance} encoding this {@code NameUsage}
     *
     * @return {@code Appearance} encoding this {@code NameUsage}
     */
    public Appearance getAppearance();
    
    /**
     * Sets {@code appearance} as {@code Apperance} encoding this {@code NameUsage}
     *
     * @param appearance {@code Appearance} encoding this {@code NameUsage}
     */
    public void setAppearance(Appearance appearance);
    
    /**
     * Returns higher name usage.
     * It may be this object itself if it is the root of the hierarchy,
     * or null if unknown.
     *
     * @return {@code NameUsage} representing the higher taxon
     */
    public NameUsage<T> getHigherNameUsage();
    //public <N extends NameUsage<?>> N getHigherNameUsage();

    /**
     * Returns {@code List} of higher name usages of the {@code NameUsage}.
     *
     * @return {@code List} of {@code NameUsage} representing higher taxa.
     */
    public List<NameUsage<T>> getNameUsagePath();
    //public <N extends NameUsage<?>> List<? extends N> getNameUsagePath();
    
    /**
     * Sets {@code higherNameUsage} as the higher taxon of this NameUsage.
     *
     * @param higherNameUsage {@code NameUsage} representing higher taxon
     */
    public boolean setHigherNameUsage(NameUsage<T> higherNameUsage);
    
    /**
     * Sets {@code higherTaxon} as higher taxon where this
     * {@code NameUsage} as the {@code index}th 
     * lower taxon of the {@code higherTaxon}
     *
     * @param higherTaxon {@code NameUsage} representing higher taxon
     * @param index {@code int} representing postion of this
     * {@code NameUsgae}
     */
    public boolean setHigherNameUsage(NameUsage<T> higherNameUsage, int index);

    /**
     * Returns position of this {@code NameUsage} in its siblings,
     * or -1 if it does not have siblings
     *
     * @return {@code int} representing postion of this
     * {@code NameUsage} in its siblings,
     * or -1 if it does not have siblings
     */
    public int getIndex();

    /**
     * Sets {@code index} as position of this {@code NameUsage} in its siblings,
     * or -1 if it does not have siblings
     *
     * @param {@code index} representing postion of this
     * {@code NameUsage} in its siblings,
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
     * @param incertaeCeis {@code boolean} true if the assignment to the higher taxon
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
     * management by giving -1 as {@code count},
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
     * Tests if {@code nameUsage} is a direct
     * lower taxon of this object
     *
     * @param nameUsage {@code NameUsage} to be tested
     * @return true if this object contains {@code nameUsage}
     * as a lower taxon just below
     */
    public boolean contains(NameUsage<?> nameUsage);

    /**
     * Tests if {@code nameUsage} is a 
     * lower taxon of this object, recursively if
     * {@code recursive} is true
     *
     * @param nameUsage {@code NameUsage} to be tested
     * @param recursive true to recursive search
     * @return true if this object contains {@code nameUsage}
     * as a lower taxon
     */
    public boolean contains(NameUsage<?> nameUsage, boolean recursive);

    /**
     * Returns {@code Enumeration} of lower taxa
     * or null if none
     *
     * @return an array of lower taxa
     * or null if none
     */
    public List<NameUsage<T>> getLowerNameUsages();

    /**
     * Sets {@code lowerNameUsages} as the list of lower taxa
     *
     * @param lowerNameUsages {@code List} representing the list of lower taxa
     */
    public void setLowerNameUsages(List<? extends NameUsage<? extends T>> lowerNameUsages);

    /**
     * Sets {@code lowerNameUsages} as the list of lower taxa
     *
     * @param lowerNameUsages an array of {@code NameUsage}s
     * representing the list of lower taxa
     */
    //public void setLowerNameUsages(NameUsage<? extends T>[] lowerNameUsagesArray);

    /**
     * Adds {@code nameUsage} to the list of lower taxa
     * It returns true if the {@code nameUsage} added to the
     * list successfuly, or false if the {@code nameUsage} is
     * already in the list.
     *
     * @param nameUsage {@code NameUsage} to be added to the list of lower taxa
     *
     * @return true if {@code nameUsage} was appended to the list of lower taxa
     * successfully, or false if {@code nameUsage} is already in the list
     */
    public boolean addLowerNameUsage(NameUsage<T> nameUsage);

    /**
     * Removes {@code nameUsage} from the list of lower taxa
     *
     * @param nameUsage {@code NameUsage} to be removed fromthe list of lower taxa
     * @return true if {@code nameUsage} is a lower taxon of the object,
     * or false if else
     */
    public boolean removeLowerNameUsage(NameUsage<T> nameUsage);

    /**
     * Removes all lower taxa
     */
    public void removeLowerNameUsages();

    /**
     * Returns {@code NameUsage} representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     *
     * @return NamedObject representing type taxon of this
     * {@code NameUsage}, or null if no type is designated
     */
    public <N extends NameUsage<?>> N getTypeUsage();

    /**
     * Returns type of  type taxon of this NameUsage, or null if no type is designated
     *
     * @return TypeStatus representing type of type taxon of this NameUsage,
     * or null if no type is designated
     */
    public TypeStatus getTypeStatus();

    /**
     * Sets type of type taxon of this NameUsage.
     *
     * @param typeStatus {@code String} representing type of type taxon of this
     * NameUsage
     */
    public void setTypeStatus(String typeStatus);

    /**
     * Sets type of type taxon of this NameUsage.
     *
     * @param typeStatus {@code TypeStatus} representing type of type taxon of this
     * NameUsage
     */
    public void setTypeStatus(TypeStatus typeStatus);

    /**
     * Returns true if this {@code NamedObject} is a type taxon
     *
     * @return true if this {@code NamedObject} is a type
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
    public void setType(String typeStatus);

    /**
     * Sets type as type of taxon
     *
     * @param type {@code TypeStatus} representing type of its type taxon
     */
    public void setType(TypeStatus typeStatus);

    /**
     * Sets type as type taxon of this NameUsage.
     *
     * @param type {@code NameUsage} to be designated as the type of
     * this {@code NameUsage}
     */
    public void setTypeUsage(NameUsage<?> type);

    /**
     * Sets {@code type} as {@code typeStatus} of this {@code NameUsage}
     * The  {@code type} may be null.  If {@code type} is null,
     * {@code typeStatus} is ignored.
     *
     * @param type {@code NameUsage} to be designated as the type of
     * this {@code NameUsage}
     * @param typeStatus {@code String} represents type of type, e.g.
     * holotype.
     * 
     */
    public void setTypeUsage(NameUsage<?> type, String typeStatus);

    /**
     * Sets {@code type} as {@code typeStatus} of this {@code NameUsage}
     * The  {@code type} may be null.  If {@code type} is null,
     * {@code typeStatus} is ignored.
     *
     * @param type {@code NameUsage} to be designated as the type of
     * this {@code NameUsage}
     * @param typeStatus {@code TypeStatus} represents type of type, e.g.
     * holotype.
     */
    public void setTypeUsage(NameUsage<?> type, TypeStatus typeStatus);

    /**
     * Adds {@code designator} as one of {@code NameUsage}s
     * designating this {@code NameUsage} as its type.
     *
     * @param designator {@code NameUsage} designating this {@code NameUsage}
     * as its type
     */
    // public void addTypeDesignator(NameUsage<? extends N, ? extends T> designator, TypeStatus typeStatus);

    /**
     * Removes {@code designator} from the list of {@code NameUsage}s
     * designating this {@code NameUsage} as its type.
     *
     * @param designator {@code NameUsage} to be remvoed from designator list
     * of this {@code NameUsage}
     */
    // public void removeTypeDesignator(NameUsage<? extends N, ? extends T> designator);

    /**
     * Returns {@code Collection} of {@code Annotatoin}s made by this {@code NameUsage}, or null if none
     *
     * @return Enumeration of {@code Annotation}s or null if none
     */
    public Collection<Annotation> getAnnotations();

    /**
     * Returns {@code Collection} of {@code Annotations} having specified {@code linkType} made by this {@code NameUsage}, or null
     * if there is no {@code Annotations} having {@code linkType}.
     *
     * @param linkType link type of {@code Annotation} to be returned
     *
     * @return Collection of {@code Annotation}s having {@code linkType}
     */
    public Collection<Annotation> getAnnotations(String linkType);

    /**
     * Sets {@code annotations} as the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @param annotations {@code Vector} representing the list of {@code Annotation}s made by this {@code NameUsage}
     */
    public void setAnnotations(Collection<? extends Annotation> annotations);

    /**
     * Adds {@code Annotation} to the list of {@code Annotation}s made by this {@code NameUsage}
     * It returns true if the {@code annotation} added to the
     * list successfuly, or false if the {@code annotation} is
     * already in the list.
     *
     * @param annotation {@code Annotation} to be added to the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @return true if {@code annotation} was appended to the list of {@code Annotation}s made by this {@code NameUsage}
     * successfully, or false if {@code annotation} is already in the list
     */
    public boolean addAnnotation(Annotation annotation);

    /**
     * Removes {@code annotation} from the list of {@code Annotation}s made by this {@code NameUsage}
     *
     * @param annotation {@code NameUsage} to be removed fromthe list of {@code Annotation}s made by this {@code NameUsage}
     */
    public boolean removeAnnotation(Annotation annotation);


    /**
     * Removes all {@code Annotation} from the list of {@code Annotation}s made by this {@code NameUsage}
     */
    public void removeAnnotations();

    /**
     * Returns {@code NameUsage} representing in which sensu the name
     * is used by this {@code NameUsage}.
     * It may designate the autoritative usage of the name.
     *
     * @return {@code NameUsage} representing {@code NameUsage} in which sens
     * it was used.  It may desiganate the authoritative usage of the name as {@code NameUsage}.
     */
    public NameUsage<?> getSensu();

    /**
     * Sets {@code sensu} in which the {@code NameUsage} is used.
     * It may designate the autoritative usage of the name.
     *
     * @param sensu {@code NameUsage} representing the sens of this {@code NameUsage}
     */
    public void setSensu(NameUsage<?> sensu);

    /**
     * Returns {@code Enumeration} of author of the name,
     * or null if none.
     * It shall be null if authors of the name match to authors of autority
     * {@code NameUsage}.
     *
     * @return Enumertion of author of the name
     * or null if none.
     */
    public Collection<Author> getAuthors();

    /**
     * Sets {@code authors} as the list of {@code Author}s of the name.
     * Is shall not be called if authors  of thename matche to authors of autority
     * {@code NameUsage}.
     *
     * @param authors {@code Collection} representing the list of {@code Author}s of the name
     */
    public void setAuthors(Collection<? extends Author> authors);

    /**
     * Adds {@code Author} to the list of {@code Author}s of the name.
     * It returns true if the {@code author} added to the
     * list successfuly, or false if the {@code author} is
     * already in the list.
     * <P>
     * Is shall not be called if authors  of thename matche to authors of autority
     * {@code NameUsage}.
     *
     * @param author {@code Author} to be added to the list of {@code Author}s of the name
     *
     * @return true if {@code author} was appended to the list
     * successfully, or false if {@code author} is already in the list
     */
    public boolean addAuthor(Author author);

    /**
     * Removes {@code author} from the list of {@code Author}s of the name
     *
     * @param author {@code NameUsage} to be removed fromthe list of {@code Author}s of the name
     */
    public boolean removeAuthor(Author author);

    /**
     * Returns authority or null if unspecified.
     *
     * @return String representing authority
     */
    public String getAuthority();

    /**
     * Sets {@code authority}.
     *
     * @param authority of the {@code NameUsage}
     */
    public void setAuthority(String authority);

    /**
     * Returns authority year in {@code int}.
     * It must be zero if authors of authority {@code NameUsage}
     * are nominal authority of the name.
     *
     * @return int representing authority year.
     * i.e. first usage record of the name
     */
    public Integer getAuthorityYear();

    /**
     * Sets year as authority year.
     * It shall be used only if authority {@code NameUsage}
     * gives aurthority author names difference from authors
     * of the authority {@code NameUsage}, or if
     * the authority {@code NameUsage} is unknown but
     * author names and year are known.
     *
     * @param year {@code int} representing authority year.
     */
    public void setAuthorityYear(Integer year);

    /**
     * Returns name of taxonomic view where this {@code NameUsage}
     * belongs to.
     *
     * @return String representing name of the taxonomic view
     */
    public String getViewName();

    /**
     * Returns name of taxonomic view where this {@code NameUsage}
     * belongs to.  The parameter {@code toSort} determines
     * words order in the name.  If {@code toSort} is true,
     * the first word represents the year of name publciation.
     *
     * @param toSort boolean to determine words order
     *
     * @return String representing name of the taxonomic view
     */
    public String getViewName(boolean toSort);

    /**
     * Returns yaer of publication as {@code String}
     *
     * @return String representing the year of name publication
     */
    public String getYear();

    /**
     * Returns root {@code NameUsage} of the name hierarchy
     * where this {@code NameUsage} belongs to.
     *
     * @return root {@code NameUsage} of the name hierarhcy.
     */
    public NameUsage<T> getRoot();

    /**
     * Returns true if lower taxa are assigned to this
     *
     * @return whether lower taxa are assigned
     */
    public boolean hasLowerNameUsages();

    /**
     * Returns {@code NameUsage} proxied by this {@code NameUsage}
     *
     * @return NameUsage proxied by this {@code NameUsage}
     */
    public NameUsage<T> getNameUsage();

    /**
     * Returns leaf taxa, i.e. taxa without children, under this {@code NameUsage}
     * as {@code Map} keyed by their persistent ID.
     *
     * @return {@code Map} containing {@code Set}s of leaf taxa under this {@code NameUsage}
     * keyed by their name literal, or null if none keyed by their persisitent ID
     */
    public Map<String, NameUsage<T>> getLeafNameUsages();

    public  Map<String, NameUsage<T>> putLeafNameUsagesTo(Map<String, NameUsage<T>> leafNameUsagesMap);

    /**
     * Returns {@code Map} containing lower taxa at {@code rank} under this {@code NameUsage}
     * keyed by their persistent ID.
     *
     * @param rank {@code Rank} of {@code NameUsage} to be included in return set of
     *
     * @return {@code Map} containing lower taxa at {@code rank} under this
     * {@code NameUsage} or null if none.
     */
    public Map<String, NameUsage<T>> getLowerNameUsagesAt(Rank rank);

    public void putLowerNameUsagesAt(Rank rank, Map<String, NameUsage<T>> lowerNameUsageMap);

    /**
     * Returns {@code Map} containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Map<String, NameUsage<T>}
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     * keyed by its persistent ID.
     */
    public Map<String, NameUsage<T>> getLeafNameUsagesUntil(String rank);

    /**
     * Returns {@code Map} containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Map} containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     * keyed by its persistent ID
     */
    public Map<String, NameUsage<T>> getLeafNameUsagesUntil(Rank rank);

    public void putLeafNameUsagesUntil(Rank rank, Map<String, NameUsage<T>> leaves);

    /**
     * Returns all lower taxa under this NameUsage.
     *
     * @return {@code Map} containing all
     * lower taxa under this {@code NameUsage}
     * or null if none, keyed by its persistent ID
     */
    public Map<String, NameUsage<T>> getAllLowerNameUsages();

    public void putAllLowerNameUsagesTo(Map<String, NameUsage<T>> lowerNameUsageMap);

    /**
     * Returns {@code Map} of  {@code Set}s, containing leaf taxa, i.e. taxa without children,
     * under this {@code NameUsage}, indexed by their ascribed names.   It is {@code Set} because single name literal
     * can be used different name usages under multilingual or multiple code hierarchies.
     *
     * <p> Note that a single literal may have multiple {@code NameUsage}s even under a
     * name hierarchy because of it can be multilingual.
     *
     * @return {@code Map} of {@code Set}s
     * containing leaf taxa, under this {@code NameUsage},
     * keyed by their ascribed names, or null if none
     */
    public Map<String, Set<NameUsage<T>>> getLeafNames();

    /**
     * Returns {@code Hashtable} of 
     * {@code Hashtable}s containing
     * lower taxa at {@code rank} under this
     * {@code NameUsage}, indexed by
     * their ascribed names, or null if none
     *
     * @param rank {@code Rank} of {@code NameUsage}
     * to be included in return set of
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing
     * lower taxa at {@code rank} under this
     * {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<T>>> getLowerNamesAt(Rank rank);

    /**
     * Returns {@code Hashtable} of 
     * {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage}, indexed by
     * their ascribed names, or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<T>>> getLeafNamesUntil(String rank);

    /**
     * Returns {@code Hashtable} of {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage} or null if none
     *
     * @param rank lowest {@code Rank} of {@code NameUsage}
     * to be included in return set
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing
     * leaf taxa at, or higher than {@code rank}
     * under this {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<T>>> getLeafNamesUntil(Rank rank);

    /**
     * Returns {@code Hashtable} of {@code Hashtable}s containing all
     * lower taxa under this {@code NameUsage}
     * or null if none
     *
     * @return {@code Hashtable} of {@code Hashtable}s containing all
     * lower taxa under this {@code NameUsage}, indexed by their ascribed names,
     * or null if none
     */
    public Map<String, Set<NameUsage<T>>> getAllLowerNames();

    public Map<String, NameUsage<T>> getSiblings();

    public Map<String, NameUsage<T>> getLowerNameUsagesSet();

    public Map<String, NameUsage<T>> getIncludants();

    public void clearIncludants();

    public Map<String, NameUsage<T>> getExcludants();

    public void clearExcludants();

    public String getRankedName();

    public String getRankedName(boolean abbreviate);

    /**
     * Returns base name literal of the name literal by removing name ending
     * specific to the rank, zero length {@code String} if base name
     * is unapplicable to the name, or null if not implemented.
     *
     * @return {@code String} representing base name literal of the name
     * literal, or zero length  {@code String} if base name
     * is unapplicable to the name, or null if not implemented.
     */
    //public String getBaseName();

    public NameUsage<T> clone();

    public NameUsage<T> create();

    public NameUsage<T> getNameUsage(Object object);

    public Integer getDescendantCount();
}

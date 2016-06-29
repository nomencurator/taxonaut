/*
 * NameUsageQueryParameter.java: a calss to specify parameters of NameUsageExchanger
 *
 * Copyright (c) 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.io;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import lombok.Getter;
import lombok.Setter;


/**
 * <CODE>NameUsageQueryParameter</CODE> provides query parameter container.
 *
 * @version 	22 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameUsageQueryParameter <N extends NameUsage<?, ?>, T extends N>
    extends QueryParameter <N, T>
{
    /** String representing the literal of <tt>NameUsage</tt> to retrieve */
    protected String literal;

    /** String representing the rank of <tt>NameUsage</tt> to retrieve */
    protected Rank rank;


    @Getter
    @Setter
    /** levels of higher <tt>NameUsage</tt>s to retrieve in int */
    protected int height;

    @Getter
    @Setter
    /** levels of higher <tt>NameUsage</tt>s to retrieve in <tt>Rank</tt>*/
    protected Rank higher;

    @Getter
    @Setter
    /** depth of lower <tt>NameUsage</tt>s to retrieve in int */
    protected int depth;

    @Getter
    @Setter
    /** depth of lower <tt>NameUsage</tt>s to retrieve in <tt>Rank</tt>*/
    protected Rank lower;


    /** the option to include synonyms */
    @Getter @Setter protected boolean includeSynonyms;

    /** the option to include basionyms */
    @Getter @Setter protected boolean includeBasionyms;

    /** the option to include vernacualr names */
    @Getter @Setter protected boolean includeVernaculars;

    /** the locale to filter names */
    @Getter @Setter protected Locale locale;


    public boolean equals(Object object)
    {
	if(this == object) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	NameUsageQueryParameter<?, ?> that =
	    (NameUsageQueryParameter<?, ?>) object;

	return super.equals(object)
	    && Objects.equals(this.literal, that.literal)
	    && Objects.equals(this.rank, that.rank)
	    && Objects.equals(this.height, that.height)
	    && Objects.equals(this.higher, that.higher)
	    && Objects.equals(this.depth, that.depth)
	    && Objects.equals(this.lower, that.lower)
	    && Objects.equals(this.includeBasionyms, that.includeBasionyms)
	    && Objects.equals(this.includeSynonyms, that.includeSynonyms)
	    && Objects.equals(this.includeVernaculars, that.includeVernaculars)
	    && Objects.equals(this.locale, that.locale)
	    ;
    }

    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    literal,
			    rank,
			    height,
			    higher,
			    depth,
			    lower,
			    includeBasionyms,
			    includeSynonyms,
			    includeVernaculars,
			    locale
			    );
    }

    public NameUsageQueryParameter() {
	this(null, null, 0, 0, null, null, Boolean.FALSE, MatchingMode.EXACT, QueryMode.OBJECTS, false, false, false, null);
    }

    public NameUsageQueryParameter(NameUsage<N, T> filter, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale) {
	this(filter, 0, 0, synchronous, matchingMode, queryMode, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    public NameUsageQueryParameter(NameUsage<N, T> filter, int height, int depth, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode, boolean inlcudeBasionyms, boolean inlcudeSynonyms, boolean includeVernaculars, Locale locale) {
	super(filter, synchronous, matchingMode, queryMode);
	if(filter != null) {
	    setLiteral(filter.getLiteral());
	    setRank(filter.getRank());
	}
	setHeight(height);
	setDepth(depth);
	setIncludeBasionyms(includeBasionyms);
	setIncludeSynonyms(includeSynonyms);
	setIncludeVernaculars(includeVernaculars);
	setLocale(locale);
    }

    public NameUsageQueryParameter(String literal, Rank rank) {
	this(literal, rank, MatchingMode.EXACT);
    }

    public NameUsageQueryParameter(String literal, Rank rank, MatchingMode matchingMode) {
	this(literal, rank, 0, 0, null, null, Boolean.FALSE, matchingMode, QueryMode.NAMEUSAGES, false, false, false, null);
    }

    public NameUsageQueryParameter(String literal, Rank rank, int height, int depth, String persistentID, String localKey, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode, boolean inlcudeBasionyms, boolean inlcudeSynonyms, boolean includeVernaculars, Locale locale) {
	super(persistentID, localKey, synchronous, matchingMode, queryMode);
	setLiteral(literal);
	setRank(rank);
	setHeight(height);
	setDepth(depth);
	setIncludeBasionyms(includeBasionyms);
	setIncludeSynonyms(includeSynonyms);
	setIncludeVernaculars(includeVernaculars);
	setLocale(locale);
    }

    public String getLiteral()
    {
	return (literal != null || getFilter() == null) ?  literal : getFilter().getLiteral();
    }

    public void setLiteral(String literal)
    {
	if(literal != null)
	    setFilter(null);
	this.literal = literal;
    }

    public Rank getRank()
    {
	return (rank != null || getFilter() == null) ? rank : getNameUsageFilter().getRank();
    }

    public void setRank(Rank rank)
    {
	if(rank != null)
	    setFilter(null);
	this.rank = rank;
    }

    public void setFilter(NameUsage<N, T> filter)
    {
	if(filter != null) {
	    setLiteral(null);
	    setRank(null);
	}

	super.setFilter(filter);
    }

    public NameUsage<?, ?> getNameUsageFilter()
    {
	return (NameUsage<?, ?>)getFilter();
    }
}

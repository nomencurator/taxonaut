/*
 * NubNameUsageMatchScore.java:  a Java implementation of GBIF CheklistBank NameUsage, or nub
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.model.gbif;

import javax.annotation.Nullable;

import org.gbif.api.vocabulary.Rank;

import org.gbif.api.model.common.LinneanClassification;

import lombok.Getter;
import lombok.Setter;

/**
 * <CODE>NubNameUsageMatchQuery</CODE> holds GBIF SpeciesAPI match query paramter.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NubNameUsageMatchQuery
{
    @Getter
    @Setter
    protected String scientificName;

    @Getter
    @Setter
    protected Rank rank;

    @Getter
    @Setter
    protected LinneanClassification classification;

    @Getter
    @Setter
    protected boolean strict;

    @Getter
    @Setter
    protected boolean verbose;

    public NubNameUsageMatchQuery(String scientificName, @Nullable Rank rank,
				  @Nullable LinneanClassification classification, boolean strict, boolean verbose)
    {
	this.scientificName = scientificName;
	this.rank = rank;
	this.classification = classification;
	this.strict = strict;
	this.verbose = verbose;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
	    + ((classification == null) ? 0 : classification.hashCode());
	result = prime * result + ((rank == null) ? 0 : rank.hashCode());
	result = prime * result
	    + ((scientificName == null) ? 0 : scientificName.hashCode());
	result = prime * result + (strict ? 1231 : 1237);
	result = prime * result + (verbose ? 1231 : 1237);
	return result;
    }
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof NubNameUsageMatchQuery))
	    return false;
	NubNameUsageMatchQuery other = (NubNameUsageMatchQuery) obj;
	if (classification == null) {
	    if (other.classification != null)
		return false;
	} else if (!classification.equals(other.classification))
	    return false;
	if (rank != other.rank)
	    return false;
	if (scientificName == null) {
	    if (other.scientificName != null)
		return false;
	} else if (!scientificName.equals(other.scientificName))
	    return false;
	if (strict != other.strict)
	    return false;
	if (verbose != other.verbose)
	    return false;
	return true;
    }
}

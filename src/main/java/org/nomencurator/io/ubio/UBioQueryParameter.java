/*
 * UBioQueryParameter.java: a calss to specify parameters for UBio
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

package org.nomencurator.io.ubio;

import java.util.Objects;

import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.NameUsageQueryParameter;
import org.nomencurator.io.QueryMode;

import org.nomencurator.model.Rank;

import org.nomencurator.model.ubio.UBioNameUsageNode;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code UBioQueryParameter} provides query parameter container for UBio.
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class UBioQueryParameter
    extends NameUsageQueryParameter<UBioNameUsageNode, UBioNameUsageNode>
{
    @Getter
    @Setter
    protected boolean excludeScientificNames;

    @Getter
    @Setter
    protected boolean excludeVernacularNames;

    public boolean equals(Object object)
    {
	if(this == object) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	UBioQueryParameter that =
	    (UBioQueryParameter) object;

	return super.equals(object)
	    && Objects.equals(this.excludeScientificNames, that.excludeScientificNames)
	    && Objects.equals(this.excludeVernacularNames, that.excludeVernacularNames)
	    ;
    }

    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    excludeScientificNames,
			    excludeVernacularNames
			    );
    }

    public UBioQueryParameter() {
	super();
	setExcludeScientificNames(false);
	setExcludeVernacularNames(false);
    }

    public UBioQueryParameter(UBioNameUsageNode filter, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	this(filter, 0, 0, synchronous, matchingMode, queryMode);
    }

    public UBioQueryParameter(UBioNameUsageNode filter, int height, int depth, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode) {
	super(filter, synchronous, matchingMode, queryMode, false, false, false, null);
	setExcludeScientificNames(false);
	setExcludeVernacularNames(false);
    }

    public UBioQueryParameter(String literal, Rank rank) {
	this(literal, rank, MatchingMode.EXACT);
    }

    public UBioQueryParameter(String literal, Rank rank, MatchingMode matchingMode) {
	this(literal, rank, 0, 0, null, null, Boolean.FALSE, matchingMode, QueryMode.OBJECTS, false, false);
    }

    public UBioQueryParameter(String literal, Rank rank, int height, int depth, String persistentID, String localKey, Boolean synchronous, MatchingMode matchingMode, QueryMode queryMode, boolean excludeScientificNames, boolean excludeVernacularNames) {
	super(literal, rank, height, depth, persistentID, localKey, synchronous, matchingMode, queryMode, false, false, !excludeVernacularNames, null);
	setExcludeScientificNames(excludeScientificNames);
	setExcludeVernacularNames(excludeVernacularNames);
    }

}

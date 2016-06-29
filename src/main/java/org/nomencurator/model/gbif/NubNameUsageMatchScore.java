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

import org.gbif.api.model.checklistbank.NameUsageMatch.MatchType;
import org.gbif.api.model.checklistbank.NameUsageMatch;

import lombok.Getter;
import lombok.Setter;

/**
 * <CODE>NubNameUsageMatchScore</CODE> is an implementation of GBIF CheklistBank NameUsage, or nub.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NubNameUsageMatchScore
{
    @Getter
    @Setter
    protected NubNameUsage usage;

    @Getter
    @Setter
    protected NubNameUsageMatchQuery query;

    @Getter
    @Setter
    protected Integer confidence;

    @Getter
    protected String note;

    @Getter
    @Setter
    protected Integer nameConfidence;

    @Getter
    @Setter
    protected Integer classificationConfidence;

    @Getter
    @Setter
    protected Integer rankConfidence;

    @Getter
    @Setter
    protected Integer acceptedConfidence;

    @Getter
    @Setter
    protected Integer singleMatchConfidence;

    @Getter
    @Setter
    protected MatchType matchType;

    public NubNameUsageMatchScore(NubNameUsage usage, NubNameUsageMatchQuery query, NameUsageMatch matchResult)
    {
	this(usage, query, matchResult.getConfidence(), matchResult.getNote(), matchResult.getMatchType());
    }

    public NubNameUsageMatchScore(NubNameUsage usage, NubNameUsageMatchQuery query, Integer confidence, String note, MatchType matchType)
    {
	this.usage = usage;
	this.query= query;
	this.confidence = confidence;
	this.matchType = matchType;
	setNote(note);
    }

    public void setNote(String note)
    {
	setNameConfidence(null);
	setClassificationConfidence(null);
	setRankConfidence(null);
	setAcceptedConfidence(null);
	setSingleMatchConfidence(null);

	this.note = note;

	if(note == null || note.length() == 0 || !note.startsWith("Individual confidence:"))
	    return;

	String confidences[] = note.split(": ");
	confidences = confidences[1].split("; ");
	for(String element : confidences) {
	    String keyValue[] = element.trim().split("=");
	    switch(keyValue[0]) {
	    case "name":
		setNameConfidence(Integer.valueOf(keyValue[1]));
		break;
	    case "classification":
		setClassificationConfidence(Integer.valueOf(keyValue[1]));
		break;
	    case "rank":
		setRankConfidence(Integer.valueOf(keyValue[1]));
		break;
	    case "accepted":
		setAcceptedConfidence(Integer.valueOf(keyValue[1]));
		break;
	    case "singleMatch":
		setSingleMatchConfidence(Integer.valueOf(keyValue[1]));
		break;
	    }
	}
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
	    + ((confidence == null) ? 0 : confidence.hashCode());
	result = prime * result
	    + ((matchType == null) ? 0 : matchType.hashCode());
	result = prime * result + ((note == null) ? 0 : note.hashCode());
	result = prime * result + ((query == null) ? 0 : query.hashCode());
	result = prime * result + ((usage == null) ? 0 : usage.hashCode());
	return result;
    }
    
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (!(obj instanceof NubNameUsageMatchScore))
	    return false;
	NubNameUsageMatchScore other = (NubNameUsageMatchScore) obj;
	if (confidence == null) {
	    if (other.confidence != null)
		return false;
	} else if (!confidence.equals(other.confidence))
	    return false;
	if (matchType != other.matchType)
	    return false;
	if (note == null) {
	    if (other.note != null)
		return false;
	} else if (!note.equals(other.note))
	    return false;
	if (query == null) {
	    if (other.query != null)
		return false;
	} else if (!query.equals(other.query))
	    return false;
	if (usage == null) {
	    if (other.usage != null)
		return false;
	} else if (!usage.equals(other.usage))
	    return false;
	return true;
    }
}

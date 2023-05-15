/*
 * ClassificationBankObject.java: provides methods to access to uBio XML Webservices
 *
 * Copyright (c) 2008, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.ubio.model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import lombok.Data;
/**
 * {@code ClassificationBankObject} provides a method to handle classificationbank object element returned from uBio XML Webservices
 *
 * @version 	30 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class ClassificationBankObject
{
    protected ServiceData serviceData;
    protected RecordedName recordedName;
    protected int classificationsID;
    protected int rankID;
    protected String rankName;
    protected Rank rank;
    protected ClassificationData classificationData;
    protected List<ClassificationNode> ancestry;
    protected List<ClassificationNode> children;
    protected List<Justification> justifications;
    protected List<Synonym> synonyms;
    protected List<VernacularName> vernacularNames;

    protected List<NamebankObject> higherNames;
    protected List<NamebankObject> lowerNames;

    protected static HashMap<Integer, ClassificationBankObject> bank;

    public static ClassificationBankObject get(String classificationsID)
    {
	return get(Integer.valueOf(classificationsID));
    }

    public static ClassificationBankObject get(int classificationsID)
    {
	return get(Integer.valueOf(classificationsID));
    }

    public static ClassificationBankObject get(Integer classificationsID)
    {
	ClassificationBankObject c = null;
	if(bank != null)
	    c = bank.get(classificationsID);

	if(c == null)
	    c = new ClassificationBankObject(classificationsID.intValue());

	return c;
    }

    public ClassificationBankObject(int classificationsID)
    {
	setClassificationsID(classificationsID);

	if(bank == null) {
	    bank = new HashMap<Integer, ClassificationBankObject>();
	}

	bank.put(Integer.valueOf(classificationsID), this);
    }

    public ClassificationBankObject(int classificationsID,
				    NamebankObject recordedName,
				    ClassificationData classification,
				    Rank rank,
				    List<NamebankObject> higherNames,
				    List<NamebankObject> lowerNames)
    {
	setClassificationsID(classificationsID);
	setRecordedName(recordedName);
	setClassificationData(classification);
	setRank(rank);
	setHigherNames(higherNames);
	setLowerNames(lowerNames);
    }

    /*
    public long getId()
    {
	return id;
    }

    public void setId(long classificationsID)
    {
	this.id = id;
    }

    public NamebankObject getRecordedName()
    {
	return recordedName;
    }

    public void setRecordedName(NamebankObject recordedName)
    {
	this.recordedName = recordedName;
    }

    public ClassificationData getClassification()
    {
	return classification;
    }

    public void setClassification(ClassificationData classification)
    {
	this.classification = classification;
    }

    public Rank getRank()
    {
	return rank;
    }

    public void setRank(Rank rank)
    {
	this.rank = rank;
    }

    public NamebankObject[] getHigherNames()
    {
	return higherNames;
    }

    public void setHigherNames(NamebankObject[] higherNames)
    {
	this.higherNames = higherNames;
    }

    public NamebankObject[] getLowerNames()
    {
	return lowerNames;
    }

    public void setLowerNames(NamebankObject[] lowerNames)
    {
	this.lowerNames = lowerNames;
    }
    */
}

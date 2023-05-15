/*
 * NamebankObject.java: provides methods to access to uBio serviceData
 *
 * Copyright (c) 2007, 2008, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.List;
import java.util.HashMap;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * An implementation of uBio {@code NamebankObject}.
 *
 *
 * @version 	30 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NamebankObject
    extends ScientificName
{
    private static final long serialVersionUID = -767200315736406378L;

    @Getter
    @Setter
    protected ServiceData serviceData;
    @Getter
    @Setter
    protected int basionymFlag;
    @Getter
    @Setter
    protected boolean basionym;
    @Getter
    @Setter
    protected int extinctFlag;
    @Getter
    @Setter
    protected boolean extinct;
    @Getter
    @Setter
    protected int homonymFlag;
    @Getter
    @Setter
    protected boolean homonym;
    @Getter
    @Setter
    protected int parentID;
    @Getter
    @Setter
    protected NamebankObject parent;

    @Getter
    @Setter
    protected List<RecordedName> homotypicSynonyms;
    @Getter
    @Setter
    protected List<VernacularName> vernacularNames;
    @Getter
    @Setter
    protected List<Mapping> mappings;
    @Getter
    @Setter
    protected List<Citation> citations;

    @Getter
    @Setter
    protected boolean saturated;

    protected static HashMap<Integer, NamebankObject> namebank;

    public static NamebankObject ZERO = new NamebankObject(0, "");

    public RecordedName getRecordedName()
    {
	return new RecordedName(getNamebankID(),
				getNameString(),
				getFullNameString());
    }

    public VernacularName getVernacularName()
    {
	return new VernacularName(getNamebankID(),
				  getNameString(),
				  getFullNameString(),
				  getLanguageCode(),
				  getLanguageName(),
				  getLanguage(),
				  getPackageID(),
				  getPackageName(),
				  getNamebankPackage(),
				  getBasionymUnit(),
				  getBasionymUnitObject(),
				  getNamebankIDLink(),
				  getNamebankObject(),
				  getNameStringLink(),
				  getFullNameStringLink()
				  );
    }

    public static NamebankObject get(String namebankID, String name)
    {
	try{
	    return get(Integer.valueOf(namebankID), name);
	}
	catch(Throwable e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static NamebankObject get(int namebankID, String name)
    {
	NamebankObject obj = null;
	if(namebankID > 0) {
	    obj = get(Integer.valueOf(namebankID));
	    /*
	    if(obj == null)
		obj = new NamebankObject(namebankID, name);
	    */
	}

	return obj;
    }

    /*
    public static NamebankObject get(int namebankID)
    {
	try{
	    return get(Integer.valueOf(namebankID));
	}
	catch(Throwable e) {
	    e.printStackTrace();
	}
	return null;
    }
    */
    
    public static NamebankObject get(String namebankID)
    {
	try{
	return get(Integer.valueOf(namebankID));
	}
	catch(Throwable e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static NamebankObject get(int namebankID)
    {
	NamebankObject obj = null;
	if(namebank != null)
	    obj = namebank.get(namebankID);

	/*
	if(obj == null) {
	    obj = new NamebankObject(namebankID);
	}
	*/

	return obj;
    }

    public static NamebankObject remove(String namebankID)
    {
	return remove(Integer.valueOf(namebankID));
    }


    public static NamebankObject remove(int namebankID)
    {
	if(namebank == null)
	    return null;

	return namebank.remove(namebankID);
    }

    public NamebankObject(String namebankID,
			  String nameString)
    {
	this(Integer.valueOf(namebankID), nameString);
    }

    public NamebankObject(int namebankID,
			  String nameString)
    {
	this(namebankID, 
	     Language.getByCode(Language.SCIENTIFIC),
	     nameString);
    }

    public NamebankObject(int namebankID,
			  Language language,
			  String nameString)
    {
	super();
	setNamebankID(namebankID);
	setLanguage(language);
	setNameString(nameString);
    }

    public NamebankObject(int namebankID)
    {
	super();
	setNamebankID(namebankID);
	if(namebank == null) {
	    namebank = new HashMap<Integer, NamebankObject>();
	}
	namebank.put(Integer.valueOf(namebankID), this);
    }


    public NamebankObject(int namebankID,
			  String nameString,
			  String fullNameString)
    {
	this(namebankID, nameString);
	setFullNameString(fullNameString);
    }

    public NamebankObject(int namebankID,
			  String nameString,
			  String fullNameString,
			  NamebankPackage namebankPackage)
    {
	this(namebankID, nameString, fullNameString);
	setNamebankPackage(namebankPackage);
    }

    public NamebankObject(int namebankID,
			  Language language,
			  String nameString,
			  NamebankPackage namebankPackage)
    {
	this(namebankID, language, nameString);
	setNamebankPackage(namebankPackage);
    }

    public NamebankObject(int namebankID,
			  String nameString,
			  String fullNameString,
			  NamebankPackage namebankPackage,
			  NamebankObject basionymUnitObject,
			  Rank rank)
    {
	this(namebankID, nameString, fullNameString, namebankPackage);
	setBasionymUnitObject(basionymUnitObject);
	setRank(rank);
    }

    public NamebankObject(int namebankID,
			  Language language,
			  String nameString,
			  NamebankPackage namebankPackage,
			  String fullNameString,
			  Rank rank,
			  NamebankObject basionymUnitObject,
			  boolean basionym,
			  String nameStringQualifier,
			  boolean extinct,
			  boolean homonym,
			  NamebankObject parent,
			  List<RecordedName> homotypicSynonyms,
			  List<VernacularName> vernacularNames)
    {
	this(namebankID, 
	     language,
	     nameString);
	setNamebankPackage(namebankPackage);
	setFullNameString(fullNameString);
	setRank(rank);
	setBasionymUnitObject(basionymUnitObject);
	setBasionym(basionym);
	setNameStringQualifier(nameStringQualifier);
	setExtinct(extinct);
	setHomonym(homonym);
	setParent(parent);
	setHomotypicSynonyms(homotypicSynonyms);
	setVernacularNames(vernacularNames);
	setSaturated(true);
    }

    /*
    public boolean isSaturated()
    {
	return saturated;
    }

    public void saturate(boolean saturated)
    {
	this.saturated = saturated;
    }

    public long getID()
    {
	return namebankID;
    }

    public Language getLanguage()
    {
	return language;
    }

    public void setLanguage(Language language)
    {
	this.language = language;
    }

    public String getNameString()
    {
	return nameString;
    }

    public void setNameString(String nameString)
    {
	this.nameString = nameString;
    }

    public NamebankPackage getNamebankPackage()
    {
	return namebankPackage;
    }

    public void setNamebankPackage(NamebankPackage namebankPackage)
    {
	this.namebankPackage = namebankPackage;
    }
    */

    public boolean isScientificName()
    {
	return Language.SCIENTIFIC.equals(getLanguage().getCode());
    }

    /*
    public String getFullNameString()
    {
	return fullNameString;
    }

    public void setFullNameString(String fullNameString)
    {
	this.fullNameString = fullNameString;
    }

    public Rank getRank()
    {
	return rank;
    }

    public void setRank(Rank rank)
    {
	this.rank = rank;
    }

    public boolean isBasionym()
    {
	return basionym;
    }

    public void setBasionym(boolean basionym)
    {
	this.basionym = basionym;
    }

    public String getNameStringQualifier()
    {
	return nameStringQualifier;
    }

    public void setNameStringQualifier(String nameStringQualifier)
    {
	this.nameStringQualifier = nameStringQualifier;
    }

    public boolean isExtinct()
    {
	return extinct;
    }

    public void setExtinct(boolean extinct)
    {
	this.extinct = extinct;
    }

    public boolean isHomonym()
    {
	return homonym;
    }

    public void setHomonym(boolean homonym)
    {
	this.homonym = homonym;
    }

    public NamebankObject getParent()
    {
	return parent;
    }

    public void setParent(NamebankObject parent)
    {
	this.parent = parent;
    }

    public RecordedName[] getHomotypicSynonyms()
    {
	if(homotypicSynonyms == null)
	    return null;
	int length = homotypicSynonyms.length;
	RecordedName[] copy = 
		  new RecordedName[length];
	System.arraycopy(homotypicSynonyms, 0,
			 copy, 0, length);
	return copy;
    }

    public void setHomotypicSynonyms(RecordedName[] synonyms)
    {
	if(homotypicSynonyms == null) {
	    if(synonyms == null)
		return;
	    homotypicSynonyms = new RecordedName[synonyms.length];
	}
	else if (synonyms == null) {
	    for(RecordedName element: homotypicSynonyms) {
		element = null;
	    }
	    homotypicSynonyms = null;
	    return;
	}
	else if (synonyms.length != homotypicSynonyms.length) {
	    for(RecordedName element: homotypicSynonyms) {
		element = null;
	    }
	    homotypicSynonyms = new RecordedName[synonyms.length];
	}
	System.arraycopy(synonyms, 0,
			 homotypicSynonyms, 0, synonyms.length);
    }

    public VernacularName[] getVernacularNames()
    {
	if(vernacularNames == null)
	    return null;
	int length = homotypicSynonyms.length;
	VernacularName[] copy = 
		  new VernacularName[length];
	System.arraycopy(homotypicSynonyms, 0,
			 copy, 0, length);
	return copy;
    }

    public void setVernacularNames(VernacularName[] vernacular)
    {
	if (vernacularNames == null) {
	    if (vernacular == null)
		return;
	    vernacularNames = new VernacularName[vernacular.length];
	}
	else if (vernacular == null) {
	    for(VernacularName element: vernacularNames) {
		element = null;
	    }
	    vernacularNames = null;
	    return;
	}
	else if (vernacular.length != vernacularNames.length) {
	    for(VernacularName element: vernacularNames) {
		element = null;
	    }
	    vernacularNames = new VernacularName[vernacular.length];
	}
	System.arraycopy(vernacular, 0,
			 vernacularNames, 0, vernacular.length);
    }

    public Citation[] getCitations()
    {
	return copyCitations(citations, null);
    }

    public void setCitations(Citation[] citations)
    {
	copyCitations(citations, this.citations);
    }

    protected Citation[] copyCitations(Citation[] src, 
						   Citation[] dest)
    {
	if(dest == null) {
	    if(src == null)
		return null;
	    dest = new Citation[src.length];
	}
	else if (src == null) {
	    for(Citation element: dest) {
		element = null;
	    }
	    dest = null;
	    return null;
	}
	else if (src.length != dest.length) {
	    for(Citation element: dest) {
		element = null;
	    }
	    dest = new Citation[src.length];
	}
	System.arraycopy(src, 0,
			 dest, 0, src.length);

	return dest;
    }

    public Mapping[] getMappings()
    {
	return copyMappings(mappings, null);
    }

    public void setMappings(Mapping[] mappings)
    {
	copyMappings(mappings, this.mappings);
    }

    protected Mapping[] copyMappings(Mapping[] src, 
					   Mapping[] dest)
    {
	if(dest == null) {
	    if(src == null)
		return null;
	    dest = new Mapping[src.length];
	}
	else if (src == null) {
	    for(Mapping element: dest) {
		element = null;
	    }
	    dest = null;
	    return null;
	}
	else if (src.length != dest.length) {
	    for(Mapping element: dest) {
		element = null;
	    }
	    dest = new Mapping[src.length];
	}
	System.arraycopy(src, 0,
			 dest, 0, src.length);

	return dest;
    }

    public NamebankObject getLink()
    {
	return link;
    }

    protected void setLink(NamebankObject namebankObject)
    {
	link = namebankObject;
    }
    */

    @Override
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final NamebankObject theOther = (NamebankObject) object;
	return super.equals(object)
	    && Objects.equals(this.getServiceData(), theOther.getServiceData())
	    && Objects.equals(this.getBasionymFlag(), theOther.getBasionymFlag())
	    && Objects.equals(this.getExtinctFlag(), theOther.getExtinctFlag())
	    && Objects.equals(this.getHomonymFlag(), theOther.getHomonymFlag())
	    && Objects.equals(this.getParentID(), theOther.getParentID())
	    && Objects.equals(this.getHomotypicSynonyms(), theOther.getHomotypicSynonyms())
	    && Objects.equals(this.getVernacularNames(), theOther.getVernacularNames())
	    && Objects.equals(this.getMappings(),theOther.getMappings())
	    && Objects.equals(this.getCitations(), theOther.getCitations())
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getServiceData(),
			    getBasionymFlag(),
			    getExtinctFlag(),
			    getHomonymFlag(),
			    getParentID(),
			    getHomotypicSynonyms(),
			    getVernacularNames(),
			    getMappings(),
			    getCitations()
			    );
    }
}

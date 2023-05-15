/*
 * ScientificName.java: provides methods to access to uBio service data
 *
 * Copyright (c) 2007, 2015, 2016 Nozomi `James' Ytow
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

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code ScientificName} provides methods to handle scientific name element returned from uBio XML Webservices
 *
 * @version 	30 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class ScientificName
    extends VernacularName
{
    private static final long serialVersionUID = 4426515953472868862L;

    @Getter
    @Setter
    protected int nameStringGroup;

    @Getter
    @Setter
    protected String nameStringQualifier;

    @Getter
    @Setter
    protected int rankID;

    @Getter
    @Setter
    protected String rankName;

    @Getter
    @Setter
    protected Rank rank;

    public ScientificName()
    {
	super();
    }

    public ScientificName(int namebankID,
			  String nameString,
			  String fullNameString,
			  String languageCode,
			  String languageName,
			  Language language,
			  int packageID,
			  String packageName,
			  NamebankPackage namebankPackage,
			  int basionymUnit,
			  NamebankObject basionymUnitObject,
			  int namebankIDLink,
			  NamebankObject namebankObject,
			  String nameStringLink,
			  String fullNameStringLink,
			  Rank rank,
			  String nameStringQualifier)
    {
	super(namebankID,
	      nameString,
	      fullNameString,
	      Language.getByCode(Language.SCIENTIFIC).getCode(),
	      Language.SCIENTIFIC,
	      Language.getByCode(Language.SCIENTIFIC),
	      packageID,
	      packageName,
	      namebankPackage,
	      basionymUnit,
	      basionymUnitObject,
	      0, null, null, null);
	setRank(rank);
	setNameStringQualifier(nameStringQualifier);
    }

    @Override
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final ScientificName that = (ScientificName) object;
	return super.equals(that)
	    && Objects.equals(this.getNameStringGroup(), that.getNameStringQualifier())
	    && Objects.equals(this.getNameStringQualifier(), that.getRankID())
	    && Objects.equals(this.getRankID(), that.getRankName())
	    && Objects.equals(this.getRankName(), that.getNameStringGroup())
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getNameStringGroup(),
			    getNameStringQualifier(),
			    getRankID(),
			    getRankName()
			    );
    }
}

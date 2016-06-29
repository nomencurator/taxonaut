/*
 * VernacularName.java: uBio VernacularName
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
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

package org.ubio.model;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * An implementation of uBio {@code VernacularName}.
 *
 *
 * @version 	21 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class VernacularName
    extends RecordedName
{
    private static final long serialVersionUID = 8922761803252210481L;

    @Getter
    @Setter
    protected String languageCode;

    @Getter
    @Setter
    protected String languageName;

    @Getter
    @Setter
    protected Language language;

    @Getter
    @Setter
    protected int packageID;

    @Getter
    @Setter
    protected String packageName;

    @Getter
    @Setter
    protected NamebankPackage namebankPackage;

    @Getter
    @Setter
    protected int basionymUnit;

    @Getter
    @Setter
    protected NamebankObject basionymUnitObject;

    @Getter
    @Setter
    protected int namebankIDLink;

    @Getter
    @Setter
    protected NamebankObject namebankObject;

    @Getter
    @Setter
    protected String nameStringLink;

    @Getter
    @Setter
    protected String fullNameStringLink;

    @Getter
    @Setter
    protected boolean saturated;

    public VernacularName()
    {
	super();
    }

    public VernacularName(int namebankID,
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
			  String fullNameStringLink)
    {
	super(namebankID, nameString, fullNameString);
	setLanguageCode(languageCode);
	setLanguageName(languageName);
	setLanguage(language);
	setPackageID(packageID);
	setPackageName(packageName);
	setNamebankPackage(namebankPackage);
	setBasionymUnit(basionymUnit);
	setBasionymUnitObject(basionymUnitObject);
	setNamebankIDLink(namebankIDLink);
	setNamebankObject(namebankObject);
	setNameStringLink(nameStringLink);
	setFullNameStringLink(fullNameStringLink);
    }

    @Override
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final VernacularName theOther = (VernacularName) object;
	return super.equals(object)
	    && Objects.equals(this.getLanguageCode(), theOther.getLanguageCode())
	    && Objects.equals(this.getLanguageName(), theOther.getLanguageName())
	    && Objects.equals(this.getPackageID(), theOther.getPackageID())
	    && Objects.equals(this.getPackageName(), theOther.getPackageName())
	    && Objects.equals(this.getBasionymUnit(), theOther.getBasionymUnit())
	    && Objects.equals(this.getNamebankIDLink(), theOther.getNamebankIDLink())
	    && Objects.equals(this.getNameStringLink(), theOther.getNameStringLink())
	    && Objects.equals(this.getFullNameStringLink(), theOther.getFullNameStringLink())
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getLanguageCode(),
			    getLanguageName(),
			    getPackageID(),
			    getPackageName(),
			    getBasionymUnit(),
			    getNamebankIDLink(),
			    getNameStringLink(),
			    getFullNameStringLink()
			    );
    }
}

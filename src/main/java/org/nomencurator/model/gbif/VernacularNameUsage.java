/*
 * VernacularNameUsage.java:  a Java implementation of GBIF CheklistBank NameUsage, or nub
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

import org.gbif.api.model.checklistbank.VernacularName;

import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;

import java.io.Serializable;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.gbif.api.vocabulary.Rank;

import org.nomencurator.model.AbstractNameUsage;
import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.Author;
import org.nomencurator.model.Name;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Publication;

import org.nomencurator.api.gbif.SpeciesAPIClient;

import org.nomencurator.util.Locales;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;
import lombok.Setter;

/**
 * <CODE>VernacularNameUsage</CODE> is an implementation of GBIF CheklistBank NameUsage, or nub.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class VernacularNameUsage
    extends AbstractNameUsage<VernacularNameUsage, VernacularNameUsage>
{
    private static final long serialVersionUID = -2520762617216765347L;

    @Getter
    private static SpeciesAPIClient dataSource;
    
    @Getter
    protected VernacularName vernacularName;

    @Getter
    @Setter
    protected NubNameUsage scientificNameUsage;

    public VernacularNameUsage create()
    {
	return new VernacularNameUsage();
    }

    public VernacularNameUsage(){
	super();
    };

    public VernacularNameUsage(VernacularName vernacularName) {
	super();
	setVernacularName(vernacularName);
    };

    public void setVernacularName(VernacularName vernacularName)
	throws RuntimeException
    {
	NameUsage<? extends VernacularNameUsage, ? extends VernacularNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof VernacularNameUsage) {
		((VernacularNameUsage)n).setVernacularName(vernacularName);
	    }
	    return;
	}

	if(this.vernacularName == vernacularName)
	    return;

	clearVernacularName();
	if(vernacularName != null) {
	    setLiteral(vernacularName.getVernacularName());
	    Language language = vernacularName.getLanguage();
	    Country country = vernacularName.getCountry();
	    String languageCode = (language == null)? null:language.getIso3LetterCode();
	    String countryCode = (country == null)? null:country.getIso3LetterCode();
	    if(languageCode != null)  {
		Locale locale = null;
		Set<Locale> locales = Locales.get(languageCode, countryCode);
		for(Locale l : locales) {
		    String variant = l.getVariant();
		    if(variant == null || variant.length() ==0) {
			locale = l;
			break;
		    }
		}
		setLocale(locale);
	    }
	    String source = vernacularName.getSource();
	    if(source != null) {
		Publication publication = new Publication();
		publication.setCitationTitle(source);
		Appearance appearance = new Appearance();
		appearance.setAppearance(source);
		appearance.setPublication(publication);
		setAppearance(appearance);
	    }
	}
	this.vernacularName = vernacularName;
    }

    protected void clearVernacularName()
    {
	NameUsage<? extends VernacularNameUsage, ? extends VernacularNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof VernacularNameUsage) {
		((VernacularNameUsage)n).clearVernacularName();
	    }
	    return;
	}
	vernacularName = null;
    }

    public String getLiteral()
    {
	NameUsage<? extends VernacularNameUsage, ? extends VernacularNameUsage> n = getNameUsage();
	if(n != this && n instanceof VernacularNameUsage) {
	    return ((VernacularNameUsage)n).getLiteral();
	}

	if(vernacularName != null)
	    return vernacularName.getVernacularName();

	return super.getLiteral();
    }

    public void setLiteral(String literal)
    {
	NameUsage<? extends VernacularNameUsage, ? extends VernacularNameUsage> n = getNameUsage();
	if(n != this && n instanceof VernacularNameUsage) {
	    ((VernacularNameUsage)n).setLiteral(literal);
	    return;
	}

	if(vernacularName != null) {
	    vernacularName.setVernacularName(literal);
	}
	else {
	    super.setLiteral(literal);
	}
    }

    protected VernacularNameUsage createNameUsage()
    {
	return new VernacularNameUsage();
    }

    protected VernacularNameUsage createNameUsage(String persistentID)
    {
	//return createNameUsage();
	return null;
    }

    protected VernacularNameUsage createNameUsage(Name<?, ?> nameUsage)
    {
	if(nameUsage != null && nameUsage instanceof VernacularNameUsage)
	    return new VernacularNameUsage(((VernacularNameUsage)nameUsage).getVernacularName());
	else
	    return createNameUsage();
    }

    protected VernacularNameUsage createNameUsage(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)

    {
	if(nameUsage != null && nameUsage instanceof VernacularNameUsage)
	    return new VernacularNameUsage(((VernacularNameUsage)nameUsage).getVernacularName());
	else
	    return createNameUsage();
    }

}

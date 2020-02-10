/*
 * DatasetMixIn.java: a mix in to deserialize Dataset from JSON using Jackson
 *
 * Copyright (c) 2020 Nozomi `James' Ytow
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

package org.nomencurator.api.gbif.jackson.mixin;

// for Jackson 1.x
// import org.codehaus.jackson.map.annotate.JsonDeserialize;
// or Jackson 2.x
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Set;

import org.nomencurator.api.gbif.jackson.deserialize.CountrySetDeserializer;
import org.nomencurator.api.gbif.jackson.deserialize.LanguageDeserializer;
import org.nomencurator.api.gbif.jackson.deserialize.LicenseDeserializer;
import org.nomencurator.api.gbif.jackson.deserialize.ContactListDeserializer;

import org.gbif.api.model.registry.Contact;
import org.gbif.api.model.registry.Dataset;

import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.Language;
import org.gbif.api.vocabulary.License;

/**
 * {@code DatasetMixIn} provieds mix in to deserialize {@link Language}. into JSON using {@link LanguageDesrializer}.
 *
 * @version 	10 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
//@JsonIgnoreProperties({"countryCoverage", "contacts"})
@JsonIgnoreProperties({"contacts"})
public interface DatasetMixIn
{
    @JsonDeserialize(using = LanguageDeserializer.class)
    Language getLanguage();

    @JsonDeserialize(using = LanguageDeserializer.class)
    Language getDataLanguage();

    @JsonDeserialize(using = LicenseDeserializer.class)
    License getLicense();
    /*
    static TypeReference<SetDeserializer<Country>> countryDeserializerType
	= new TypeReference<SetDeserializer<Country>>(){};
    @JsonDeserialize(using = countryDeserializerType)
    */
    @JsonDeserialize(using = CountrySetDeserializer.class)
    Set<Country> getCountryCoverage();

    @JsonDeserialize(using = ContactListDeserializer.class)
    List<Contact> getContacts();
    
}

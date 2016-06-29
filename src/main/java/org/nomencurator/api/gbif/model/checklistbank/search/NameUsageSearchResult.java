/*
 * ParsedName.java:  a set of setter methods to extend org.gbif.api.model.checklistbank.ParsedName
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

package org.nomencurator.api.gbif.model.checklistbank.search;

import static com.google.common.collect.Lists.newArrayList;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.gbif.api.model.checklistbank.Description;
import org.gbif.api.model.checklistbank.VernacularName;

import org.nomencurator.api.gbif.model.checklistbank.search.SerializedDescriptions;
import org.nomencurator.api.gbif.model.checklistbank.search.SerializedVernacularNames;

/**
 * <tt>NameUsageSearchResult</tt> extends <tt>org.gbif.api.model.checklistbank.search.NameUsageSearchResult</tt>
 * to provides auxiliary access to some attributes.
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NameUsageSearchResult extends org.gbif.api.model.checklistbank.search.NameUsageSearchResult {

    @Getter
    @Setter
    // private SerializedDescriptions descriptionsSerialized;
    private ProxiedList<Description> descriptionsSerialized = new ProxiedList<Description>();

    @Getter
    @Setter
    // private SerializedVernacularNames vernacularNamesSerialized;
    private ProxiedList<VernacularName> vernacularNamesSerialized = new ProxiedList<VernacularName>();

    @Setter
    private LinkedHashMap<Integer, String> higherClassificationMap;

    /*
    public NameUsageSearchResult() {
	super();
	descriptionsSerialized = new ProxiedList<Description> (getDescriptions());
	vernacularNamesSerialized = new ProxiedList<VernacularName> (getVernacularNames());
    }

    public void setDescriptions(List<Description> descriptions) {
	super.setDescriptions(descriptions);
	// descriptionsSerialized.setEntities(descriptions);
    }

    public void setVernacularNames(List<VernacularName> vernacularNames) {
	super.setVernacularNames(vernacularNames);
	vernacularNamesSerialized.setEntities(vernacularNames);
    }
    */
}

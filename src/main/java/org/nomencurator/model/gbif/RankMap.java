/*
 * RankMap.java:  mapping between rank abbrebiation in GBIF CheklistBank and Nomencurator Rank
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * <tt>RankMap</tt> provieds mappings between
 * <tt>org.gbif.api.vocabulary.Rank</tt> and 
 * <tt>org.nomencurator.model.Rank</tt>
 *
 * @version 	17 Jan. 2015
 * @author 	Nozomi `James' Ytow
 */
public class RankMap {
    private static Map<org.nomencurator.model.Rank, org.gbif.api.vocabulary.Rank> toGBIF;
    private static Map<String, org.gbif.api.vocabulary.Rank> toGBIFByName;
    private static Map<org.gbif.api.vocabulary.Rank, org.nomencurator.model.Rank> fromGBIF;

    private RankMap () {};

    static {
	toGBIF = Collections.synchronizedMap(new HashMap<org.nomencurator.model.Rank, org.gbif.api.vocabulary.Rank>());
	toGBIFByName = Collections.synchronizedMap(new HashMap<String, org.gbif.api.vocabulary.Rank>());
	fromGBIF = Collections.synchronizedMap(new HashMap<org.gbif.api.vocabulary.Rank, org.nomencurator.model.Rank>());

	toGBIF.put(org.nomencurator.model.Rank.DOMAIN, org.gbif.api.vocabulary.Rank.DOMAIN);
	toGBIF.put(org.nomencurator.model.Rank.KINGDOM, org.gbif.api.vocabulary.Rank.KINGDOM);
	toGBIF.put(org.nomencurator.model.Rank.get("subkingdom"), org.gbif.api.vocabulary.Rank.SUBKINGDOM);
	toGBIF.put(org.nomencurator.model.Rank.get("superphylum"),org.gbif.api.vocabulary.Rank.SUPERPHYLUM);
	toGBIF.put(org.nomencurator.model.Rank.PHYLUM,org.gbif.api.vocabulary.Rank.PHYLUM);
	toGBIF.put(org.nomencurator.model.Rank.get("subphylum"),org.gbif.api.vocabulary.Rank.SUBPHYLUM);
	toGBIF.put(org.nomencurator.model.Rank.get("superclass"),org.gbif.api.vocabulary.Rank.SUPERCLASS);
	toGBIF.put(org.nomencurator.model.Rank.CLASS,org.gbif.api.vocabulary.Rank.CLASS);
	toGBIF.put(org.nomencurator.model.Rank.get("subclass"),org.gbif.api.vocabulary.Rank.SUBCLASS);
	toGBIF.put(org.nomencurator.model.Rank.get("superorder"),org.gbif.api.vocabulary.Rank.SUPERORDER);
	toGBIF.put(org.nomencurator.model.Rank.ORDER,org.gbif.api.vocabulary.Rank.ORDER);
	toGBIF.put(org.nomencurator.model.Rank.get("suborder"),org.gbif.api.vocabulary.Rank.SUBORDER);
	toGBIF.put(org.nomencurator.model.Rank.get("infraorder"),org.gbif.api.vocabulary.Rank.INFRAORDER);
	toGBIF.put(org.nomencurator.model.Rank.get("superfamily"),org.gbif.api.vocabulary.Rank.SUPERFAMILY);
	toGBIF.put(org.nomencurator.model.Rank.FAMILY,org.gbif.api.vocabulary.Rank.FAMILY);
	toGBIF.put(org.nomencurator.model.Rank.get("subfamily"),org.gbif.api.vocabulary.Rank.SUBFAMILY);
	toGBIF.put(org.nomencurator.model.Rank.TRIBE,org.gbif.api.vocabulary.Rank.TRIBE);
	toGBIF.put(org.nomencurator.model.Rank.get("subtribe"),org.gbif.api.vocabulary.Rank.SUBTRIBE);
	toGBIF.put(org.nomencurator.model.Rank.get("supergenus"),org.gbif.api.vocabulary.Rank.SUPRAGENERIC_NAME);
	toGBIF.put(org.nomencurator.model.Rank.GENUS,org.gbif.api.vocabulary.Rank.GENUS);
	toGBIF.put(org.nomencurator.model.Rank.get("subgenus"),org.gbif.api.vocabulary.Rank.SUBGENUS);
	toGBIF.put(org.nomencurator.model.Rank.SECTION,org.gbif.api.vocabulary.Rank.SECTION);
	toGBIF.put(org.nomencurator.model.Rank.get("subsection"),org.gbif.api.vocabulary.Rank.SUBSECTION);
	toGBIF.put(org.nomencurator.model.Rank.SERIES,org.gbif.api.vocabulary.Rank.SERIES);
	toGBIF.put(org.nomencurator.model.Rank.get("subseries"),org.gbif.api.vocabulary.Rank.SUBSERIES);
	toGBIF.put(org.nomencurator.model.Rank.get("infragenus") ,org.gbif.api.vocabulary.Rank.INFRAGENERIC_NAME);
	toGBIF.put(org.nomencurator.model.Rank.SPECIES,org.gbif.api.vocabulary.Rank.SPECIES);
	toGBIF.put(org.nomencurator.model.Rank.get("aggrigate species"), org.gbif.api.vocabulary.Rank.INFRASPECIFIC_NAME);
	toGBIF.put(org.nomencurator.model.Rank.get("subspecies"), org.gbif.api.vocabulary.Rank.SUBSPECIES);
	toGBIF.put(org.nomencurator.model.Rank.get("infrasubspecies"),org.gbif.api.vocabulary.Rank.INFRASUBSPECIFIC_NAME);
	toGBIF.put(org.nomencurator.model.Rank.VARIETY,org.gbif.api.vocabulary.Rank.VARIETY);
	toGBIF.put(org.nomencurator.model.Rank.get("subvariety"), org.gbif.api.vocabulary.Rank.SUBVARIETY);
	toGBIF.put(org.nomencurator.model.Rank.FORM,org.gbif.api.vocabulary.Rank.FORM);
	toGBIF.put(org.nomencurator.model.Rank.get("subform"), org.gbif.api.vocabulary.Rank.SUBFORM);
	toGBIF.put(org.nomencurator.model.Rank.get("cultiver group"),org.gbif.api.vocabulary.Rank.CULTIVAR_GROUP);
  	toGBIF.put(org.nomencurator.model.Rank.get("cultiver"),org.gbif.api.vocabulary.Rank.CULTIVAR);
	toGBIF.put(org.nomencurator.model.Rank.get("strain"),org.gbif.api.vocabulary.Rank.STRAIN);
	toGBIF.put(org.nomencurator.model.Rank.UNRANKED,org.gbif.api.vocabulary.Rank.UNRANKED);

	for(org.gbif.api.vocabulary.Rank rank : toGBIF.values()) {
	    toGBIFByName.put(rank.getMarker(), rank);
	}
	for(org.nomencurator.model.Rank rank  : toGBIF.keySet()) {
	    fromGBIF.put(toGBIF.get(rank), rank);
	}
    }

    public static org.gbif.api.vocabulary.Rank get(org.nomencurator.model.Rank rank)
    {
	if(rank == null)
	    return org.gbif.api.vocabulary.Rank.UNRANKED;
	return toGBIF.get(rank);
    }

    public static org.gbif.api.vocabulary.Rank get(String rankName)
    {
	if(rankName == null)
	    return org.gbif.api.vocabulary.Rank.UNRANKED;
	return toGBIFByName.get(rankName);
    }

    public static org.nomencurator.model.Rank get(org.gbif.api.vocabulary.Rank rank)
    {
	return fromGBIF.get(rank);
    }
}

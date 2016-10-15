/*
 * SpeciesAPI.java:  an interface definition of SpeciesAPI of GBIF
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

package org.nomencurator.api.gbif;

import java.io.IOException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import org.gbif.api.model.checklistbank.Description;
import org.gbif.api.model.checklistbank.NameUsage;
import org.gbif.api.model.checklistbank.NameUsageMediaObject;
import org.gbif.api.model.checklistbank.Reference;
import org.gbif.api.model.checklistbank.SpeciesProfile;
import org.gbif.api.model.checklistbank.TypeSpecimen;
import org.gbif.api.model.checklistbank.VernacularName;

import org.gbif.api.model.checklistbank.search.NameUsageSearchRequest;
import org.gbif.api.model.checklistbank.search.NameUsageSearchResult;

import org.gbif.api.model.common.MediaObject;

import org.gbif.api.service.checklistbank.NameUsageService;
import org.gbif.api.service.checklistbank.NameUsageMatchingService;
import org.gbif.api.service.checklistbank.NameUsageSearchService;

import org.gbif.api.vocabulary.Rank;

import org.nomencurator.api.gbif.model.checklistbank.Distribution;
import org.nomencurator.api.gbif.model.checklistbank.ParsedName;


/**
 * <CODE>SpeciesAPI</CODE> defines an interface to use GBIF SpeciesAPI.
 *
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
public interface SpeciesAPI extends NameUsageService, NameUsageMatchingService, NameUsageSearchService, NameParserService,  NameUsageExtensionServices {

    /**
     * Returns a List of all name usages across all checklists or specified one.
     *
     * @param locale of the best vernacular name to use for a usage, or null to restrict to scientific name.
     * @param datasetKey dataset checklist key to lmit, or null to be unspecified
     * @param sourceId   source checklist key to limit, or null for any of data sources.
     *
     * @return List of name usages
     */
    public List<NameUsage> list(@Nullable UUID datasetKey, @Nullable String sourceId, @Nullable Locale ... locales) throws IOException;
    
    /**
     * Returns a List of all name usages with a given canonical name across all or specified checklists.
     *
     * @param locale of the best vernacular name to use for a usage, or null to restrict to scientific name.
     * @param canonicalName the canonical name of a name usage.
     * @param datasetKeys   an array of dataset checklist key to lmit, or null to be unspecified
     *
     * @return List of name usages matching the exact canonical name
     */
    public List<NameUsage> listByCanonicalName(String canonicalName, @Nullable List<Locale> locales, @Nullable UUID ... datasetKeys) throws IOException;

    /**
     * Returns a List of  all accepted lower name usages for a given name usages
     *
     * @param parentKey identifies the higher name usage
     * @param locale of the best vernacular name to use for a usage, or null to restrict to scientific name.
     *
     * @return List of lower name usages.
     */
    public List<NameUsage> listChildren(int parentKey, @Nullable Locale ... locales) throws IOException;

    /**
     * Retruns a List of all root name usages for the given checklist, i.e. accepted usages without any higher name usage.
     *
     * @param datasetKey the checklist containig name usages to list
     * @param locale of the best vernacular name to use for a usage, or null to restrict to scientific name.
     *
     * @return List of root name usages
     *
     * @see org.gbif.api.model.Constants#NUB_DATASET_KEY
     */
    public List<NameUsage> listRoot(UUID datasetKey, @Nullable Locale ... locales) throws IOException;

    /**
     * Lists all synonym name usages for a given accepted name usage.
     *
     * @param usageKey identifies a name usage
     * @param locale of the best vernacular name to use for a usage, or null to restrict to scientific name.
     *
     * @return List of synonym name usages.
     */
    public List<NameUsage> listSynonyms(int usageKey, @Nullable Locale ... locales) throws IOException;
    
    /**
     * Issues a SearchRequest and retrieves a response resulting of the search operation.
     * The actual result information will contain a list elements of type T.
     *
     * @param searchRequest the searchRequest that contains the search parameters
     *
     * @return the SearchResponse resulting of the search operation
     */
    public List<NameUsageSearchResult> fullTextSearch(NameUsageSearchRequest searchRequest) throws IOException;

    /**
     * Returns a List of all descriptions for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     *
     * @return List of descriptions
     */
    public List<Description> getDescripitons(int usageKey) throws IOException;

    /**
     * Returns a List of all distributions for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     *
     * @return List of distributions
     */
    public List<Distribution> getDistributions(int usageKey) throws IOException;

    /**
     * Returns a List of all media for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     *
     * @return List of media
     */
    //public List<Image> getImages(int usageKey) throws IOException;
    public List<NameUsageMediaObject> getMedia(int usageKey) throws IOException;

    /**
     * Returns a List of all references for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     *
     * @return List of references
     */
    public List<Reference> getReferences(int usageKey) throws IOException;


    /**
     * Returns a List of all species profiles for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     *
     * @return List of <CODE>SpeciesProfile</CODE>s
     */
    public List<SpeciesProfile> getSpeciesProfiles(int usageKey) throws IOException;

    /**
     * Returns a List of all vernacular names for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     *
     * @return List of <CODE>VernacularName</CODE>s
     */
    public List<VernacularName> getVernacularNames(int usageKey) throws IOException;

    /**
     * Returns a List of all type specimens for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     *
     * @return List of <CODE>TypeSpecimen</CODE>s
     */
    public List<TypeSpecimen> getTypeSpecimens (int usageKey) throws IOException;

    /** 
     * Lists all <tt>NameUsage</tt>s of combinations or names based on the <tt>basionym</tt>, execpt the basionym itself.
     *
     * @param basionym the name usage key of the basionym
     * @param locales an array of locales defining vernacular names, or null to exclude vernacular names.
     *
     * @return List of name usages sharing the same basionym.
     */
    public List<NameUsage> listCombinations(int basionym, Locale ... locales) throws IOException;
}

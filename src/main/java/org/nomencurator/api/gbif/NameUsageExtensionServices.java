/*
 * NameUsageExtensionServices.java:  an interface definition for some of GBIF SpeciesAPI functions
 *
 * Copyright (c) 2014, 2015, 2016 Nozomi `James' Ytow
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

import com.google.common.collect.Multimap;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nullable;

import org.gbif.api.model.checklistbank.Description;
// import org.gbif.api.model.checklistbank.Distribution;
//import org.gbif.api.model.checklistbank.Image;
import org.gbif.api.model.checklistbank.NameUsage;
import org.gbif.api.model.checklistbank.NameUsageMatch;
import org.gbif.api.model.checklistbank.NameUsageMetrics;
import org.gbif.api.model.checklistbank.NameUsageMediaObject;
// import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.api.model.checklistbank.Reference;
import org.gbif.api.model.checklistbank.SpeciesProfile;
import org.gbif.api.model.checklistbank.TypeSpecimen;
import org.gbif.api.model.checklistbank.VernacularName;
import org.gbif.api.model.checklistbank.VerbatimNameUsage;

import org.gbif.api.model.checklistbank.search.NameUsageSearchParameter;
import org.gbif.api.model.checklistbank.search.NameUsageSearchRequest;
import org.gbif.api.model.checklistbank.search.NameUsageSearchResult;
import org.gbif.api.model.checklistbank.search.NameUsageSuggestRequest;

import org.gbif.api.model.common.LinneanClassification;
import org.gbif.api.model.common.MediaObject;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;

import org.gbif.api.model.common.search.Facet;
import org.gbif.api.model.common.search.FacetedSearchRequest;
import org.gbif.api.model.common.search.SearchParameter;
import org.gbif.api.model.common.search.SearchRequest;
import org.gbif.api.model.common.search.SearchResponse;

import org.gbif.api.service.checklistbank.NameUsageService;
import org.gbif.api.service.checklistbank.NameUsageMatchingService;
import org.gbif.api.service.checklistbank.NameUsageSearchService;

/*
import org.gbif.api.vocabulary.IdentifierType;
import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.api.vocabulary.Origin;
*/
import org.gbif.api.vocabulary.Rank;
/*
import org.gbif.api.vocabulary.TaxonomicStatus;
*/

import org.nomencurator.api.gbif.model.checklistbank.Distribution;
import org.nomencurator.api.gbif.model.checklistbank.ParsedName;


/**
 * <CODE>NameUsageExtensionServices</CODE> defines an interface as set of methods extending <CODE>org.gbif.api.service.checklistbank.NameUsageComponentService</CODE>.
 * Interfaces defined by extending <CODE>org.gbif.api.service.checklistbank.NameUsageComponentService</CODE> relevant to Species API shares the same method name and parameters type,
 * it is not possible to provide a imterfaces extending them of which methods are different in types of return value.
 *
 * @version 	19 June 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NameUsageExtensionServices {

    /*
     * Returns all descriptions for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return description pages
     */
    public PagingResponse<Description> getDescripitons(int usageKey, @Nullable Pageable page);

    /*
     * Returns all distributions for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return distribution pages
     */
    public PagingResponse<Distribution> getDistributions(int usageKey, @Nullable Pageable page);

    /*
     * Returns all images for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return image pages
     */
    //public PagingResponse<Image> getImages(int usageKey, @Nullable Pageable page);
    public PagingResponse<NameUsageMediaObject> getMedia(int usageKey, @Nullable Pageable page);

    /*
     * Returns all references for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return reference pages
     */
    public PagingResponse<Reference> getReferences(int usageKey, @Nullable Pageable page);


    /*
     * Returns all species profiles for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return SpeciesProfile pages
     */
    public PagingResponse<SpeciesProfile> getSpeciesProfiles(int usageKey, @Nullable Pageable page);

    /*
     * Returns all vernacular names for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return VernacularName pages
     */
    public PagingResponse<VernacularName> getVernacularNames(int usageKey, @Nullable Pageable page);

    /*
     * Returns all type specimens for a name usage speficied by usasgeKey
     *
     * @param usageKey name usage key
     * @param page paging specifier, could be null
     *
     * @return TypeSpecimen pages
     */
    public PagingResponse<TypeSpecimen> getTypeSpecimens (int usageKey, @Nullable Pageable page);

}

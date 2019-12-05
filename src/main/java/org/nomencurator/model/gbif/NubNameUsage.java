/*
 * NubNameUsage.java: a Java implementation of GBIF CheklistBank
 * NameUsage, or nub
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

package org.nomencurator.model.gbif;

import java.io.IOException;
import java.io.Serializable;

import java.net.URI;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import org.gbif.api.vocabulary.Habitat;
import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.api.vocabulary.Origin;
import org.gbif.api.vocabulary.Rank;
import org.gbif.api.vocabulary.TaxonomicStatus;
import org.gbif.api.vocabulary.TaxonomicStatus.*;

import org.gbif.api.model.checklistbank.Description;
import org.gbif.api.model.checklistbank.NameUsageMatch;
import org.gbif.api.model.checklistbank.VernacularName;

import org.gbif.api.model.checklistbank.search.NameUsageSearchResult;
import org.gbif.api.model.checklistbank.search.NameUsageSuggestResult;

import org.gbif.api.model.common.LinneanClassification;
import org.gbif.api.model.common.LinneanClassificationKeys;

import org.gbif.api.model.registry.Dataset;

import org.gbif.api.service.checklistbank.NameUsageService;

import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.api.vocabulary.Origin;
import org.gbif.api.vocabulary.ThreatStatus;

import org.nomencurator.api.gbif.SpeciesAPIClient;

import org.nomencurator.model.AbstractNameUsage;
import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.Author;
import org.nomencurator.model.Name;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Publication;

import org.nomencurator.util.Locales;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.Getter;

/**
 * {@code NubNameUsage} is an implementation of GBIF CheklistBank NameUsage, or nub.
 *
 * @version 	15 Oct. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NubNameUsage
    extends AbstractNameUsage<NubNameUsage>
{
    private static final long serialVersionUID = 1412309127412404817L;

    @Getter
    private static SpeciesAPIClient dataSource;

    private static String pidPrefix = "urn:lsid:www.gbif.org:species:";

    public static String getPidPrefix()
    {
	return pidPrefix;
    }

    protected String pid;

    /** Species API NameUsage object to be wrapped */
    @Getter
    protected org.gbif.api.model.checklistbank.NameUsage scientificNameUsage;

    /** Species API NameUsageSearchResult object to be wrapped */
    @Getter
    protected NameUsageSearchResult nameUsageSearchResult;

    /** Species API NameUsageSuggestResult object to be wrapped */
    @Getter
    protected NameUsageSuggestResult nameUsageSuggestResult;

    /** Species API NameUsageMatch object to be wrapped */
    @Getter
    protected NameUsageMatch nameUsageMatch;

    /** Dataset containing the NameUsage to be wrapped */
    @Getter
    protected Dataset dataset;

    /** Sub dataset containing the NameUsage to be wrapped */
    @Getter
    protected Dataset constituentDataset;

    // protected Collection<VernacularName> vernacularNames;
    protected Annotation vernacularNames;

    protected Map<Rank, NubNameUsage> path;

    protected NubNameUsage nub;
    protected NubNameUsage higher;
    protected NubNameUsage proParte;
    protected NubNameUsage acceptedNameUasage;
    protected NubNameUsage basionymNameUsage;

    @Override
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final NubNameUsage other = (NubNameUsage) object;
	return super.equals(object)
	    && Objects.equals(this.pid, other.pid)
	    && Objects.equals(this.scientificNameUsage, other.scientificNameUsage)
	    && Objects.equals(this.nameUsageSearchResult, other.nameUsageSearchResult)
	    && Objects.equals(this.nameUsageSuggestResult, other.nameUsageSuggestResult)
	    && Objects.equals(this.nameUsageMatch, other.nameUsageMatch)
	    && Objects.equals(this.vernacularNames, other.vernacularNames)
	    && Objects.equals(this.path, other.path)
	    && Objects.equals(this.nub, other.nub)
	    && Objects.equals(this.higher, other.higher)
	    && Objects.equals(this.proParte, other.proParte)
	    && Objects.equals(this.acceptedNameUasage, other.acceptedNameUasage)
	    && Objects.equals(this.basionymNameUsage, other.basionymNameUsage)
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    pid,
			    scientificNameUsage,
			    nameUsageSearchResult,
			    nameUsageSuggestResult,
			    nameUsageMatch,
			    vernacularNames,
			    path,
			    nub,
			    higher,
			    proParte,
			    acceptedNameUasage,
			    basionymNameUsage
			    );
    }


    public String getPersistentID(String separator, boolean withClassName)
    {
	NameUsage<?> n = getEntity();
	if(n != this) {
	    return n.getPersistentID(separator, withClassName);
	}

	StringBuffer pid = new StringBuffer(pidPrefix);

	if(this.scientificNameUsage != null)
	    pid.append(this.scientificNameUsage.getKey().toString());
	else if (this.nameUsageSearchResult != null)
	    pid.append(this.nameUsageSearchResult.getKey().toString());
	else if (this.nameUsageSuggestResult != null)
	    pid.append(this.nameUsageSuggestResult.getKey().toString());

	return pid.toString();
    }

    public String getLocalKey()
    {
	if(this.scientificNameUsage != null)
	    return scientificNameUsage.getKey().toString();
	else if (this.nameUsageSearchResult != null)
	    return  this.nameUsageSearchResult.getKey().toString();
	else if (this.nameUsageSuggestResult != null)
	    return this.nameUsageSuggestResult.getKey().toString();
	else
	    return "";
    }

    public NubNameUsage create()
    {
	return new NubNameUsage();
    }

    public NubNameUsage(){
	super();
    };

    public NubNameUsage(org.gbif.api.model.checklistbank.NameUsage scientificNameUsage){
	/*
	super();
	setScientificName(scientificNameUsage);
	*/
	this(scientificNameUsage, null, null);
    };

    public NubNameUsage(NameUsageSearchResult nameUsageSearchResult) {
	this(null, nameUsageSearchResult, null);
    }

    public NubNameUsage(NameUsageSuggestResult nameUsageSuggestResult){
	this(null, null,nameUsageSuggestResult);
    }

    public NubNameUsage(org.gbif.api.model.checklistbank.NameUsage scientificNameUsage,
				  NameUsageSearchResult nameUsageSearchResult,
				  NameUsageSuggestResult nameUsageSuggestResult) {
	super();
	if(isConformal(scientificNameUsage, nameUsageSearchResult, nameUsageSuggestResult)) {
	    if(scientificNameUsage != null) {
		setScientificName(scientificNameUsage);
	    }
	    if(nameUsageSearchResult != null) {
		setNameUsageSearchResult(nameUsageSearchResult);
	    }
	    if(nameUsageSuggestResult != null){
		setNameUsageSuggestResult(nameUsageSuggestResult);
	    }
	}
    };

    public NubNameUsage(NameUsage<? > nameUsage) {
	super();
	if(nameUsage == null)
	    return;
	if(nameUsage instanceof NubNameUsage) {
	    NubNameUsage nub = (NubNameUsage)nameUsage;
	    setScientificName(nub.getScientificNameUsage());
	    setNameUsageSearchResult(nub.getNameUsageSearchResult());
	    setNameUsageSuggestResult(nub.getNameUsageSuggestResult());
	}
	else {
	    setLiteral(nameUsage.getLiteral());
	    setRank(nameUsage.getRank());
	    // higher and lowers...
	}

    }

    public boolean setDataset(Dataset dataset)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		return ((NubNameUsage)n).setDataset(dataset);
	    }
	}

	if(dataset != null) {
	    if(scientificNameUsage != null
	       && ! Objects.equals(scientificNameUsage.getDatasetKey(), dataset.getKey())) {
	       return false;
	    }

	    if(nameUsageSearchResult != null
	       && ! Objects.equals(nameUsageSearchResult.getDatasetKey(), dataset.getKey())) {
	       return false;
	    }
	}

	this.dataset = dataset;

	return true;
    }

    public boolean setConstituentDataset(Dataset dataset)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		return ((NubNameUsage)n).setConstituentDataset(dataset);
	    }
	}

	if(dataset != null) {
	    if(scientificNameUsage != null
	       && ! Objects.equals(scientificNameUsage.getConstituentKey(), dataset.getKey())) {
	       return false;
	    }

	    if(nameUsageSearchResult != null
	       && ! Objects.equals(nameUsageSearchResult.getConstituentKey(), dataset.getKey())) {
	       return false;
	    }
	}

	this.constituentDataset = dataset;

	return true;
    }

    public String getDatasetTitle()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		return ((NubNameUsage)n).getDatasetTitle();
	    }
	}

	if(dataset != null) {
	    return dataset.getTitle();
	}
	else {
	    UUID datasetKey = null;
	    org.gbif.api.model.checklistbank.NameUsage scientificNameUsage = getScientificNameUsage();
	    if(scientificNameUsage != null) {
		datasetKey = scientificNameUsage.getDatasetKey();
	    }
	    if(datasetKey == null) {
		NameUsageSearchResult nameUsageSearchResult = getNameUsageSearchResult();
		if(nameUsageSearchResult != null) {
		    datasetKey = nameUsageSearchResult.getDatasetKey();
		}
	    }
	    if(datasetKey != null) {
		return datasetKey.toString();
	    }
	}

	return null;
    }

    protected boolean isConformal(org.gbif.api.model.checklistbank.NameUsage scientificNameUsage,
				  NameUsageSearchResult nameUsageSearchResult,
				  NameUsageSuggestResult nameUsageSuggestResult)
    {
	return isConformal(nameUsageSearchResult, nameUsageSuggestResult) &&
	    isConformal(scientificNameUsage, nameUsageSuggestResult) &&
	    isConformal(scientificNameUsage, nameUsageSearchResult);
    }

    protected boolean isConformal(org.gbif.api.model.checklistbank.NameUsage scientificNameUsage,
				  NameUsageSearchResult nameUsageSearchResult)
    {
	if(scientificNameUsage == null || nameUsageSearchResult == null)
	    return true;
	return scientificNameUsage.getKey().equals(nameUsageSearchResult.getKey());
    }

    protected boolean isConformal(org.gbif.api.model.checklistbank.NameUsage scientificNameUsage,
				  NameUsageSuggestResult nameUsageSuggestResult)
    {
	if(scientificNameUsage == null || nameUsageSuggestResult == null)
	    return true;
	return scientificNameUsage.getKey().equals(nameUsageSuggestResult.getKey());
    }

    protected boolean isConformal(NameUsageSearchResult nameUsageSearchResult,
				  NameUsageSuggestResult nameUsageSuggestResult)
    {
	if(nameUsageSearchResult == null || nameUsageSuggestResult == null)
	    return true;
	return nameUsageSearchResult.getKey().equals(nameUsageSuggestResult.getKey());
    }

    public boolean setScientificName(org.gbif.api.model.checklistbank.NameUsage scientificNameUsage)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		return ((NubNameUsage)n).setScientificName(scientificNameUsage);
	    }
	}

	if(this.scientificNameUsage == scientificNameUsage ||
	   (this.scientificNameUsage != null && scientificNameUsage != null &&
	    this.scientificNameUsage.equals(scientificNameUsage)) ||
	   !isConformal(scientificNameUsage, this.nameUsageSearchResult, this.nameUsageSuggestResult)
	   ){
	    return false;
	}

	setHigherNameUsage(null);
	setLowerNameUsages(null);
	clearScientificName();

	if(scientificNameUsage != null){
	    if(scientificNameUsage.getRank() != null) {
		setRank(RankMap.get(RankMap.get(scientificNameUsage.getRank().getMarker())));
	    }

	    // FIXME 20150730
	    // It dows now work because SpeciesAPI changed to return zero numDescendts
	    // for non-GBIF backborn records, even if it has descandants.
	    /*
	    if(scientificNameUsage.getNumDescendants() == 0)
		this.lowerNameUsages = Collections.emptyList();
	    */
	}

	this.scientificNameUsage = scientificNameUsage;

	return true;
    }

    protected void clearScientificName()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		((NubNameUsage)n).clearScientificName();
	    }
	    return;
	}

	setRank(null);
	scientificNameUsage = null;
    }

    public boolean setNameUsageSearchResult(NameUsageSearchResult nameUsageSearchResult)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		return ((NubNameUsage)n).setNameUsageSearchResult(nameUsageSearchResult);
	    }
	}

	if(this.nameUsageSearchResult == nameUsageSearchResult ||
	   (this.nameUsageSearchResult != null && nameUsageSearchResult != null &&
	    this.nameUsageSearchResult.equals(nameUsageSearchResult)) ||
	   !isConformal(this.scientificNameUsage, nameUsageSearchResult, this.nameUsageSuggestResult)
	   ){
	    return false;
	}

	clearNameUsageSearchResult();
	if(scientificNameUsage == null && nameUsageSearchResult != null) {
	    setRank(RankMap.get(RankMap.get(nameUsageSearchResult.getRank().getMarker())));
	    if(nameUsageSearchResult.getNumDescendants() == 0) {
		setLowerNameUsages(null);
		this.lowerNameUsages = Collections.emptyList();
	    }
	}

	this.nameUsageSearchResult = nameUsageSearchResult;

	return true;
    }

    protected void clearNameUsageSearchResult()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		((NubNameUsage)n).clearNameUsageSearchResult();
	    }
	    return;
	}

	if(scientificNameUsage == null && nameUsageSuggestResult == null) {
	    setRank(null);
	}

	nameUsageSearchResult = null;
    }

    public boolean setNameUsageSuggestResult(NameUsageSuggestResult nameUsageSuggestResult)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		return ((NubNameUsage)n).setNameUsageSuggestResult(nameUsageSuggestResult);
	    }
	}

	if(this.nameUsageSuggestResult == nameUsageSuggestResult ||
	   (this.nameUsageSuggestResult != null && nameUsageSuggestResult != null &&
	    this.nameUsageSuggestResult.equals(nameUsageSuggestResult)) ||
	   !isConformal(this.scientificNameUsage, this.nameUsageSearchResult, nameUsageSuggestResult)
	   ){
	    return false;
	}

	clearNameUsageSuggestResult();
	if(scientificNameUsage == null && nameUsageSearchResult == null && nameUsageSuggestResult != null) {
	    setRank(RankMap.get(RankMap.get(nameUsageSuggestResult.getRank().getMarker())));
	}
	this.nameUsageSuggestResult = nameUsageSuggestResult;

	return true;
    }

    protected void clearNameUsageSuggestResult()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		((NubNameUsage)n).clearNameUsageSuggestResult();
	    }
	    return;
	}

	if(scientificNameUsage == null && nameUsageSearchResult == null) {
	    setRank(null);
	}

	nameUsageSuggestResult = null;
    }

    public String getLiteral()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getLiteral();
	}

	String cannonicalName = getCanonicalOrScientificName();

	return (cannonicalName == null) ? super.getLiteral() : cannonicalName;
    }

    public String getCanonicalOrScientificName()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getCanonicalOrScientificName();
	}

	if(scientificNameUsage != null)
	    return scientificNameUsage.getCanonicalOrScientificName();
	else if (nameUsageSearchResult != null) {
	    String cannonicalName = nameUsageSearchResult.getCanonicalName();

	    return (cannonicalName == null) ? nameUsageSearchResult.getScientificName() : cannonicalName;
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getCanonicalName();
	}
	else
	    return null;
    }

    public void setLiteral(String literal)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    ((NubNameUsage)n).setLiteral(literal);
	    return;
	}

	if(scientificNameUsage != null) {
	    scientificNameUsage.setCanonicalName(literal);
	}
	else if (nameUsageSearchResult != null) {
	    nameUsageSearchResult.setCanonicalName(literal);
	}
	else if (nameUsageSuggestResult != null) {
	    nameUsageSuggestResult.setCanonicalName(literal);
	}
	else {
	    super.setLiteral(literal);
	}
    }

    protected NubNameUsage createNameUsage()
    {
	return new NubNameUsage();
    }

    protected NubNameUsage createNameUsage(Name<?> nameUsage)
    {
	if(nameUsage instanceof org.gbif.api.model.checklistbank.NameUsage)
	    return new NubNameUsage((org.gbif.api.model.checklistbank.NameUsage)nameUsage);
	else if(nameUsage instanceof NubNameUsage) {
	    NubNameUsage nub = (NubNameUsage)nameUsage;
	    return new NubNameUsage(nub.getScientificNameUsage(),
				    nub.getNameUsageSearchResult(),
				    nub.getNameUsageSuggestResult());
	}
	else
	    return createNameUsage();
    }

    protected NubNameUsage createNameUsage(NameUsage<?> nameUsage)
    {
	NubNameUsage nub = null;
	if(nameUsage instanceof org.gbif.api.model.checklistbank.NameUsage)
	    nub = new NubNameUsage((org.gbif.api.model.checklistbank.NameUsage)nameUsage);
	else if(nameUsage instanceof NubNameUsage) {
	    nub = (NubNameUsage)nameUsage;
	    nub = new NubNameUsage(nub.getScientificNameUsage(),
				    nub.getNameUsageSearchResult(),
				    nub.getNameUsageSuggestResult());
	}
	else {
	    nub = new NubNameUsage(nameUsage);
	}
	return nub;
    }

    protected NubNameUsage createNameUsage(String persistentID)
    {
	NubNameUsage usage = null;
	if(dataSource != null) {
	    try {
		usage = new NubNameUsage(dataSource.get(Integer.valueOf(persistentID)));
	    }
	    catch (MalformedURLException e) {
	    }
	    catch (IOException e) {
	    }
	}
	else
	    usage = createNameUsage();

	return usage;
    }

    public Boolean isExtinct()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).isExtinct();
	}
	if(nameUsageSearchResult != null) {
	    return nameUsageSearchResult.isExtinct();
	}
	return null;
    }

    public List<Habitat> getHabitats()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getHabitats();
	}
	if(nameUsageSearchResult != null) {
	    return nameUsageSearchResult.getHabitats();
	}
	return null;
    }

    /*
    public List<NomenclaturalStatus> getNomenclaturalStatus()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getNomenclaturalStatus();
	}
	if(nameUsageSearchResult != null) {
	    return nameUsageSearchResult.getNomenclaturalStatus();
	}
	return null;
    }
    */

    public List<ThreatStatus> getThreatStatuses()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getThreatStatuses();
	}
	if(nameUsageSearchResult != null) {
	    return nameUsageSearchResult.getThreatStatuses();
	}
	return null;
    }

    public List<Description> getDescriptions()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getDescriptions();
	}
	if(nameUsageSearchResult != null) {
	    return nameUsageSearchResult.getDescriptions();
	}
	return null;
    }

    public List<VernacularName> getVernacularNames()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getVernacularNames();
	}
	if(nameUsageSearchResult != null) {
	    return nameUsageSearchResult.getVernacularNames();
	}
	return null;
    }

    public LinkedHashMap<Integer, String> getHigherClassificationMap()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getHigherClassificationMap();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getHigherClassificationMap();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getHigherClassificationMap();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getHigherClassificationMap();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getClazz()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getClazz();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getClazz();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getClazz();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getClazz();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getFamily()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getFamily();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getFamily();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getFamily();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getFamily();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getGenus()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getGenus();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getGenus();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getGenus();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getGenus();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getKingdom()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getKingdom();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getKingdom();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getKingdom();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getKingdom();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getOrder()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getOrder();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getOrder();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getOrder();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getOrder();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getPhylum()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getPhylum();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getPhylum();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getPhylum();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getPhylum();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getSpecies()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getSpecies();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getSpecies();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getSpecies();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getSpecies();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getSubgenus()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getSubgenus();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getSubgenus();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getSubgenus();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getSubgenus();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    String getHigherRank(Rank rank)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getHigherRank(rank);
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getHigherRank(rank);
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getHigherRank(rank);
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getHigherRank(rank);
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getClassKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getClassKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getClassKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getClassKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getClassKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getFamilyKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getFamilyKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getFamilyKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getFamilyKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getFamilyKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getGenusKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getGenusKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getGenusKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getGenusKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getGenusKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getKingdomKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getKingdomKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getKingdomKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getKingdomKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getKingdomKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getOrderKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getOrderKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getOrderKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getOrderKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getOrderKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getPhylumKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getPhylumKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getPhylumKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getPhylumKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getPhylumKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getSpeciesKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getSpeciesKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getSpeciesKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getSpeciesKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getSpeciesKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getSubgenusKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getSubgenusKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getSubgenusKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getSubgenusKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getSubgenusKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    Integer getHigherRankKey(Rank rank)
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getHigherRankKey(rank);
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getHigherRankKey(rank);
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getHigherRankKey(rank);
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getHigherRankKey(rank);
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public Integer getSourceTaxonKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getSourceTaxonKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getSourceTaxonKey();
	}
	/*
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getSourceTaxonKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getSourceTaxonKey();
	}
	*/
	else {
	    return null;
	}
    }

    public String getAccepted()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getAccepted();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getAccepted();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getAccepted();
	}
	else {
	    return null;
	}
    }

    public Integer getAcceptedKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getAcceptedKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getAcceptedKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getAcceptedKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public String getAccordingTo()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getAccordingTo();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getAccordingTo();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getAccordingTo();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public String getAuthorship()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getAuthorship();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getAuthorship();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getAuthorship();
	}
	else {
	    return null;
	}
    }

    public String getBasionym()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getBasionym();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getBasionym();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getBasionym();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public Integer getBasionymKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getBasionymKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getBasionymKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getBasionymKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public String getCanonicalName()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getCanonicalName();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getCanonicalName();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getCanonicalName();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getCanonicalName();
	}
	else {
	    return null;
	}
    }

    //@NotNull
    public UUID getDatasetKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getDatasetKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getDatasetKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getDatasetKey();
	}
	else {
	    return null;
	}
    }

    //@NotNull
    public Integer getKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getKey();
	}
	else {
	    return null;
	}
    }

    public NameType getNameType()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getNameType();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getNameType();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getNameType();
	}
	else {
	    return null;
	}
    }

    public Collection<NomenclaturalStatus> getNomenclaturalStatus()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getNomenclaturalStatus();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getNomenclaturalStatus();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getNomenclaturalStatus();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public Integer getNubKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getNubKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getNubKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getNubKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getNubKey();
	}
	else {
	    return null;
	}
    }

    public Integer getNumDescendants()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getNumDescendants();
	}
	if(scientificNameUsage != null) {
	    return Integer.valueOf(scientificNameUsage.getNumDescendants());
	}
	else if (nameUsageSearchResult != null) {
	    return  Integer.valueOf(nameUsageSearchResult.getNumDescendants());
	}
	else {
	    return null;
	}
    }

    //@NotNull
    public Origin getOrigin()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getOrigin();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getOrigin();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getOrigin();
	}
	else {
	    return null;
	}
    }

    public String getParent()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getParent();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getParent();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getParent();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getParent();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public Integer getParentKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getParentKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getParentKey();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getParentKey();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getParentKey();
	}
	else {
	    return null;
	}
    }

    public Integer getProParteKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getProParteKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getProParteKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public String getPublishedIn()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getPublishedIn();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getPublishedIn();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getPublishedIn();
	}
	else {
	    return null;
	}
    }

    //@NotNull
    public String getScientificName()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getScientificName();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getScientificName();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getScientificName();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public UUID getConstituentKey()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getConstituentKey();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getConstituentKey();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public String getVernacularName()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getVernacularName();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getVernacularName();
	}
	else {
	    return null;
	}
    }

    public String getRemarks()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getRemarks();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getRemarks();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public URI getReferences()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getReferences();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getReferences();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public String getTaxonID()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getTaxonID();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getTaxonID();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getTaxonID();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public TaxonomicStatus getTaxonomicStatus()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getTaxonomicStatus();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getTaxonomicStatus();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getTaxonomicStatus();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public Date getModified()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getModified();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getModified();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public Date getLastCrawled()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getLastCrawled();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getLastCrawled();
	}
	else {
	    return null;
	}
    }

    //@Nullable
    public Date getLastInterpreted()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getLastInterpreted();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getLastInterpreted();
	}
	else {
	    return null;
	}
    }

    //@NotNull
    /*
    public Set<NameUsageIssue> getIssues()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).getIssues();
	}
	if(scientificNameUsage != null) {
	    return scientificNameUsage.getIssues();
	}
	else if (nameUsageSearchResult != null) {
	    return  nameUsageSearchResult.getIssues();
	}
	else if (nameUsageSuggestResult != null) {
	    return nameUsageSuggestResult.getIssues();
	}
	else {
	    return null;
	}
    }
    */


    public Boolean isNub()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).isNub();
	}
	if(scientificNameUsage != null) {
	    return Boolean.valueOf(scientificNameUsage.isNub());
	}
	else if (nameUsageSearchResult != null) {
	    return  Boolean.valueOf(nameUsageSearchResult.getKey().equals(nameUsageSearchResult.getNubKey()));
	}
	else if (nameUsageSuggestResult != null) {
	    return  Boolean.valueOf(nameUsageSuggestResult.getKey().equals(nameUsageSuggestResult.getNubKey()));
	}
	else {
	    return null;
	}
    }

    public Boolean isProParte()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).isProParte();
	}
	if(scientificNameUsage != null) {
	    return Boolean.valueOf(scientificNameUsage.isProParte());
	}
	else {
	    return null;
	}
    }

    public Boolean isSynonym()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this && n instanceof NubNameUsage) {
	    return ((NubNameUsage)n).isSynonym();
	}
	if(scientificNameUsage != null) {
	    return Boolean.valueOf(scientificNameUsage.isSynonym());
	}
	else if (nameUsageSearchResult != null) {
	    return Boolean.valueOf(nameUsageSearchResult.isSynonym());
	}
	else {
	    return null;
	}
    }

    public Boolean synonym(final Name<?> name)
    {
	if (name != null &&  (name instanceof NubNameUsage))
	    return ((NubNameUsage)name).isSynonymOf(this);

	return super.synonym(name);
    }

    public Boolean synonymOf(final NameUsage<?> nameUsage)
    {
	Boolean synonymy = super.synonym(nameUsage);

	if(synonymy != null
	   && !synonymy
	   && nameUsage != null
	   && (nameUsage instanceof NubNameUsage)) {

	    final Integer acceptedKey = getAcceptedKey();
	    if(acceptedKey != null && acceptedKey.equals(Integer.valueOf(nameUsage.getLocalKey())))
		synonymy = Boolean.TRUE;
	}
	return synonymy;
    }

    public String getViewName()
    {
	NameUsage<?> n = getNameUsage();
	if(n != this) {
	    return n.getViewName();
	}

	/*
	if(viewName == null || viewName.length() == 0)
	    viewName = getDatasetTitle();
	*/


	String viewName = super.getViewName();
	if(viewName == null || viewName.length() == 0)
	    viewName = getAccordingTo();

	if(viewName == null || viewName.length() == 0)
	    viewName = getDatasetTitle();

	return viewName;
    }

    public Integer getDescendantCount()
    {
	NameUsage<?> n = getNameUsage();
	if(n != this) {
	    return n.getDescendantCount();
	}

	if(scientificNameUsage != null)
	    return scientificNameUsage.getNumDescendants();

	return super.getDescendantCount();

    }

    public String getAuthority()
    {
	NameUsage<?> n = getNameUsage();
	if(n != this) {
	    return n.getAuthority();
	}

	String authority = super.getAuthority();
	if((authority == null || authority.length() == 0)
	   && scientificNameUsage != null)
	    authority = scientificNameUsage.getAuthorship();

	return authority;
    }

    public Integer getAuthorityYear()
    {
	List<Integer> years = new ArrayList<>();
	String[] phrases = getAuthority().split(",");

	for(String phrase : phrases) {
	    try{
		years.add(Integer.parseInt(phrase.trim()));
	    } catch (NumberFormatException e) {
	    }
	}
	if(years.isEmpty()) {
	    for(String phrase : phrases) {
		String[] words = phrase.trim().split(" ");
		for(String word : words) {
		    try{
			years.add(Integer.parseInt(word.trim()));
		    } catch (NumberFormatException e) {
		    }
		}
	    }
	}
	if(years.isEmpty())
	    return null;

	return years.get(0);
    }


    public String getSummary()
    {
	NameUsage<?> n = getNameUsage();
	if(n != this) {
	    return n.getSummary();
	}

	StringBuffer summary = new StringBuffer(super.getSummary());
	if(scientificNameUsage != null) {
	    String authorship = scientificNameUsage.getAuthorship();
	    // summary.append("\n");
	    // summary.append("rank: ").append(scientificNameUsage.getRank());
	    if(authorship != null && authorship.length() > 0) {
		summary.append("\n");
		summary.append("authorship: ").append(authorship);
	    }
	    summary.append("\n");
	    summary.append("key: ").append(scientificNameUsage.getKey());
	    summary.append("\n");
	    summary.append("nubKey: ").append(scientificNameUsage.getNubKey());
	    String taxonKey = getTaxonID();
	    if (taxonKey != null) {
		summary.append("\n");
		summary.append("taxonKey: ").append(taxonKey);
	    }
	    TaxonomicStatus status = /*scientificNameUsage.*/getTaxonomicStatus();
	    if(status != null) {
		summary.append("\n");
		summary.append("taxonomic status: ").append(status.toString().toLowerCase().replace('_', ' '));
		if(status.isSynonym() || scientificNameUsage.isSynonym()) {
		    Integer acceptedKey = scientificNameUsage.getAcceptedKey();
		    if(acceptedKey != null) {
			summary.append("\n");
			summary.append("accepted: ").append(scientificNameUsage.getAccepted());
			summary.append("\n");
			summary.append("acceptedKey: ").append(acceptedKey);
		    }
		} else {
		    Integer basionymKey = scientificNameUsage.getBasionymKey();
		    if(basionymKey != null) {
			summary.append("\n");
			summary.append("basionym: ").append(scientificNameUsage.getBasionym());
		    summary.append("\n");
		    summary.append("basionymKey: ").append(basionymKey);
		    }
		}
	    }
	    else if(scientificNameUsage.isSynonym()) {
		summary.append("\n");
		summary.append("synonym");
		Integer acceptedKey = scientificNameUsage.getAcceptedKey();
		if(acceptedKey != null) {
		    summary.append("\n");
		    summary.append("accepted: ").append(scientificNameUsage.getAccepted());
		    summary.append("\n");
		    summary.append("acceptedKey: ").append(acceptedKey);
		}
	    }
	    summary.append("\n");
	    summary.append("number of descendent name usages: ").append(scientificNameUsage.getNumDescendants());

	    Origin origin = scientificNameUsage.getOrigin();
	    if(origin != null) {
		    summary.append("\n");
		    summary.append("origin of data: ").append(origin.toString().toLowerCase().replace('_', ' '));
	    }
	    String str = scientificNameUsage.getRemarks();
	    if(str != null) {
		    summary.append("\n");
		    summary.append("remark: ").append(str);
	    }
	    str = scientificNameUsage.getPublishedIn();
	    if(str != null) {
		    summary.append("\n");
		    summary.append("published in: ").append(str);
	    }
	    str = scientificNameUsage.getAccordingTo();
	    if(str != null) {
		    summary.append("\n");
		    summary.append("according to: ").append(str);
	    }
	    if(dataset == null) {
		    summary.append("\n");
		    summary.append("dataset UUID: ").append(scientificNameUsage.getDatasetKey());
	    }
	}
	else if (nameUsageSearchResult != null) {
	    summary.append("\n");
	    summary.append("key: ").append(nameUsageSearchResult.getKey());
	    summary.append("\n");
	    summary.append("nubKey: ").append(nameUsageSearchResult.getNubKey());
	    TaxonomicStatus status = nameUsageSearchResult.getTaxonomicStatus();
	    summary.append("\n");
	    summary.append("taxonomic status: ").append(status.toString().toLowerCase().replace('_', ' '));
	    if(status.isSynonym() || nameUsageSearchResult.isSynonym()) {
		Integer acceptedKey = nameUsageSearchResult.getAcceptedKey();
		if(acceptedKey != null) {
		    summary.append("\n");
		    summary.append("accepted: ").append(nameUsageSearchResult.getAccepted());
		    summary.append("\n");
		    summary.append("acceptedKey: ").append(acceptedKey);
		}
	    } else {
		Integer basionymKey = nameUsageSearchResult.getBasionymKey();
		if(basionymKey != null) {
		    summary.append("\n");
		    summary.append("basionym: ").append(nameUsageSearchResult.getBasionym());
		    summary.append("\n");
		    summary.append("basionymKey: ").append(basionymKey);
		}
	    }
	    summary.append("\n");
	    summary.append("nubmer of dicendent name usages: ").append(nameUsageSearchResult.getNumDescendants());

	    Origin origin = nameUsageSearchResult.getOrigin();
	    if(origin != null) {
		    summary.append("\n");
		    summary.append("origin of data: ").append(origin.toString().toLowerCase().replace('_', ' '));
	    }
	    if(dataset == null) {
		    summary.append("\n");
		    summary.append("dataset UUID: ").append(nameUsageSearchResult.getDatasetKey());
	    }
	}
	else if (nameUsageSuggestResult != null) {
	    summary.append("\n");
	    summary.append("key: ").append(nameUsageSuggestResult.getKey());
	    summary.append("\n");
	    summary.append("nubKey: ").append(nameUsageSuggestResult.getNubKey());
	    Integer parentKey = nameUsageSuggestResult.getParentKey();
	    if(parentKey != null) {
		summary.append("\n");
		summary.append("parent: ").append(nameUsageSuggestResult.getParent());
		summary.append("\n");
		summary.append("parentKey: ").append(parentKey);
	    }
	}
	if(dataset != null) {
	    summary.append("\n");
	    summary.append("dataset: ").append(dataset.getTitle());
	    summary.append("\n");
	    summary.append("dataset UUID: ").append(dataset.getKey());
	}

	return summary.toString();
    }

    public boolean isImplicit()
    {
	NameUsage<? extends NubNameUsage> n = getNameUsage();
	if(n != this) {
	    if(n instanceof NubNameUsage) {
		return ((NubNameUsage)n).isImplicit();
	    }
	}

	org.gbif.api.model.checklistbank.NameUsage scientificNameUsage = getScientificNameUsage();
	if (scientificNameUsage == null)
	    return super.isImplicit();

	return Origin.SOURCE != scientificNameUsage.getOrigin();
    }
}

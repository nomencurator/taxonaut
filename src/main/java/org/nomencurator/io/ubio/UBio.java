/*
 * UBio.java: communicate with a uBio SOPA server
 *
 * Copyright (c) 2006, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071, JP19K12711
 */

package org.nomencurator.io.ubio;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

// import org.apache.axis.AxisFault;
import org.apache.axis2.AxisFault;

import org.nomencurator.io.AbstractNameUsageExchanger;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.MatchingMode.*;
import org.nomencurator.io.QueryMode;
import org.nomencurator.io.QueryMode.*;
import org.nomencurator.io.QueryParameter;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

import org.nomencurator.model.ubio.UBioNameUsageNode;

import org.nomencurator.util.XMLEntityReference;

import org.ubio.io.ClassificationBankSearchResult;
import org.ubio.io.NamebankSearchResult;

import org.ubio.io.xml.XMLWebService;

import org.ubio.model.Citation;
import org.ubio.model.ClassificationBankObject;
import org.ubio.model.ClassificationData;
import org.ubio.model.ClassificationNode;
import org.ubio.model.JuniorName;
import org.ubio.model.NamebankObject;
import org.ubio.model.RecordedName;
import org.ubio.model.ScientificName;
import org.ubio.model.SeniorName;
import org.ubio.model.Synonym;
import org.ubio.model.VernacularName;

import org.ubio.util.Base64Codex;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code UBio} provides a mapping between Nomencurator data and a uBio SOAP server
 *
 * @version 	03 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class UBio
    extends AbstractNameUsageExchanger<UBioNameUsageNode>
{
    @Getter
    @Setter
    protected XMLWebService xmlWebService;

    public final String PUBLICATION_PREFIX = "urn:lsid:ubio.org:publicationbank:";

    public UBio(String keyCode)
    {
	super();
	try {
	    setXmlWebService(new XMLWebService(keyCode));
	}
	catch (Throwable e) {
	}
    }

    public NamebankObject namebankObject(int namebankID)
    {
	return xmlWebService.namebankObject(namebankID);
    }

    public UBioNameUsageNode uBioNameUsageNode(int namebankID)
    {
	return (UBioNameUsageNode)createNameUsageNode(xmlWebService.namebankObject(namebankID));
    }

    //public Collection<NamedObject<UBioNameUsageNode, UBioNameUsageNode>> getObjects(String localKey, MatchingMode matchingMode)
    public Collection<UBioNameUsageNode> getObjects(String localKey, MatchingMode matchingMode)
    {
	//Collection<NamedObject<UBioNameUsageNode, UBioNameUsageNode>> results = null;
	Collection<UBioNameUsageNode> results = null;
	int namebankID = getNamebankID(localKey);
	if(namebankID == 0)
	    return null;
	UBioNameUsageNode node =
	    (UBioNameUsageNode)createNameUsageNode(xmlWebService.namebankObject(namebankID));
	node.setObjectExchanger(this);
	if(node != null) {
	    //results = new ArrayList<NamedObject<UBioNameUsageNode, UBioNameUsageNode>>(1);
	    results = new ArrayList<UBioNameUsageNode>(1);
	    results.add(node);
	}
	return results;
    }

    protected Collection<NameUsage<UBioNameUsageNode>> getExactNameUsages(String literal, Rank rank, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return getNameUsages(literal, rank, MatchingMode.EXACT, includeBasionyms, includeSynonyms, includeVernaculars, locale);
    }

    
    public Collection<NameUsage<UBioNameUsageNode>>getNameUsages(String literal, Rank rank, MatchingMode matchingMode, boolean includeBasionyms, boolean includeSynonyms, boolean includeVernaculars, Locale locale)
    {
	return getNameUsages(new UBioQueryParameter(literal, rank, matchingMode));
    }

    public Collection<NameUsage<UBioNameUsageNode>>getNameUsages(UBioQueryParameter parameter)
    {
	if(parameter == null)
	    return null;

	//AllNames names = uBio.namebankSearch(parameter.getLiteral());
	NamebankSearchResult names = xmlWebService.namebankSearch(parameter.getLiteral());
	if(names == null)
	    return null;

	List<ScientificName> scientificNames = names.getScientificNames();
	List<VernacularName> vernacularNames = names.getVernacularNames();
	if(scientificNames == null && vernacularNames == null)
	    return null;

	Collection<UBioNameUsageNode> uv = new ArrayList<UBioNameUsageNode>();
	HashMap<String, UBioNameUsageNode> seniorHash = new HashMap<String, UBioNameUsageNode>();
	Collection<UBioNameUsageNode> juniorVector = new ArrayList<UBioNameUsageNode>();
	/*
	int nameCount = 0;
	if((matchingMode & QUERY_TYPE_MASK) == CONTAINS) {
	    if(rank == null) {
		if(scientificNames != null)
		    nameCount += scientificNames.length;
		if(vernacularNames != null)
		    nameCount += vernacularNames.length;
	    }
	    else {
	    }
	}
	*/

	int i = 0;

	//both ScientificName and VernacularName represent
	//NamebankObject (as subset)
	if(scientificNames != null &&  !parameter.isExcludeScientificNames()) {
	    for(ScientificName sName : scientificNames) {

		int namebankID = sName.getNamebankID();
		//String r = sName.getRankName().toLowerCase().replace("-", "");
		String r = normalize(sName.getRankName());

		String rank = parameter.getRank().getName();
		if(rank != null && rank.length() > 0 && !rank.equals(r))
		    continue;

		//String literal = XMLEntityReference.decode(Base64Codex.decode(sName.getNameString()));
		String literal = XMLEntityReference.decode(XMLWebService.decode(sName.getNameString()));
		//String literal = sName.getNameString();
		if((parameter.getMatchingMode() == MatchingMode.EXACT) && !parameter.getLiteral().equals(literal))
		    continue;

		UBioNameUsageNode u = 
		    new UBioNameUsageNode();
		u.setObjectExchanger(this);
		u.setNamebankID(namebankID);

		//uv.add(u);

		//u.setNameObject(u);
		u.setLiteral(literal);
		u.setRank(Rank.get(r));
		String authority = 
		    //		    XMLEntityReference.decode(Base64Codex.decode(sName.getFullNameString()));
		    XMLEntityReference.decode(XMLWebService.decode(sName.getFullNameString()));
		if(authority != null) {
		    if(authority.startsWith(literal))
		       authority = authority.substring(literal.length()).trim();
		    if(authority.length() > 0) {
			u.setAuthority(authority);
		    }
		}
		ClassificationBankSearchResult hierarchies = 
		    xmlWebService.classificationBankSearch(namebankID);
		List<SeniorName> seniors = hierarchies.getSeniorNames();
		if(seniors != null) {
		    for(SeniorName senior: seniors) {
			UBioNameUsageNode un =
			    new UBioNameUsageNode();
			un.setObjectExchanger(this);
			uv.add(un);
			un.setNameObject(u);
			un.setNominal(false);
			int cbID = senior.getClassificationBankID();
			un.setClassificationBankID(cbID);
			seniorHash.put(String.valueOf(cbID), un);

			String classificationTitle =
			    XMLEntityReference.decode(senior.getClassificationTitle());
			    //Base64Codex.decode(senior.getClassificationTitle());
			un.setViewName(classificationTitle);
			un.setClassificationTitleID(senior.getClassificationTitleID());
			un.setRank(Rank.get(r));
			un.setLiteral(literal);
			if(authority != null) {
			    un.setAuthority(authority);
			}
			Publication p = new Publication();
			p.setCitationTitle(classificationTitle);
			un.setPublication(p);
		    }
		}
		List<JuniorName> juniors = hierarchies.getJuniorNames();
		if(juniors != null) {

		    for(JuniorName junior: juniors) {
			UBioNameUsageNode jn = 
			    new UBioNameUsageNode();
			jn.setObjectExchanger(this);
			uv.add(jn);
			juniorVector.add(jn);
			jn.setNominal(false);
			jn.setNameObject(u);
			jn.setNamebankID(u.getNamebankID());
			jn.setLiteral(literal);
			jn.setRank(Rank.get(r));
			jn.setViewName(junior.getClassificationTitle());
			jn.setClassificationTitleID(junior.getClassificationTitleID());
			jn.setSeniorClassificationBankID(junior.getSeniorClassificationBankID());
			Publication p = new Publication();
			p.setCitationTitle(junior.getClassificationTitle());
			jn.setPublication(p);

		    }
		}
	    }
	    for(UBioNameUsageNode junior : juniorVector) {
		UBioNameUsageNode senior = 
		    seniorHash.get(String.valueOf(junior.getSeniorClassificationBankID()));
		if(senior != null) {
		    junior.setHomotypicSeniorSynonym(senior);
		    Annotation annotation = new Annotation();
		    annotation.setLinkType("synonym");
		    annotation.addAnnotator(senior);
		    senior.addRelevantAnnotation(annotation);
		    annotation.addAnnotatant(junior);
		    junior.addRelevantAnnotation(annotation);
		}
		else {
		    /*
		    ClassificationBankObject cls =
			classificationBankObject(junior.getSeniorClassificationBankID, 0, 0, 0, 0);
		    */
		}

	    }
	    juniorVector.clear();
	}
	seniorHash.clear();

	if(vernacularNames != null && !parameter.isExcludeVernacularNames()) {
	    for(VernacularName vName : vernacularNames) {
		UBioNameUsageNode u =
		    new UBioNameUsageNode(vName.getNamebankID());
		u.setObjectExchanger(this);
		uv.add(u);
		//u.setLiteral(XMLEntityReference.decode(Base64Codex.decode(vName.getNameString())));
		u.setLiteral(XMLEntityReference.decode(XMLWebService.decode(vName.getNameString())));
		//u.setLiteral(vName.getNameString());
	    }
	}
	
	if(uv.isEmpty())
	    return null;

	Collection<NameUsage<UBioNameUsageNode>> toReturn = new ArrayList<>(uv.size());
	for (UBioNameUsageNode result : uv)
	    toReturn.add((NameUsage<UBioNameUsageNode>)result);
	uv.clear();

	return toReturn;
    }

    public UBioNameUsageNode getHierarchy(UBioNameUsageNode nameUsage)
    {
	/*
	NameUsage root = getRootPath(nameUsage);
	//setLowerNameUsages(root, FULL_DEPTH);
	setLowerNameUsages(nameUsage, FULL_DEPTH);
	return root;
	*/
	return getPartialHierarchy(nameUsage, FULL_HEIGHT, FULL_DEPTH);
    }

    public List<UBioNameUsageNode> getLowerNameUsages(UBioNameUsageNode nameUsage, int depth)
    {
	if(nameUsage == null)
	    return null;

	UBioNameUsageNode un = 
	    (UBioNameUsageNode)nameUsage.getHomotypicSeniorSynonym();
	if(un == null)
	    un = nameUsage;

	ClassificationBankObject hierarchy = 
	    xmlWebService.classificationBankObject(un.getClassificationBankID(), 1, 1, 1, 1);

	
	if(hierarchy == null)
	    return new ArrayList<UBioNameUsageNode>();

	ClassificationData classificationData = hierarchy.getClassificationData();
	nameUsage.setClassificationBankObject(hierarchy);
	
	Publication p = nameUsage.getPublication();
	if(p == null) {
	    p = new Publication();
	    nameUsage.setPublication(p);
	}
	p.setCitationTitle(classificationData.getClassificationTitle());
	p.setNotes(classificationData.getClassificationDescription());

	List<Synonym> synonyms = hierarchy.getSynonyms();
	if(synonyms != null) {
	    for(Synonym synonym: synonyms) {
		int synID = synonym.getNamebankID();
		String synName = synonym.getNameString();
		if(synName != null)
		    synName = XMLEntityReference.decode(synName);
		String synAuth = synonym.getAuthorString();
		if(synAuth != null)
		    synAuth = XMLEntityReference.decode(XMLWebService.decode(synonym.getAuthorString()));
		List<Citation> citations = synonym.getCitations();
	    }
	}

	UBioNameUsageNode[] lowerTaxa = null;
	List<ClassificationNode> children = hierarchy.getChildren();
	return getLowerNameUsages(children, depth, null, p);
    }

    //public UBioNameUsageNode getHierarchy(UBioNameUsageNode nameUsage, int height, int depth)
    public UBioNameUsageNode getPartialHierarchy(UBioNameUsageNode node, int height, int depth)
    {
	ClassificationBankObject hierarchy = null;
	ClassificationData classificationData = null;
	if(node instanceof UBioNameUsageNode) {
	    UBioNameUsageNode uNode = node;
	    UBioNameUsageNode homotypic =
		(UBioNameUsageNode)uNode.getHomotypicSeniorSynonym();
	    if(homotypic != null) {
		node = homotypic;
	    }
	    else {
		int seniorID = 
		    uNode.getSeniorClassificationBankID();
		if(seniorID != UBioNameUsageNode.INVALID_ID) {
		    homotypic = new UBioNameUsageNode();
		    homotypic.setObjectExchanger(this);
		    homotypic.setClassificationBankID(seniorID) ;
		    hierarchy = 
			xmlWebService.classificationBankObject(seniorID, 1, 1, 1, 1);
		    if(hierarchy != null) {
			homotypic.setClassificationBankObject(hierarchy);
			classificationData =
			    hierarchy.getClassificationData();
			uNode.setHomotypicSeniorSynonym(homotypic);
			Annotation annotation = new Annotation();
			annotation.setLinkType("synonym");
			annotation.addAnnotator(homotypic);
			homotypic.addRelevantAnnotation(annotation);
			annotation.addAnnotatant(uNode);
			uNode.addRelevantAnnotation(annotation);

			RecordedName rn = hierarchy.getRecordedName();
			uNode = new UBioNameUsageNode();
			uNode.setObjectExchanger(this);
			uNode.setNamebankID(rn.getNamebankID());
			String literal = XMLEntityReference.decode(XMLWebService.decode(rn.getNameString()));
			//String literal = rn.getNameString();
			uNode.setLiteral(literal);
			String authority = rn.getFullNameString();
			if(authority != null && literal != null) {
			    authority = XMLEntityReference.decode(XMLWebService.decode(authority).substring(literal.length()).trim());
			}
			uNode.setAuthority(authority);
			String rank = normalize(hierarchy.getRankName());
			uNode.setRank(Rank.get(rank));
			homotypic.setNameObject(uNode);

			homotypic.setRank(Rank.get(rank));
			homotypic.setLiteral(literal);
			homotypic.setAuthority(authority);
			ClassificationData cd =
			    hierarchy.getClassificationData();
			homotypic.setClassificationTitleID(cd.getClassificationTitleID());
			homotypic.setViewName(XMLEntityReference.decode(cd.getClassificationTitle()));

			node = homotypic;

	List<Synonym> synonyms = hierarchy.getSynonyms();
	if(synonyms != null) {
	    for(Synonym synonym: synonyms) {
		int synID = synonym.getNamebankID();
		String synName = synonym.getNameString();
		if(synName != null)
		    synName = XMLEntityReference.decode(XMLWebService.decode(synName));
		String synAuth = synonym.getAuthorString();
		if(synAuth != null)
		    synAuth = XMLEntityReference.decode(XMLWebService.decode(synonym.getAuthorString()));
		List<Citation> citations = synonym.getCitations();
	    }
	}

		    }
		}
	    }
	}

	if(hierarchy == null) {
	    hierarchy = 
		classificationBankObject(node);
	}

	node.setClassificationBankObject(hierarchy);

	if(hierarchy == null) {
	    return null;
	}

	classificationData =
	    hierarchy.getClassificationData();

	node.setLowerNameUsages(getLowerNameUsages(node, depth));

	UBioNameUsageNode rootNode = 
	    (UBioNameUsageNode)getRootPath(node, hierarchy, height);

	List<Synonym> synonyms = hierarchy.getSynonyms();
	if(synonyms != null) {
	    for(Synonym synonym: synonyms) {
		int synID = synonym.getNamebankID();
		String synName = synonym.getNameString();
		if(synName != null)
		    synName = XMLEntityReference.decode(XMLWebService.decode(synName));
		String synAuth = synonym.getAuthorString();
		if(synAuth != null)
		    synAuth = XMLEntityReference.decode(XMLWebService.decode(synonym.getAuthorString()));
		List<Citation> citations = synonym.getCitations();
	    }
	}

	/*

	List<ClassificationNode> children = hierarchy.getChildren();
	
	if(children != null) {
	    NameUsage[] lowerTaxa = new UBioNameUsageNode[children.length];
	    int i = 0;
	    for(Child lowerTaxon: children) {
		UBioNameUsageNode taxon = 
		    new UBioNameUsageNode(lowerTaxon.getNamebankID());
		taxon.setObjectExchanger(this);
		lowerTaxa[i++] = taxon;
		String str =lowerTaxon.getNameString();
		if(str != null)
		    taxon.setLiteral(Base64Codex.decode(str));
		taxon.setRank(lowerTaxon.getRankName());

		str =lowerTaxon.getAuthorString();
		if(str != null) {
		    taxon.setAuthority(Base64Codex.decode(str));
		}
		//int classificationID = lowerTaxon.getClassificationBankID();
	    }
	    node.setLowerNameUsages(lowerTaxa);
	}
	else {
	}
	*/

	return rootNode;
    }

    protected String normalize(String rank)
    {
	return rank.toLowerCase().replace("-","");
	/*
	if(rank == null)
	    return rank;

	rank = rank.toLowerCase();
	Rank r = Rank.get(rank);
	if(r != null) 
	    rank = r.getName();
	else {
	    String[] parts = rank.split("-");
	    if(parts.length < 2) {
		if(rank.startsWith("super")) {
		    parts = new String[2];
		    parts[0] = rank.substring(0, 5);
		    parts[1] = rank.substring(5, rank.length());
		}
		else if(rank.startsWith("sub")) {
		    parts = new String[2];
		    parts[0] = rank.substring(0, 3);
		    parts[1] = rank.substring(3, rank.length());
		}
	    }
	    if(parts.length > 1) {
		r = Rank.get(parts[1]);
		if(r != null) {
		    if(parts[0].equals("super"))
			parts[0] = "sup";
		    rank = parts[0] + r.getName();
		}
		else
		    rank = parts[0] + parts[1];
	    }
	}

	return rank;
	*/
    }


    public static NameUsageNode<?> createNameUsageNode(NamebankObject object)
    {
       return setValue(object, null);
    }

    public static NameUsageNode<?> setValue(NamebankObject object, UBioNameUsageNode node)
    {
	if(object == null)
	    return null;

	if(node == null) {
	    node = new UBioNameUsageNode();
	}

	node.setValues(object);

	List<Citation> citations = object.getCitations();

	return node;
    }

    public Locale getLocale(String language)
    {
	return null;
    }

    public static int getNamebankID(String persistentID)
    {
	if(persistentID == null ||
	   !(persistentID.startsWith(UBioNameUsageNode.NAMEBANK_PREFIX)))
	    return 0;

	return Integer.valueOf(persistentID.substring(UBioNameUsageNode.NAMEBANK_PREFIX.length())).intValue();
    }

    public static String getLSID(NamebankObject object)
    {
	return getLSID(object.getNamebankID());
    }

    public static String getLSID(int namebankID)
    {
	return new StringBuffer(UBioNameUsageNode.NAMEBANK_PREFIX).append(namebankID).toString();
    }

    public Collection<NameUsage<UBioNameUsageNode>>getRoots(String name)
	throws IOException
    {
	return getRoots(getNameUsages(getObjects(name)));
    }

    public Collection<NameUsage<UBioNameUsageNode>>getRoots(UBioNameUsageNode[] nameUsages)
    {
	List<NameUsage<UBioNameUsageNode>> roots = new ArrayList<>();
	for(UBioNameUsageNode nameUsage: nameUsages) {
	    roots.add(getRoot(nameUsage));
	}
	return roots;
		  
    }

    public UBioNameUsageNode getRoot(UBioNameUsageNode nameUsage)
    {
	return getRoot(nameUsage, classificationBankObject(nameUsage));
    }

    protected ClassificationBankObject classificationBankObject(UBioNameUsageNode node)
    {
	if(node == null || 
	   !(node instanceof UBioNameUsageNode))
	    return null;

	UBioNameUsageNode uNode = node;

	if(uNode == uNode.getNameObject()) {
	    return null;// FIXME
	}

	uNode = (UBioNameUsageNode)uNode.getHomotypicSeniorSynonym();
	if(uNode == null)
	    uNode = node;

	return xmlWebService.classificationBankObject(uNode.getClassificationBankID());
    }

    protected NameUsage<?> getRootPath(UBioNameUsageNode nameUsage, 
				    ClassificationBankObject hierarchy)
    {
	return getRootPath(nameUsage, hierarchy, NameUsageExchanger.FULL_HEIGHT);
    }

    protected NameUsage<?> getRootPath(UBioNameUsageNode nameUsage, 
				    ClassificationBankObject hierarchy,
				    int height)
    {
	return getRootPath(nameUsage, hierarchy, height, true);
    }

    protected NameUsage<?> getRootPath(UBioNameUsageNode nameUsage, 
				    ClassificationBankObject hierarchy,
				    boolean expand)
    {
	return getRootPath(nameUsage, hierarchy, NameUsageExchanger.FULL_HEIGHT, expand);
    }

    protected NameUsage<?> getRootPath(UBioNameUsageNode nameUsage, 
				    ClassificationBankObject hierarchy,
				    int height,
				    boolean expand)
    {
	if(hierarchy == null)
	    return null;

	nameUsage.setClassificationBankObject(hierarchy);

	String authority = null;
	if(nameUsage != null)
	    authority = nameUsage.getAuthority();

	UBioNameUsageNode node = nameUsage;
	RecordedName recordedName = hierarchy.getRecordedName();
	String nameString = XMLEntityReference.decode(recordedName.getNameString());
	if(!nameString.equals(node.getLiteral())) {
	    setValue(xmlWebService.namebankObject(recordedName.getNamebankID()), node);
	}
	String viewName = nameUsage.getViewName();
        List<ClassificationNode> higherTaxa = hierarchy.getAncestry();
	if(higherTaxa != null) {
	    if(height == -1)
		height = higherTaxa.size();
	    for(int i = 0; i < height; i++) {
		ClassificationNode higherTaxon = higherTaxa.get(i);
		int id = higherTaxon.getClassificationBankID();
		UBioNameUsageNode taxon = 
		    new UBioNameUsageNode(UBioNameUsageNode.getClassificationBankID(id));
		taxon.setObjectExchanger(this);
		taxon.setViewName(viewName);
		taxon.setLiteral(XMLEntityReference.decode(higherTaxon.getNameString()));
		String str = normalize(higherTaxon.getRankName());
		if(str != null)
		    taxon.setRank(Rank.get(str));
		String author = higherTaxon.getAuthorString();
		if(author != null) {
		    taxon.setAuthority(Base64Codex.decode(author));
		}
		else if (authority != null)
		    taxon.setAuthority(authority);


		taxon.setClassificationBankID(id);
		List<UBioNameUsageNode> lowerTaxa = null;
		Publication p = nameUsage.getPublication();
		if(p == null)
		    p = new Publication();
		if(expand) {
		    ClassificationBankObject bankObject =
			xmlWebService.classificationBankObject(id, 1, 0, 0, 0);
		    if(bankObject != null) {
			taxon.setClassificationBankObject(bankObject);
			ClassificationData classificationData =
			    bankObject.getClassificationData();
			p.setCitationTitle(XMLEntityReference.decode(classificationData.getClassificationTitle()));
			p.setNotes(XMLEntityReference.decode(classificationData.getClassificationDescription()));
	
			List<ClassificationNode> children = bankObject.getChildren();
			if(children != null) {
			    lowerTaxa = getLowerNameUsages(children, 1, node, p);
			}
		    }
			
		}
		if(lowerTaxa != null)
		    taxon.setLowerNameUsages(lowerTaxa);
		else
		    node.setHigherNameUsage(taxon);

		node = taxon;
	    }
	}
	return node;
    }

    protected List<UBioNameUsageNode> getLowerNameUsages(List<ClassificationNode> children,
						       int depth,
						       UBioNameUsageNode toExclude,
						       //String viewName)
						       Publication p)
    {
	if(children == null)
	    return null;

	List<UBioNameUsageNode> lowerTaxa = new ArrayList<>();

	int i = 0;
	int exclude = -1;
	if(toExclude != null)
	    exclude = toExclude.getClassificationBankID();
	UBioNameUsageNode taxon = null;
	for(ClassificationNode child: children) {
	    int id = child.getClassificationBankID();
	    if(id == exclude) {
		taxon = toExclude;
	    }
	    else {
		taxon = new UBioNameUsageNode(id);
		taxon.setObjectExchanger(this);
	    }

	    lowerTaxa.add(taxon);
	    i++;

	    //taxon.setViewName(viewName);
	    taxon.setPublication(p);

	    String str = child.getNameString();
	    if(str != null)
		//taxon.setLiteral(Base64Codex.decode(str));
		taxon.setLiteral(XMLEntityReference.decode(str));
	    str = normalize(child.getRankName());
	    if(str != null) 
		taxon.setRank(Rank.get(str));

	    str =child.getAuthorString();
	    if(str != null) {
		//taxon.setAuthority(XMLEntityReference.decode(Base64Codex.decode(str)));
		taxon.setAuthority(XMLEntityReference.decode(XMLWebService.decode(str)));
	    }
	    taxon.setClassificationBankID(id);

	    if(depth > 0)
		depth--;
	    if(depth != 0) {
		ClassificationBankObject bankObject =
		    //uBio.classificationBankObject(id, 1, 0, 0, 0);
		    xmlWebService.classificationBankObject(id, 1, 0, 0, 0);
		if(bankObject != null) {
		    taxon.setClassificationBankObject(bankObject);
		    ClassificationData classificationData =
			    bankObject.getClassificationData();
		    List<ClassificationNode> grandChildren = 
			bankObject.getChildren();
		    if(grandChildren == null) {
			List<UBioNameUsageNode> emptyList = Collections.emptyList();
			//taxon.setLowerNameUsages(Collections.synchronizedList(new ArrayList<UBioNameUsageNode>(0)));
			taxon.setLowerNameUsages(emptyList);
		    }
		    else
			//taxon.setLowerNameUsages(getLowerTaxa(grandChildren, depth, null, viewName));
			taxon.setLowerNameUsages(getLowerNameUsages(grandChildren, depth, null, p));
		    //taxon.setLowerNameUsagesResolved(true);
		}
		else {

		}
	    }
	    
	}
	
	return lowerTaxa;
    }

    protected UBioNameUsageNode getRoot(UBioNameUsageNode nameUsage, 
				ClassificationBankObject hierarchy)
    {
	if(hierarchy == null)
	    return null;

	String authority = null;
	if(nameUsage != null)
	    authority = nameUsage.getAuthority();
	UBioNameUsageNode taxon = null;
        List<ClassificationNode> higherTaxa = hierarchy.getAncestry();
	if(higherTaxa != null && !higherTaxa.isEmpty()) {
	    ClassificationNode higherTaxon = higherTaxa.get(higherTaxa.size() - 1);
	    taxon = 
		new UBioNameUsageNode(higherTaxon.getNamebankID());
	    taxon.setObjectExchanger(this);
	    //taxon.setLiteral(XMLEntityReference.decode(Base64Codex.decode(higherTaxon.getNameString())));
	    taxon.setLiteral(XMLEntityReference.decode(XMLWebService.decode(higherTaxon.getNameString())));
	    //taxon.setLiteral(higherTaxon.getNameString());
	    String str = normalize(higherTaxon.getRankName());
	    if(str != null)
		taxon.setRank(Rank.get(str));

	    str = higherTaxon.getAuthorString();
	    if(str != null) {
		//taxon.setAuthority(XMLEntityReference.decode(Base64Codex.decode(str)));
		taxon.setAuthority(XMLEntityReference.decode(XMLWebService.decode(str)));
	    }
	    else if (authority != null)
		taxon.setAuthority(authority);
	    taxon.setClassificationBankID(higherTaxon.getClassificationBankID());
	}
	return taxon;
    }

    public static Publication set(Citation citation, Publication publication)
    {
	if(publication == null)
	    publication = new Publication();
	//publication.setObjectExchanger(this);
	//	publication.setAuthorNames(XMLEntityReference.decode(Base64Codex.decode(citation.getAuthor())));
	publication.setAuthorNames(XMLEntityReference.decode(XMLWebService.decode(citation.getAuthor())));
	publication.setYear(String.valueOf(citation.getPublicationYear()));
	String contentsTitle = 
	    XMLEntityReference.decode(XMLWebService.decode(citation.getArticleTitle()));
	publication.setContentsTitle(contentsTitle);
	
	String citationTitle =
	    XMLEntityReference.decode(XMLWebService.decode(citation.getPublicationTitle())).trim();
	if(citationTitle.endsWith(".")) {
	    citationTitle = 
		citationTitle.substring(0, citationTitle.length() - 1);
	}

	String[] elements = citationTitle.split(";");

	if(elements.length > 1) {
	    citationTitle = elements[0].trim();
	    String[] vnp = elements[elements.length - 1].split(":");
	    if(vnp.length > 1) {
		String[] pages = vnp[vnp.length - 1].split("-");
		if(pages.length > 1) {
		    if(pages[0].length() > pages[1].length()) {
			pages[1] = 
			    pages[0].substring(0, pages[0].length() - pages[1].length()) +
			    pages[1];
		    }
		    publication.setFirstPage(pages[0]);
		    publication.setLastPage(pages[1]);
		}
		else {
		    publication.setFirstPage(vnp[vnp.length - 1]);
		    publication.setLastPage(vnp[vnp.length - 1]);
		}
		
	    }
	    String[] volume = 
		vnp[0].split("\\([0-9]+\\)");
	    if(volume.length > 0) {
		publication.setVolume(volume[0]);
		if(volume[0].length() < vnp[0].length()) {
		    publication.setVolume(volume[0]);
		    publication.setIssue(vnp[0].substring(volume[0].length() + 1, vnp[0].length() - 1));
		}
	    }
	    else {

	    }

	    
	    if(citationTitle.endsWith(".")) {
		citationTitle = 
		    citationTitle.substring(0, citationTitle.length() - 1);
	    }
	    else {
		int fullStop = citationTitle.lastIndexOf(".");
		if(fullStop > 0) {
		    String tail =
			citationTitle.substring(fullStop + 1, citationTitle.length() - 1).trim();
		    citationTitle =
			citationTitle.substring(0, fullStop);
		    if(tail.startsWith(publication.getYear())) {
			tail = 
			    tail.substring(publication.getYear().length() + 1, tail.length() - 1).trim();
		    }
		}
	    }
	}
	

	publication.setCitationTitle(citationTitle);

	return publication;
    }

    protected void resolveHigherNameUsages(NameUsage<UBioNameUsageNode> nameUsage)
    {
	//fixme
    }

    /**
     * Resolve lower {@code NameUsage}s  just under the  {@code nameUsage}.
     * It is expected to override depending on  data sources.
     *
     * @param nameUsage of interest
     */
    protected void resolveLowerNameUsages(NameUsage<UBioNameUsageNode> nameUsage)
    {
	// fixme
    }

    /**
     * Returns null since it is not supported.
     *
     */
    @Override
    public Collection<NameUsage<UBioNameUsageNode>>getRelevantNameUsages(NameUsage<UBioNameUsageNode> nameUsage) {
	return null;
    }

    /**
     * Clears object cache if the <code>Exchanger</code> has.
     */
    @Override
    public void clear()
    {
    }

    /*
    public Collection<NameUsage<UBioNameUsageNode>> integrateHierarchies(Collection<? extends NameUsage<UBioNameUsageNode>> nameUsages)
    {
	return super.integrateHierarchies(nameUsages);
    }
    */
}

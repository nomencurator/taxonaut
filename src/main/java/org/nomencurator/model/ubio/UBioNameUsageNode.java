/*
 * UBioNameUsageNode.java:  a Java implementation of NameUsageNode class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2006, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model.ubio;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.NameUsageExchanger;

import org.nomencurator.io.ubio.UBio;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.Name;
import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.AbstractNameUsage;
import org.nomencurator.model.AbstractNameUsageNode;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

import org.ubio.model.Citation;
import org.ubio.model.ClassificationBankObject;
import org.ubio.model.ClassificationData;
import org.ubio.model.ClassificationNode;
import org.ubio.model.Justification;
import org.ubio.model.NamebankObject;
import org.ubio.model.RecordedName;
import org.ubio.model.ServiceData;
import org.ubio.model.Synonym;
import org.ubio.model.VernacularName;

import org.ubio.util.Base64Codex;

import org.w3c.dom.Element;

/**
 * An implementation of {@code NameUsageNode} in Nomencurator
 * data model to handle uBio NamebankObject and ClassificationBankObject.
 *
 * uBio datamodel has two type of name relevant objects, {@code NamebankObject}
 * and {@code ClassificationBankObject}.  Each object has ID number unique in
 * an uBio data source; no object shares the same number as their ID.  A 
 * {@code NamebankObject} is a representation of a name string, while a
 * {@code ClassificationBankObject} is a usage of a name in a classification.
 * A {@code ClassificationBankObject} has a NamebankObjectID representing the
 * name string of the usage of a name in a classification, and a ClassificationID
 * representing the classification.  Junior synonyms in a classification are, however,
 * not treated as a {@code ClassificationBankObject} while it is a {@code NameUsage}
 * in the Nomencurator model.
 *
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class UBioNameUsageNode
    extends AbstractNameUsageNode<UBioNameUsageNode, UBioNameUsageNode>
{
    private static final long serialVersionUID = 8438841604594238724L;

    public static final String SCIENTIFIC_NAME_LANG = "sci";

    public static final String NAMEBANK_PREFIX = "urn:lsid:ubio.org:namebank:";
    public static final String CLASSIFICATIONBANK_PREFIX = "urn:lsid:ubio.org:classificationbank:";
    public static final int INVALID_ID = 0;

    protected int parentID;

    protected String nameBankID;

    protected int namebankID;

    protected int classificationBankID;

    protected int classificationTitleID;

    protected String classificationTitle;

    protected UBioNameUsageNode namebankObject;

    protected UBioNameUsageNode seniorNamebankObject;

    protected int seniorNamebankID;

    protected int seniorClassificationBankID;

    protected boolean nominal;

    protected Publication[] citations;
    
    protected UBio uBio;
    
    protected ClassificationBankObject classificationBankObject;

    protected NameUsage<?, ?> homotypicSeniorSynonym;

    public UBioNameUsageNode create()
    {
	return new UBioNameUsageNode();
    }

    /** Constructs an "empty" <code>NameUsageNode</code> */
    public UBioNameUsageNode()
    {
	super();
	nominal = true;
	setEditable(false);
    }

    /**
     * Constructs a <COCE>NameUsageNode} using
     * existing {@code NameUsage}
     *
     * @param nameUsage existing {@code NameUsage}
     * to be pointed by this object
     */
    public UBioNameUsageNode(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	super(nameUsage);
	nominal = false;
	setEditable(false);
	setContentsResolved(false);
    }

    /**
     * Constructs a <code>NameUsageNode</code> object having
     * <code>persistentID</code> as its representation,
     */
    public UBioNameUsageNode(String persistentID)
    {
	//fix me
	super(persistentID);
	nominal = true;
	setEditable(false);
	resolved = false;
	setContentsResolved(false);
    }

    /**
     * Constructs an "empty" <code>NameUsageNode</code> representing
     * a uBio Namebank object of {@code namebankID}
     */
    public UBioNameUsageNode(int namebankID)
    {
	this(new StringBuffer(NAMEBANK_PREFIX).append(namebankID).toString());
	setEditable(false);
	setContentsResolved(false);
    }

    /** Constructs an "empty" <code>NameUsageNode</code> */
    public UBioNameUsageNode(NamebankObject namebankObject)
    {
	super();
	setValues(namebankObject);
	setEditable(false);
	setContentsResolved(false);
    }

    public ClassificationBankObject getClassificationBankObject()
    {
	return classificationBankObject;
    }

    public void setClassificationBankObject(ClassificationBankObject uBioObject)
    {
	classificationBankObject = uBioObject;
    }

    public void setParentID(int id)
    {
	parentID = id;
    }

    public int getParentID()
    {
	return parentID;
    }

    public boolean isNominal()
    {
	return nominal;
    }

    public void setNominal(boolean nominal)
    {
	this.nominal = nominal;
    }

    /**
     * Returns {@code String} representing the name used
     *
     * @return {@code String} representing the name used
     */ 
    public String getLiteral()
    {
	NameUsage<?, ?> n = getNameUsage();
	if(n != this) {
	    return n.getLiteral();
	}

	UBioNameUsageNode nameObject = getNameObject();
	
	if(nameObject != null && 
	   nameObject != this) {
	    return nameObject.getLiteral();
	}

	//return super.getLiteral();
	return nameLiteral;
    }


    /**
     * Returns {@code String} representing rank name
     *
     * @return {@code String} representing rank name
     */ 
    public Rank/*String*/ getRank()
    {
	NameUsage<?, ?> n = getNameUsage();
	if(n != this)
	    return n.getRank();

	UBioNameUsageNode nameObject = getNameObject();
	
	if(nameObject != null && 
	   nameObject != this)
	    return nameObject.getRank();

	//return super.getRank();
	return rank;
    }

    public Locale getLocale()
    {
	NameUsage<?, ?> n = getNameUsage();
	if(n != this)
	    return n.getLocale();

	UBioNameUsageNode nameObject = getNameObject();
	
	if(nameObject != null && 
	   nameObject != this)
	    return nameObject.getLocale();

	return super.getLocale();
    }

    public void setValues(NamebankObject namebankObject)
    {
	if(namebankObject == null)
	    return;

	nominal = false;
	setPersistentID(NAMEBANK_PREFIX+ namebankObject.getNamebankID());
	String str = Base64Codex.decode(namebankObject.getNameString());
	setLiteral(Base64Codex.decode(namebankObject.getNameString()));
	//setLiteral(str);
	nameLiteral = str;
	String fullName = Base64Codex.decode(namebankObject.getFullNameString());
	if(fullName != null &&
	   !fullName.equals(getLiteral())) {
	    setAuthority(fullName.substring(getLiteral().length() + 1));
	}
	setRank(Rank.get(namebankObject.getRankName().toLowerCase()));
	parentID = namebankObject.getParentID();
	setLocale(Locale.forLanguageTag(namebankObject.getLanguageCode()));
	List<Citation>  citations = namebankObject.getCitations();
	Publication publication = null;
	if(citations != null) {
	    if(citations.size() == 1) {
		publication = createPublication(citations.get(0));
	    }
	    else {
	    }
	}
	if(publication != null)
	    setPublication(publication);
	resolved = true;
    }

    public NameUsage<UBioNameUsageNode, UBioNameUsageNode> getHigherNameUsage()
    {
	NameUsage<UBioNameUsageNode, UBioNameUsageNode> n = getNameUsage();
	if(n != this)
	    return n.getHigherNameUsage();

	if(higherNameUsage == null &&
	   parentID != 0 &&  uBio != null) {
	    setHigherNameUsage(uBio.uBioNameUsageNode(parentID));
	    //setHigherNameUsage(new UBioNameUsageNode(uBio.namebankObject(parentID)));
	}
	
	return higherNameUsage;
    }

    
    public NameUsage<?, ?> getHigherNameUsage(UBio uBio)
	//public NameUsage<?, ?> getHigherNameUsage(UBio uBio, boolean b)
    {
	//setUBio(uBio);
	//setObjectExchanger(uBio);
	return getHigherNameUsage();
	/*
	NameUsage<?, ?> n = getNameUsage();
	if(n != this)
	    return n.getHigherNameUsage();

	if(higherNameUsage == null &&
	   parentID != 0 &&  uBio != null) {
	    setHigherNameUsage(new UBioNameUsageNode(uBio.getNamebankClient().namebankObject(parentID)));
	}
	
	return higherNameUsage;
	*/
    }

    protected UBioNameUsageNode createNameUsageNode()
    {
	return new UBioNameUsageNode();
    }

    protected UBioNameUsageNode createNameUsageNode(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	return new UBioNameUsageNode(nameUsage);
    }

    protected UBioNameUsageNode createNameUsageNode(String persistentID)
    {
	return new UBioNameUsageNode(persistentID);
    }

    /*
    protected AbstractNameUsage<UBioNameUsageNode, UBioNameUsageNode> createNameUsage()
    {
	return (NameUsage<UBioNameUsageNode, UBioNameUsageNode>)createNameUsageNode();
    }
    */

    /*
    protected NameUsage<UBioNameUsageNode, UBioNameUsageNode> createNameUsage(Name<?, ?> nameUsage)
    {
	if(nameUsage instanceof NameUsage)
	    return (NameUsage<UBioNameUsageNode, UBioNameUsageNode>)createNameUsageNode(getNameUsage(nameUsage));

	return null;
    }

    protected NameUsage<UBioNameUsageNode, UBioNameUsageNode> createNameUsage(NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>> nameUsage)
    {
	    return (NameUsage<UBioNameUsageNode, UBioNameUsageNode>)createNameUsageNode(nameUsage);
    }


    protected NameUsage<UBioNameUsageNode, UBioNameUsageNode> createNameUsage(String persistentID)
    {
	return (NameUsage<UBioNameUsageNode, UBioNameUsageNode>)createNameUsageNode(persistentID);
    }
    */

    protected boolean isA(String objectName)
    {
	if(objectName != null &&
	   objectName.startsWith(NAMEBANK_PREFIX)) {
	    return true;
	}

	return super.isA(objectName);
    }

    /**
     * Returns persistent ID representing this {@code NamedObject}.
     *
     * @return String representing persistent ID of this {@code NamedObject}.
     */
    public void setPersistentID(String pid)
    {
	nameBankID = pid;
    }

    /*
    public String getPersistentID()
    {
	return nameBankID;
    }
    */

    public void setNameObject(UBioNameUsageNode namebankObject)
    {
	this.namebankObject = namebankObject;
    }

    public UBioNameUsageNode getNameObject()
    {
	return namebankObject;
    }

    public void setSeniorNamebankID(int namebankID)
    {
	seniorNamebankID = namebankID;
    }

    public int getSeniorNamebankID()
    {
	return seniorNamebankID;
    }

    public int getNamebankID()
    {
	return namebankID;
    }

    public void setNamebankID(int namebankID)
    {
	NameUsage<?, ?> n = getNameUsage();
	if(n != this &&
	   n instanceof UBioNameUsageNode) {
	    ((UBioNameUsageNode)n).setNamebankID(namebankID);
	}
	else
	    this.namebankID = namebankID;
    }

    public int getClassificationBankID()
    {
	return classificationBankID;
    }

    public void setClassificationBankID(int classificationBankID)
    {
	/*
	NameUsage<?, ?> n = getNameUsage();
	if(n != null && n != this &&
	   n instanceof UBioNameUsageNode) {
	    ((UBioNameUsageNode)n).setClassificationBankID(classificationBankID);
	}
	else
	*/
	    this.classificationBankID = classificationBankID;
    }
    public int getSeniorClassificationBankID()
    {
	return seniorClassificationBankID;
    }

    public void setSeniorClassificationBankID(int classificationBankID)
    {
	/*
	NameUsage<?, ?> n = getNameUsage();
	if(n != this &&
	   n instanceof UBioNameUsageNode) {
	    ((UBioNameUsageNode)n).setSeniorClassificationBankID(classificationBankID);
	}
	else
	*/
	    this.seniorClassificationBankID = classificationBankID;
    }

    public int getClassificationTitleID()
    {
	return classificationTitleID;
    }

    public void setClassificationTitleID(int classificationTitleID)
    {
	NameUsage<?, ?> n = getNameUsage();
	if(n != this &&
	   n instanceof UBioNameUsageNode) {
	    ((UBioNameUsageNode)n).setClassificationBankID(classificationTitleID);
	}
	else
	    this.classificationTitleID = classificationTitleID;
    }

    public void setViewName(String viewName)
    {
	classificationTitle = viewName;
    }

    public String getViewName()
    {
	if(classificationTitle != null)
	    return classificationTitle;
	if(getNameObject() == this)
	    return getPersistentID();

	return super.getViewName();
    }

    /*
    public Object getEntity()
    {
	if(namebankObject != null)
	    return namebankObject;

	return super.getEntity();
    }
    */

    public Publication createPublication(Citation citation)
    {
	if(citation == null)
	    return null;

	Publication p = new Publication();
	String s = Base64Codex.decode(citation.getAuthor());
	if(s != null) {
	    p.setAuthorNames(s);
	    //add author analysis here....
	}
	p.setYear(String.valueOf(citation.getPublicationYear()));
	s = Base64Codex.decode(citation.getArticleTitle());
	if(s != null)
	    p.setContentsTitle(s);
	s = Base64Codex.decode(citation.getPublicationTitle());
	if(s != null)
	    p.setCitationTitle(s);
	return p;
    }

    public static String getNamebankID(int id)
    {
	return new StringBuffer(NAMEBANK_PREFIX).append(id).toString();
    }

    public static String getClassificationBankID(int id)
    {
	return new StringBuffer(CLASSIFICATIONBANK_PREFIX).append(id).toString();
    }

    public String getPersistentID()
    {
	int id = getClassificationBankID();
	UBioNameUsageNode senior = 
	    (UBioNameUsageNode)getHomotypicSeniorSynonym();
	if(id != INVALID_ID &&
	   (senior == null || senior == this)) {
	    return new StringBuffer(CLASSIFICATIONBANK_PREFIX).append(id).toString();
	}

	id = getNamebankID();
	int seniorID = getSeniorClassificationBankID();
	if(seniorID != INVALID_ID ||
	   (senior != null && senior != this)) {
	    //return new StringBuffer(CLASSIFICATIONBANK_PREFIX).append(senior.getClassificationBankID()).append('_').append(id).toString();
	    return new StringBuffer(CLASSIFICATIONBANK_PREFIX).append(seniorID).append('_').append(id).toString();
	}

	return new StringBuffer(NAMEBANK_PREFIX).append(id).toString();
    }

    public String getPersistentID(String separator, boolean withClassName)
    {
	return getPersistentID();
    }

    public String getSummary()
    {
	if(entity != null)
	    return ((NamedObject)getEntity()).getSummary();

	if(classificationBankObject == null)
	    return super.getSummary();
	StringBuffer buf = new StringBuffer();
	ServiceData service = classificationBankObject.getServiceData();
	if(service != null) {
	    buf.append("version: ").append(service.getCurrentVersion()).append('\n');
	    buf.append("Accessed at ").append(service.getDateStamp());
	    buf.append(" ").append(service.getTimeStamp()).append('\n');
	}

	RecordedName rname = classificationBankObject.getRecordedName();
	if(rname != null) {
	    buf.append("Namebank ID: ").append(rname.getNamebankID()).append('\n');
	    buf.append("Name string: ").append(Base64Codex.decode(rname.getNameString())).append('\n');
	    buf.append("Full name string: ").append(Base64Codex.decode(rname.getFullNameString())).append('\n');
	}

	buf.append("Classifications ID: ").append(classificationBankObject.getClassificationsID()).append('\n');

	buf.append("Rank: ").append(classificationBankObject.getRankName());
	buf.append(" (ID: ").append(classificationBankObject.getRankID()).append(")\n");

	ClassificationData cdata = classificationBankObject.getClassificationData();
	if(cdata != null) {
	    buf.append("Classification: ").append(Base64Codex.decode(cdata.getClassificationTitle()));
	    buf.append(" (ID: ").append(cdata.getClassificationTitleID());
	    buf.append(" parent: ").append(cdata.getClassificationTitleIDParent());
	    buf.append(" root: ").append(cdata.getClassificationRoot()).append(")\n");
	    buf.append("\t").append(Base64Codex.decode(cdata.getClassificationDescription())).append(")\n");
	}

	List<Synonym> synonyms = classificationBankObject.getSynonyms();
	if(synonyms != null &&
	   !synonyms.isEmpty()) {
	    int i = 0;
	    for(Synonym synonym : synonyms) {
		if(i > 0) {
		    buf.append("\n");
		}
		buf.append("syn[").append(i++).append("] ");
		buf.append(synonym.getNamebankID()).append(": ");
		buf.append(Base64Codex.decode(synonym.getNameString())).append(" ");
		buf.append(Base64Codex.decode(synonym.getAuthorString()));
		List<Citation> citations = synonym.getCitations();
		if(citations != null && !citations.isEmpty()) {
		    buf.append(" in \n");
		    int j = 0;
		    for(Citation citation : citations) {
			buf.append("\t").append(j++).append(". ");
			buf.append(Base64Codex.decode(citation.getAuthor())).append(" ");
			buf.append(citation.getPublicationYear()).append(". ");
			buf.append(Base64Codex.decode(citation.getArticleTitle())).append(". ");
			buf.append(Base64Codex.decode(citation.getPublicationTitle())).append("\n");
		    }
		}
		else
			buf.append("\n");
	    }
	}

	List<VernacularName> vernacularNames = 
	    classificationBankObject.getVernacularNames();
	if(vernacularNames != null && !vernacularNames.isEmpty()) {
	    int i = 0;
	    for(VernacularName vernacularName : vernacularNames) {
		if(i > 0) {
		    buf.append("\n");
		}
		buf.append("vernacular[").append(i++).append("] ");
		buf.append(vernacularName.getNamebankID()).append(": ");
		buf.append(Base64Codex.decode(vernacularName.getNameString())).append(" (");
		buf.append(vernacularName.getLanguageName()).append(") in ");
		buf.append(vernacularName.getPackageID()).append(": ");
		buf.append(vernacularName.getPackageName()).append("/");
		buf.append(vernacularName.getBasionymUnit()).append(":");
		buf.append(vernacularName.getNamebankIDLink()).append(" ");
		buf.append(vernacularName.getNameStringLink()).append(" ");
		buf.append(vernacularName.getFullNameStringLink());
	    }
	}

	List<ClassificationNode> ancestry = classificationBankObject.getAncestry();
	if(ancestry != null &&  !ancestry.isEmpty()) {
	    int i = 0;
	    for(ClassificationNode acncestor : ancestry) {
		if(i > 0) {
		    buf.append("\n");
		}
		buf.append("in [").append(i++).append("] ");
		buf.append(acncestor.getNamebankID()).append(": ");
		buf.append(Base64Codex.decode(acncestor.getNameString())).append(" ");
		buf.append(Base64Codex.decode(acncestor.getAuthorString()));
	    }
	}

	List<ClassificationNode> children = classificationBankObject.getChildren();
	if(children != null && !children.isEmpty()) {
	    int i = 0;
	    for(ClassificationNode child : children) {
		buf.append("incl:[").append(i++).append("] ");
		buf.append(child.getClassificationBankID()).append("/");
		buf.append(child.getNamebankID()).append(": ");
		buf.append(child.getRankName()).append(" ");
		buf.append(Base64Codex.decode(child.getNameString())).append(" ");
		buf.append(Base64Codex.decode(child.getAuthorString())).append("\n");
	    }
	}

	List<Justification> justifications =
	    classificationBankObject.getJustifications();
	if(justifications != null && !justifications.isEmpty()) {
	    int i = 0;
	    for(Justification justification : justifications) {
		buf.append("justify[").append(i++).append("] ");
		buf.append(justification.getNameString()).append(": ");
		buf.append(justification.getArticleAuthor()).append(" ");
		buf.append(justification.getArticlePublicationYear()).append(". ");
		buf.append(justification.getArticleTitle()).append("\n");
	    }
	}
	return buf.toString();
    }

    public NameUsage<?, ?> getHomotypicSeniorSynonym()
    {
	return homotypicSeniorSynonym;
    }

    public void setHomotypicSeniorSynonym(NameUsage<?, ?> seniorSynonym)
    {
	homotypicSeniorSynonym = seniorSynonym;
    }

    public Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> getSeniorSynonyms()
    {
	Collection<Annotation> synonymizers = getAnnotations("synonym");
	Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> synonyms = new ArrayList<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>>();
	for(Annotation annotation: synonymizers) {
	    Iterator<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> annotators = annotation.getAnnotators();
	    while(annotators.hasNext()) {
		NameUsage<?, ?> annotator = annotators.next();
		if(!synonyms.contains(annotator))
		    synonyms.add(annotator);
	    }
	}

	return synonyms;
    }


    public void setSeniorSynonym(NameUsage<?, ?>[] seniorSynonyms)
    {
	//do we need this?
    }

    public void addSeniorSynonym(NameUsage<?, ?> seniorSynonym)
    {
	//do we need this?
    }

    public void removeSeniorSynonym(NameUsage<?, ?> seniorSynonym)
    {
	//do we need this?
    }
}

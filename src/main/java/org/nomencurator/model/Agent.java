/*
 * Agent.java:  a Java implementation of Agent class
 * for Nomencurator data strucutre
 *
 * Copyright (c) 1999, 2002, 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.model;

import java.io.Serializable;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.nomencurator.io.sql.NamedObjectConnection;

import org.nomencurator.util.ArrayUtility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of Agent data structure of Nomencurator data model.
 *
 * @see 	org.nomencurator.model.NamedObject
 * @see 	org.nomencurator.model.Affiliation
 * @see 	org.nomencurator.model.Publication
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	01 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class Agent
    extends AbstractNamedObject<Agent>
    implements Serializable, Cloneable
{
    private static final long serialVersionUID = -4167097712357697231L;

    /** {@code String} representing address of the {@code Agenet} */
    protected String address;

    /** {@code String} representing address of the {@code Agent} */
    protected String phone;

    /** {@code String} representing address of the {@code Agent} */
    protected String fax;

    /** {@code String} representing the URL of the {@code Agenet} */
    protected String url;

    /** {@code String} representing e-mail address to contact to the {@code Agent} */
    protected String eMail;

    /** Date in {@code Calendar} when the {@code Agent} started, established or was born */
    protected Calendar beginningDate;

    /** Date as {@code Calendar } when the {@code Agent} stopped, ceased or dead */
    protected Calendar endingDate;

    protected Agent[] members;

    protected Agent[] memberOf;

    /** {@code Vector} representing publication list of the {@code Person} */
    protected Publication[] publications;

    /**
     * Constructs an {@code Auahot} having
     * {@code author} as its name, i.e. perisitent ID
     *
     * @param author {@code String} representing its name,
     * i.e. perisitent ID
     */
    public Agent(String name)
    {
	super(name);
	//analyze(name);
    }
    
    /**
     * Constructs copy of {@code author}
     *
     * @param author {@code Agent}
     */
    public Agent(Agent agent)
    {
	super();
	//copy(agent);
    }

    /**
     * Constructs an {@code Agent} object using XML data
     * given by {@code xml}
     *
     * @param xml {@code Element} specifying {@code Agent}
     *
     */
    public Agent(Element xml)
    {
	super();
	/*
	NodeList childNodes = xml.getChildNodes();
	int subChildCount = childNodes.getLength();

	boolean toBeResolved = false;

	String persistentID = null;
	for (int j = 0; j < subChildCount; j++) {
	    Node child = childNodes.item(j);
	    if(child.getNodeType () == Node.ELEMENT_NODE) {
		Element element = (Element)child;
		String tagName = element.getTagName();

		if (tagName.equals ("oid"))
		    persistentID = getString(element);
		else if(tagName.equals ("surname"))
		    surname = getString(element);
		else if(tagName.equals ("firstName"))
		    firstName = getString(element);
		else if(tagName.equals ("middleNames"))
		    middleNames =getString(element);
		else if(tagName.equals ("surnamePrefix"))
		    surnamePrefix = getString(element);
		else if(tagName.equals ("title"))
		    title = getString(element);
		else if(tagName.equals ("epithet"))
		    epithet = getString(element);
		else if(tagName.equals ("birthDate")) {
		    //birth = new GregorianCalendar();
		    //birth.setTime(getDate(getString(element)));
		    birthDate = new Date(getDate(getString(element)).getTime());
		}
		else if(tagName.equals ("deathDate")) {
		    //deathDate = new GregorianCalendar();
		    //deathDate.setTime(getDate(getString(element)));
		    deathDate = new Date(getDate(getString(element)).getTime());
		}
		else if(tagName.equals ("publication")) {
		    String pID = getString(element);
		    Publication p =
			(Publication)curator.get(pID);
		    if(p != null) {
			Array.add(p, publications);
			toBeResolved = true;
		    }
		    else {
			p = new Publication();
			p.setLiteral(pID);
			curator.put(p);
			addPublication(p);
		    }
		}
		else if(tagName.equals ("affiliation")) {
		    String pID = getString(element);
		    Affiliation a =
			(Affiliation)curator.get(pID);
		    if(a == null) {
			a = new Affiliation();
			a.setLiteral(pID);
			curator.put(a);
			addAffiliation(a);
		    }
		    addAffiliation(a);

		}
		else{}
            }
	    
	}

	if(persistentID != null &&
	   !persistentID.equals(getLiteral()))
	    setLiteral(persistentID); //i.e. other key data are empty

	if(toBeResolved)
	    curator.resolve(this);
	*/
    }

    /**
     * Sets {@code address} as address of 
     * this {@code Agent}.
     *
     * @param address to set
     */
    public void setAddress(String address)
    {
	this.address = address;
    }

    /**
     * Returns a address of this {@code Agent}.
     *
     * @return address of this {@code Agent}
     */
    public String getAddress()
    {
	return address;
    }

    /**
     * Sets {@code eMail} as e-mail address of 
     * this {@code Agent}.
     *
     * @param e-mail address to set
     */
    public void setEMail(String eMail)
    {
	this.eMail = eMail;
    }

    /**
     * Returns e-mail address of this {@code Agent}.
     *
     * @return e-mail address of this {@code Agent}
     */
    public String getEMail()
    {
	return eMail;
    }

    /**
     * Sets {@code phone} as phone of 
     * this {@code Agent}.
     *
     * @param phone to set
     */
    public void setPhone(String phone)
    {
	this.phone = phone;
    }

    /**
     * Returns phone of this {@code Agent}.
     *
     * @return phone of this {@code Agent}
     */
    public String getPhone()
    {
	return phone;
    }

    /**
     * Sets {@code fax} as fax of 
     * this {@code Agent}.
     *
     * @param fax to set
     */
    public void setFax(String fax)
    {
	this.fax = fax;
    }

    /**
     * Returns fax of this {@code Agent}.
     *
     * @return fax of this {@code Agent}
     */
    public String getFax()
    {
	return fax;
    }

    /**
     * Sets {@code url} as URL of 
     * this {@code Agent}.
     *
     * @param url to set
     */
    public void setURL(String url)
    {
	this.url = url;
    }

    /**
     * Returns URL of this {@code Agent}.
     *
     * @return URL of this {@code Agent}
     */
    public String getURL()
    {
	return url;
    }

    /**
     * Returns {@code Calendar} rerpresending the beginning date
     * of the agent, i.e. starting, established, or birth
     *
     * @return Calendar representing the beginning date of the agent
     */
    public Calendar getBeginningDate()
    {
	return beginningDate;
    }
    
    /**
     * Sets {@code beginningDate} as the begninning date of the agent,,
     * i.e. starting, established, or birth
     *
     * @param beginningDate begging of the agent
     */
    public void setBeginningDate(Calendar beginningDate)
    {
	if(this.beginningDate == beginningDate)
	    return;

	this.beginningDate = beginningDate;
    }
    
    /**
     * Returns {@code Calendar} rerpresending the ending date
     * of the agent, i.e. starting, established, or birth
     *
     * @return Calendar representing the ending date of the agent
     */
    public Calendar getEndingDate()
    {
	return endingDate;
    }
    
    /**
     * Sets {@code endingDate} as the begninning date of the agent,,
     * i.e. starting, established, or birth
     *
     * @param endingDate begging of the agent
     */
    public void setEndingDate(Calendar endingDate)
    {
	if(this.endingDate == endingDate)
	    return;

	this.endingDate = endingDate;
    }
    
    
    /**
     * Returns an array of {@code Agent}s assigned
     * to a team represened by this object, or null if
     * this is not a team.
     *
     * @return array of {@code Agent}s assigned to this
     * team, or null if this is not a team.
     */
    public Agent[] getMembers()
    {
	return ArrayUtility.copy(members);
    }

    /**
     * Sets contens of {@code members} as members assigned
     * to a team represened by this object.  Set null to make
     * this a non-team object.
     *
     * @param members array of {@code Agent}s bo be 
     * assigned to this team, or null to make this a not-team
     * object.
     */
    public void setMembers(Agent[] members)
    {
	if(this.members == members)
	    return;

	if(this.members != null) {
	    Agent[] agents = this.members;
	    this.members = null;
	    for(Agent a : agents) {
		a.removeMemberOf(this);
	    }
	    ArrayUtility.clear(agents);
	}

	this.members = ArrayUtility.copy(members);
	if(this.members != null) {
	    for(Agent a : this.members) {
		a.addMemberOf(this);
	    }
	}
    }

    /**
     * Adds {@code member} as member of this
     * {@code Agent}.
     *
     * @param member {@code Agent} to be member of this
     * {@code Agent}.
     */
    public void addMember(Agent member)
    {
	if(member == this)
	    throw new IllegalArgumentException(getClass().getName() + "addMember(): self membership is prohibited.");
	if(member == null || 
	   ArrayUtility.contains(member, members))
	    return;
	members = ArrayUtility.add(member, members);
	member.addMemberOf(this);
    }

    /**
     * Removes {@code member} from the member list of this
     * {@code Agent}.
     *
     * @param member {@code Agent} to be removed from
     * the member list of this {@code Agent}.
     */
    public void removeMember(Agent member)
    {
	if(member == this)
	    throw new IllegalArgumentException(getClass().getName() + "#removeMember(): self membership is prohibited.");
	if(member == null || 
	   !ArrayUtility.contains(member, members))
	    return;
	members = ArrayUtility.remove(member, members);
	member.removeMemberOf(this);
    }

    /**
     * Returns an array of {@code Agent}s which
     * this object is assigned to, or null if this is
     * not assigned to any team or not a division of
     * any institute.
     *
     * @return array of {@code Agent}s assigned to this
     * team, or null if this is not a team.
     */
    public Agent[] getMemberOf()
    {
	return ArrayUtility.copy(memberOf);
    }

    /**
     * Sets contens of {@code memberOf} as memberOf assigned
     * to a team represened by this object.  Set null to make
     * this a non-team object.
     *
     * @param memberOf array of {@code Agent}s bo be 
     * assigned to this team, or null to make this a not-team
     * object.
     */
    public void setMemberOf(Agent[] memberOf)
    {
	if(this.memberOf == memberOf)
	    return;

	if(this.memberOf != null) {
	    Agent[] agents = this.memberOf;
	    this.memberOf = null;
	    for(Agent a : agents) {
		a.removeMember(this);
	    }
	    ArrayUtility.clear(agents);
	}

	this.memberOf = ArrayUtility.copy(memberOf);
	if(this.memberOf != null) {
	    for(Agent a : this.memberOf) {
		a.addMember(this);
	    }
	}
    }

    /**
     * Adds {@code member} as member of this
     * {@code Agent}.
     *
     * @param member {@code Agent} to be member of this
     * {@code Agent}.
     */
    public void addMemberOf(Agent member)
    {
	if(member == this)
	    throw new IllegalArgumentException(getClass().getName() + "#addMemberOf(): self membership is prohibited.");
	if(member == null || 
	   ArrayUtility.contains(member, memberOf))
	    return;
	memberOf = ArrayUtility.add(member, memberOf);
	member.addMember(this);
    }

    /**
     * Removes {@code member} from the member list of this
     * {@code Agent}.
     *
     * @param member {@code Agent} to be removed from
     * the member list of this {@code Agent}.
     */
    public void removeMemberOf(Agent member)
    {
	if(member == this)
	    throw new IllegalArgumentException(getClass().getName() + "#removeMemberOf(): self membership is prohibited.");

	if(member == null || 
	   !ArrayUtility.contains(member, memberOf))
	    return;
	memberOf = ArrayUtility.remove(member, memberOf);
	member.removeMember(this);
    }

    /**
     * Constructs an empty {@code Agent} object
     */
    public Agent()
    {
	super();
	//analyze(null);
    }
    
    /**
     * Returns {@code Enumeration} of author's {@code Publication}s
     * or null if none
     *
     * @return Enumeration of author's {@code Publication}s
     * or null if none
     */
    public Publication[] getPublications()
    {
	if(entity != null)
	    return ((Person)getEntity()).getPublications();

	return ArrayUtility.copy(publications);
    }

    /**
     * Sets {@code publications} as {@code Vector} representing author's publication list
     *
     * @param publications {@code Vector} representing author's publication list
     */
    public void setPublications(Collection<? extends Publication> publications)
    {
	if(entity != null)
	    ((Person)getEntity()).setPublications(publications);

	if(this.publications != null) {
	    for(int i = 0; i < this.publications.length; i++) {
		this.publications[i].removeAuthor(this);
		this.publications[i] = null;
	    }
	}

	if(publications == null)
	    return;

	int size = publications.size();
	this.publications = new Publication[size];
	publications.toArray(this.publications);
    }

    /**
     * Adds {@code publication} to author's publication list
     * with returning true if it is added successfully, or false if not.
     *
     * @param publication {@code Publication} to be added to author's publication list
     *
     * @return true if {@code publication} was added to the publication list successfully, or false if not.
     */
    public boolean addPublication(Publication publication)
    {
	if(entity != null)
	    return ((Person)getEntity()).addPublication(publication);
	
	Publication[] added = ArrayUtility.add(publication, publications);
	if(added != publications) {
	    if(publications != null)
		ArrayUtility.clear(publications);
	    publications = added;
	    return true;
	}
	return false;
    }

    /**
     * Removes {@code publication} from author's publication list.
     *
     * @param publication {@code Publication} to be removed from author's publication list
     */
    public void removePublication(Publication publication)
    {
	if(entity != null)
	    ((Person)getEntity()).removePublication(publication);
	else {
	    ArrayUtility.remove(publication, publications);
	    publication.removeAuthor(this);
	}
    }

    /**
     * Clears author's publication list.
     */
    public void clearPublication()
    {
	if(entity != null)
	    ((Person)getEntity()).clearPublication();
	else
	    ArrayUtility.clear(publications);
    }

    /**
     * Returns a persistent ID representing this {@code Agent}
     * with specified  {@code separator}.  It contains class name header
     * if {@code withClassName} true.
     * The subclasses must provide this method.
     *
     * @param separator {@code String} to be used as the field separator
     * @param withClassName {@code boolean} specifying with or without
     * class name header
     *
     * @return String representing this {@code Agent}
     */
    public String getPersistentID(String separator, boolean withClassName)
    {
	if(entity != null)
	    return getEntity().getPersistentID(separator, withClassName);

	StringBuffer pid = null;

	if(withClassName)
	    pid = getClassNameHeaderBuffer();
	else
	    pid = new StringBuffer();
	/*
	String str = getFullname();
	if(str != null)
	    pid.append(str);
	//chenge it as locale independent!
	//pid.append(separator).append(birthDate);
	//pid.append(separator).append(deathDate);
	Calendar c = Calendar.getInstance();
	pid.append(separator);
	if(birthDate != null) {
	    c.setTime(birthDate);
	    appendCalendar(pid, c, ".");
	}
	pid.append(separator);
	if(deathDate != null) {
	    c.setTime(deathDate);
	    appendCalendar(pid, c, ".");
	}
	*/
	return pid.toString();
    }
    
    /**
     * Returnes number of fields separators in persistent ID
     *
     * @return int representing number of fields separators in persistent ID
     */ 
    public int getFieldSepartorsCount()
    {
	return 2;
    }

    /**
     * Merges {@code namedObject} with this {@code Agent}
     * if possible, but not yet implemented.
     * It returns true if merged.
     *
     * @param namedObject a {@code NamedObject} to be merged
     *
     * @return true if merged, or false if not mergiable
     */
    public boolean merge(NamedObject<?> namedObject)
    {
	if(!(namedObject instanceof Agent))
	    return false;
	return false; //not yet implemented
    }

    /**
     * Parses a {@code line} and sets values of this object accordint to it
     *
     * @param line {@code String} containing fragment of data to be set
     */
    public void parseLine(String line){
    }

    public Object clone()
    {
	return new Agent(this);
    }

    /**
     * Returns XML {@code String} representing this objet 
     *
     * @return XML {@code String} representing this objet
     */
    public String toXML()
    {
	if(entity != null)
	    return getEntity().toXML();

	StringBuffer buf = new StringBuffer();
	/*
	buf.append("<Agent>\n");
	buf.append("<oid>").append(getPersistentID()).append("</oid>\n");
	buf.append("<surname>").append(getSurname()).append("</surname>\n");
	buf.append("<firstName>").append(getFirstName()).append("</firstName>\n");
	buf.append("<middleNames>").append(getMiddleNames()).append("</middleNames>\n");
	buf.append("<title>").append(getTitle()).append("</title>\n");
	buf.append("<epithet>").append(getEpithet()).append("</epithet>\n");
	buf.append("<surrnamePrefix>").append(getSurnamePrefix()).append("</surnamePrefix>\n");
	if(getBirthDate() != null) {
    	    buf.append("<birthDate>").append(getBirthDate().toString()).append("</birthDate>\n");
    	}
    	if(getDeathDate() != null) {
    	    buf.append("<deathDate>").append(getDeathDate().toString()).append("</deathDate>\n");
    	}
	if(publications != null) {
	    for(int i = 0; i < publications.length; i++) {
    	        buf.append("<publication>").append(publications[i].getPersistentID()).append("</publication>\n");
	    }
	}
	if(affiliations != null) {
	    for(int i = 0; i < affiliations.length; i++) {
    	        buf.append("<affiliation>").append(affiliations[i].getPersistentID()).append("</affiliation>\n");
	    }
	}
	
	// buf.append("<previous>").append(getPrevious().getPersistentID()).append("</previous>");
	// buf.append("<next>").append(getNext().getPersistentID()).append("</next>");
	buf.append("</Agent>\n");
	*/
        return buf.toString();
    }	
	
    
    /**
     * create XML String of the all Related NamedObject
     *
     * @return XML String of this {@code Agent} object
     */
    public String toRelevantXML()
    {
	if(entity != null)
	    return getEntity().toRelevantXML();

	// create XML String of the Agent itself
	StringBuffer buf = new StringBuffer();
	buf.append(toXML());
	/*
	// create XML of the Publications
	if(publications != null) {
	    for(int i = 0; i < publications.length; i++) {
		buf.append(publications[i].toXML());
	    }
    	}
	// create XML of the Affiliation
	if(affiliations != null) {
	    for(int i = 0; i < affiliations.length; i++) {
		buf.append(affiliations[i].toXML());
	    }
    	}
	*/
	return buf.toString();
    }

    /**
     * Sets valuse of this {@code NamedObject} to
     * {@code statement} using {@code connection}.
     * from specified {@code index} of the {@code statement}
     *
     * @param statement {@code PraredStatement} to which
     * value of the this {@code NamedObject} to be set
     * @param connection {@code NamedObjectConnection}
     * to be used to set values
     * @param index {@code int} from where values to be set
     * into the {@code statement}
     *
     * @return int index of the next parameter to be set if there is
     *
     * @exception SQLException
     */
    public int setValues(PreparedStatement statement,
			 NamedObjectConnection<?> connection,
			 int index)
	throws SQLException
    {
	return connection.setValues(statement, this, index);
    }


    /**
     * Sets a {@code object} as the entity of the name
     *
     * @param object representing the entity
     */
    //public void setEntity(Object object)
    /*
    public void setEntity(Agent object)
    {
	if(object == this ||
	   (object != null && !(object instanceof Agent)))
	    throw new IllegalArgumentException(object.getClass().getName() + " can't be an entity of " + getClass().getName());
	entity = (Agent)object;
	super.setEntity(object);
    }
    */

    /**
     * Returns a summarized expression of this {@code NamedObject}
     *
     * @return String representing summary of this {@code NamedObject}
     */
    public String getSummary()
    {
	if(entity != null)
	    return getEntity().getSummary();

	return getLiteral();
    }

    public static Calendar getCalendar(String xmlDateTime)
    {
	//process YYYY-MM-DD format
	int year = Integer.valueOf(xmlDateTime.substring(0,5));
	int month = Integer.valueOf(xmlDateTime.substring(5,7));
	int dayOfMonth = Integer.valueOf(xmlDateTime.substring(7,9));
	int hourOfDay = 0;
	int minute = 0;
	int second = 0;
	String timezone = null;
	if(xmlDateTime.length() > 10) {
	    switch(xmlDateTime.substring(10,11)) {
	    case "Z":
		timezone = "GMT+0";
		break;
	    case "+":
	    case "-":
		timezone = "GMT"+xmlDateTime.substring(10,16);
		break;
	    case "T":
		int index = xmlDateTime.indexOf("Z");
		if(index > -1) {
		    timezone = "GMT+0";
		}
		else {
		    index = xmlDateTime.indexOf("+");
		    if(index < 0) {
			index = xmlDateTime.indexOf("-");
		    }
		    if(index > -1) {
			timezone = "GMT"+xmlDateTime.substring(index,index + 6);
		    }
		}
		hourOfDay = Integer.valueOf(xmlDateTime.substring(11,13));
		minute = Integer.valueOf(xmlDateTime.substring(14,16));
		if(index < 0)
		    index = xmlDateTime.length();
		second = (int)(0.5 + Double.valueOf(xmlDateTime.substring(17,index)));
		break;
	    default:
		break;
	    }
	}
	Calendar calendar = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
	if(timezone != null)
	    calendar.setTimeZone(TimeZone.getTimeZone(timezone));
	
	return calendar;
    }
}

/*
 * LinkType.java:  managing link type of Annotation
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2004, 2015, 2016, 2019 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.model;

/**
 * {@code LinkType} represents link types of Annotation data structure in Nomencurator data model
 *
 * @see org.nomencurator.model.Annotation
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class LinkType
{
    /** name of link type */
    protected String linkType;

    /** cardinarity of annotators */
    protected String annotatorCardinality;

    /** cardinarity of annotatants */
    protected String annotatantCardinality;

    public LinkType()
    {
	this("", "", "");
    }

    public LinkType(String linkType,
		    String annotatorCardinality,
		    String annotatantCardinality)
    {
	setLinkType(linkType);
	setAnnotatorCardinality(annotatorCardinality);
	setAnnotatantCardinality(annotatantCardinality);
    }

    public void setLinkType(String linkType)
    {
	this.linkType = linkType;
    }

    public String toString()
    {
	return getLinkType();
    }

    public String getLinkType()
    {
	return linkType;
    }

    public void setAnnotatorCardinality(String annotatorCardinality)
    {
	this.annotatorCardinality = annotatorCardinality;
    }

    public String getAnnotatorCardinality()
    {
	return annotatorCardinality;
    }

    public void setAnnotatantCardinality(String annotatantCardinality)
    {
	this.annotatantCardinality = annotatantCardinality;
    }

    public String getAnnotatantCardinality()
    {
	return annotatantCardinality;
    }
}

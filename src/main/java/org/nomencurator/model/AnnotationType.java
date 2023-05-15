/*
 * AnnotationType.java:  managing link type of Annotation
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2004, 2015 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number, JP17300071
 */

package org.nomencurator.model;

import lombok.Getter;

/**
 * <tt>AnnotationType</tt> represents link types of Annotation data structure in Nomencurator data model.
 *
 * @see org.nomencurator.model.Annotation
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class AnnotationType
{
    /** name of link type */
    protected String annotationType;

    /** cardinarity of annotators in <tt>String</tt> */
    protected String annotatorCardinality;

    /** cardinarity of annotators in <tt>int</tt> */
    @Getter
    protected int maxAnnotators;

    /** cardinarity of annotatants */
    protected String annotatantCardinality;

    /** cardinarity of annotatants in <tt>int</tt>*/
    protected int maxAnnotatants;

    public AnnotationType()
    {
	this("", "", "");
    }

    public AnnotationType(String annotationType,
		    String annotatorCardinality,
		    String annotatantCardinality)
    {
	setAnnotationType(annotationType);
	setAnnotatorCardinality(annotatorCardinality);
	setAnnotatantCardinality(annotatantCardinality);
    }

    public void setAnnotationType(String annotationType)
    {
	this.annotationType = annotationType;
    }

    public String toString()
    {
	return getAnnotationType();
    }

    public String getAnnotationType()
    {
	return annotationType;
    }

    public void setAnnotatorCardinality(String annotatorCardinality)
    {
	this.annotatorCardinality = annotatorCardinality;
	switch(annotatorCardinality) {
	case "0":
	    maxAnnotators = 0;
	    break;
	case "1":
	    maxAnnotators = 1;
	    break;
	default:
	    maxAnnotators = -1;
	}
    }

    public String getAnnotatorCardinality()
    {
	return annotatorCardinality;
    }

    public void setAnnotatantCardinality(String annotatantCardinality)
    {
	this.annotatantCardinality = annotatantCardinality;
	switch(annotatantCardinality) {
	case "0":
	    maxAnnotatants = 0;
	    break;
	case "1":
	    maxAnnotatants = 1;
	    break;
	default:
	    maxAnnotatants = -1;
	}
    }

    public String getAnnotatantCardinality()
    {
	return annotatantCardinality;
    }
}

/*
 * Author.java: Java implementation of author record returned
 * from uBio XML Webservices
 *
 * Copyright (c) 2008, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.ubio.model;

/**
 * {@code Author} provides a Java implementation of author element returned
 * from uBio XML Webservices
 *
 * @version 	30 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Author
{
    long id;
    long group;
    String authorString;
    String displayForm;
    long attribute;

    public Author(){}

    public Author(long id,
		  long group,
		  String authorString,
		  String displayForm,
		  long attribute)
    {
	setId(id);
	setGroup(group);
	setAuthorString(authorString);
	setDisplayForm(displayForm);
	setAttribute(attribute);
    }

    public void setId(long id)
    {
	this.id = id;
    }

    public long getId()
    {
	return id;
    }

    public void setGroup(long group)
    {
	this.group = group;
    }

    public long getGroup()
    {
	return group;
    }

    public void setAttribute(long attribute)
    {
	this.attribute = attribute;
    }

    public long getAttribute()
    {
	return attribute;
    }

    public void setAuthorString(String authorString)
    {
	this.authorString = authorString;
    }

    public String setAuthorString()
    {
	return authorString;
    }

    public void setDisplayForm(String displayForm)
    {
	this.displayForm = displayForm;
    }

    public String setDisplayForm()
    {
	return displayForm;
    }
}

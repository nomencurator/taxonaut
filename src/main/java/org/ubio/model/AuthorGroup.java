/*
 * AuthorGroup.java: Java implementation of author group record returned
 * by uBio XML Webservices
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

package org.ubio.model;

/**
 * <tt>AuthorGroup</tt> provides a Java implementation of author group element returned
 * by uBio XML Webservices
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class AuthorGroup
{
    long id;
    String dates;
    Author[] authors;
    Source[] references;
    Attribute[] attributes;

    public AuthorGroup(long id,
		      String dates,
		      Author[] authors,
		      Source[] references,
		      Attribute[] attributes)
    {
	setId(id);
	setDates(dates);
	this.authors = authors;
	this.references = references;
	this.attributes = attributes;
    }

    public void setId(long id)
    {
	this.id = id;
    }

    public long getId()
    {
	return id;
    }

    public void setDates(String dates)
    {
	this.dates = dates;
    }

    public String setDates()
    {
	return dates;
    }

    public Author[] getAuthors()
    {
	return authors;
    }

    public Attribute[] getAttributes()
    {
	return attributes;
    }

    public Source[] getReferences()
    {
	return references;
    }
}

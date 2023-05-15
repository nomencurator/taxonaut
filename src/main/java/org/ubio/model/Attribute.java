/*
 * Attribute.java: Java implementation of attribute element returned
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

/**
 * {@code Attribute} provides a Java implementation of attribute element returned
 * by uBio XML Webservices
 *
 * @version 	30 June 2016
 * @author 	Nozomi `James' Ytow
 */
package org.ubio.model;

public class Attribute
{
    long id;
    String attribute;

    public Attribute(long id,
		     String attribute)
    {
	setId(id);
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

    public void setAttribute(String attribute)
    {
	this.attribute = attribute;
    }

    public String getAttribute()
    {
	return attribute;
    }
}

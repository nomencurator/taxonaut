/*
 * Mapping.java: provides methods to access to uBio Mapping
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

import java.util.Hashtable;

import lombok.Data;

/**
 * {@code Mapping} provides a method to handle mapping element returned from uBio XML Webservices
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class Mapping
{
    protected long id;

    protected String foreignKey;
    protected String collectionTitle;
    protected String collectionURL;
    protected String logoFile;
    protected String logoFileLinkIT;

    protected static Hashtable<Long, Mapping>collections;

    public static Mapping get(Long id)
    {
	if(collections == null)
	    return null;

	return collections.get(id);
    }

    public Mapping(long id,
		      String foreignKey,
		      String collectionTitle,
		      String collectionURL,
		      String logoFile,
		      String logoFileLinkIT)
    {
	setId(id);
	setForeignKey(foreignKey);
	setCollectionTitle(collectionTitle);
	setCollectionURL(collectionURL);
	setLogoFile(logoFile);
	setLogoFileLinkIT(logoFileLinkIT);
    }
}

/*
 * Citation.java: Java implementation of citation record returned
 * from uBio XML Webservices
 *
 * Copyright (c) 2007, 2015, 2016 Nozomi `James' Ytow
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

import java.io.Serializable;

import lombok.Data;

/**
 * {@code Citation} provides a Java implementation of citation element returned
 * by uBio XML Webservices
 *
 * @version 	30 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class Citation
    implements Serializable
{
    private static final long serialVersionUID = -3978397463047435409L;

    protected String author;
    protected int publicationYear;
    protected String articleTitle;
    protected String publicationTitle;

    public Citation() {};

    public Citation(String author,
		    int publicationYear,
		    String articleTitle,
		    String publicationTitle)
    {
	setAuthor(author);
	setPublicationYear(publicationYear);
	setArticleTitle(articleTitle);
	setPublicationTitle( publicationTitle);
    }
}

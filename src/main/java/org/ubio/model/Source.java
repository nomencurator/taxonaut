/*
 * Source.java: Java implementation of source element returned
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

package org.ubio.model;

import java.io.Serializable;

import lombok.Data;

/**
 * {@code Source} provides methods to handle source element returned from uBio XML Webservices
 *
 * @version 	25 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class Source
    implements Serializable
{
    private static final long serialVersionUID = -5124001505861302577L;

    long id;
    String source;

    public Source(long id,
		  String source)
    {
	setId(id);
	setSource(source);
    }
}

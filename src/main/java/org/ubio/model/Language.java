/*
 * Language.java: provides data object to handle "language" of uBio
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

import java.util.Hashtable;

/**
 * {@code Language} provides methods to handle lanugage element returned from uBio XML Webservices
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class Language
{
    public static String SCIENTIFIC="sci";
    
    protected String code;
    protected String name;
    protected static Hashtable<String, Language> byCode;
    protected static Hashtable<String, Language> byName;

    static {
	put(SCIENTIFIC, "Scientific Name");
    }

    protected Language(String code,
		       String name)
    {
	if(code == null || name == null)
	    return;
	this.code = code;
	this.name = name;

	if(byCode == null) {
	    byCode = new Hashtable<String, Language>();
	}
	if(byCode.get(code) == null) {
	    byCode.put(code, this);
	}

	if(byName == null) {
	    byName = new Hashtable<String, Language>();
	}
	if(byName.get(name) == null) {
	    byName.put(name, this);
	}
    }

    public static Language put(String code, String name)
    {
	Language language = getByCode(code);
	if(language == null) {
	    language = new Language(code, name);
	}
	return language;
    }

    public static Language get(String key, Hashtable<String, Language> table)
    {
	if(table == null || key == null)
	    return null;
	return  table.get(key);
    }

    public static Language get(String code, String name)
    {
	Language l = null;
	if(byCode != null)
	    l = getByCode(code);
	if(l == null)
	    l = new Language(code, name);

	return  l;
    }

    public static Language getByCode(String code)
    {
	return get(code, byCode);
    }

    public static Language getByName(String name)
    {
	return get(name, byName);
    }

    public String getCode()
    {
	return code;
    }

    public String getName()
    {
	return name;
    }
}

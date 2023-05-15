/*
 * QueryMessages.java
 *
 * Copyright (c) 2003, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import java.text.MessageFormat;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code QueryMesssages} provides query result
 * messages under given locale
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class QueryMessages
{
    protected Locale locale;

    protected String recordMessage;
    protected String recordsMessage;
    protected String foundForMessage;
    protected String foundMessage;
    protected String notFoundForMessage;
    protected String notFoundMessage;

    public QueryMessages(Locale locale) 
    {
	setLocale(locale);
    }
    
    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	if(locale == null)
	    locale = Locale.getDefault();

	recordMessage = ResourceKey.RECORD;
	recordsMessage = ResourceKey.RECORDS;
	foundForMessage = ResourceKey.FOUND_FOR_MESSAGE;
	foundMessage = ResourceKey.FOUND_MESSAGE;
	notFoundForMessage = ResourceKey.NOT_FOUND_FOR_MESSAGE;
	notFoundMessage = ResourceKey.NOT_FOUND_MESSAGE;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    recordMessage = resource.getString(recordMessage);
	    recordsMessage = resource.getString(recordsMessage);
	    foundForMessage = resource.getString(foundForMessage);
	    foundMessage = resource.getString(foundMessage);
	    notFoundForMessage = resource.getString(notFoundForMessage);
	    notFoundMessage = resource.getString(notFoundMessage);
	}
	catch(MissingResourceException e) {
	    e.printStackTrace();
	}

	if(recordMessage == null)
	    recordMessage = ResourceKey.RECORD;
	if(recordsMessage == null)
	    recordsMessage = ResourceKey.RECORDS;
	if(foundForMessage == null)
	    foundForMessage = ResourceKey.FOUND_FOR_MESSAGE;
	if(foundForMessage == null)
	    foundMessage = ResourceKey.FOUND_MESSAGE;
	if(notFoundForMessage == null)
	    notFoundForMessage = ResourceKey.NOT_FOUND_FOR_MESSAGE;
	if(notFoundForMessage == null)
	    notFoundMessage = ResourceKey.NOT_FOUND_MESSAGE;
    }

    public String getMessage(int records, Object[] arguments)
    {

	StringBuffer buffer = new StringBuffer();
	if (arguments != null) {
	    for(Object argument:arguments) {
		if(argument == null)
		    continue;
		String arg = argument.toString();
		if(arg.length() > 0) {
		    if (buffer.length() > 0)
			buffer.append(' ');
		    buffer.append(arg);
		}
	    }
	}

	String message = records < 2 ? recordMessage : recordsMessage;
	String formatter = buffer.length() == 0 ?
	    (records == 0 ? notFoundMessage :  foundMessage) :
	    (records == 0 ? notFoundForMessage :  foundForMessage);

	if(records == 0) {
	    if (buffer.length() == 0)
		return MessageFormat.format(formatter, new Object[]{message});
	    else
		return MessageFormat.format(formatter, new Object[]{message, buffer.toString()});
	}
	else if (buffer.length() == 0)
	    return MessageFormat.format(formatter,
				    new Object[]{Integer.toString(records),  message});
	return MessageFormat.format(formatter,
				    new Object[]{Integer.toString(records),  message, buffer.toString()});
    }
} 

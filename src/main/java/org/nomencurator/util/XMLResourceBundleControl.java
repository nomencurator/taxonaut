/*
 * XMLResourceBundleControl.java:  a utility crass to handle properties in XML
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
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

package org.nomencurator.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * {@code XMLResourceBundleControl} provides methods to handle properties in XML
 * @version 	13 Apr. 2016
 * @author 	Nozomi `James' Ytow
 */
public class XMLResourceBundleControl
    extends ResourceBundle.Control
{
    public static final String XML_FORMAT = "xml";
    public static final List<String> FORMAT_XML
	= Collections.unmodifiableList(Arrays.asList(XML_FORMAT));
    public static final List<String> FORMAT_XML_OR_DEFAULT
	= Collections.unmodifiableList(Arrays.asList(XML_FORMAT, "java.class", "java.properties"));

    public List<String> getFormats(String baseName)
    {
	if (baseName == null)
	    throw new NullPointerException();
	return FORMAT_XML;
    }

    public ResourceBundle newBundle(String baseName,
				    Locale locale,
				    String format,
				    ClassLoader loader,
				    boolean reload)
	throws IllegalAccessException,
	       InstantiationException,
	       IOException
    {
	if (baseName == null
	    || locale == null
	    || format == null
	    || loader == null)
	    throw new NullPointerException();

	ResourceBundle bundle = null;
	if (format.equals(XML_FORMAT)) {
	    String bundleName = toBundleName(baseName, locale);
	    String resourceName = toResourceName(bundleName, format);
	    InputStream stream = null;

	    if (reload) {
		URL url = loader.getResource(resourceName);
		if (url != null) {
		    URLConnection connection = url.openConnection();
		    if (connection != null) {
			connection.setUseCaches(false);
			stream = connection.getInputStream();
		    }
		}
	    }
	    else {
		stream = loader.getResourceAsStream(resourceName);
	    }
	    
	    if (stream != null) {
		BufferedInputStream bufferedStream = new BufferedInputStream(stream);
		bundle = new XMLResourceBundle(bufferedStream);
		bufferedStream.close();
	    }
	}
	else if (format.equals("java.class")
		 || format.equals("java.properties")) {
	    bundle = super.newBundle(baseName, locale, format, loader, reload);
	}
	return bundle;
    }

    public class XMLResourceBundle
	extends ResourceBundle {

	protected Properties properties;

	public XMLResourceBundle(InputStream stream)
	    throws IOException
	{
	    super();
	    properties = new Properties();
	    properties.loadFromXML(stream);
	}

	protected Object handleGetObject(String key) {
	    return properties.getProperty(key);
	}

	public Enumeration<String> getKeys()
	{
	    return Collections.enumeration(properties.stringPropertyNames());
	}
    }
}

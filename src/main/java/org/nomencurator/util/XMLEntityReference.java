/*
 * XMLEntityReference.java:  a utility crass to handle entity reference of XML
 *
 * Copyright (c) 2007, 2015 Nozomi `James' Ytow
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

/**
 * <code>XMLEntityReference</code> provides methods to convert entity reference in XML
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class XMLEntityReference
{
    private XMLEntityReference() { }

    public static String decode(String xml)
    {
	return (xml == null?xml:xml.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"").replaceAll("&apos;", "\'"));
    }

    public static String encode(String text)
    {
	return (text == null?text:text.replaceAll("\'", "&apos;").replaceAll("\"", "&quot;").replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;"));
    }
}

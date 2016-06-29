/*
 * XMLSerializable.java: an interface to convert a NamedObject
 * to an XML string.
 *
 * Copyright (c) 2003, 2004, 2015 Nozomi `James' Ytow
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

package org.nomencurator.model;

/**
 * <CODE>XMLSerializable</CODE> provides an interface to convert 
 * a <CODE>NamedObject</CODE> to an XML string.
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface XMLSerializable
{
    /**
     * Returns XML <CODE>String</CODE> representing this object
     *
     * @return XML <CODE>String</CODE> representing this object
     */
    public String toXML();

    /**
     * Appends an XML representing the object at the end of
     * <CODE>buffer</CODE>.  If <CODE>buffer</CODE> is null,
     * it creates a new <CODE>StringBuffer</CODE>.
     *
     * @param buffer <CODE>StringBuffer</CODE> to which the
     * XML to be appended, or null to create a new
     * CODE>StringBuffer</CODE>
     *
     * @return StringBuffer containing the XML at the end
     */
    public StringBuffer toXML(StringBuffer buffer);

    /**
     * Returns XML <CODE>String</CODE> of the all relevant <CODE>NamedObject</CODE>s
     *
     * @return XML <CODE>String</CODE> representing all <CODE>NamedObject</CODE>s 
     * relationg to the <CODE>NamedObject</CODE>
     */
    public String toRelevantXML();

    /**
     * Appends an XML representing all objects relevant to
     * the object at the end of <CODE>buffer</CODE>.
     * If <CODE>buffer</CODE> is null, it creates 
     * a new <CODE>StringBuffer</CODE>.
     *
     * @param buffer <CODE>StringBuffer</CODE> to which the
     * XML to be appended, or null to create a new
     * CODE>StringBuffer</CODE>
     *
     * @return StringBuffer containing the XML at the end
     */
    public StringBuffer toRelevantXML(StringBuffer buffer);
}

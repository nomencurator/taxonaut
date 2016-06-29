/*
 * NamedTreeAdaptor.java: an implemantation of NamedTree
 *
 * Copyright (c) 2006, 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import javax.swing.JTree;

//import org.nomencurator.util.Array;

/**
 * <code>NamedTree</code> defines an interface to name to a <CODE>JTree</CODE>.
 * Here we assume name in sense of <CODE>NameUsage</CODE>.  A single name
 * string may designate to different <CODE>JTree</CODE>, but such situtation
 * is not covered by this interface because it is a named tree.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NamedTreeAdaptor
    implements NamedTree
{
    protected JTree tree;
    protected String literal;
    protected String[] literals;

    /**
     * Creates a <CODE>NamedTree</CODE> for <CODE>tree</CODE>
     */
    public NamedTreeAdaptor(JTree tree)
    {
	this.tree = tree;
    }

    /**
     * Returns the <CODE>JTree</CODE> designated by this <CODE>NamedTree</CODE>
     */
    public JTree getTree()
    {
	return tree;
    }

    /**
     * Sets <CODE>name</CODE> as the name of the <CODE>JTree</CODE>
     */
    public void setTreeName(String name)
    {
	literal = name;
    }

    /**
     * Returns <CODE>String</CODE> representing the name of the <CODE>JTree</CODE>
     */
    public String getTreeName()
    {
	if(literals == null)
	    return literal;
	return literals[0];
    }

    /*
    public void addName(String name)
    {
	int index = 0;
	if(literals == null &&
	   literal != null &&
	   literal.length() > 0) {
	    if(literal.equals(name))
		return;
	    literals = new String[1];
	    literals[0] = literal;
	    literal = null;
	}
	if(!Array.contains(name, literals))
	    literals = Array.add(name, literals);
    }

    public void removeName(String name)
    {
	if(literals != null) {
	    literals = Array.remove(name, literals);
	    if(literals.length == 1) {
		literal = literals[0];
		literals[0] = null;
		literals = null;
	    }
	}
    }

    public String[] getNames()
    {
	if(literals != null)
	    return Array.copy(literals);
	
	if(literal == null ||
	   literal.length()  == 0)
	    return new String[0];

	String[] names = new String[1];
	names[0]=literal;
	return names;
    }
    */
}

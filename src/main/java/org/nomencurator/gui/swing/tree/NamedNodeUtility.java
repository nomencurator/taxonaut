/*
 * NamedNodeUtility.java: an interface for rough set operation
 *
 * Copyright (c) 2005, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import javax.swing.tree.TreeNode;

/**
 * {@code NamedNodeUtility} provides a method can be shared by
 * {@code NamedNode} instances
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NamedNodeUtility
{
    static NamedNodeUtility dummy;

    private NamedNodeUtility() {};

    /**
     * Returnes parent node of the {@code NamedNode},
     * some node having a name on the parental chain if the parent node is unnamed,
     * or null if none node on the prental chain has name or if the 
     * {@code NamedNode} is the root node.
     */
    public static NamedNode<?> getNamedParent(TreeNode node)
    {
	if(node == null)
	    throw new IllegalArgumentException("NamedNode#getNamedParent does not allow null argument");

	node = node.getParent();

	if(node == null ||
	   !(node instanceof NamedNode))
	    return null;

	NamedNode<?> namedParent = (NamedNode<?>)node;

	String parentName = namedParent.getLiteral();
	//String parentName = namedParent.getName();
	if(parentName != null && parentName.length() > 0)
	    return namedParent;

	return namedParent.getNamedParent();
    }
}

/*
 * TrimmableTreeNode.java:  a MutableTreeNode of which footstanp
 * of children can be minimized
 *
 * Copyright (c) 2006, 2015 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <CODE>TrimmableTreeNode</CODE> is a <CODE>MutableTreeNode<CODE>
 * allowing users to minimize memory consumpution by trimming
 * allocated memory to link to relevant nodes.
 *
 * @version 	20 June 2015
 * @author 	Nozomi `James' Ytow
 */
class TrimmableTreeNode
    extends DefaultMutableTreeNode
{
    private static final long serialVersionUID = 666573762467600558L;

    protected static Object[] dummyChildren;
    
    static{
	dummyChildren =	new Object[1];
	dummyChildren[0] = null;
    };

    public TrimmableTreeNode()
    {
	super();
	//this(null);
    }

    public TrimmableTreeNode(Object userObject)
    {
	super(userObject);
	//this(userObject, true);
    }

    public TrimmableTreeNode(Object userObject, boolean allowsChildren)
    {
	super(userObject,  allowsChildren);
	//super(userObject,  (allowsChildren)? dummyChildren : null);
    }

    /**
     * Trims memory allocated to store relevant nodes.
     */
    public void trim()
    {
	if(children != null)
	    children.trimToSize();
    }
}

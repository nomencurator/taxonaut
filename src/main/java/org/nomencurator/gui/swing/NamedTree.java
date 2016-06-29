/*
 * NamedTree.java: an interface to name to a JTree
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

package org.nomencurator.gui.swing;

import javax.swing.JTree;
/**
 * <code>NamedTree</code> defines an interface to name to a <CODE>JTree</CODE>.
 * Here we assume name in sense of <CODE>NameUsage</CODE>.  A single name
 * string may designate to different <CODE>JTree</CODE>, but such situtation
 * is not covered by this interface because it is a named tree.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface NamedTree
{
    /**
     * Returns the <CODE>JTree</CODE> designated by this <CODE>NamedTree</CODE>
     */
    public JTree getTree();

    /**
     * Sets <CODE>name</CODE> as the name of the <CODE>JTree</CODE>
     */
    public void setTreeName(String name);

    /**
     * Returns <CODE>String</CODE> representing the name of the <CODE>JTree</CODE>
     */
    public String getTreeName();

    // do we need the following methods for synonyms?
    //public void addName(String name);
    //public void removeName(String name);
    //public String[] getNames();
}

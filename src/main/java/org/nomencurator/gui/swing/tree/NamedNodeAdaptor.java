/*
 * NamedNodeAdaptor.java: an interface for rough set operation
 *
 * Copyright (c) 2002, 2003, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;


/**
 * {@code NamedNodeAdaptor} provides an implementation of
 * {@code NamedNode} subset.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NamedNodeAdaptor
{
    protected NamedNode<?> node;

    protected RoughSetAdaptor rough;

    public NamedNodeAdaptor(NamedNode<?> node)
    {
	this.node = node;
    }

    protected RoughSetAdaptor getRoughSetAdaptor()
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(node);
	return rough;
    }


    public void addIncludant(NamedNode<?> node)
    {
	getRoughSetAdaptor().addIncludant(node);
    }

    public void removeIncludant(NamedNode<?> node)
    {
	if(rough != null)
	    rough.removeIncludant(node);
    }

    public Map<String, NamedNode<?>> getIncludants()
    {
	return getRoughSetAdaptor().getIncludants();
    }

    public void clearIncludants()
    {
	if(rough != null)
	    rough.clearIncludants();
    }

    public void addExcludant(NamedNode<?> node)
    {
	getRoughSetAdaptor().addExcludant(node);
    }

    public void removeExcludant(NamedNode<?> node)
    {
	if(rough != null)
	    rough.removeExcludant(node);
    }

    public Map<String, NamedNode<?>> getExcludants()
    {
	return getRoughSetAdaptor().getExcludants();
    }

    public void clearExcludants()
    {
	if(rough != null)
	    rough.clearExcludants();
    }


    public boolean isCompatible(NamedNode<?> node)
    {
	return getRoughSetAdaptor().isCompatible(node);
    }

    public Map<String, NamedNode<?>> getCrossSection(NamedNode<?> node)
    {
	return getRoughSetAdaptor().getCrossSection(node);
    }

    public List<Map<String, NamedNode<?>>> getIntersection(NamedNode<?> node)
    {
	return getRoughSetAdaptor().getIntersection(node);
    }

    public List<Map<String, NamedNode<?>>> getUnion(NamedNode<?> node)
    {
	return getRoughSetAdaptor().getUnion(node);
    }

    public double getCoverage(NamedNode<?> node)
    {
	return getRoughSetAdaptor().getCoverage(node);
    }

    /*
    public int getHeight()
    {
	return node.getPath().length; 
    }
    */
}


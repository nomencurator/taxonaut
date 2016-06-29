/*
 * HierarchiesFrame.java:  a class provaides Frame to compare hierarchies
 * using Nomencurator.
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.gui.swing;

import java.awt.GraphicsConfiguration;

import java.util.Locale;

import javax.swing.JFrame;

import org.nomencurator.model.NameUsage;
/**
 * {@code Hierarchies} provides a table to compare
 * hierarchies using Nomencuartor.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
class HierarchiesFrame<N extends NameUsage<?, ?>>
    extends JFrame
{
    private static final long serialVersionUID = -2611443589109401968L;

    protected Hierarchies<N> comparator;

    public HierarchiesFrame()
    {
	this(Locale.getDefault());
    }

    public HierarchiesFrame(Locale locale)
    {
	super();
	setHierarchies(new Hierarchies<N>(locale));
    }

    public HierarchiesFrame(GraphicsConfiguration gc)
    {
	super(gc);
	setHierarchies(new Hierarchies<N>());
    }

    public HierarchiesFrame(String title)
    {
	super(title);
	setHierarchies(new Hierarchies<N>());
    }

    public HierarchiesFrame(String title, GraphicsConfiguration gc)
    {
	this(Locale.getDefault(), title, gc);
    }

    public HierarchiesFrame(Locale locale, String title, GraphicsConfiguration gc)
    {
	super(title, gc);
	setHierarchies(new Hierarchies<N>(locale));
    }

    public Hierarchies<N> getHierarchies()
    {
	return comparator;
    }

    protected void setHierarchies(Hierarchies<N> comparator)
    {
	this.comparator = comparator;
	getContentPane().add(comparator);
    }
}

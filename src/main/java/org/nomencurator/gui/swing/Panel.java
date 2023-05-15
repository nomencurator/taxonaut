/*
 * Panel.java: an abstract class provides utility a function
 *
 * Copyright (c) 2007, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * <CODE>Panel</CODE> extends <CODE>JPanel</CODE> to provide
 * a utility funtion
 *
 * @version 	28 June 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class Panel
    extends JPanel
{
    private static final long serialVersionUID = -6583359976547863057L;

    public Panel(Locale locale, int subtype)
    {
	this(new BorderLayout(), locale, subtype);
    }

    public Panel(LayoutManager2 layout, Locale locale, int subtype)
    {
	super(layout);
	if(layout instanceof BorderLayout) {
	    ((BorderLayout)getLayout()).setHgap(6);
	}

	createComponents(locale, subtype);
	layoutComponents();
	setComponentsSize();
    }

    /**
     * Creates <CODE>Components</CODE>
     *
     * @param locale <CODE>Locale</CODE> to determine texts in labels and buttons.
     * VM default locale will be used if the value is null.
     */
    protected abstract void createComponents(Locale locale,
				    int components);

    /** Layout <CODE>Components</CODE> */
    protected abstract void layoutComponents();

    /** Set size of <CODE>Components</CODE> */
    protected abstract void setComponentsSize();

    /**
     * Sets preferred size of component to match with reference size.
     * Height of reference size is increased if the component is taller
     * than reference size.  It returens preference size of the component.
     *
     * @param component <CODE>JComponent</CODE> of which size to be modified
     * @param referenceSize <CODE>Dimension</CODE> representing height to which
     * the height of the <CODE>component</CODE> matchs.
     *
     * @return Dimension containing preference size of the component
     */
    public static Dimension setPreferredComponentHeight(JComponent component, Dimension referenceSize)
    {
	Dimension size = component.getPreferredSize();
	if(size.height < referenceSize.height)
	    size.height =  referenceSize.height;
	else
	    referenceSize.height = size.height;

	component.setPreferredSize(size);

	return size;
    }
}

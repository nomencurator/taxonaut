/*
 * NameUsagePanel.java:  an abstract class providing a GUI to
 * NamedObject of Nomencurator
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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

import java.util.Locale;

import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.NameUsage;

/**
 * <CODE>NameUsagePanel</CODE> provides a GUI to access to
 * a <CODE>NameUsage</CODE> instance
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NameUsagePanel
    extends AbstractNameUsagePanel<NameUsage<?, ?>>
{
    private static final long serialVersionUID = -7062028044711547579L;

    /**
     * Constructs a <CODE>NameUsagePanel</CODE> for a new
     * <CODE>NameUsage</CODE> using default Locale
     */
    public NameUsagePanel()
    {
	this(new DefaultNameUsage());
    }

    /**
     * Constructs a <CODE>NameUsagePanel</CODE> for
     * <CODE>nameusage</CODE> using default Locale
     *
     * @param nameusage <CODE>NameUsage</CODE> to be handled by this 
     * <CODE>NameUsagePanel</CODE>
     *
     */
    public NameUsagePanel(NameUsage<?, ?> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs a <CODE>NameUsagePanel</CODE> for a new
     * <CODE>NameUsage</CODE> using <CODE>locale</CODE>
     *
     * @param locale <CODE>Locale</CODE> to be used to to
     * provides messages and layout of components in this
     * <CODE>NameUsagePanel</CODE>
     */
    public NameUsagePanel(Locale locale)
    {
	this(new DefaultNameUsage(), locale);
    }

    /**
     * Constructs a <CODE>NameUsagePanel</CODE> for
     * <CODE>nameusage</CODE> using <CODE>locale</CODE>
     *
     * @param nameusage <CODE>NameUsage</CODE> to be handled by this 
     * <CODE>NameUsagePanel</CODE>
     *
     * @param locale <CODE>Locale</CODE> to be used to to
     * provides messages and layout of components in this
     * <CODE>NameUsagePanel</CODE>
     */
    public NameUsagePanel(NameUsage<?, ?> nameUsage,
		       Locale locale)
    {
	super(nameUsage, locale);
    }

    /**
     * Constructs a <CODE>NameUsagePanel</CODE> for
     * <CODE>nameusage</CODE> using <CODE>locale</CODE>
     *
     * @param nameusage <CODE>NameUsage</CODE> to be handled by this 
     * <CODE>NameUsagePanel</CODE>
     *
     * @param locale <CODE>Locale</CODE> to be used to to
     * provides messages and layout of components in this
     * <CODE>NameUsagePanel</CODE>
     */
    public NameUsagePanel(NameUsage<?, ?> nameUsage,
		       Locale locale, boolean isDialog)
    {
	super(nameUsage, locale, isDialog);
    }

    protected NameUsagePanel createNameUsagePanel()
    {
	NameUsagePanel nameUsagePanel = new NameUsagePanel();
	nameUsagePanel.setModified(true);
	return nameUsagePanel;
    }

    protected NameUsage<?, ?> duplicate()
    {
	return new DefaultNameUsage(getNamedObject());
    }

    protected NameUsage<?, ?> createNamedObject()
    {
	return new DefaultNameUsage();
    }

}


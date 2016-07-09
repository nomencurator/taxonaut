/*
 * NameUsagePanel.java: an abstract class providing a GUI to
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
 * {@code NameUsagePanel} provides a GUI to access to
 * a {@code NameUsage} instance
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NameUsagePanel
    extends AbstractNameUsagePanel<NameUsage<?>>
{
    private static final long serialVersionUID = -7062028044711547579L;

    /**
     * Constructs a {@code NameUsagePanel} for a new
     * {@code NameUsage} using default Locale
     */
    public NameUsagePanel()
    {
	this(new DefaultNameUsage());
    }

    /**
     * Constructs a {@code NameUsagePanel} for
     * {@code nameusage} using default Locale
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code NameUsagePanel}
     *
     */
    public NameUsagePanel(NameUsage<?> nameUsage)
    {
	super(nameUsage);
    }

    /**
     * Constructs a {@code NameUsagePanel} for a new
     * {@code NameUsage} using {@code locale}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code NameUsagePanel}
     */
    public NameUsagePanel(Locale locale)
    {
	this(new DefaultNameUsage(), locale);
    }

    /**
     * Constructs a {@code NameUsagePanel} for
     * {@code nameusage} using {@code locale}
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code NameUsagePanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code NameUsagePanel}
     */
    public NameUsagePanel(NameUsage<?> nameUsage,
		       Locale locale)
    {
	super(nameUsage, locale);
    }

    /**
     * Constructs a {@code NameUsagePanel} for
     * {@code nameusage} using {@code locale}
     *
     * @param nameusage {@code NameUsage} to be handled by this 
     * {@code NameUsagePanel}
     *
     * @param locale {@code Locale} to be used to to
     * provides messages and layout of components in this
     * {@code NameUsagePanel}
     */
    public NameUsagePanel(NameUsage<?> nameUsage,
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

    protected NameUsage<?> duplicate()
    {
	return new DefaultNameUsage(getNamedObject());
    }

    protected NameUsage<?> createNamedObject()
    {
	return new DefaultNameUsage();
    }

}


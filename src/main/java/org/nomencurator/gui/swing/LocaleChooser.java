/*
 * LocaleChooser.java:  a JCombobox to choose a Locale
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

import javax.swing.JComboBox;

import org.nomencurator.gui.swing.model.LocaleModel;

import org.nomencurator.gui.swing.plaf.basic.BasicLocaleChooserRenderer;

/**
 * {@code LocaleChooser} provides JComboBox to choose a <tt>Locale</tt>.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class LocaleChooser
    extends JComboBox<Locale>
{
    private static final long serialVersionUID = 6939005657693201696L;

    /**
     * Constract a {@code LocaleChooser} using VM default locale.
     */
    @SuppressWarnings("unchecked")
    public LocaleChooser()
    {
	super(new LocaleModel());
	setRenderer(new BasicLocaleChooserRenderer());
    }

    @Override
    public Locale getSelectedItem()
    {
	Locale locale = null;

	Object selection = super.getSelectedItem();

	if(selection instanceof Locale) {
	    locale = (Locale)selection;
	    if(locale == LocaleModel.UNSPECIFIED)
		locale = null;
	}
	/*
	else if (selection instanceof String) {
	    String item = (String)selection;
	    if(item.length() > 0)
		locale = Locale.get(item);
	}
	else {
	    locale = Locale.get(selection.toString());
	}
	*/

	return locale;
    }

}
    

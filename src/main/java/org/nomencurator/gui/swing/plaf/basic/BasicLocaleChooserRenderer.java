/*
 * BasicLocaleChooserRenderer.java:  a ComboBoxRenderer drawing locale contents.
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

package org.nomencurator.gui.swing.plaf.basic;

import java.awt.Component;

import java.util.Locale;

import javax.swing.JList;

import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.nomencurator.util.NomenclaturalCode;

/**
 * {@code BasicLocaleChooserRenderer} provides a {@code ComboBoxRenderer} of
 * {@code Locale}s.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class BasicLocaleChooserRenderer
    extends BasicComboBoxRenderer
{
    private static final long serialVersionUID = -1876057911354736475L;

    public BasicLocaleChooserRenderer()
    {
    }
    
    @SuppressWarnings("rawtypes")
    public Component getListCellRendererComponent(
                                                 JList list,
                                                 Object value,
                                                 int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus)
    {
	if (value instanceof Locale) {
	    value = NomenclaturalCode.getCode().getDisplayName((Locale)value, list.getLocale());
	}

	return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}

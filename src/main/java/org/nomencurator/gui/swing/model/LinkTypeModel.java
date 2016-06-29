/*
 * LinkType.java:  managing link type of Annotation
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2004, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.model;

import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultComboBoxModel;

import org.nomencurator.model.LinkType;
import org.nomencurator.model.LinkTypes;

/**
 * {@code LinkTypeModel} provides a {@code ComboBoxModel}  to select a link type.
 *
 * @see org.nomencurator.model.LinkType
 * @see org.nomencurator.model.LinkTypes
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class LinkTypeModel
    extends DefaultComboBoxModel<LinkType>
    implements Observer
{
    private static final long serialVersionUID = 6483140713123518722L;

    static protected LinkTypes linkTypes = new LinkTypes();

    public LinkTypeModel()
    {
	super(linkTypes.getLinkTypes());
	linkTypes.addObserver(this);
    }

    static public LinkType get(String typeName)
    {
	return linkTypes.get(typeName);
    }

    static public void add(LinkType linkType)
    {
	linkTypes.add(linkType);
    }

    public void update(Observable observable, Object arg)
    {
    }
} 

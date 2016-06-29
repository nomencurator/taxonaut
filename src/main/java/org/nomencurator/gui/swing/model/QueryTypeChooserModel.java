/*
 * QueryTypeChooserModel.java:  a class provides layouted fields for query to 
 * Nomencurator.
 *
 * Copyright (c) 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.nomencurator.model.Agent;
import org.nomencurator.model.Annotation;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

/**
 * {@code QueryTypeChooserModel} provides a ComboBoxModel for {@code QueryPane}
 * to select type of query.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class QueryTypeChooserModel
    extends DefaultComboBoxModel<String>
{
    private static final long serialVersionUID = -2407403777088081432L;

    public QueryTypeChooserModel() {
	super();
	addElement("Agent");
	addElement("Annotation");
	addElement("Publication");
	addElement("NameUsage");
    }
}
    

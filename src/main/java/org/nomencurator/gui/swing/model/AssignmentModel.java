/*
 * AssignmentModel.java:  what do I do?
 *
 * Copyright (c) 2002, 2003, 2014, 2015, 2019 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP19K12711
 */

package org.nomencurator.gui.swing.model;

import java.util.Map;

import org.nomencurator.beans.PropertyChanger;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Rank;

/**
 * <code>AssignmentModel</code> to do something... assignment to what?  Mapping betwwn NameUsage and NameUsageNode?
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class AssignmentModel
    extends PropertyChanger
{
    Map<?, ?> assignments;

    Map<?, ?> rankedAssignments;
}

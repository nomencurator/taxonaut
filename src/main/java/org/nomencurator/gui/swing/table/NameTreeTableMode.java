/*
 * NameTreeTableMode.java: an enumeration  of analysis modes for NaeTreeTableModel
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.table;

/**
 * {@code NameTreeTalbeMode} enumerates data analysis modes of {@code NameTreeTableModel}
 *
 * @version 	12 Apr. 2016
 * @author 	Nozomi `James' Ytow
 */
public enum NameTreeTableMode
{
    ASSIGNMENTS,
    COMMONS,
    MISSINGS,
    DIFFERENCE,
    INCONSISTENCY,
    SYNONYMS
    ;
    
    protected int mask;
    
    private NameTreeTableMode()
    {
	mask = ordinal() - 1;
	mask = (mask == -1) ? mask : 1 << mask;
    }
    
    public int mask()
    {
	return mask;
    }
}

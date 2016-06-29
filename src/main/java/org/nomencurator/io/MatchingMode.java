/*
 * MatchingMode.java: an enumeration of matiching modes to retrieve data sources.
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

package org.nomencurator.io;

/**
 * <CODE>MatchingMode</CODE> enumerats matiching modes to retrieve data sources.
 *
 * @version 	04 Jan. 2015
 * @author 	Nozomi `James' Ytow
 */
public enum MatchingMode
{
    EXACT,
    FUZZY,
    SUGGEST,
    FULL_TEXT,
    CONTAINS
    ;

    public static String getResourcePrefix() {
	return "MATCHING_MODE_";
    }
}

/*
 * ParsedName.java:  a set of setter methods to extend org.gbif.api.model.checklistbank.ParsedName
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

package org.nomencurator.api.gbif.model.checklistbank;

import lombok.Setter;

/**
 * <tt>ParsedName</tt> extends <tt>org.gbif.api.model.checklistbank.ParsedName</tt>
 * to provide an auxiliary access to country code.
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class ParsedName extends org.gbif.api.model.checklistbank.ParsedName {

    @Setter
    private String canonicalName;

    public String getCanonicalName() {
	return canonicalName();
    }

    @Setter
    private String canonicalNameWithMarker;

    public String getCanonicalNameWithMarker() {
	return canonicalNameWithMarker();
    }

    @Setter
    private String canonicalNameComplete;

    public String getCanonicalNameComplete() {
	return canonicalNameComplete();
    }

    @Setter
    public String canonicalSpeciesName;

    public String getCanonicalSpeciesName() {
	return canonicalSpeciesName();
    }
}

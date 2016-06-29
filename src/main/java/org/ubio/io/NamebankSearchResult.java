/*
 * NamebankSearchResult.java: provides a container of uBio namebank_search result.
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

package org.ubio.io;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.ubio.model.ServiceData;
import org.ubio.model.ScientificName;
import org.ubio.model.VernacularName;

/**
 * {@code NamebankSearchResult} provides a contains of uBio namebank_search result.
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NamebankSearchResult
{
    @Getter
    @Setter
    protected ServiceData serviceData;
    @Getter
    @Setter
    protected String scientificQuery;
    @Getter
    @Setter
    protected List<ScientificName> scientificNames;
    @Getter
    @Setter
    protected String vernacularQuery;
    @Getter
    @Setter
    protected List<VernacularName> vernacularNames;

    public NamebankSearchResult()
    {
    }

    public NamebankSearchResult(ServiceData serviceData)
    {
	setServiceData(serviceData);
    }

    public NamebankSearchResult(ServiceData serviceData,
				List<ScientificName> scientificNames,
				List<VernacularName> verncularNames)
    {
	this(serviceData);
	setScientificNames(scientificNames);
	setVernacularNames(verncularNames);
    }
}

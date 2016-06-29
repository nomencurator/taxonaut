/*
 * ClassificationBankSearchResult.java: provides a container of uBio namebank_search result.
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

import lombok.Data;

import org.ubio.model.ServiceData;
import org.ubio.model.JuniorName;
import org.ubio.model.SeniorName;

/**
 * {@code ClassificationBankSearchResult} provides a contains of uBio namebank_search result.
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class ClassificationBankSearchResult
{
    protected ServiceData serviceData;
    protected List<SeniorName> seniorNames;
    protected List<JuniorName> juniorNames;

    public ClassificationBankSearchResult()
    {
    }

    public ClassificationBankSearchResult(ServiceData serviceData)
    {
	setServiceData(serviceData);
    }

    public ClassificationBankSearchResult(ServiceData serviceData,
				List<SeniorName> seniroNames,
				List<JuniorName> juniorNames)
    {
	this(serviceData);
	setSeniorNames(seniorNames);
	setJuniorNames(juniorNames);
    }
}

/*
 * ServiceData.java: provides a container of uBio ServiceData
 *
 * Copyright (c) 2007, 2015, 2016 Nozomi `James' Ytow
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

package org.ubio.model;

import lombok.Data;

/**
 * {@code ServiceData} provides a container of uBio ServiceData
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class ServiceData 
{
    protected String currentVersion;
    protected String dateStamp;
    protected String timeStamp;

    public ServiceData(String curretVersion,
		       String dateStamp,
		       String timeStamp)
    {
	setCurrentVersion(curretVersion);
	setDateStamp(dateStamp);
	setTimeStamp(timeStamp);
    }
}

/*
 * NamebankPackage.java: provides methods to access to uBio XML Webservices
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

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code NamebankPackage} provides methods to handle namebank package element returned from uBio XML Webservices
 *
 * @version 	20 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NamebankPackage
{
    @Getter
    @Setter
    protected int packageID;

    @Getter
    @Setter
    protected String packageName;

    @Getter
    @Setter
    protected int parentPackageID;

    @Getter
    @Setter
    protected int packageLevel;

    protected static Map<Integer, NamebankPackage> packages;

    public static NamebankPackage get(String packageID)
    {
	return get(Integer.valueOf(packageID).intValue());
    }

    public static NamebankPackage get(int packageID)
    {
	if(packages == null)
	    return null;

	return packages.get(String.valueOf(packageID));

    }

    public static NamebankPackage get(String packageID,
				      String packageName)
    {
	return get(Integer.valueOf(packageID).intValue(),
		   packageName);
    }

    public static NamebankPackage get(int packageID,
				      String packageName)
    {
	NamebankPackage pkg = get(packageID);

	if(pkg == null) {
	    pkg = new NamebankPackage(packageID, packageName);
	}
	return pkg;
    }


    protected NamebankPackage(int packageID,
			      String packageName)
    {
	setPackageID(packageID);
	setPackageName(packageName);

	if(packages == null) {
	    packages = new HashMap<Integer, NamebankPackage>();
	}

	packages.put(Integer.valueOf(packageID), this);
    }

}

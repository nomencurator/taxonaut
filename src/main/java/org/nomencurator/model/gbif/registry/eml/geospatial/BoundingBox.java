/*
 * BoundingBox.java: a Java implementation of BoundingBox for GBIF Registry API
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model.gbif.registry.eml.geospatial;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * {@code BoundingsBox} provides a workaound for {@code boundingBox} data handling using JASON.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class BoundingBox
    extends org.gbif.api.model.registry.eml.geospatial.BoundingBox
{
    private static final long serialVersionUID = -5309749317159753996L;

    public BoundingBox() {
	super();
    }

    public BoundingBox(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
	super(minLatitude,maxLatitude, minLongitude, maxLongitude);
    }

    public BoundingBox(org.gbif.api.model.registry.eml.geospatial.BoundingBox boundingBox) {
	this(boundingBox.getMinLatitude(),boundingBox.getMaxLatitude(), boundingBox.getMinLongitude(), boundingBox.getMaxLongitude());
    }

    public void setGlobalCoverage(boolean globalCoverage) {
	if(globalCoverage) 
	    super.setGlobalCoverage(-180, 180, -90, 90);
	else
	    super.setGlobalCoverage(0,0,0,0);
    }

    @JsonIgnore
    public void setGlobalCoverage(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
	super.setGlobalCoverage(minLatitude, maxLatitude, minLongitude, maxLongitude);
    }
}

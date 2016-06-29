/*
 * GeospatialCoverage.java: a Java implementation of GeospatialCoverage for GBIF Registry API
 *
 * Copyright (c) 2015 Nozomi `James' Ytow
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

import org.nomencurator.model.gbif.registry.eml.geospatial.BoundingBox;

public class GeospatialCoverage
    extends org.gbif.api.model.registry.eml.geospatial.GeospatialCoverage
{
    public GeospatialCoverage() {
    }

    public GeospatialCoverage(String description, org.gbif.api.model.registry.eml.geospatial.BoundingBox boundingBox) {
	super(description, boundingBox == null ? null : new BoundingBox(boundingBox));
    }

    public void setBoundingBox(org.gbif.api.model.registry.eml.geospatial.BoundingBox boundingBox) {
	if(boundingBox != null)
	    boundingBox = new BoundingBox(boundingBox);
	super.setBoundingBox(boundingBox);
    }
}

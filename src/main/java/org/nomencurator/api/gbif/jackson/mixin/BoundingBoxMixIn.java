/*
 * BoundingBoxMixIn.java: a mix in to deserialize BoundingBox from JSON using Jackson
 *
 * Copyright (c) 2016, 2020 Nozomi `James' Ytow
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

package org.nomencurator.api.gbif.jackson.mixin;

// for Jackson 1.x
// import org.codehaus.jackson.map.annotate.JsonDeserialize;
// or Jackson 2.x
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.gbif.api.model.registry.eml.geospatial.BoundingBox;

import org.nomencurator.api.gbif.jackson.deserialize.BoundingBoxDeserializer;

/**
 * {@code BoundingBoxMixIn} provieds mix in to deserialize {@link BoundingBox}. into JSON using {@link BoundingBoxDesrializer}.
 *
 * @version 	10 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
public interface BoundingBoxMixIn
{
    @JsonDeserialize(using = BoundingBoxDeserializer.class)
    BoundingBox getBoundingBox();
}

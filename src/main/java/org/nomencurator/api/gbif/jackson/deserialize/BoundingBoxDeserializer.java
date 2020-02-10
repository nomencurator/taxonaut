/*
 * BoundingBoxDeserializer.java: a Jackson JSON deserializer of BoundingBox for GBIF Registry API
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

package org.nomencurator.api.gbif.jackson.deserialize;

import java.io.IOException;

// for Jackson 1.x
// import org.codehaus.jackson.JsonNode;
// import org.codehaus.jackson.JsonParser;
// import org.codehaus.jackson.map.DeserializationContext;
// import org.codehaus.jackson.map.JsonDeserializer;
// or Jackson 2.x
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;

import org.gbif.api.model.registry.eml.geospatial.BoundingBox;

/**
 * {@code BoundingBoxDeserializer} provides a Jackson  {@link JsonDeserializer} for {@link BoundingBox}.
 *
 * @version 	10 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
public class BoundingBoxDeserializer extends JsonDeserializer<BoundingBox> {
    @Override public BoundingBox deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException
    {
	JsonNode node = jsonParser.getCodec().readTree(jsonParser);
	/* Jackson 1.x
	return new BoundingBox(node.get("minLatitude").getDoubleValue(),
			       node.get("maxLatitude").getDoubleValue(),
			       node.get("minLongitude").getDoubleValue(),
			       node.get("maxLongitude").getDoubleValue()
			       );
	*/
	/* Jackson 2.x */
	return new BoundingBox(node.get("minLatitude").doubleValue(),
			       node.get("maxLatitude").doubleValue(),
			       node.get("minLongitude").doubleValue(),
			       node.get("maxLongitude").doubleValue()
			       );
    }
}

/*
 * SetDesrializert.java:  a deserizaliser for Set
 *
 * Copyright (c) 2020 Nozomi `James' Ytow
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

// for Jackson 1.x
// import org.codehaus.jackson.map.ObjectMapper;
// import org.codehaus.jackson.type.TypeReference;
// or Jackson 2.x
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Strings;

import java.io.IOException;

import java.util.Set;

import org.gbif.api.vocabulary.Country;

import org.nomencurator.api.gbif.jackson.ObjectMapperFactory;
import org.nomencurator.api.gbif.jackson.deserialize.SetDeserializer;

/**
 * {@code SetDeserializer} provides a {@link JsonDeserializer} for {@link Set},
 *
 * @version 	09 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
public class CountrySetDeserializer extends SetDeserializer<Country> {
    /*
    @Override
    public Set<T> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
	ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
	Set<T> set = mapper.readValue(jp, new TypeReference<Set<T>>() {});
	return set;
    }
    */
}

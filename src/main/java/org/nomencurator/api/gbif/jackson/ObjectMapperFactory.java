/*
 * ObjectMapperFactory.java:  a factory class to 
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

package org.nomencurator.api.gbif.jackson;

// for Jackson 1.x
// import org.codehaus.jackson.map.ObjectMapper;
// or Jackson 2.x
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@code ObjectMapperFactory} provides a factory method to provide singe, shared {@link ObjectMapper}.
 *
 * @version 	10 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
public class ObjectMapperFactory {

    static final ObjectMapper mapper = new ObjectMapper();

    private ObjectMapperFactory() {}

    public static ObjectMapper getObjectMapper() {
	return mapper;
    }
}

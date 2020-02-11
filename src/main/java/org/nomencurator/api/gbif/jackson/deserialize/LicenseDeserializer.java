/*
 * LicenseDesrializert.java:  a deserizaliser for License
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

/*
 * Copyright 2014 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.google.common.base.Strings;

import java.io.IOException;
import java.util.Optional;

import org.gbif.api.vocabulary.License;

/**
 * {@code LicenseDeserializer} provides a Jackson  {@link JsonDeserializer} for {@link License},
 * adopted from {@link LicenseSerde.LenienseJsonDesrializer} code copyrighted by GBIF.
 *
 * @version 	11 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
public class LicenseDeserializer extends JsonDeserializer<License> {
    @Override
    public License deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      if (jp.getCurrentToken() == JsonToken.VALUE_STRING) {
	  return getLicense(jp.getText());
      }
      return getLicense((String) ctxt.handleUnexpectedToken(String.class, jp.getCurrentToken(), jp, "Expected String", jp.getText()));
    }

    static public License getLicense(String expression) {
        if (Strings.isNullOrEmpty(expression)) {
          return License.UNSPECIFIED;
        }
        // first, try by url
        Optional<License> license = License.fromLicenseUrl(expression);
        if (license.isPresent()) {
          return license.get();
        }

        // then, try by name
        return License.fromString(expression).orElse(License.UNSUPPORTED);
    }
}

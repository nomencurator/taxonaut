/*
 * LanguageDeserializer.java:  a deserializer for License object
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

import com.google.common.annotations.VisibleForTesting;

// for Jackson 1.x
// import org.codehaus.jackson.map.ObjectMapper;
// import org.codehaus.jackson.type.TypeReference;
// or Jackson 2.x
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

import org.gbif.api.vocabulary.Language;

/**
 * {@code LanguageDeserializer} provides a Jackson  {@link JsonDeserializer} for {@link Language},
 * adopted from {@link Language.LenientDesrializer} code copyrighted by GBIF.
 *
 * @version 	11 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
public class LanguageDeserializer extends JsonDeserializer<Language> {
    @Override
    public Language deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
      try {
        if (jp != null && jp.getTextLength() > 0) {
          return lenientParse(jp.getText());
        } else {
          return Language.UNKNOWN; // none provided
        }
      } catch (Exception e) {
	  return lenientParse((String) ctxt.handleUnexpectedToken(String.class, jp.getCurrentToken(), jp,
								  "Unable to deserialize language from provided value (hint: not an ISO 2 or 3 character?): "
								  + jp.getText(), jp.getText()));
      }
    }

    @VisibleForTesting
    static Language lenientParse(String value) {
      Language l = Language.fromIsoCode(value);
      // backwards compatible
      if (Language.UNKNOWN == l) {
        try {
          l = Language.valueOf(value);
        } catch( IllegalArgumentException e) {
          l = Language.UNKNOWN;
        }
      }
      return l;
    }
}

/*
 * LanguageMixIn.java: a mix in to deserialize Language from JSON using Jackson
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

package org.nomencurator.api.gbif.jackson.mixin;

// for Jackson 1.x
// import org.codehaus.jackson.map.annotate.JsonDeserialize;
// or Jackson 2.x
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.nomencurator.api.gbif.jackson.deserialize.LanguageDeserializer;

import org.gbif.api.vocabulary.Language;

/**
 * {@code LanguageMixIn} provieds a mix in to deserialize  JSON int {@link Language} using {@link LanguageDesrializer}.
 *
 * @version 	10 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
public interface LanguageMixIn
{
    @JsonDeserialize(using = LanguageDeserializer.class)
    Language getLanguage();
}

/*
 * NameUsageMixIn.java: a mix in to deserialize NameUsage from JSON using Jackson
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
// import org.codehaus.jackson.annotate.JsonIgnore;
// import org.codehaus.jackson.annotate.JsonIgnoreProperties;
// or Jackson 2.x
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.gbif.api.model.checklistbank.NameUsage;

/**
 * {@code NameUsageMixIn} provieds mix in to deserialize {@link NameUsage}. into JSON using {@link NameUsageDesrializer}.
 *
 * @version 	10 Feb. 2020
 * @author 	Nozomi `James' Ytow
 */
@JsonIgnoreProperties({"synonym"})
public interface NameUsageMixIn
{
}

/*
 * Distribution.java:  a set of setter methods to extend org.gbif.api.model.Distribution
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.api.gbif.model.checklistbank;

import org.gbif.api.vocabulary.Country;

/**
 * <tt>Distribuion</tt> extends <tt>org.gbif.api.model.checklistbank.Distribution</tt>
 * to provide an auxiliary access to country code.
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class Distribution extends org.gbif.api.model.checklistbank.Distribution {

  /**
   * @param countryCode the country code of the Country to set
   */
  public void setCountry(String countryCode) {
      setCountry(Country.fromIsoCode(countryCode));
  }
}

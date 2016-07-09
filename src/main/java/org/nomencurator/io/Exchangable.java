/*
 * Exchangable.java: an interface of Object
 * exchangable with sources using ObjectExchanger.
 *
 * Copyright (c) 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.io;

import org.nomencurator.model.NamedObject;

/**
 * {@code Exchangable} defines data exchangable with
 * data sources implementing {@code ObjectExchanger}
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface Exchangable<T extends NamedObject<?>>
{
    /**
     * Returns a {@code ObjectExchanger} to access to the data source,
     * or null if it is an internal object
     */
    public ObjectExchanger<T> getExchanger();

    /**
     * Sets {@code exchanger} as the {@code ObjectExchanger}
     * to access to the data source, or null to prohibit access to
     * external data storage.
     *
     * @param exchanger an {@code ObjectExchanger} used as exteral
     * data storage
     */
    public void setExchanger(ObjectExchanger<T> exchanger);
}

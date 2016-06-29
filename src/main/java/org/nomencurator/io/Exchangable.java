/*
 * Exchangable.java: an interface of <CODE>Object</CODE>
 * exchangable with sources using <CODE>ObjectExchanger</CODE>
 *
 * Copyright (c) 2006, 2014, 2015 Nozomi `James' Ytow
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
 * <CODE>Exchangable</CODE> defines data exchangable with
 * data sources implementing <CODE>ObjectExchanger</CODE>
 *
 * @version 	08 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface Exchangable<N extends NamedObject<?, ?>, T extends N>
								    //public interface Exchangable<N extends NamedObject<?, ?>, T extends N, E extends NamedObject<N, T>>
{
    /**
     * Returns a <CODE>ObjectExchanger</CODE> to access to the data source,
     * or null if it is an internal object
     */
    public ObjectExchanger<N, T> getExchanger();
    //public ObjectExchanger<N, T, E> getExchanger();

    /**
     * Sets <CODE>exchanger</CODE> as the <CODE>ObjectExchanger</CODE>
     * to access to the data source, or null to prohibit access to
     * external data storage.
     *
     * @param exchanger an <CODE>ObjectExchanger</CODE> used as exteral
     * data storage
     */
    public void setExchanger(ObjectExchanger<N, T> exchanger);
    //public void setExchanger(ObjectExchanger<N, T, E> exchanger);
}

/*
 * RoughMap.java:  a class to provide a rough set
 * for TaxoNote based on Nomencurator data model
 *
 * Copyright (c) 2002, 2014, 2015 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.util;

import java.util.List;

import org.nomencurator.util.Map;

/**
 * <code>RoughMap</code> is an interface to implement Rough set like operation
 * using <tt>Map</tt>s
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface RoughMap<K, V>
{
    public Map<K, V> getPositive();

    public void setPositive(Map<K, V> positive);

    public Map<K, V> getNegative();

    public void setNegatives(Map<K, V> negative);

    public Map<K, V> getBoundary();

    public void setBoundary(Map<K, V> boundary);

    public Map<K, V> getUpper();

    public RoughMap<K, V> intersection(RoughMap<K, V>  roughMap);

    public RoughMap<K, V> union(RoughMap<K, V> roughMap);
    
    public List<? extends Map<K, V>> crossSection(RoughMap<K, V> roughMap);

}

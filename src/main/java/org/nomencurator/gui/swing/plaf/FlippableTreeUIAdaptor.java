/*
 * FlippableTreeUIAdaptor.java:  an method to be shared by FlippableTreeUI implemantations
 *
 * Copyright (c) 2022 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.gui.swing.plaf;

import javax.swing.tree.AbstractLayoutCache;

import org.nomencurator.gui.swing.Flippable;

/**
 * {@code FlippableTreeUIAdaptor} provides a convenience method shared by  {@code FlippableTreeUI} instances.
 *
 * @version 	03 May. 2022
 * @author 	Nozomi `James' Ytow
 */
public class FlippableTreeUIAdaptor
{
    /**
     * Returns the {@code Flipper} of given {@code ui}
     *
     * @param ui {@code FlippableTreeUI} to handle
     * @return Flipper of the instance, of null if unavailable
     */
    public static Flippable getFlipper(FlippableTreeUI ui)
    {
	AbstractLayoutCache layoutCache = ui.getLayoutCache();
	if(layoutCache instanceof Flippable)
	    return (Flippable)layoutCache;
	return null;
    }
}

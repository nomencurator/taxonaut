/*
 * UIBroaker.java: defines API to manage UI loading scheme
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

import java.beans.PropertyChangeListener;
import javax.swing.LookAndFeel;

/**
 * {@code UIBroaker} provides API defintion to manga UI loading sheme.
 *
 * @version 	09 May 2022
 * @author 	Nozomi `James' Ytow
 */
public interface UIBroaker
{
    /**
     * Setup {@code UIBroaker} to load UI class approriate to given P
     *
     * @param shorName of which {@code LookAndFeel} instence to look up
     * @return class name of {@code LookAndFeel} instence class name having {@code shortNam}
     */
    public void setUIBroaker(LookAndFeel lookAndFeel);
}

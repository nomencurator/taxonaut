/*
 * PropertyChangeable.java:  an interface to ensure an Object listenable
 *
 * Copyright (c) 2019 Nozomi `James' Ytow
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

package org.nomencurator.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * {@code PropertyChangeable} ensure an Object is listenable by {@code PropertyChangeEvent}.
 * 
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public interface PropertyChangeable
{
    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
    public void firePropertyChange(PropertyChangeEvent evt);
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue);
    public void firePropertyChange(String propertyName, int oldValue, int newValue);
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);
    public PropertyChangeListener[] getPropertyChangeListeners();
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName);
    public boolean hasListeners(String propertyName);
    public void removePropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}

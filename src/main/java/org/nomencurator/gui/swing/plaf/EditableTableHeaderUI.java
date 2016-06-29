/*
 * EditableTableHeaderUI.java
 *
 * Copyright (c) 2005, 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.plaf;

import java.awt.Component;

import java.awt.event.MouseEvent;

/**
 * <code>EditableTableHedearUI</code> definces editable header API
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface EditableTableHeaderUI
{
    /**
     * Returns true if the heder is under edition
     *
     * @return true if the heder is under edition
     */
    public boolean isEditing();

    /**
     * Returns <CODE>Component</CODE> editing the header
     * or null if not under edition.
     *
     * @return <CODE>Component</CODE> editing the header
     * or null if not under edition.
     */
    public Component getEditorComponent();

    /**
     * Converts <CODE>event</CODE> to a <CODE>MouseEvent</CODE>
     * in the <CODE>destination</CODE>
     *
     * @param event source <CODE>MouseEvent</CODE>
     * @param destination <CODE>Componenet</CODE> to which the
     * <CODE>event</CODE> to be converted
     *
     * @return converted <CODE>MouseEvent</CODE>
     */
    public MouseEvent convertMouseEvent(MouseEvent event,
					Component destination);

    /**
     * Converts <CODE>event</CODE> on <CODE>source</CODE> to a
     * <CODE>MouseEvent</CODE> in the <CODE>destination</CODE>
     *
     * @param source where the <CODE>event</CODE> happened
     * @param event <CODE>source</CODE> <CODE>MouseEvent</CODE>
     * @param destination <CODE>Componenet</CODE> to which the
     * <CODE>event</CODE> to be converted
     *
     * @return converted <CODE>MouseEvent</CODE>
     */
    public MouseEvent convertMouseEvent(Component source,
					MouseEvent event,
					Component destination);
}

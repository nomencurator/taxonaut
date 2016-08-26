/*
 * ProgressEvent.java: Event encapsulating a progress report
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

import java.util.EventObject;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code ProgressEvent} provides an event to inform the progress of a process.
 *
 * @version 	27 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class ProgressEvent
    extends EventObject
{
    private static final long serialVersionUID = -2644906561014363108L;

    /** Minimum value of progress, typically zero */
    @Getter @Setter protected int minimum;

    /** Maximum value of progress, or -1 if unlimited */
    @Getter @Setter protected int maximum;

    /** Current value of progress */
    @Getter @Setter protected int current;

    /** Optional message of the event */
    @Getter @Setter protected String message;

    public ProgressEvent(Object source)
    {
	this(source, 0, 100, 0, null);
    }

    public ProgressEvent(Object source, int minimum, int maximum, int current, String message)
{
	super(source);
	setMinimum(minimum);
	setMaximum(maximum);
	setCurrent(current);
	setMessage(message);
    }

}

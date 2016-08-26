/*
 * ProgressLisetener.java: an intreface to listen for ProgressEvents
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
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

import java.util.EventListener;

/**
 * {@code ProgressListener} defines an interface to listen for {@code ProgressEvent}s.
 *
 * @version 	26 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public interface ProgressListener
    extends EventListener
{
    /**
     * Invoked when the target of the listener has made a progress.
     *
     * @param event a {@code ProgressEvent} object
     */
    public void progressMade(ProgressEvent event);
}

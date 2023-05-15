/*
 * Base64Codex.java: provides a codex for Base64 and UTF-8
 *
 * Copyright (c) 2016, 2019 Nozomi `James' Ytow
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

package org.ubio.util;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import java.util.Objects;

/**
 * {@code Base64Codex} provides a codex for Base64 and UTF-8.
 *
 * @version 	02 Dec 2019
 * @author 	Nozomi `James' Ytow
 */
public class Base64Codex
{
    protected static final String UTF8="UTF-8";

    protected static Encoder encoder;
    protected static Decoder decoder;

    public static String encode(String text)
    {
	if(text == null)
	    return text;
	try {
	    if (Objects.isNull(encoder))
		encoder = Base64.getEncoder();
	    return encoder.encodeToString(text.getBytes());
	}
	catch (Throwable e) {
	    return null;
	}
    }

    public static String decode(String base64)
    {
	if(base64 == null)
	    return null;

	try {
	    if (Objects.isNull(decoder))
		decoder = Base64.getDecoder();
	    return new String(decoder.decode(base64), UTF8);
	}
	catch (Throwable e) {
	    return null;
	}
    }
}

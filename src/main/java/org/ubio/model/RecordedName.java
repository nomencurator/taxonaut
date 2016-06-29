/*
 * RecoededName.java: uBio's  RecoededName
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

package org.ubio.model;

import java.io.Serializable;

import java.util.Objects;

import lombok.Data;

/**
 * An implementation of uBio {@code RecordedName}.
 *
 *
 * @version 	21 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class RecordedName implements Serializable
{
    private static final long serialVersionUID = 7522206561038536790L;

    protected int namebankID;
    protected String nameString;
    protected String fullNameString;

    public RecordedName()
    {
    }

    public RecordedName(int namebankID,
			String nameString,
			String fullNameString)
    {
	setNamebankID(namebankID);
	setNameString(nameString);
	setFullNameString(fullNameString);
    }

    @Override
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final RecordedName theOther = (RecordedName) object;
	return super.equals(object)
	    && Objects.equals(this.getNamebankID(), theOther.getNamebankID())
	    && Objects.equals(this.getNameString(), theOther.getNameString())
	    && Objects.equals(this.getFullNameString(), theOther.getFullNameString())
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getNamebankID(),
			    getNameString(),
			    getFullNameString()
			    );
    }
}

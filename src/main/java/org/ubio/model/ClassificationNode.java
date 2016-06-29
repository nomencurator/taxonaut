/*
 * ClassificationNode.java: provides container of uBio Ancestor and Child
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code ClassificationNode} provides container of uBio Ancestor and Child.
 *
 * @version 	30 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class ClassificationNode
    extends Synonym
{
    private static final long serialVersionUID = -6905952456691875163L;

    @Getter
    @Setter
    protected int classificationBankID;
    @Getter
    @Setter
    protected String rankName;

    private static final List<Citation> emptyCitations = Collections.emptyList();

    public ClassificationNode()
    {
    }

    public void setCitations(List<Citation> citations)
    {
    }

    public List<Citation> getCitations()
    {
	return emptyCitations;
    }

    @Override
    public boolean equals(Object object) {
	if(object == this) return true;
	if(object == null) return false;
	if(getClass() != object.getClass()) return false;

	final ClassificationNode theOther = (ClassificationNode) object;
	return super.equals(object)
	    && Objects.equals(this.getClassificationBankID(), theOther.getClassificationBankID())
	    && Objects.equals(this.getRankName(), theOther.getRankName())
	    ;
    }

    @Override
    public int hashCode() {
	return Objects.hash(super.hashCode(),
			    getClassificationBankID(),
			    getRankName()
			    );
    }
}

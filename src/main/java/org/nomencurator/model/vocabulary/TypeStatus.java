/*
 * TypeStatus.java:  an enumeration of type status
 *
 * Copyright (c) 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model.vocabulary;

import lombok.Getter;

/**
 * Enumeration of nomenclature codes
 *
 * @version 	05 July 2016
 * @author 	Nozomi `James' Ytow
 */
public enum TypeStatus
{
    ALLOLECTOTYPE ("allolectotype",null),
    ALLONEOTYPE ("alloneotype",null),
    ALLOTYPE ("allotype",null),
    COTYPE("cotype",null),
    EPITYPE("epitype",null),
    EXEPITYPE("exepitype",null),
    EXHOLOTYPE("exholotype",null),
    EXISOTYPE("exisotype",null),
    EXLECTOTYPE("exlectotype",null),
    EXNEOTYPE("exneotype",null),
    EXPARATYPE("exparatype",null),
    EXSYNTYPE("exsyntype",null),
    EXTYPE("extype",null),
    GENOTYPE("genotype",null),
    HAPANTOTYPE("hapantotype",null),
    HOLOTYPE("holotype",null),
    ICONOTYPE("iconotype",null),
    ISOLECTOTYPE("isolectotype",null),
    ISONEOTYPE("isoneotype",null),
    ISOTYPE("isotype",null),
    LECTOTYPE("lectotype",null),
    NEOTYPE("neotype",null),
    /**
     * A constant indicates non-specimen type, i.e. type taxon.
     * The word "nominotype" was taken from ICZN 4th ed. 
     * Art. 37, 44, 47 and 61.2.
     */
    NOMINOTYPE("nominotype",null),
    ORIGINALMATERIAL("original material",null),
    PARALECTOTYPE("paralectotype",null),
    PARANEOTYPE("paraneotype",null),
    PARATYPE("paratype",null),
    SYNTYPE("syntype",null),
    TOPOTYPE("topotype",null),
    TYPE_SERIES("type series",null),
    TYPE_HOST("type host",null),
    UNDEFINED("undefined",null)
    ;

    @Getter
    private String typeStatus;

    private String description;

    TypeStatus(String typeStatus, String description) {
	this.typeStatus = typeStatus;
	this.description = description;
    }

    /**
     * Convert {@code typeStatus} to a {@code TypeStatus},
     * or null if {@code typeStatus} is null.
     *
     * @param typeStatus a {@code String} representing name of type status.
     * @return TypeStatus of given name, or TyeStatus.UNDEFINED if the name is
     * not listed in {@code TypeStatus}.
     */
    public static TypeStatus typeStatus(String typeStatus)
    {
	TypeStatus status = null;
	if (typeStatus != null && typeStatus.length() > 0) {
	    try {
		status = valueOf(typeStatus);
	    }
	    catch (IllegalArgumentException e) {
		status = UNDEFINED;
	    }
	}
	return status;
    }
}

/*
 * AnnotationType.java:  an enumeration of annotation types
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public enum AnnotationType
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
    NOMINOTYPE("nominotype",null),
    ORIGINALMATERIAL("original material",null),
    PARALECTOTYPE("paralectotype",null),
    PARANEOTYPE("paraneotype",null),
    PARATYPE("paratype",null),
    SYNTYPE("syntype",null),
    TOPOTYPE("topotype",null),
    TYPE_SERIES("type series",null),
    TYPE_HOST("type host",null)
    ;

    @Getter
    private String typeName;

    private String description;

    AnnotationType(String typeName, String description) {
	this.typeName = typeName;
	this.description = description;
    }
}

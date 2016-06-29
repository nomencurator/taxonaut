/*
 * FlatTableParser.java
 *
 * Copyright (c) 2005, 2015 Nozomi `James' Ytow
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

package org.nomencurator.io.sql;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;

import java.text.MessageFormat;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.Agent;
import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Person;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

/**
 * <CODE>FlatTableParser</CODE> parse a flat table to generate
 * a set of object as tapules in tables of a DBMS.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.NamedObject
 * @see java.sql.Connection
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class FlatTableParser
{
    public FlatTableParser()
    {
    }
}

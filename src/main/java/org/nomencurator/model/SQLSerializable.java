/*
 * SQLSerializable.java: an interface provaides methods to save to and
 * read from a database.
 *
 * Copyright (c) 2003, 2004, 2015 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.nomencurator.io.sql.NamedObjectConnection;

/**
 * {@code SQLSerializable} provides an interface to save/retrieve
 * an{@code Object} to/from a database accessible via SQL.
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface SQLSerializable<T extends NamedObject<?>>
{
    /**
     * Sets {@code sql} as a SQL to create table
     * in DBMS specified bye {@code sqlType}
     * for the {@code SQLObject}
     *
     * @param sqlType {@code Locale} representing the target SQL subset
     *
     * @param key {@code String} representing resource key to access to the SQL
     *
     * @param sql {@code String} representing the SQL to create the table
     *
     * @param parameters {@code String} representing parameters of the SQL
     */
    /*
    public void setCreateTableSQL(Locale sqlType,
				  String key,
				  String sql,
				  String parameters);
    */

    /**
     * Returns a SQL to create a relevant table in given
     * {@code sqlType}
     *
     * @param sqlType {@code Locale} representing the target SQL subset
     *
     * @return String representing a SQL to create relevant table
     */
    public String getCreateTableSQL(Locale sqlType);

    /**
     * Appends a SQL to create a relevant table at the end of
     * {@code buffer}.  If {@code buffer} is null,
     * it creates a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * SQL to be appended, or null to create a new {@code StringBuffer}
     * @param sqlType {@code Locale} representing the target SQL subset
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer getCreateTableSQL(StringBuffer buffer,
					  Locale sqlType);

    /**
     * Returns a SQL to insert the object to a relevant table
     * with {@code objectID}
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return String representing a SQL to insert the object
     * to a relevant table
     */
    public String toSQL(int objectID);

    /**
     * Appends a SQL to insert to a relevant table at the end of
     * {@code buffer}.  If {@code buffer} is null,
     * it creates a new {@code StringBuffer}.
     *
     * @param buffer {@code StringBuffer} to which the
     * SQL to be appended, or null to create a new {@code StringBuffer}
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer toSQL(StringBuffer buffer, int objectID);

    /**
     * Inserts the object to the database specified by the
     * {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return object ID of inserted object
     */
    public int insert(Connection connection)
    	throws SQLException;

    /**
     * Inserts the object to the database specified by the
     * {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return object ID of inserted object
     */
    public int update(Connection connection)
    	throws SQLException;

    /**
     * Inserts the object to the database specified by the
     * {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return object ID of inserted object
     */
    public int delete(Connection connection)
    	throws SQLException;

    /**
     * Returns object ID of the named object, or zero if the
     * object doesn't exist in the database behind
     * given {@code connection}
     *
     * @param connection {@code NamedObjectConnection} to the database
     *
     * @return the object ID of the NamedObject in the {@code connection}
     * or zero if it is no in the database.
     */
    public int getObjetID(NamedObjectConnection<? extends T> connection)
    	throws SQLException;

    /**
     * Sets {@code objectID} of the NamedObject for the
     * object in the database behind {@code connection}.
     *
     * @param objectID {@code int} of object ID in the database
     * @param connection {@code NamedObjectConnection} to the database
     */
    public void setObjetID(int objectID, NamedObjectConnection<? extends T> connection);
    //throws SQLException;

    /**
     * Returns a precompiled statement to insert an object
     * of this class to {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return List containing precompiled SQL statement to insert
     */
    public List<PreparedStatement> prepareInsertStatement(Connection connection)
    	throws SQLException;

    /**
     * Returns a precompiled statement to insert an object
     * of this class to {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return List containing precompiled SQL statement to update
     */
    public List<PreparedStatement> prepareUpdateStatement(Connection connection)
    	throws SQLException;

    /**
     * Returns a precompiled statement to delete an object
     * of this class in {@code connection}
     *
     * @param connection {@code Connection} to the database
     *
     * @return List containing precompiled SQL statement to delete
     */
    public List<PreparedStatement> prepareDeleteStatement(Connection connection)
    	throws SQLException;

    /**
     * Sets paremeters to {@code PreparedStatement} in 
     * {@code statements} to INSERT to the database
     *
     * @param statements {@code Iterator} of 
     * {@code PreparedStatement} to which 
     * paramters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setInsertParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException;

    /**
     * Sets paremeters to {@code PreparedStatement} in 
     * {@code statements} to UPDATE data in the database
     *
     * @param statements {@code Iterator} of 
     * {@code PreparedStatement} to which 
     * paramters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setUpdateParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException;

    /**
     * Sets paremeters to {@code PreparedStatement} in 
     * {@code statements} to DELETE data in the database
     *
     * @param statements {@code Iterator} of 
     * {@code PreparedStatement} to which 
     * paramters to be set
     *
     * @param objectID unique ID number in {@code long}
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setDeleteParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException;
}

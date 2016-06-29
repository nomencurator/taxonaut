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

package org.nomencurator.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

//import org.nomencurator.sql.NamedObjectConnection;
import org.nomencurator.io.sql.NamedObjectConnection;

/**
 * <CODE>SQLSerializable</CODE> provides an interface to save/retrieve
 * an<CODE>Object</CODE> to/from a database accessible via SQL.
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface SQLSerializable<N extends NamedObject<?, ?>, T extends N>
{
    /**
     * Sets <CODE>sql</CODE> as a SQL to create table
     * in DBMS specified bye <CODE>sqlType</CODE>
     * for the <CODE>SQLObject</CODE>
     *
     * @param sqlType <CODE>Locale</CODE> representing the target SQL subset
     *
     * @param key <CODE>String</CODE> representing resource key to access to the SQL
     *
     * @param sql <CODE>String</CODE> representing the SQL to create the table
     *
     * @param parameters <CODE>String</CODE> representing parameters of the SQL
     */
    /*
    public void setCreateTableSQL(Locale sqlType,
				  String key,
				  String sql,
				  String parameters);
    */

    /**
     * Returns a SQL to create a relevant table in given
     * <CODE>sqlType</CODE>
     *
     * @param sqlType <CODE>Locale</CODE> representing the target SQL subset
     *
     * @return String representing a SQL to create relevant table
     */
    public String getCreateTableSQL(Locale sqlType);

    /**
     * Appends a SQL to create a relevant table at the end of
     * <CODE>buffer</CODE>.  If <CODE>buffer</CODE> is null,
     * it creates a new <CODE>StringBuffer</CODE>.
     *
     * @param buffer <CODE>StringBuffer</CODE> to which the
     * SQL to be appended, or null to create a new
     * CODE>StringBuffer</CODE>
     * @param sqlType <CODE>Locale</CODE> representing the target SQL subset
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer getCreateTableSQL(StringBuffer buffer,
					  Locale sqlType);

    /**
     * Returns a SQL to insert the object to a relevant table
     * with <CODE>objectID</CODE>
     *
     * @param objectID unique ID number in <CODE>long</CODE>
     * to be assigned to the object on the databaes
     *
     * @return String representing a SQL to insert the object
     * to a relevant table
     */
    public String toSQL(int objectID);

    /**
     * Appends a SQL to insert to a relevant table at the end of
     * <CODE>buffer</CODE>.  If <CODE>buffer</CODE> is null,
     * it creates a new <CODE>StringBuffer</CODE>.
     *
     * @param buffer <CODE>StringBuffer</CODE> to which the
     * SQL to be appended, or null to create a new
     * CODE>StringBuffer</CODE>
     *
     * @param objectID unique ID number in <CODE>long</CODE>
     * to be assigned to the object on the databaes
     *
     * @return StringBuffer containing the SQL at the end
     */
    public StringBuffer toSQL(StringBuffer buffer, int objectID);

    /**
     * Inserts the object to the database specified by the
     * <CODE>connection</CODE>
     *
     * @param connection <CODE>Connection</CODE> to the database
     *
     * @return object ID of inserted object
     */
    public int insert(Connection connection)
    	throws SQLException;

    /**
     * Inserts the object to the database specified by the
     * <CODE>connection</CODE>
     *
     * @param connection <CODE>Connection</CODE> to the database
     *
     * @return object ID of inserted object
     */
    public int update(Connection connection)
    	throws SQLException;

    /**
     * Inserts the object to the database specified by the
     * <CODE>connection</CODE>
     *
     * @param connection <CODE>Connection</CODE> to the database
     *
     * @return object ID of inserted object
     */
    public int delete(Connection connection)
    	throws SQLException;

    /**
     * Returns object ID of the named object, or zero if the
     * object doesn't exist in the database behind
     * given <CODE>connection</CODE>
     *
     * @param connection <CODE>NamedObjectConnection</CODE> to the database
     *
     * @return the object ID of the NamedObject in the <CODE>connection</CODE>
     * or zero if it is no in the database.
     */
    public int getObjetID(NamedObjectConnection<? extends N> connection)
    	throws SQLException;

    /**
     * Sets <CODE>objectID</CODE> of the NamedObject for the
     * object in the database behind <CODE>connection</CODE>.
     *
     * @param objectID <CODE>int</CODE> of object ID in the database
     * @param connection <CODE>NamedObjectConnection</CODE> to the database
     */
    public void setObjetID(int objectID, NamedObjectConnection<? extends N> connection);
    //throws SQLException;

    /**
     * Returns a precompiled statement to insert an object
     * of this class to <CODE>connection</CODE>
     *
     * @param connection <CODE>Connection</CODE> to the database
     *
     * @return List containing precompiled SQL statement to insert
     */
    public List<PreparedStatement> prepareInsertStatement(Connection connection)
    	throws SQLException;

    /**
     * Returns a precompiled statement to insert an object
     * of this class to <CODE>connection</CODE>
     *
     * @param connection <CODE>Connection</CODE> to the database
     *
     * @return List containing precompiled SQL statement to update
     */
    public List<PreparedStatement> prepareUpdateStatement(Connection connection)
    	throws SQLException;

    /**
     * Returns a precompiled statement to delete an object
     * of this class in <CODE>connection</CODE>
     *
     * @param connection <CODE>Connection</CODE> to the database
     *
     * @return List containing precompiled SQL statement to delete
     */
    public List<PreparedStatement> prepareDeleteStatement(Connection connection)
    	throws SQLException;

    /**
     * Sets paremeters to <CODE>PreparedStatement</CODE> in 
     * <CODE>statements</CODE> to INSERT to the database
     *
     * @param statements <CODE>Iterator</CODE> of 
     * <CODE>PreparedStatement</CODE> to which 
     * paramters to be set
     *
     * @param objectID unique ID number in <CODE>long</CODE>
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setInsertParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException;

    /**
     * Sets paremeters to <CODE>PreparedStatement</CODE> in 
     * <CODE>statements</CODE> to UPDATE data in the database
     *
     * @param statements <CODE>Iterator</CODE> of 
     * <CODE>PreparedStatement</CODE> to which 
     * paramters to be set
     *
     * @param objectID unique ID number in <CODE>long</CODE>
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setUpdateParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException;

    /**
     * Sets paremeters to <CODE>PreparedStatement</CODE> in 
     * <CODE>statements</CODE> to DELETE data in the database
     *
     * @param statements <CODE>Iterator</CODE> of 
     * <CODE>PreparedStatement</CODE> to which 
     * paramters to be set
     *
     * @param objectID unique ID number in <CODE>long</CODE>
     * to be assigned to the object on the databaes
     *
     * @return number of parameters
     */
    public void setDeleteParameters(Iterator<PreparedStatement> statements,
				    int objectID)
	throws SQLException;
}

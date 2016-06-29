/*
 * DataSource.java
 *
 * Copyright (c) 2007, 2015 Nozomi `James' Ytow
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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <CODE>DataSource</CODE> stores information about a database to
 * connect to using JDBC.
 *
 * @version 	08 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class DataSource
{
    /** <CODE>String</CODE> stores a URL of the database */
    protected String url;

    /** <CODE>String</CODE> stores a username to access to the database */
    protected String user;

    /** <CODE>String</CODE> stores a password for the username */
    protected String password;

    /** <CODE>String</CODE> stored JDBC driver name */
    protected String driverName;

    /** <CODE>Driver</CODE> to access to the database */
    protected Driver driver;

    /** An array of <CODE>Connection</CODE>s to access to the database */
    protected Connection[] connections;

    /**
     * Creates a <CDOE>DataSource</CODE> with no information.
     */
    public DataSource()
    {
	this("");
    }


    /**
     * Creates a <CDOE>DataSource</CODE> object having given
     * parameters.
     *
     * @param url <CODE>String</CODE> representing data source as a URL
     */
    public DataSource(String url)
    {
	this(url, "");
    }

    /**
     * Creates a <CDOE>DataSource</CODE> object having given
     * parameters.
     *
     * @param url <CODE>String</CODE> representing data source as a URL
     * @param user <CODE>String</CODE> representing user name to access to the database
     */
    public DataSource(String url, String user)
    {
	this(url, user, "", null);
    }

    /**
     * Creates a <CDOE>DataSource</CODE> object having given
     * parameters.
     *
     * @param url <CODE>String</CODE> representing data source as a URL
     * @param user <CODE>String</CODE> representing user name to access to the database
     * @param password <CODE>String</CODE> representing password for the user
     */
    public DataSource(String url, String user, String password)
    {
	this(url, user, password, null);
    }


    /**
     * Creates a <CDOE>DataSource</CODE> object having given
     * parameters.
     *
     * @param url <CODE>String</CODE> representing the data source as a URL
     * @param user <CODE>String</CODE> representing user name to access to the database
     * @param password <CODE>String</CODE> representing password for the user
     * @param driverName <CODE>String</CODE> representing name of the JDBC driver
     */
    public DataSource(String url, String user, String password, String driverName)
    {
	setURL(url);
	setUser(user);
	setPassword(password);
	setDriverName(driverName);
    }

    /**
     * Sets <CODE>url</CODE> as the URL of the data source
     *
     * @param url <CODE>String</CODE> representing the data source as a URL
     */
    public void setURL(String url)
    {
	this.url = unnull(url);
    }

    /**
     * Sets <CODE>user</CODE> as the user to access to the database
     *
     * @param user <CODE>String</CODE> representing user name to access to the database
     */
    public void setUser(String user)
    {
	this.user = unnull(user);
    }

    /**
     * Sets <CODE>password</CODE> as password for the user to access to the database
     *
     * @param password <CODE>String</CODE> representing password for the user
     */
    public void setPassword(String passwordser)
    {
	this.password = unnull(password);
    }

    public void setDriverName(String driverName)
    {
	this.driverName = driverName;
	if(driverName == null ||
	   driverName.length() == 0) {
	    if(url.length() == 0)
		driver = null;
	    else {
		loadDriver();
	    }
	}
	else if (url.length() > 0) {
	    try {
		Class.forName(driverName);
		loadDriver();
	    }
	    catch (Exception e) {
		driver = null;
	    }
	}
	else
	    driver = null;
    }

    public Driver getDriver()
    {
	return driver;
    }

    public String getDriverName()
    {
	return driverName;
    }

    public String getPassword()
    {
	return password;
    }

    public String getUser()
    {
	return user;
    }

    public String getURL()
    {
	return url;
    }

    private String unnull(String str)
    {
	return (str == null)? "":str;
    }

    private void loadDriver()
    {
	try {
	    driver = DriverManager.getDriver(url);
	}
	catch(SQLException e) {
	    driver = null;
	}
    }
}

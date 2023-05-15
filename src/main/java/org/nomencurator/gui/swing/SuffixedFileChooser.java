/*
 * SuffixedFileChooser.java:  a FileChooser for TaxoNote
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import java.io.File;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code SuffixedFileChooser} provides a file chooser with
 * filter by the suffix of a file name.
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class SuffixedFileChooser
    extends JFileChooser
{
    private static final long serialVersionUID = -5858939169015213559L;

    protected SuffixedFileFilter xml;
    protected SuffixedFileFilter msAccess;
    protected SuffixedFileFilter fileMaker;
    protected SuffixedFileFilter odbc;

    public SuffixedFileChooser()
    {
	super();
    }

    public SuffixedFileChooser(File currentDirectory)
    {
	super(currentDirectory);
    }
    
    public SuffixedFileChooser(File currentDirectory, FileSystemView view)
    {
	super(currentDirectory, view);
    }
     
    public SuffixedFileChooser(FileSystemView view)
    {
	super(view);
    }

    public SuffixedFileChooser(String currentDirectoryPath)
    {
	super(currentDirectoryPath);
    }

    public SuffixedFileChooser(String currentDirectoryPath, FileSystemView view)
    {
	super(currentDirectoryPath, view);
    }

    protected void setup(FileSystemView view)
    {
	super.setup(view);
	removeChoosableFileFilter(getAcceptAllFileFilter());

	xml = new SuffixedFileFilter("xml");
	addChoosableFileFilter(xml);

	msAccess = new SuffixedFileFilter("mdb");
	addChoosableFileFilter(msAccess);

	fileMaker = new SuffixedFileFilter("fmp");
	addChoosableFileFilter(fileMaker);

	odbc = new SuffixedFileFilter("dsn");
	addChoosableFileFilter(odbc);

	addChoosableFileFilter(getAcceptAllFileFilter());
	setFileFilter(xml);
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	super.setLocale(locale);
	if(xml == null)
	    return;

	String xmlText = ResourceKey.XML_FILE;
	String msAccessText = ResourceKey.MS_ACCESS_FILE;
	String fileMakerText = ResourceKey.FILEMAKER_FILE;
	String odbcText = ResourceKey.ODBC_FILE;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    xmlText = resource.getString(xmlText);
	    msAccessText = resource.getString(msAccessText);
	    fileMakerText = resource.getString(fileMakerText);
	    odbcText = resource.getString(odbcText);
	}
	catch(MissingResourceException e) {
	    if(xmlText == null)
		xmlText = ResourceKey.XML_FILE;
	    if(msAccessText == null)
		msAccessText = ResourceKey.MS_ACCESS_FILE;
	    if(fileMakerText == null)
		fileMakerText = ResourceKey.FILEMAKER_FILE;
	    if(odbcText == null)
		odbcText = ResourceKey.ODBC_FILE;
	}
	xml.setDescription(xmlText);
	msAccess.setDescription(msAccessText);
	fileMaker.setDescription(fileMakerText);
	odbc.setDescription(odbcText);
    }

}

class SuffixedFileFilter
    extends FileFilter
{
    protected String filter;

    protected String description;

    public SuffixedFileFilter(String suffix)
    {
	this(suffix, null);
    }

    public SuffixedFileFilter(String suffix, String description)
    {
	super();
	setFilter(suffix);
	setDescription(description);
    }

    public boolean accept(File file)
    {
	boolean accepted = file.isDirectory();

	String description;

	if(!accepted) {
	    String suffix = getSuffix(file);

	    if(suffix != null)
		accepted = suffix.equals(filter);
	}

	return accepted;
    }

    public void setDescription(String description)
    {
	this.description = description;
    }

    public String getDescription()
    {
	return description;
    }

    protected String getSuffix(File file)
    {
	String path = file.getPath();
	int index = path.lastIndexOf('.');
	if(index < 0 || index == path.length())
	    return null;

	return path.substring(index + 1).toLowerCase();
    }

    protected void setFilter(String suffix)
    {
	if(suffix != null)
	    suffix = suffix.toLowerCase();

	if(suffix != null &&
	   suffix.equals(filter))
	    return;

	filter = suffix;
    }
}

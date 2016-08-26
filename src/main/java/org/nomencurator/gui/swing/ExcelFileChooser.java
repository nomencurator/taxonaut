/*
 * ExcelFileChooser.java:  a FileChooser for TaxoNote
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

package org.nomencurator.gui.swing;

import java.io.File;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import lombok.Getter;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code ExcelFileChooser} provides a file chooser 
 * to export data.
 *
 * @version 	27 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class ExcelFileChooser
    extends JFileChooser
{
    private static final long serialVersionUID = 6077258371003251339L;

    /**
     * {@link FileNameExtensionFilter} to manage .xsl fromat files.
     */
    @Getter protected FileNameExtensionFilter xlsFilter;

    /**
     * {@link FileNameExtensionFilter} to manage .xslx fromat files.
     */
    @Getter protected FileNameExtensionFilter xlsxFilter;

    /**
     * Constructs a ExcelFileChooser pointing to user's default directory.
     */
    public ExcelFileChooser()
    {
	super();
    }

    /**
     * Constructs a ExcelFileChooser pointing to given {@code path}, either a file or a directry.
     *
     * @param path a {@link File} object specifying a file or a directory
     */
    public ExcelFileChooser(File path)
    {
	super(path);
    }
    
    /**
     * Constructs a ExcelFileChooser pointing to given {@code path}, either a file or a directry, of the file system {@code view}.
     *
     * @param path a {@link File} object specifying a file or a directory
     * @param view a {@link FileSystemView} of {@code path}
     */
    public ExcelFileChooser(File currentDirectory, FileSystemView view)
    {
	super(currentDirectory, view);
    }
     
    /**
     * Constructs a ExcelFileChooser pointing to user's default directory of the file system {@code view}.
     *
     * @param view a {@link FileSystemView} object 
     */
    public ExcelFileChooser(FileSystemView view)
    {
	super(view);
    }

    /**
     * Constructs a ExcelFileChooser pointing to given {@code path}, either a file or a directry.
     *
     * @param path a {@link String} specifying a file or a directory, or null to point to user's  default directory.
     */
    public ExcelFileChooser(String currentDirectoryPath)
    {
	super(currentDirectoryPath);
    }

    /**
     * Constructs a ExcelFileChooser pointing to given {@code path}, either a file or a directry, of the file system {@code view}.
     *
     * @param path a {@link String} specifying a file or a directory, or null to point to user's  default directory.
     * @param view a {@link FileSystemView} of {@code path}
     */
    public ExcelFileChooser(String currentDirectoryPath, FileSystemView view)
    {
	super(currentDirectoryPath, view);
    }

    @Override
    protected void setup(FileSystemView view)
    {
	super.setup(view);
	removeChoosableFileFilter(getAcceptAllFileFilter());

	xlsFilter = new FileNameExtensionFilter("Microsfot Excel 97/2000/XP/2003 (.xls)",  "xls");
	addChoosableFileFilter(xlsFilter);
	xlsxFilter = new FileNameExtensionFilter("Microsfot Excel 2007/2010/2013 (.xlsx)",  "xlsx");
	addChoosableFileFilter(xlsxFilter);

	addChoosableFileFilter(getAcceptAllFileFilter());
	setFileFilter(xlsxFilter);
    }

}

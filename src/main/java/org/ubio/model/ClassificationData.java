/*
 * ClassificationData.java: provides container of uBio ClassificationData
 *
 * Copyright (c) 2008, 2015, 2016 Nozomi `James' Ytow
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

import lombok.Data;

import java.util.Hashtable;

/**
 * {@code ClassificationData} provides container of uBio ClassificationData.
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
@Data
public class ClassificationData
    implements Serializable
{
    private static final long serialVersionUID = 7268814959750045844L;

    protected int classificationTitleID;
    protected String classificationTitle;
    protected String classificationDescription;
    protected int classificationRoot;
    protected int classificationTitleIDParent;
    protected NamebankObject root;
    protected ClassificationData parent;
    protected boolean saturated;

    public static ClassificationData ZERO = new ClassificationData(0);

    protected static Hashtable<Integer, ClassificationData> bank;

    public static ClassificationData get(String classificationTitleID)
    {
	return get(Integer.valueOf(classificationTitleID));
    }

    public static ClassificationData get(int classificationTitleID)
    {
	return get(Integer.valueOf(classificationTitleID));
    }

    public static ClassificationData get(Integer classificationTitleID)
    {
	ClassificationData c = null;
	if(bank != null)
	    c = bank.get(classificationTitleID);

	if(c == null)
	    c = new ClassificationData(classificationTitleID.intValue());

	return c;
    }

    public ClassificationData(int classificationTitleID)
    {
	setClassificationTitleID(classificationTitleID);

	if(bank == null) {
	    bank = new Hashtable<Integer, ClassificationData>();
	}

	bank.put(Integer.valueOf(classificationTitleID), this);
	setSaturated(false);
    }

    public ClassificationData(int classificationTitleID,
			      String classificationTitle,
			      String classificationDescription,
			      int classificationRoot,
			      NamebankObject root,
			      int classificationTitleIDParent,
			      ClassificationData parent)
    {
	setClassificationTitleID(classificationTitleID);
	setClassificationTitle(classificationTitle);
	setClassificationDescription(classificationDescription);
	setClassificationRoot(classificationRoot);
	setRoot(root);
	setClassificationTitleIDParent(classificationTitleIDParent);
	setParent(parent);
	setSaturated(true);
    }

    /*
    public boolean isSaturated()
    {
	return saturated;
    }

    public void saturate(boolean saturated)
    {
	this.saturated = saturated;
    }
    */

    /*
    public long getId()
    {
	return id;
    }

    public void setId(int classificationTitleID)
    {
	this.id = id;
    }

    public String getTitle()
    {
	return title;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public String getDescription()
    {
	return description;
    }

    public void setDescription(String description)
    {
	this.description = description;
    }

    public NamebankObject getRoot()
    {
	return root;
    }

    public void setRoot(NamebankObject root)
    {
	this.root = root;
    }

    public ClassificationData getParent()
    {
	return parent;
    }

    public void setParent(ClassificationData parent)
    {
	this.parent = parent;
    }
    */
}

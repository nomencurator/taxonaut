/*
 * CitationModel.java:  a java Document to manage Puclibation data
 *
 * Copyright (c) 2004, 2015 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.model;

import java.awt.BorderLayout;

import java.util.Observable;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.nomencurator.model.Publication;

/**
 * <CODE>CitationModel</CODE> provides a set of <CODE>Document</CODE>s
 * providing data model for <CODE>CitationPanel</CODE> instances.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.Publication
 * @see org.nomencurator.swing.CitationPanel
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class CitationModel
    extends Observable
{
    /** <CODE>Publication</CODE> represented by this <CODE>CitationModel</CODE> */
    protected Publication publication;

    /** <CODE>Document</CODE> retaining verbatim list of authors */
    protected Document authors;

    /** <CODE>Document</CODE> retaining verbatim list of authors' affiliation */
    protected Document affiliations;

    /** <CODE>Document</CODE> retaining doi string if the Publication has */
    protected Document doi;

    /** <CODE>Document</CODE> retaining ISBN or ISSN number if unique to the Publication */
    protected Document isxn;

    /** <CODE>Document</CODE> retaining title of Publication to be appeared as citation information, e.g. journal title in case of articles */
    protected Document citationTitle;

    /** <CODE>Document</CODE> retaining title of contents, e.g. title of article in a journal */
    protected Document contentsTitle;

    /** <CODE>Document</CODE> retaining year of publication */
    protected Document year;

    /** <CODE>Document</CODE> retaining volume number or string if the publication or journal has */
    protected Document volume;

    /** <CODE>Document</CODE> retaining issue number in the volume, if given */
    protected Document issue;

    /** <CODE>Document</CODE> retaining page number where the publication started */
    protected Document firstPage;

    /** <CODE>Document</CODE> retaining page number where the publication ended */
    protected Document lastPage;

    /** <CODE>Document</CODE> retaining name of publisher if the publication is a book */
    protected Document publisher;

    /** <CODE>Document</CODE> retaining place of publication if relevant */
    protected Document place;

    /** <CODE>Document</CODE> retaining received date if given */
    protected Document received;

    /** <CODE>Document</CODE> retaining revised date if given */
    protected Document 	revised;

    /** <CODE>Document</CODE> retaining accepted date if given */
    protected Document accepted;

    /** <CODE>Document</CODE> retaining published date if given */
    protected Document published;

    /**
     * Creates a <CODE>CitationModel</CODE> representing
     * a newly created <CODE>Publication</CODE>
     */
    public CitationModel()
    {
	this(new Publication());
    }

    /**
     * Creates a <CODE>CitationModel</CODE> representing
     * given <CODE>publication</CODE>
     *
     * @param publication <CODE>Publication</CODE> to be represented
     */
    public CitationModel(Publication publication)
    {
	super();
	authors = new PlainDocument();
	affiliations = new PlainDocument();
	doi = new PlainDocument();
	isxn = new PlainDocument();
	citationTitle = new PlainDocument();
	contentsTitle = new PlainDocument();
	year = new PlainDocument();
	volume = new PlainDocument();
	issue = new PlainDocument();
	firstPage = new PlainDocument();
	lastPage = new PlainDocument();
	publisher = new PlainDocument();
	place = new PlainDocument();
	received = new PlainDocument();
	revised = new PlainDocument();
	accepted = new PlainDocument();
	published = new PlainDocument();
	setPublication(publication);
    }

    /**
     * Sets <CODE>publication</CODE> as <CODE>Publication</CODE>
     * represented by this <CODE>CitationModel</CODE>
     *
     * @param publication to be represented
     */
    public void setPublication(Publication publication)
    {
	if(this.publication != null) {
	    saveData(this.publication);
	}
	this.publication = publication;
	loadData(publication);
    }

    /**
     * Returns <CODE>Publication</CODE> represented
     * by this <CODE>CitationModel</CODE>
     *
     * @return Publication represented
     * by this <CODE>CitationModel</CODE>
     */
    public Publication getPublication()
    {
	return publication;
    }

    protected void saveData(Publication publication)
    {
	if(publication == null)
	    return;
	publication.setAuthorNames(getValue(authors));
	publication.setAffiliations(getValue(affiliations));
	publication.setCitationTitle(getValue(citationTitle));
	publication.setContentsTitle(getValue(contentsTitle));
	publication.setDOI(getValue(doi));
	publication.setISXN(getValue(isxn));
	publication.setYear(getValue(year));
	publication.setVolume(getValue(volume));
	publication.setIssue(getValue(issue));
	publication.setFirstPage(getValue(firstPage));
	publication.setLastPage(getValue(lastPage));
	publication.setPublisher(getValue(publisher));
	publication.setPlace(getValue(place));
	publication.setReceived(getValue(received));
	publication.setRevised(getValue(revised));
	publication.setAccepted(getValue(accepted));
	publication.setPublished(getValue(published));
    }

    protected void loadData(Publication publication)
    {
	if(publication == null)
	    return;
	setValue(authors, publication.getAuthorNames());
	setValue(affiliations, publication.getAffiliations());
	setValue(citationTitle, publication.getCitationTitle());
	setValue(contentsTitle, publication.getContentsTitle());
	setValue(doi, publication.getDOI());
	setValue(isxn, publication.getISXN());
	setValue(year, publication.getYear());
	setValue(volume, publication.getVolume());
	setValue(issue, publication.getIssue());
	setValue(firstPage, publication.getFirstPage());
	setValue(lastPage, publication.getLastPage());
	setValue(publisher, publication.getPublisher());
	setValue(place, publication.getPlace());
	setValue(received, publication.getReceived());
	setValue(revised, publication.getRevised());
	setValue(accepted, publication.getAccepted());
	setValue(published, publication.getPublished());
    }

    public static void setValue(Document document, String text)
    {
	setValue(document, text, null);
    }

    public static void setValue(Document document,
				String text,
				AttributeSet attribute)
    {
	int length = document.getLength();
	if(length != 0) {
	    try { 
		document.remove(0, length);
	    }
	    catch (BadLocationException e) {
	    }
	}
	try { 
	    document.insertString(0, text, attribute);
	}
	catch (BadLocationException e) {
	}
    }

    public static String getValue(Document document)
    {
	String value = null;
	try { 
	    value = document.getText(0, document.getLength());
	}
	catch (BadLocationException e) {
	}

	return value;
    }

    /**
     * Returns <CODE>Document</CODE> retaining verbatim list of authors
     *
     * @return verbatim list of authors as a <CODE>Document</CODE>
     */
    public Document getAuthors()
    {
	return authors;
    }

    /**
     * Returns <CODE>Document</CODE> retaining verbatim list of authors' affiliation
     *
     * @return verbatim list of authors' affiliation as a <CODE>Document</CODE>
     */
    public Document getAffiliations()
    {
	return affiliations;
    }

    /**
     * Returns  <CODE>Document</CODE> retaining doi string
     *
     * @return doi as a <CODE>Document</CODE>
     */
    public Document getDoi()
    {
	return doi;
    }

    /**
     * Returns <CODE>Document</CODE> retaining ISBN or ISSN number starting with
     * either of "ISBN" or "ISSN" if unique to the Publication
     *
     * @return ISBN or ISSN of the publication as a <CODE>Document</CODE>
     */
    public Document getISXN()
    {
	return isxn;
    }

    /**
     * Returns <CODE>Document</CODE> retaining title of Publication
     * to be appeared as citation information, e.g. journal title in case of articles
     *     
     * @return citation title as a <CODE>Document</CODE>
     */
    public Document getCitationTitle()
    {
	return citationTitle;
    }

    /**
     * Returns <CODE>Document</CODE> retaining title of contents,
     * e.g. title of an article in a journal
     *
     * @return contents title as a <CODE>Document</CODE>
     */
    public Document getContentsTitle()
    {
	return contentsTitle;
    }

    /**
     * Returns <CODE>Document</CODE> retaining year of publication
     *
     * @return  year as a <CODE>Document</CODE>
     */
    public Document getYear()
    {
	return year;
    }

    /**
     * Returns <CODE>Document</CODE> retaining volume number or string
     *
     * @return volume as a <CODE>Document</CODE>
     */
    public Document getVolume()
    {
	return volume;
    }

    /**
     * <CODE>Document</CODE> retaining issue number in the volume
     *
     * @return issue as a <CODE>Document</CODE>
     */
    public Document getIssue()
    {
	return issue;
    }

    /**
     * Returns <CODE>Document</CODE> retaining page number where the publication started
     *
     * @return first page of the publication as a <CODE>Document</CODE>
     */
    public Document getFirstPage()
    {
	return firstPage;
    }

    /**
     * Returns <CODE>Document</CODE> retaining page number where the publication ended
     *
     * @return last page of the publication as a <CODE>Document</CODE>
     */
    public Document getLastPage()
    {
	return lastPage;
    }

    /**
     * Returns <CODE>Document</CODE> retaining name of publisher if the publication is a book
     *
     * @return publisher as a <CODE>Document</CODE>
     */
    public Document getPublisher()
    {
	return publisher;
    }

    /**
     * Returns <CODE>Document</CODE> retaining place of publication if relevant
     *
     * @return place as a <CODE>Document</CODE>
     */
    public Document getPlace()
    {
	return place;
    }

    /**
     * Returns <CODE>Document</CODE> retaining received date if given
     *
     * @return recieved date as a <CODE>Document</CODE>
     */
    public Document getReceived()
    {
	return received;
    }

    /**
     * Returns <CODE>Document</CODE> retaining revised date
     *
     * @return revised date as a <CODE>Document</CODE>
     */
    public Document getRevised()
    {
	return 	revised;
    }

    /**
     * Returns <CODE>Document</CODE> retaining accepted date if given
     *
     * @return acceptated date as a <CODE>Document</CODE>
     */
    public Document getAccepted()
    {
	return accepted;
     }

    /**
     * Returns <CODE>Document</CODE> retaining published date if given
     *
     * @return published date as a <CODE>Document</CODE>
     */
    public Document getPublished()
    {
	return published;
    }

}


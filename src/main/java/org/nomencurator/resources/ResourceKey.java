/*
 * ResourceKey.java:  a class provides constants to access
 * resources for TaxoNote
 *
 * Copyright (c) 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.Agent;
import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.Publication;

/**
 * {@code ResourceKey} provides keys to access resources
 * for Nomencurator and TaxoNote
 *
 * @version 	22 Sep. 2016
 * @author 	Nozomi `James' Ytow
 */
public class ResourceKey
{
    /** Taxonaut text resource name */
    static final public String TAXONAUT = "org.nomencurator.resources.Taxonaut";

    /** Key to version of the Java Runtime Envrionment */
    public static String JAVA_VERSION = "java.version";

    /** Key to vendor name of the Java Runtime Envrionment */
    public static String JAVA_VENDOR = "java.vendor";

    /** Key to vnedor URL */
    public static String JAVA_VENDOR_URL = "java.vendor.url";

    /** Key to place where Java is installed */
    public static String JAVA_HOME = "java.home";

    /** Key to vesion of Java VM specification */
    public static String JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";

    /** Key to the vendor of Java VM specification */
    public static String JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";

    /** Key to the name of Java VM specification */
    public static String JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name";

    /** Key to  version of Java VM */
    public static String  JAVA_VM_VERSION = "java.vm.version";

    /** Key to vender name of the Java VM */
    public static String  JAVA_VM_VENDOR = "java.vm.vendor";

    /** Key to the name of the Java VM */
    public static String JAVA_VM_NAME = "java.vm.name";

    /** Key to version of the Jave Runtime Environemnt specification */
    public static String JAVA_SPECIFICATION_VERSION = "java.specification.version";
 
   /** Key to vendor name of the  Jave Runtime Environemnt specification */
    public static String  JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";

    /** Key to  of the  Jave Runtime Environemnt specification */
    public static String JAVA_SPECIFICATION_NAME = "java.specification.name";

    /** Key to version of Java Class */
    public static String JAVA_CLASS_VERSION = "java.class.version";

    /** Key to the class path */
    public static String  JAVA_CLASS_PATH = "java.class.path";

    /** Key to the library path */
    public static String  JAVA_LIBRARY_PATH = "java.library.path";

    /** Key to path of default temporary directory */
    public static String JAVA_IO_TMPDIR = "java.io.tmpdir";

    /** Key to name of JIT complier to be used */
    public static String JAVA_COMPLIER = "java.compiler";

    /** Key to path of extension directories */
    public static String JAVA_EXT_DIRS = "java.ext.dirs";

    /** Key to name of the operating system */
    public static String OS_NAME = "os.name";

    /** Key to architecture of the operating system */
    public static String  OS_ARCH = "os.arch";

    /** Key to version of the operating system */
    public static String OS_VERSION = "os.version";

    /** Key to file separator charcter */
    public static String FILE_SEPARATOR = "file.separator";

    /** Key to path separator charactor */
    public static String PATH_SEPARATOR = "path.separator";

    /** Key to line saparator character */
    public static String  LINE_SEPARATOR = "line.separator";

    /** Key to user's name */
    public static String USER_NAME = "user.name";

    /** Key to user's home directory */
    public static String USER_HOME = "user.home";

    /** Key to user's current working directroy */
    public static String USER_DIR = "user.dir";

    /** Key to JDBC drivers */
    public static String JDBC_DRIVERS = "jdbc.drivers";

    //Keys for general menus
    /** Key to file text resource */
    static final public String FILE = "FILE";

    /** Key to file text resource */
    static final public String FILE_MNEMONIC = "FILE_MNEMONIC";

    /** Key to new file text resource */
    static final public String NEW_FILE = "NEW_FILE"; 

    /** Key to open text resource */
    static final public String OPEN = "OPEN";

    /** Key to open text resource */
    static final public String CLOSE = "CLOSE";

    /** Key to import text resource */
    static final public String IMPORT = "IMPORT";

    /** Key to open text resource */
    static final public String EXPORT = "EXPORT";

    /** Key to save text resource */
    static final public String SAVE = "SAVE";

    /** Key to save as text resource */
    static final public String SAVE_AS = "SAVE_AS";

    /** Key to print text resource */
    static final public String PRINT = "PRINT";

    /** Key to exit text resource */
    static final public String EXIT = "EXIT";

    /** Key to exit text resource */
    static final public String LANGUAGES = "LANGUAGES";

    /** Key to exit text resource */
    static final public String COLOR = "COLOR";

    /** Key to help text resource */
    static final public String HELP = "HELP";

    /** Key to version text resource */
    static final public String VERSION = "VERSION";

    /** Key to show detail menu text resource */
    static final public String SHOW_DETAIL = "SHOW_DETAIL";


    //Button labels
    /** Key to add to comparator text resource */
    static final public String ADD_TO_COMPARATOR = "ADD_TO_COMPARATOR";

    /** Key to cancel text resource */
    static final public String CANCEL = "CANCEL"; 

    /** Key to clear text resource */
    static final public String CLEAR = "CLEAR"; 

    /** Key to copy text resource */
    static final public String COPY = "COPY"; 

    /** Key to comparison text resource */
    static final public String COMPARE = "COMPARE";

    /** Key to duplicate text resource */
    static final public String DUPLICATE = "DUPLICATE"; 

    /** Key to lock text resource */
    static final public String LOCK = "LOCK"; 

    /** Key to new text resource */
    static final public String NEW = "NEW"; 

    /** Key to delete text resource */
    static final public String DELETE = "DELETE"; 

    /** Key to ok text resource */
    static final public String OK = "OK"; 

    /** Key to search text resource */
    static final public String SEARCH = "SEARCH"; 


    //FileFilter messages
    /** Key to text file text resource */
    static final public String TEXT_FILE = "TEXT_FILE"; 

    /** Key to XML file text resource */
    static final public String XML_FILE = "XML_FILE"; 

    /** Key to MS Access file text resource */
    static final public String MS_ACCESS_FILE = "MS_ACCESS_FILE"; 

    /** Key to MS Excel file text resource */
    static final public String MS_EXCEL_FILE = "MS_EXCEL_FILE"; 

    /** Key to MS Word file text resource */
    static final public String MS_WORD_FILE = "MS_WORD_FILE"; 

    /** Key to FileMaker file text resource */
    static final public String FILEMAKER_FILE = "FILEMAKER_FILE"; 

    /** Key to ODBC text resource */
    static final public String ODBC_FILE = "ODBC_FILE"; 

    //Tab text of detail panels
    /** Key to author tab text resource */
    static final public String AUTHOR_TAB = "AUTHOR_TAB";

    /** Key to publication tab text resource */
    static final public String PUBLICATION_TAB = "PUBLICATION_TAB";

    /** Key to publication tab text resource */
    static final public String IN_PUBLICATION_TAB = "IN_PUBLICATION_TAB";

    /** Key to appearance tab text resource */
    static final public String APPEARANCE_TAB = "APPEARANCE_TAB";
    
    /** Key to nameusage tab text resource */
    static final public String NAMEUSAGE_TAB = "NAMEUSAGE_TAB";
    
    /** Key to JTabbedPane containing relevant informations */
    static final public String RELEVANT_TABS = "RELEVANT_TABS";

    /** Key to JTabbedPane containing relevant informations of NameUsagePanel*/
    static final public String NAMEUAGE_RELEVANT_TABS = "NAMEUSAGE_RELEVANT_TABS";

    /** Key to JTabbedPane containing relevant informations of AppearancePanel*/
    static final public String APPEARANCE_RELEVANT_TABS = "APPEARANCE_RELEVANT_TABS";

    /** Key to JTabbedPane containing relevant informations */
    static final public String PUBLICATION_RELEVANT_TABS = "PUBLICATION_RELEVANT_TABS";

    /** Key to JTabbedPane containing relevant informations */
    static final public String AUTHOR_RELEVANT_TABS = "AUTHOR_RELEVANT_TABS";

    // Table column
    /** Key to comparison column text resource */
    static final public String TO_COMPARE_COLUMN = "TO_COMPARE_COLUMN"; 

    /** Key to descendants count column text resource */
    static final public String DESCENDANTS_COUNT_COLUMN = "DESCENDANTS_COUNT_COLUMN"; 

    //Label text on border, field, etc
   /** Key to publications text resource */
    static final public String AFFILIATIONS = "AFFILIATIONS";

    /** Key to author text resource */
    static final public String AUTHOR = "AUTHOR";

    /** Key to author text resource */
    static final public String AUTHOR_COLUMN = "AUTHOR_COLUMN";

    /** Key to author text resource */
    static final public String AUTHORS = "AUTHORS";

    /** Key to authority text resource */
    static final public String AUTHORITY = "AUTHORITY";

    /** Key to authority text resource */
    static final public String AUTHORITY_COLUMN = "AUTHORITY_COLUMN";

    /** Key to citation information text resource */
    static final public String CITATION = "CITATION";

    /** Key to citation information text resource */
    static final public String CITATION_COLUMN = "CITATION_COLUMN";

    /** Key to appearance information text resource */
    static final public String APPEARANCE = "APPEARANCE";

    /** Key to dataset column text resource */
    static final public String DATASET_COLUMN = "DATASET_COLUMN"; 

    /** Key to appearance information text resource */
    static final public String TAXA_AND_TREATMENT = "TAXA_AND_TREATMENT";

    /** Key to appearance information text resource */
    static final public String PAGE = "PAGE";

    /** Key to appearance information text resource */
    static final public String PAGE_COLUMN = "PAGE_COLUMN";

    /** Key to appearance information text resource */
    static final public String LINES = "LINES";

    /** Key to appearance information text resource */
    static final public String LINES_COLUMN = "LINES_COLUMN";

    /** Key to appearance information text resource */
    static final public String ANNOTATION = "ANNOTATION";

    /** Key to appearance information text resource */
    static final public String ANNOTATIONS = "ANNOTATIONS";

    /** Key to appearance information text resource */
    static final public String NAME = "NAME";

    /** Key to appearance information text resource */
    static final public String NAME_FIELD = "NAME_FIELD";

    /** Key to appearance information text resource */
    static final public String NAME_COLUMN = "NAME_COLUMN";

    /** Key to appearance information text resource */
    static final public String NAME_DETAIL = "NAME_DETAIL";

    /** Key to appearance information text resource */
    static final public String NAMES_COLUMN = "NAMES_COLUMN";

    /** Key to appearance information text resource */
    static final public String LOCALE = "LOCALE";

    /** Key to appearance information text resource */
    static final public String LOCALE_COLUMN = "LOCALE_COLUMN";

    /** Key to classifications text resource */
    static final public String CLASSIFICAITON = "CLASSIFICATION";

    /** Key to classifications text resource */
    static final public String CLASSIFICAITONS = "CLASSIFICATIONS";

    /** Key to details text resource */
    static final public String DETAILS = "DETAILS";

    /** Key to details text resource */
    static final public String RESULTS = "RESULTS";

    /** Key to classifications text resource */
    static final public String HIERARCHY = "HIERARCHY";
	
    /** Key to name appearance text resource */
    static final public String NAME_APPEARANCE = "NAME_APPEARACNE";

    /** Key to name usage text resource */
    static final public String NAME_USAGE = "NAME_USAGE";

    /** Key to name usage text resource */
    static final public String NAME_USAGES = "NAME_USAGES";
	
    /** Key to publication text resource */
    static final public String PUBLICATION = "PUBLICATION";

    /** Key to publications text resource */
    static final public String PUBLICATIONS = "PUBLICATIONS";
 
    /** Key to rank text resource */
    static final public String RANK = "RANK"; 

    /** Key to rank text resource */
    static final public String RANK_COLUMN = "RANK_COLUMN"; 

    /** Key to result text resource */
    static final public String RESULT = "RESULT";

    /** Key to result text resource */
    static final public String RELEVANT_TAXA = "RELEVANT_TAXA";

    /** Key to sensu text resource */
    static final public String SENSU = "SENSU"; 

    /** Key to sensu text resource */
    static final public String SENSU_COLUMN = "SENSU_COLUMN"; 

    /** Key to sensu text resource */
    static final public String SINCE_COLUMN = "SINCE_COLUMN"; 

    /** Key to sensu text resource */
    static final public String UNTIL_COLUMN = "UNTIL_COLUMN"; 

    /** Key to sensu text resource */
    static final public String USED_IN = "USED_IN"; 

    /** Key to taxon name text resource */
    static final public String TAXA = "TAXA"; 

    /** Key to taxon name text resource */
    static final public String TAXON_NAME = "TAXON_NAME"; 

    /** Key to taxon name text resource */
    static final public String TAXON_NAME_COLUMN = "TAXON_NAME_COLUMN"; 

    /** Key to year text resource */
    static final public String YEAR = "YEAR"; 

    /** Key to year text resource */
    static final public String YEAR_COLUMN = "YEAR_COLUMN"; 

    /** Key to inconsistent taxa text resource */
    static final public String HIGHER_TAXON = "HIGHER_TAXON";

    /** Key to inconsistent taxa text resource */
    static final public String HIGHER_TAXON_DIALOG = "HIGHER_TAXON_DIALOG";

    /** Key to inconsistent taxa text resource */
    static final public String LOWER_TAXA = "LOWER_TAXA";

    /** Key to inconsistent taxa text resource */
    static final public String LOWER_TAXON_DIALOG = "LOWER_TAXON_DIALOG";

    /** Key to as is text resource */
    static final public String AS_IS = "AS_IS";

    /** Key to inconsistent taxa text resource */
    static final public String INCONSISTENT_TAXA = "INCONSISTENT_TAXA";

    /** Key to inconsistent taxa text resource */
    static final public String INCERTAE_SEDIS = "INCERTAE_SEDIS";

    /** Key to inconsistent taxa text resource */
    static final public String INSTITUTE_COLUMN = "INSTITUTE_COLUMN";

    /** Key to inconsistent taxa text resource */
    static final public String TYPE = "TYPE";

    /** Key to synonym text resource */
    static final public String SYNONYM = "SYNONYM";

    /** Key to synonyms text resource */
    static final public String SYNONYMS = "SYNONYMS";

    /** Key to rank text resource */
    static final public String LINK_TYPE = "LINK_TYPE"; 

    /** Key to rank text resource */
    static final public String ANNOTATION_TYPE = "ANNOTATION_TYPE"; 

    /** Key to rank text resource */
    static final public String ANNOTATOR_TAXA = "ANNOTATOR_TAXA"; 

    /** Key to rank text resource */
    static final public String ANNOTATOR_TAXON = "ANNOTATOR_TAXON"; 

    /** Key to rank text resource */
    static final public String ANNOTATOR_CARDINALITY = "ANNOTATOR_CARDINALITY"; 

    /** Key to rank text resource */
    static final public String ANNOTATED_TAXA = "ANNOTATED_TAXA"; 

    /** Key to rank text resource */
    static final public String ANNOTATED_TAXON = "ANNOTATED_TAXON"; 

    /** Key to rank text resource */
    static final public String ANNOTATANT_CARDINALITY = "ANNOTATANT_CARDINALITY"; 

    /** Key to inconsistent taxa text resource */
    static final public String RELEVANT_TAXA_DIALOG = "RELEVANT_TAXA_DIALOG";

    /** Key to different taxa text resource */
    static final public String DIFFERENT_TAXA = "DIFFERENT_TAXA";

    /** Key to missing taxa text resource */
    static final public String MISSING_TAXA = "MISSING_TAXA";

    /** Key to common taxa text resource */
    static final public String COMMON_TAXA = "COMMON_TAXA";

    /** Key to surname text resource */
    static final public String SURNAME = "SURNAME";

    /** Key to first name text resource */
    static final public String FIRST_NAME = "FIRST_NAME";

    /** Key to middle names text resource */
    static final public String MIDDLE_NAMES = "MIDDLE_NAMES";

    /** Key to title text resource */
    static final public String TITLE = "TITLE";

    /** Key to birth text resource */
    static final public String BIRTH = "BIRTH";

    /** Key to death text resource */
    static final public String DEATH = "DEATH";

    /** Key to contents title text resource */
    static final public String CONTENT_LABEL = "CONTENT_LABEL";

    /** Key to journal title text resource */
    static final public String JOURNAL_CITATION_LABEL="JOURNAL_CITATION_LABEL";

    /** Key to journal title text resource */
    static final public String BOOK_CITATION_LABEL = "BOOK_CITATION_LABEL";

    /** Key to journal title text resource */
    static final public String BOOK_SECTION_CITATION_LABEL = "BOOK_SECTION_CITATION_LABEL";

    /** Key to journal title text resource */
    static final public String MANUSCRIPT_CITATION = "MANUSCRIPT_CITATION";

    /** Key to journal title text resource */
    static final public String MAGAZINE_CITATION = "MAGAZINE_CITATION";

    /** Key to journal title text resource */
    static final public String CONFERENCE_PROCEEDINGS_CITATION = "CONFERENCE_PROCEEDINGS_CITATION";

    /** Key to text resource */
    static final public String PUBLICATION_YEAR="PUBLICATION_YEAR";

    /** Key to text resource */
    static final public String VOLUME="VOLUME";

    /** Key to text resource */
    static final public String ISSUE = "ISSUE";

    /** Key to text resource */
    static final public String MANUSCRIPT_ISSUE = "MANUSCRIPT_ISSUE";

    /** Key to text resource */
    static final public String FIRST_PAGE = "FIRST_PAGE";

    /** Key to text resource */
    static final public String LAST_PAGE = "LAST_PAGE";

    /** Key to text resource */
    static final public String RECEIVED = "RECEIVED";

    /** Key to text resource */
    static final public String REVISED = "REVISED";

    /** Key to text resource */
    static final public String ACCEPTED = "ACCEPTED";

    /** Key to text resource */
    static final public String PUBLISHED = "PUBLISHED";

    /** Key to text resource */
    static final public String MANUSCRIPT_PUBLISHED = "MANUSCRIPT_PUBLISHED";

    /** Key to text resource */
    static final public String DOI = "DOI";
    /** Key to text resource */
    static final public String ISSN = "ISSN";
    /** Key to text resource */
    static final public String ISBN = "ISBN";

    /** Key to text resource */
    static final public String PUBLISHER = "PUBLISHER";

    /** Key to text resource */
    static final public String PLACE = "PLACE";

    //publication types
    static final public String JOURNAL_ARTICLE = "JOURNAL_ARTICLE";
    static final public String BOOK = "BOOK";
    static final public String BOOK_SECTION = "BOOK_SECTION";
    static final public String EDITED_BOOK = "EDITED_BOOK";
    static final public String CONFERENCE_PROCEEDINGS = "CONFERENCE_PROCEEDINGS";
    static final public String MANUSCRIPT = "MANUSCRIPT";
    static final public String MAGAZINE_ARTICLE = "MAGAZINE_ARTICLE";
    static final public String NEWSPAPER_ARTICLE = "NEWSPAPER_ARTICLE";
    static final public String THESIS = "THESIS";
    static final public String REPORT = "REPORT";
    static final public String PERSONAL_COMMUNICATION = "PERSONAL_COMMUNICATION";
    static final public String BOOK_SERIES = "BOOK_SERIES";
    static final public String JOURNAL = "JOURNAL";
    static final public String MAGAZINE = "MAGAZINE";
    static final public String COMPUTER_PROGRAM = "COMPUTER_PROGRAM";
    static final public String ELECTRONIC_SOURCE = "ELECTRONIC_SOURCE";
    static final public String AUDIOVISUAL_MATERIAL = "AUDIOVISUAL_MATERIAL";
    static final public String FILM_OR_BROADCAST = "FILM_OR_BROADCAST";
    static final public String ARTWORK = "ARTWORK";
    static final public String PATENT = "PATENT";
    static final public String MAP = "MAP";
    static final public String HEARING = "HEARING";
    static final public String BILL = "BILL";
    static final public String STATUE = "STATUE";
    static final public String CASE = "CASE";
    static final public String GENERIC = "GENERIC";
    static final public String OTHER = "OTHER";

    //Query GUI
    static final public String QUERY_TYPE = "QUERY_TYPE";

    static final public String EXACT_QUERY = "EXACT_QUERY";

    static final public String CONTAINS_QUERY = "CONTAINS_QUERY";

    static final public String FUZZY_QUERY = "FUZZY_QUERY";

    //MatchingMode
    static final public String MATCHING_MODE_EXACT = "MATCHING_MODE_EXACT";
    static final public String MATCHING_MODE_FUZZY = "MATCHING_MODE_FUZZY";
    static final public String MATCHING_MODE_SUGGEST = "MATCHING_MODE_SUGGEST";
    static final public String MATCHING_MODE_FULL_TEXT = "MATCHING_MODE_FULL_TEXT";
    static final public String MATCHING_MODE_CONTAINS = "MATCHING_MODE_CONTAINS";

    static final public String VERNACULR_LABEL = "VERNACULR_LABEL";
    static final public String SYNONYM_LABEL = "SYNONYM_LABEL";
    static final public String BASIONOYM_LABEL = "BASIONYM_LABEL";

    // TreeGUI
    static final public String ALIGNER_LABEL = "ALIGNER_LABEL";

    //Messages
    /** Key to record text resource */
    static final public String RECORD = "RECORD"; 

    /** Key to records text resource */
    static final public String RECORDS = "RECORDS"; 

    /** Key to found message resource */
    static final public String FOUND_MESSAGE = "FOUND_MESSAGE"; 

    /** Key to found for message resource */
    static final public String FOUND_FOR_MESSAGE = "FOUND_FOR_MESSAGE"; 

    /** Key to not found message resource */
    static final public String NOT_FOUND_MESSAGE = "NOT_FOUND_MESSAGE"; 

    /** Key to not found for message resource */
    static final public String NOT_FOUND_FOR_MESSAGE = "NOT_FOUND_FOR_MESSAGE";


    // SQL
    /** SQL resource base name */
    static final public String NOMENCURATOR = "org.nomencurator.resources.Nomencurator";

    /** SQL resource base name */
    static final public String QUERY = "org.nomencurator.resources.Query";
 
    /** SQL resource name */
    public static final Locale SQL = new Locale("sql", "");

    /** SQL2 resource name */
    public static final Locale SQL2 = new Locale("sql", "SQL2");

    /** ODBC based resource name */
    public static final Locale ODBC = new Locale("sql", "ODBC", "");

    /** SQL3 resource name */
    public static final Locale SQL3 = new Locale("sql", "SQL3");

    /** PostgreSQL resource name */
    public static final Locale POSTGRESQL = new Locale("sql", "PostgreSQL", "");

    /** MySQL resource name */
    public static final Locale MYSQL = new Locale("sql", "MySQL", "");

    /** Oracle resource name */
    public static final Locale ORACLE = new Locale("sql", "Oracle", "");

    /** MicrosoftAccess resource name */
    public static final Locale MS_ACCESS = new Locale("sql", "Access", "");

    /** Microsoft SQL Server resource name */
    public static final Locale SQLSERVER = new Locale("sql", "SQLServer", "");

    /** DB2 resource name */
    public static final Locale DB2 = new Locale("sql", "DB2", "");

    /** FileMaker resource name */
    public static final Locale FILEMAKER = new Locale("sql", "FileMaker", "");

    /** Key to JDBC driver */
    public static final String JDBC_DRIVER = "JDBC_DRIVER";

    /** Key to JDBC Connection prefix */
    public static final String JDBC_CONNECTION = "JDBC_CONNECTION";

    /** Key to type name in the SQL to be mapped to Java String */
    public static final String SQL_STRING = "SQL_STRING";


    /** Key to SQL statement to create table_types table */
    public static final String CREATE_TABLE_TYPES = "CREATE_TABLE_TYPES";

    /** Key to parameters of CREATE_TABLE_TYPES resource */
    public static final String PARAM_TABLE_TYPES = "PARAM_TABLE_TYPES";

    /** Key to SQL statement to create tables table*/
    public static final String CREATE_TABLES = "CREATE_TABLES";

    /** Key to parameters of CREATE_TABLES resource */
    public static final String PARAM_TABLES = "PARAM_TABLES";

    /** Key to SQL statement to create locales table */
    public static final String CREATE_LOCALES = "CREATE_LOCALES";

    /** Key to parameters of CREATE_LOCALES resource */
    public static final String PARAM_LOCALES = "PARAM_LOCALES";

    /** Key to SQL statement to create table_explanations table */
    public static final String CREATE_TABLE_EXPLANATIONS = "CREATE_TABLE_EXPLANATIONS";

    /** Key to parameters of CREATE_TABLE_EXPLANATIONS resource */
    public static final String PARAM_TABLE_EXPLANATIONS = "PARAM_TABLE_EXPLANATIONS";

    /** Key to SQL statement to create copyright table */
    public static final String CREATE_COPYRIGHTS = "CREATE_COPYRIGHTS";

    /** Key to parameters of CREATE_COPYRIGHT resource */
    public static final String PARAM_COPYRIGHTS = "PARAM_COPYRIGHTS";

    /** Key to SQL statement to create contributor table */
    public static final String CREATE_CONTRIBUTORS = "CREATE_CONTRIBUTORS";

    /** Key to parameters of CREATE_CONTRIBUTOR resource */
    public static final String PARAM_CONTRIBUTORS = "PARAM_CONTRIBUTORS";

    /** Key to SQL statement to create object_types table */
    public static final String CREATE_OBJECT_TYPES = "CREATE_OBJECT_TYPES";

    /** Key to parameters of CREATE_OBJECT_TYPES resource */
    public static final String PARAM_OBJECT_TYPES = "PARAM_OBJECT_TYPES";

    /** Key to SQL statement to create named_object table */
    public static final String CREATE_NAMED_OBJECTS = "CREATE_NAMED_OBJECTS";

    /** Key to parameters of CREATE_NAMED_OBJECT resource */
    public static final String PARAM_NAMED_OBJECTS = "PARAM_NAMED_OBJECTS";

    /** Key to SQL statement to create parsed_object table */
    public static final String CREATE_PARSED_OBJECTS = "CREATE_PARSED_OBJECTS";

    /** Key to parameters of CREATE_PARSED_OBJECT resource */
    public static final String PARAM_PARSED_OBJECTS = "PARAM_PARSED_OBJECTS";

    /** Key to SQL statement to create author table */
    public static final String CREATE_PEOPLE = "CREATE_PEOPLE";

    /** Key to parameters of CREATE_AUTHOR resource */
    public static final String PARAM_PEOPLE = "PARAM_PEOPLE";

    /** Key to SQL statement to create pseudonym table */
    public static final String CREATE_PSEUDONYM = "CREATE_PSEUDONYM";

    /** Key to parameters of CREATE_PSEUDONYM resource */
    public static final String PARAM_PSEUDONYM = "PARAM_PSEUDONYM";

    /** Key to SQL statement to create institution table */
    public static final String CREATE_INSTITUTIONS = "CREATE_INSTITUTIONS";

    /** Key to parameters of CREATE_INSTITUTION resource */
    public static final String PARAM_INSTITUTIONS = "PARAM_INSTITUTIONS";

    /** Key to SQL statement to create institution table */
    public static final String CREATE_INSTITUTION_CODES = "CREATE_INSTITUTION_CODES";

    /** Key to parameters of CREATE_INSTITUTION resource */
    public static final String PARAM_INSTITUTION_CODES = "PARAM_INSTITUTION_CODES";

    /** Key to SQL statement to create institution_history table */
    public static final String CREATE_INSTITUTION_HISTORY = "CREATE_INSTITUTION_HISTORY";

    /** Key to parameters of CREATE_INSTITUTION_HISTORY resource */
    public static final String PARAM_INSTITUTION_HISTORY = "PARAM_INSTITUTION_HISTORY";

    /** Key to SQL statement to create affiliation table */
    public static final String CREATE_AFFILIATIONS = "CREATE_AFFLIATIONS";

    /** Key to parameters of CREATE_AFFLIATION resource */
    public static final String PARAM_AFFILIATIONS = "PARAM_AFFLIATIONS";

    /** Key to SQL statement to create publication_type table */
    public static final String CREATE_PUBLICATION_TYPES = "CREATE_PUBLICATION_TYPES";

    /** Key to parameters of CREATE_PUBLICATION_TYPE resource */
    public static final String PARAM_PUBLICATION_TYPES = "PARAM_PUBLICATION_TYPES";

    /** Key to SQL statement to create publication_type table */
    public static final String CREATE_PUBLICATION_FIELDS = "CREATE_PUBLICATION_FIELDS";

    /** Key to parameters of CREATE_PUBLICATION_TYPE resource */
    public static final String PARAM_PUBLICATION_FIELDS = "PARAM_PUBLICATION_FIELDS";

    /** Key to SQL statement to create publication table */
    public static final String CREATE_PUBLICATIONS = "CREATE_PUBLICATIONS";

    /** Key to parameters of CREATE_PUBLICATION resource */
    public static final String PARAM_PUBLICATIONS = "PARAM_PUBLICATIONS";

    /** Key to SQL statement to create isxn table */
    public static final String CREATE_ISXN = "CREATE_ISXN";

    /** Key to parameters of CREATE_ISXN resource */
    public static final String PARAM_ISXN = "PARAM_ISXN";

    /** Key to SQL statement to create shelfmark table */
    public static final String CREATE_SHELFMARKS = "CREATE_SHELFMARKS";

    /** Key to parameters of CREATE_SHELFMARK resource */
    public static final String PARAM_SHELFMARKS = "PARAM_SHELFMARKS";

    /** Key to SQL statement to create authorship table */
    public static final String CREATE_AUTHORSHIP = "CREATE_AUTHORSHIP";

    /** Key to parameters of  resource CREATE_AUTHORSHIP";*/
    public static final String PARAM_AUTHORSHIP = "PARAM_AUTHORSHIP";

    /** Key to SQL statement to create citations table */
    public static final String CREATE_CITATIONS = "CREATE_CITATIONS";

    /** Key to parameters of  resource CREATE_CITATIONS";*/
    public static final String PARAM_CITATIONS = "PARAM_CITATIONS";

    /** Key to SQL statement to create appearances table */
    public static final String CREATE_APPEARANCES = "CREATE_APPEARANCES";

    /** Key to parameters of CREATE_APPEARANCES resource */
    public static final String PARAM_APPEARANCES = "PARAM_APPEARANCES";

    /** Key to SQL statement to create codes table */
    public static final String CREATE_CODES = "CREATE_CODES";

    /** Key to parameters of CREATE_CODES resource */
    public static final String PARAM_CODES = "PARAM_CODES";

    /** Key to SQL statement to create naming_conventions table */
    public static final String CREATE_NAMING_CONVENTIONS = "CREATE_NAMING_CONVENTIONS";

    /** Key to parameters of CREATE_NAMING_CONVENTIONS resource */
    public static final String PARAM_NAMING_CONVENTIONS = "PARAM_NAMING_CONVENTIONS";

    /** Key to SQL statement to create ranks table */
    public static final String CREATE_RANKS = "CREATE_RANKS";

    /** Key to parameters of CREATE_RANKS resource */
    public static final String PARAM_RANKS = "PARAM_RANKS";

    /** Key to SQL statement to create rank_names table */
    public static final String CREATE_RANK_NAMES = "CREATE_RANK_NAMES";

    /** Key to parameters of CREATE_RANK_NAMES resource */
    public static final String PARAM_RANK_NAMES = "PARAM_RANK_NAMES";

    /** Key to SQL statement to create name_endings table */
    public static final String CREATE_NAME_ENDINGS = "CREATE_NAME_ENDINGS";

    /** Key to parameters of CREATE_NAME_ENDINGS resource */
    public static final String PARAM_NAME_ENDINGS = "PARAM_NAME_ENDINGS";

    /** Key to SQL statement to create name_usages table */
    public static final String CREATE_NAME_USAGES = "CREATE_NAME_USAGES";

    /** Key to parameters of CREATE_NAME_USAGES resource */
    public static final String PARAM_NAME_USAGES = "PARAM_NAME_USAGES";

    /** Key to SQL statement to create linkage_types table */
    public static final String CREATE_LINKAGE_TYPES = "CREATE_LINKAGE_TYPES";

    /** Key to parameters of CREATE_LINKAGE_TYPES resource */
    public static final String PARAM_LINKAGE_TYPES = "PARAM_LINKAGE_TYPES";

    /** Key to SQL statement to create linkage_groups table */
    public static final String CREATE_LINKAGE_GROUPS = "CREATE_LINKAGE_GROUPS";

    /** Key to parameters of CREATE_LINKAGE_GROUPS resource */
    public static final String PARAM_LINKAGE_GROUPS = "PARAM_LINKAGE_GROUPS";

    /** Key to SQL statement to create linkages table */
    public static final String CREATE_LINKAGES = "CREATE_LINKAGES";

    /** Key to parameters of CREATE_LINKAGES resource */
    public static final String PARAM_LINKAGES = "PARAM_LINKAGES";

    /** Key to SQL statement to create nameusage_nodes table */
    public static final String CREATE_NAMEUSAGE_NODES = "CREATE_NAMEUSAGE_NODES";

    /** Key to parameters of CREATE_NAMEUSAGE_NODES resource */
    public static final String PARAM_NAMEUSAGE_NODES = "PARAM_NAMEUSAGE_NODES";

    /** Key to SQL statement to create name_tokens table */
    public static final String CREATE_NAME_TOKENS = "CREATE_NAME_TOKENS";

    /** Key to parameters of CREATE_NAME_TOKENS resource */
    public static final String PARAM_NAME_TOKENS = "PARAM_NAME_TOKENS";

    /** Key to SQL statement to create token_nameusage table */
    public static final String CREATE_TOKEN_NAMEUSAGE = "CREATE_TOKEN_NAMEUSAGE";

    /** Key to parameters of CREATE_TOKEN_NAMEUSAGE resource */
    public static final String PARAM_TOKEN_NAMEUSAGE = "PARAM_TOKEN_NAMEUSAGE";

    /** Key to SQL statement to create evaluations table */
    public static final String CREATE_EVALUATIONS = "CREATE_EVALUATIONS";

    /** Key to parameters of CREATE_EVALUATIONS resource */
    public static final String PARAM_EVALUATIONS = "PARAM_EVALUATIONS";

    /** Key to SQL statement to create object_id_pool table */
    public static final String CREATE_OBJECT_ID_POOL = "CREATE_OBJECT_ID_POOL";

    /** Key to parameters of CREATE_OBJECT_ID_POOL resource */
    public static final String PARAM_OBJECT_ID_POOL = "PARAM_OBJECT_ID_POOL";

    protected static final String[][] CREATE_TABLES_SQUENCE_KEYS = {
	{
	    CREATE_TABLE_TYPES,
	    CREATE_TABLES,
	    CREATE_LOCALES,
	    CREATE_TABLE_EXPLANATIONS,
	    CREATE_COPYRIGHTS,
	    CREATE_CONTRIBUTORS,
	    CREATE_OBJECT_TYPES,
	    CREATE_NAMED_OBJECTS,
	    CREATE_PARSED_OBJECTS,
	    CREATE_PEOPLE,
	    CREATE_PSEUDONYM,
	    CREATE_INSTITUTIONS,
	    CREATE_INSTITUTION_CODES,
	    CREATE_INSTITUTION_HISTORY,
	    CREATE_AFFILIATIONS,
	    CREATE_PUBLICATION_TYPES,
	    CREATE_PUBLICATION_FIELDS,
	    CREATE_PUBLICATIONS,
	    CREATE_ISXN,
	    CREATE_SHELFMARKS,
	    CREATE_AUTHORSHIP,
	    CREATE_CITATIONS,
	    CREATE_APPEARANCES,
	    CREATE_CODES,
	    CREATE_NAMING_CONVENTIONS,
	    CREATE_RANKS,
	    CREATE_RANK_NAMES,
	    CREATE_NAME_ENDINGS,
	    CREATE_NAME_USAGES,
	    CREATE_LINKAGE_TYPES,
	    CREATE_LINKAGE_GROUPS,
	    CREATE_LINKAGES,
	    CREATE_NAMEUSAGE_NODES,
	    CREATE_NAME_TOKENS,
	    CREATE_TOKEN_NAMEUSAGE,
	    CREATE_EVALUATIONS,
	    CREATE_OBJECT_ID_POOL
	},
	{
	    PARAM_TABLE_TYPES,
	    PARAM_TABLES,
	    PARAM_LOCALES,
	    PARAM_TABLE_EXPLANATIONS,
	    PARAM_COPYRIGHTS,
	    PARAM_CONTRIBUTORS,
	    PARAM_OBJECT_TYPES,
	    PARAM_NAMED_OBJECTS,
	    PARAM_PARSED_OBJECTS,
	    PARAM_PEOPLE,
	    PARAM_PSEUDONYM,
	    PARAM_INSTITUTIONS,
	    PARAM_INSTITUTION_CODES,
	    PARAM_INSTITUTION_HISTORY,
	    PARAM_AFFILIATIONS,
	    PARAM_PUBLICATION_TYPES,
	    PARAM_PUBLICATION_FIELDS,
	    PARAM_PUBLICATIONS,
	    PARAM_ISXN,
	    PARAM_SHELFMARKS,
	    PARAM_AUTHORSHIP,
	    PARAM_CITATIONS,
	    PARAM_APPEARANCES,
	    PARAM_CODES,
	    PARAM_NAMING_CONVENTIONS,
	    PARAM_RANKS,
	    PARAM_RANK_NAMES,
	    PARAM_NAME_ENDINGS,
	    PARAM_NAME_USAGES,
	    PARAM_LINKAGE_TYPES,
	    PARAM_LINKAGE_GROUPS,
	    PARAM_LINKAGES,
	    PARAM_NAMEUSAGE_NODES,
	    PARAM_NAME_TOKENS,
	    PARAM_TOKEN_NAMEUSAGE,
	    PARAM_EVALUATIONS,
	    PARAM_OBJECT_ID_POOL
	}
    };

    public static String[][] getCreateTablesSequenceKeys()
    {
	return CREATE_TABLES_SQUENCE_KEYS;
    }


    /** Key to stored procedure to get the next object ID */
    public static final String GET_NEXT_OBJECT_ID_PROCEDURE = "GET_NEXT_OBJECT_ID_PROCEDURE";

    /** Key to SQL statemet to select a subclass of NamedObject */
    public static final String SELECT_ALL_NAMED_OBJECT_OF_TYPE = "SELECT_ALL_NAMED_OBJECT_OF_TYPE";

    /** Key to SQL statemet to select a NamedObject by object ID */
    public static final String SELECT_NAMED_OBJECT_BY_OID = "SELECT_NAMED_OBJECT_BY_OID";

    /** Key to SQL statemet to select a NamedObject by persistent ID */
    public static final String SELECT_NAMED_OBJECT_BY_PID = "SELECT_NAMED_OBJECT_BY_PID";

    /** Key to SQL statemet to select a NamedObject by persistent ID with fluctuation */
    public static final String SELECT_NAMED_OBJECT_LIKE_PID = "SELECT_NAMED_OBJECT_LIKE_PID";

    /** Key to SQL statemet to select an object ID by persistent ID */
    public static final String SELECT_OID_BY_PID = "SELECT_OID_BY_PID";

    /** Key to SQL statemet to select a persistent ID by object ID */
    public static final String SELECT_PID_BY_OID = "SELECT_PID_BY_OID";

    /** Key to SQL statemet to select object type by object ID */
    public static final String SELECT_TYPE_BY_OID = "SELECT_TYPE_BY_OID";

    /** Key to SQL statemet to select object type by persistent ID */
    public static final String SELECT_TYPE_BY_PID = "SELECT_TYPE_BY_PID";

    /** Key to SQL statemet to select lower taxa by object ID of the higher taxon */
    public static final String SELECT_LOWER_TAXA_BY_OID = "SELECT_LOWER_TAXA_BY_OID";

    /** Key to SQL statemet to select object IDs of lower taxa by object ID of the higher taxon */
    public static final String SELECT_LOWER_TAXA_OID_BY_OID = "SELECT_LOWER_TAXA_OID_BY_OID";

    /** Key to SQL statemet to select persistent IDs of lower taxa by object ID of the higher taxon */
    public static final String SELECT_LOWER_TAXA_PID_BY_OID = "SELECT_LOWER_TAXA_PID_BY_OID";

    /** Key to SQL statemet to select a locale */
    public static final String SELECT_LCALE = "SELECT_LCALE";

    /** Key to SQL statemet to select a table as whole */
    public static final String SELECT_TABLE = "SELECT_TABLE";

    /** Key to SQL statemet to set status of a transaction */
    public static final String SET_TRANSACTION = "SET_TRANSACTION";

    /** Key to SQL statemet to start a transaction */
    public static final String START_TRANSACTION = "START_TRANSACTION";

    /** Key to SQL statemet to commit a transaction */
    public static final String COMMIT_TRANSACTION = "COMMIT_TRANSACTION";

    /** Key to SQL statemet to rollback a transaction */
    public static final String ROLLBACK_TRANSACTION = "ROLLBACK_TRANSACTION";

    /** Key to SQL statemet to set savepoint of a transaction */
    public static final String TRANSACTION_SAVEPOINT = "TRANSACTION_SAVEPOINT";

    /** Key to SQL statemet to insert a NamedObject */
    public static final String INSERT_NAMED_OBJECT = "INSERT_NAMED_OBJECT";

    /** Key to SQL statemet to insert a NamedObject */
    public static final String INSERT_NAMED_OBJECT_PROCEDURE = "INSERT_NAMED_OBJECT_PROCEDURE";

    /** Key to SQL statemet to delete an OID in pool */
    public static final String DELETE_OID_IN_POOL = "DELETE_OID_IN_POOL";

    /** Key to SQL table name for NamedObject */
    public static final String TABLE_NAMED_OBJECT = "TABLE_NAMED_OBJECT";

    /** Key to SQL table name for poold object ID */
    public static final String TABLE_OID_POOL = "TABLE_OID_POOL";

    /** Key to SQL table name for Author */
    public static final String TABLE_AUTHOR = new Agent().getClassName();

    /** Key to SQL table name for Publication */
    public static final String TABLE_PUBLICATION = new Publication().getClassName();

    /** Key to SQL table name for Appearance */
    public static final String TABLE_APPEARANCE = new Appearance().getClassName();

    /** Key to SQL table name for NameUsage */
    public static final String TABLE_NAMEUSAGE = new DefaultNameUsage().getClassName();

    /** Key to SQL table name for Annotation */
    public static final String TABLE_ANNOTATION = new Annotation().getClassName();

    /** Key to SQL table name for NameUsageNode */
    public static final String TABLE_NAMEUSAGENODE = new DefaultNameUsageNode().getClassName();

    /** Key to SQL table name for Locale */
    public static final String TABLE_LOCALE = "TABLE_LOCALE";

    /** Key to SQL table name for Rank */
    public static final String TABLE_RANK = "TABLE_RANK";

    /** Key to SQL statement to set values of NamedObject */
    public static final String SET_NAMED_OBJECT = "SET_NAMED_OBJECT";

    /** Key to SQL statement to set values of Author */
    public static final String SET_AUTHOR = "SET_" + TABLE_AUTHOR;

    /** Key to SQL statement to set values of Publiation */
    public static final String SET_PUBLICATION = "SET_" + TABLE_PUBLICATION;

    /** Key to SQL statement to set values of Appearacne */
    public static final String SET_APPEARANCE = "SET_" + TABLE_APPEARANCE;

    /** Key to SQL statement to set values of NameUsage */
    public static final String SET_NAMEUSGE = "SET_" + TABLE_NAMEUSAGE;

    /** Key to SQL statement to set values of Annotation */
    public static final String SET_ANNOTATION = "SET_" + TABLE_ANNOTATION;

    /** Key to SQL statement to set values of NameUsageNode */
    public static final String SET_NAMEUSAGENODE = "SET_" + TABLE_NAMEUSAGENODE;


    public static final String FULL_DEPTH = "FULL_DEPTH";
    public static final String FULL_HEIGHT = "FULL_HEIGHT";
    public static final String DEPTH_LABEL = "DEPTH_LABEL";
    public static final String HEIGHT_LABEL = "HEIGHT_LABEL";

    public static final String[] GET_NEXT_OBJECT_ID_STATEMENT_KEYS = 
    {
	"GET_NEXT_OBJECT_ID_0",
	"GET_NEXT_OBJECT_ID_1",
	"GET_NEXT_OBJECT_ID_2",
	"GET_NEXT_OBJECT_ID_3",
	"GET_NEXT_OBJECT_ID_4",
	"GET_NEXT_OBJECT_ID_5",
	"GET_NEXT_OBJECT_ID_6",
	"GET_NEXT_OBJECT_ID_7"
    };

    static Locale[] locales;

    static {
	List<Locale> localeList = new ArrayList<Locale>();
	Locale[] available = Locale.getAvailableLocales();
	for(int i = 0; i < available.length; i++) {
	    try {
		ResourceBundle resource =
		    ResourceBundle.getBundle(TAXONAUT, available[i]);
		if(available[i].equals(resource.getLocale()))
		    localeList.add(available[i]);
	    }
	    catch(MissingResourceException e) {

	    }
	}
	locales = localeList.toArray(new Locale[localeList.size()]);
	localeList.clear();
    }

    public static Locale[] getSupportedLocale()
    {
	return locales;
    }

    protected static String[] publicationTypeKeys =
    {
	ResourceKey.JOURNAL_ARTICLE,
	ResourceKey.BOOK,
	ResourceKey.BOOK_SECTION,
	ResourceKey.EDITED_BOOK,
	ResourceKey.CONFERENCE_PROCEEDINGS,
	ResourceKey.MANUSCRIPT,
	ResourceKey.MAGAZINE_ARTICLE,
	ResourceKey.NEWSPAPER_ARTICLE,
	ResourceKey.THESIS,
	ResourceKey.REPORT,
	ResourceKey.PERSONAL_COMMUNICATION,
	ResourceKey.BOOK_SERIES,
	ResourceKey.JOURNAL,
	ResourceKey.MAGAZINE,
	ResourceKey.COMPUTER_PROGRAM,
	ResourceKey.ELECTRONIC_SOURCE,
	ResourceKey.AUDIOVISUAL_MATERIAL,
	ResourceKey.FILM_OR_BROADCAST,
	ResourceKey.ARTWORK,
	ResourceKey.PATENT,
	ResourceKey.MAP,
	ResourceKey.HEARING,
	ResourceKey.BILL,
	ResourceKey.STATUE,
	ResourceKey.CASE,
	ResourceKey.GENERIC,
	ResourceKey.OTHER
    };

    public static String[] getPublicationTypeKeys()
    {
	return publicationTypeKeys;
    }

    /**
     * Utility method returning an {@code Object} associating to
     * specified {@code resoruceKey} in {@code ResourceBundle}
     * of {@code resourceBaseName} and {@code locale}
     *
     * @param resourceBaseName base name of the resource
     * @param locale of the resource
     * @param resourceKey key for the resource {@code Object}
     *
     * @return Object associating to {@code resourceKey}
     *
     * @exception NullPointerException if {@code resourceKey} is null
     *
     * @exception MissingResourceException if object associating to {@code resourceKey} is not found
     */
    public static Object getResourceObject(String resourceBaseName,
					   Locale locale,
					   String resourceKey)
	throws MissingResourceException
    {
	Object resourceObject;
	resourceObject =
	    ResourceBundle.getBundle(resourceBaseName, locale).getObject(resourceKey);
	return resourceObject;
    }

    public static String getResourceString(String resourceBaseName,
					  Locale locale,
					  String resourceKey)
    {
	return (String)getResourceObject(resourceBaseName, locale, resourceKey);
    }

    public static String[] getResourceStringArray(String resourceBaseName,
						  Locale locale,
						  String resourceKey)
    {
	return (String[])getResourceObject(resourceBaseName, locale, resourceKey);
    }

}

package org.nomencurator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Locale;
import java.util.Locale.Builder;

import org.nomencurator.gui.swing.Taxonaut;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
	Locale locale = new Locale("la");
	System.out.println(new Builder().setLocale(locale).setExtension(Locale.PRIVATE_USE_EXTENSION,"iczn").build().toLanguageTag());
	System.out.println(new Builder().setLanguageTag("x-iczn").build().toLanguageTag());
	System.out.println(new Builder().setExtension(Locale.PRIVATE_USE_EXTENSION,"iczn").build().toLanguageTag());
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}

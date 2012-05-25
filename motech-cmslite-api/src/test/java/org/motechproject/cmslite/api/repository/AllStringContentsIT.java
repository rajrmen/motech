package org.motechproject.cmslite.api.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationCmsLiteApi.xml")
public class AllStringContentsIT {
    @Autowired
    AllStringContents allStringContents;
    @Autowired
    @Qualifier("cmsLiteDatabase")
    protected CouchDbConnector couchDbConnector;
    private StringContent stringContent;

    @Before
    public void setUp() {
        stringContent = new StringContent("language", "name", "value");
        couchDbConnector.create(stringContent);
    }
   

    @Test
    public void shouldAddStringContent() throws CMSLiteException {
        allStringContents.addContent(stringContent);
        StringContent fetchedContent = couchDbConnector.get(StringContent.class, stringContent.getId());
        assertNotNull(fetchedContent);
        assertEquals(stringContent.getName(), fetchedContent.getName());
        assertEquals(stringContent.getLanguage(), fetchedContent.getLanguage());
        assertEquals(stringContent.getValue(), fetchedContent.getValue());

        couchDbConnector.delete(fetchedContent);
    }

    @Test
    public void shouldAddStringContentWithMetaData() throws CMSLiteException {
        HashMap<String, String> metadata = new HashMap<String, String>();
        metadata.put("duration", "100");
        stringContent = new StringContent("language", "name", "value", metadata);
        allStringContents.addContent(stringContent);
        couchDbConnector.create(stringContent);
        StringContent fetchedContent = couchDbConnector.get(StringContent.class, stringContent.getId());
        assertNotNull(fetchedContent);
        Map<String,String> savedMetaData = stringContent.getMetadata();
        assertEquals(savedMetaData.size(),1 );
        assertEquals(savedMetaData.get("duration"), "100");
    }
    @Test
    public void shouldGetStringContent() {
        StringContent fetchedContent = allStringContents.getContent(stringContent.getLanguage(), stringContent.getName());
        assertNotNull(fetchedContent);
        assertEquals(stringContent.getName(), fetchedContent.getName());
        assertEquals(stringContent.getLanguage(), fetchedContent.getLanguage());
        assertEquals(stringContent.getValue(), fetchedContent.getValue());
    }
    
    @Test
    public void shouldUpdateStringContent() throws CMSLiteException {
        allStringContents.addContent(stringContent);
        StringContent fetchedContent = couchDbConnector.get(StringContent.class, stringContent.getId());
        assertNotNull(fetchedContent);
        assertEquals(stringContent.getName(), fetchedContent.getName());
        assertEquals(stringContent.getLanguage(), fetchedContent.getLanguage());
        assertEquals(stringContent.getValue(), fetchedContent.getValue());

        stringContent = new StringContent("language", "name", "newValue");
        couchDbConnector.create(stringContent);
        allStringContents.addContent(stringContent);
        fetchedContent = couchDbConnector.get(StringContent.class, stringContent.getId());
        assertNotNull(fetchedContent);
        assertEquals(stringContent.getName(), fetchedContent.getName());
        assertEquals(stringContent.getLanguage(), fetchedContent.getLanguage());
        assertEquals(stringContent.getValue(), fetchedContent.getValue());
        couchDbConnector.delete(stringContent);        
    }

    @Test
    public void shouldReturnTrueIfStringContentAvailable() throws CMSLiteException {
        allStringContents.addContent(stringContent);
        assertTrue(allStringContents.isContentAvailable(stringContent.getLanguage(), stringContent.getName()));
    }

    @Test
    public void shouldReturnFalseIfStringContentNotAvailable() throws CMSLiteException {
        allStringContents.addContent(stringContent);
        assertFalse(allStringContents.isContentAvailable("en", "unknownContent"));
	    couchDbConnector.delete(stringContent);
    }
    
    @Test
    public void shouldReturnNullWhenLanguageOrNameNotPresent() {
        StringContent sContent = allStringContents.getContent("notALanguage", "notAName");
        assertNull(sContent);
    }
    
    @Test
    public void shouldNotRetrieveAResourceIfCaseDoesNotMatch() throws CMSLiteException {
    	allStringContents.addContent(stringContent);   	
        StringContent sContent = allStringContents.getContent("Language", "Name");
        assertNull(sContent);
        couchDbConnector.delete(stringContent);
    }
}

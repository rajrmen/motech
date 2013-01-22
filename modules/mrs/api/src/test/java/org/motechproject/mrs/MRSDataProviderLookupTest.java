package org.motechproject.mrs;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.MotechObject;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MRSDataProviderLookupTest {
    private static final String FIELD_KEY = "id";
    private static final String FIELD_VALUE = "12345";

    private static Map<String, String> lookupFields;


    @Mock
    private MRSPatientAdapter patientAdapter;

    @Mock
    private MRSFacilityAdapter facilityAdapter;

    @Mock
    private MRSPersonAdapter personAdapter;
    
    @Mock
    private MRSPatient mrsPatient;

    @Mock
    private MRSFacility mrsFacility;

    @Mock
    private MRSPerson mrsPerson;

    @Mock
    private ResourceLoader resourceLoader;

    private MRSDataProviderLookup providerLookup;

    @BeforeClass
    public static void setLookupFields() {
        lookupFields = new HashMap<>();
        lookupFields.put(FIELD_KEY, FIELD_VALUE);
    }

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(resourceLoader.getResource("task-data-provider.json")).thenReturn(null);
        when(patientAdapter.getPatient(FIELD_VALUE)).thenReturn(mrsPatient);
        when(facilityAdapter.getFacility(FIELD_VALUE)).thenReturn(mrsFacility);
        when(personAdapter.getPerson(FIELD_VALUE)).thenReturn(mrsPerson);

        providerLookup = new MRSDataProviderLookup(resourceLoader);
    }

    @Test
    public void shouldReturnNullWhenClassIsNotSupported() {
        // given
        String clazz = MotechObject.class.getSimpleName();

        // when
        Object object = providerLookup.lookup(clazz, lookupFields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenMapNotContainsSupportedField() {
        // given
        String clazz = MRSFacility.class.getSimpleName();
        HashMap<String, String> fields = new HashMap<>();

        // when
        Object object = providerLookup.lookup(clazz, fields);

        // then
        assertNull(object);
    }

    @Test
    public void shouldReturnNullWhenListIsNull() {
        // given
        String patientClass = MRSPatient.class.getSimpleName();
        String facilityClass = MRSFacility.class.getSimpleName();
        String personClass = MRSPerson.class.getSimpleName();

        // when
        Object patient = providerLookup.lookup(patientClass, lookupFields);
        Object facility = providerLookup.lookup(facilityClass, lookupFields);
        Object person = providerLookup.lookup(personClass, lookupFields);

        // then
        assertNull(patient);
        assertNull(facility);
        assertNull(person);
    }

    @Test
    public void shouldReturnNullWhenListIsEmpty() {
        // given
        String patientClass = MRSPatient.class.getSimpleName();
        String facilityClass = MRSFacility.class.getSimpleName();
        String personClass = MRSPerson.class.getSimpleName();

        providerLookup.setFacilityAdapters(new ArrayList<MRSFacilityAdapter>());
        providerLookup.setPatientAdapters(new ArrayList<MRSPatientAdapter>());
        providerLookup.setPersonAdapters(new ArrayList<MRSPersonAdapter>());

        // when
        Object patient = providerLookup.lookup(patientClass, lookupFields);
        Object facility = providerLookup.lookup(facilityClass, lookupFields);
        Object person = providerLookup.lookup(personClass, lookupFields);

        // then
        assertNull(patient);
        assertNull(facility);
        assertNull(person);
    }

    @Test
    public void shouldReturnObject() {
        // given
        String patientClass = MRSPatient.class.getSimpleName();
        String facilityClass = MRSFacility.class.getSimpleName();
        String personClass = MRSPerson.class.getSimpleName();

        providerLookup.setPatientAdapters(Arrays.asList(patientAdapter));
        providerLookup.setFacilityAdapters(Arrays.asList(facilityAdapter));
        providerLookup.setPersonAdapters(Arrays.asList(personAdapter));

        // when
        MRSPatient patient = (MRSPatient) providerLookup.lookup(patientClass, lookupFields);
        MRSFacility facility = (MRSFacility) providerLookup.lookup(facilityClass, lookupFields);
        MRSPerson person = (MRSPerson) providerLookup.lookup(personClass, lookupFields);

        // then
        assertEquals(mrsPatient, patient);
        assertEquals(mrsFacility, facility);
        assertEquals(mrsPerson, person);
    }
}

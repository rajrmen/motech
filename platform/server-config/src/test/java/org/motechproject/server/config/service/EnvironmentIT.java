package org.motechproject.server.config.service;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class EnvironmentIT {

    private Environment environment;

    @Before
    public void setUp() {
        environment = new EnvironmentImpl();
    }

    @Test
    public void shouldFetchGivenEnvironmentVariableIfExists(){
        String path = "PATH";
        String value = environment.getValue(path);
        assertThat(value, notNullValue());
    }

    @Test
    public void shouldReturnNullWhenEnvironmentVariableDoesNotExist(){
        String path = "XYZ";
        String value = environment.getValue(path);
        assertThat(value, nullValue());
    }
}

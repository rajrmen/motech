package org.motechproject.server.config.service.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.config.domain.BootstrapConfig;
import org.motechproject.server.config.service.BootstrapConfigLoader;
import org.motechproject.server.config.service.ConfigFileReader;
import org.motechproject.server.config.service.Environment;
import org.motechproject.server.config.service.MotechConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class BootstrapConfigLoaderImplTest {
    @Mock
    private Environment environmentMock;

    @Mock
    private ConfigFileReader configFileReader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnBootstrapConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        String fileLocation = "file_location";
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_CONFIG_DIR)).thenReturn(fileLocation);

        Properties properties = new Properties();
        String dbhost = "test.hostname";
        properties.put("db.host", dbhost);
        when(configFileReader.getProperties(new File(fileLocation))).thenReturn(properties);

        BootstrapConfigLoader bootstrapConfigLoader = new BootstrapConfigLoaderImpl(configFileReader, environmentMock);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig();
        expectedBootstrapConfig.setDBHost(dbhost);

        assertThat(bootstrapConfigLoader.getBootstrapConfig(), IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfFileSpecifiedInEnvironmentVariableNotAccessible() throws IOException {
        String fileLocation = "file_location";
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_CONFIG_DIR)).thenReturn(fileLocation);
        when(configFileReader.getProperties(new File(fileLocation))).thenThrow(new IOException());

        new BootstrapConfigLoaderImpl(configFileReader, environmentMock).getBootstrapConfig();
    }

    @Test
    public void shouldReturnBootStrapConfigFromEnvVariableIfMotechConfigDirNotSpecified(){
        String db_name = "db_instance";
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_DB_INSTANCE )).thenReturn(db_name);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig();
        expectedBootstrapConfig.setDBHost(db_name);

        BootstrapConfig actualBootStrapConfig = new BootstrapConfigLoaderImpl(configFileReader, environmentMock).getBootstrapConfig();

        assertThat(actualBootStrapConfig.getDBHost(), IsEqual.equalTo(expectedBootstrapConfig.getDBHost()));
    }


    @Test
    public void shouldReturnDefaultValueIfPropertyTENANT_IDIsNotSpecifiedInFile() throws IOException {
        String fileLocation = "file_location";
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_CONFIG_DIR)).thenReturn(fileLocation);

        Properties properties = new Properties();
        when(configFileReader.getProperties(new File(fileLocation))).thenReturn(properties);

        BootstrapConfig bootstrapConfig = new BootstrapConfigLoaderImpl(configFileReader, environmentMock).getBootstrapConfig();

        assertThat(bootstrapConfig.getTenantId(), IsEqual.equalTo(BootstrapConfig.DEFAULT_TENANT_ID));

    }

    @Test
    public void shouldReturnDefaultValueIfEnvironmentVariableTENANT_IDIsNotSpecified(){
        String db_name = "db_instance";
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_DB_INSTANCE )).thenReturn(db_name);
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_TENANT_ID)).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig();
        expectedBootstrapConfig.setTenantId(BootstrapConfig.DEFAULT_TENANT_ID);

        BootstrapConfig actualBootStrapConfig = new BootstrapConfigLoaderImpl(configFileReader, environmentMock).getBootstrapConfig();

        assertThat(actualBootStrapConfig.getTenantId(), IsEqual.equalTo(expectedBootstrapConfig.getTenantId()));
    }

    @Test
    public void shouldReturnBootStrapConfigFromFileAtDefaultLocationIfEnvVariablesAreNotSpecified() throws IOException {
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_CONFIG_DIR)).thenReturn(null);
        when(environmentMock.getValue(BootstrapConfigLoaderImpl.MOTECH_DB_INSTANCE )).thenReturn(null);

        Properties properties = new Properties();
        String dbhost = "test.hostname";
        properties.setProperty("db.host", dbhost);
        when(configFileReader.getProperties(new File(BootstrapConfigLoaderImpl.DEFAULT_CONFIG_LOCATION))).thenReturn(properties);

        BootstrapConfig bootstrapConfig = new BootstrapConfigLoaderImpl(configFileReader, environmentMock).getBootstrapConfig();
        assertThat(bootstrapConfig.getDBHost(), IsEqual.equalTo(dbhost));
    }


    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {

         when(configFileReader.getProperties(new File(BootstrapConfigLoaderImpl.DEFAULT_CONFIG_LOCATION))).thenReturn(new Properties());
         new BootstrapConfigLoaderImpl(configFileReader, environmentMock).getBootstrapConfig();
         InOrder inOrder = inOrder(environmentMock, configFileReader);


         inOrder.verify(environmentMock).getValue(BootstrapConfigLoaderImpl.MOTECH_CONFIG_DIR);
         inOrder.verify(environmentMock).getValue(BootstrapConfigLoaderImpl.MOTECH_DB_INSTANCE);
         inOrder.verify(configFileReader).getProperties(new File(BootstrapConfigLoaderImpl.DEFAULT_CONFIG_LOCATION));

     }

}

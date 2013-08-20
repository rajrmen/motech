package org.motechproject.server.config.bootstrap.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.config.bootstrap.ConfigFileReader;
import org.motechproject.server.config.bootstrap.Environment;
import org.motechproject.server.config.bootstrap.MotechConfigurationException;
import org.motechproject.server.config.domain.BootstrapConfig;
import org.motechproject.server.config.domain.ConfigSource;
import org.motechproject.server.config.domain.DBConfig;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class BootstrapConfigLoaderImplTest {

    private final String bootstrapFileLocation = "file_location";
    private final String bootstrapFile = bootstrapFileLocation + "/" + BootstrapConfigLoaderImpl.BOOTSTRAP_PROPERTIES;
    private final String dbUrl = "http://localhost:5984";
    private final String username = "user";
    private final String password = "pass";
    private final String tenantId = "test_tenant_id";
    private final String configSource = ConfigSource.FILE.getName();
    @Mock
    private Environment environmentMock;
    @Mock
    private ConfigFileReader configFileReaderMock;
    private BootstrapConfigLoaderImpl bootstrapConfigLoader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bootstrapConfigLoader = new BootstrapConfigLoaderImpl(configFileReaderMock, environmentMock);
        bootstrapConfigLoader.setDefaultBootstrapConfigLocation("/etc/motech/config");
    }

    @Test
    public void shouldReturnBootstrapConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_DIR)).thenReturn(bootstrapFileLocation);

        Properties properties = new Properties();
        properties.put("db.url", dbUrl);
        properties.put("db.username", username);
        properties.put("db.password", password);
        properties.put("tenant.id", tenantId);
        properties.put("config.source", configSource);

        when(configFileReaderMock.getProperties(new File(bootstrapFile))).thenReturn(properties);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.FILE);

        assertThat(bootstrapConfigLoader.getBootstrapConfig(), IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfConfigFileReaderCanNotReadFileSpecifiedInEnvironmentVariable() throws IOException {
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_DIR)).thenReturn(bootstrapFileLocation);
        when(configFileReaderMock.getProperties(new File(bootstrapFile))).thenThrow(new IOException());

        bootstrapConfigLoader.getBootstrapConfig();
    }

    @Test
    public void shouldReturnBootStrapConfigValuesFromEnvironmentVariableWhenMotechConfigDirIsNotSpecified() throws IOException {
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_DIR)).thenReturn(null);
        when(environmentMock.getValue(Environment.MOTECH_DB_URL)).thenReturn(dbUrl);
        when(environmentMock.getValue(Environment.MOTECH_DB_USERNAME)).thenReturn(username);
        when(environmentMock.getValue(Environment.MOTECH_DB_PASSWORD)).thenReturn(password);
        when(environmentMock.getValue(Environment.MOTECH_TENANT_ID)).thenReturn(tenantId);
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_SOURCE)).thenReturn(configSource);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, username, password), tenantId, ConfigSource.FILE);

        assertThat(bootstrapConfigLoader.getBootstrapConfig(), IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfTenantIdIsNotSpecified() {
        when(environmentMock.getValue(Environment.MOTECH_DB_URL)).thenReturn(dbUrl);
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_SOURCE)).thenReturn("FILE");
        when(environmentMock.getValue(Environment.MOTECH_TENANT_ID)).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.FILE);

        BootstrapConfig actualBootStrapConfig = bootstrapConfigLoader.getBootstrapConfig();

        assertThat(actualBootStrapConfig, IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnDefaultValueIfConfigSourceIsNotSpecified() {
        when(environmentMock.getValue(Environment.MOTECH_DB_URL)).thenReturn(dbUrl);
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_SOURCE)).thenReturn(null);
        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI);

        BootstrapConfig actualBootStrapConfig = bootstrapConfigLoader.getBootstrapConfig();

        assertThat(actualBootStrapConfig, IsEqual.equalTo(expectedBootstrapConfig));
    }

    @Test
    public void shouldReturnBootStrapConfigFromFileAtDefaultLocation_If_NoEnvironmentVariablesAreSpecified() throws IOException {
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_DIR)).thenReturn(null);
        when(environmentMock.getValue(Environment.MOTECH_DB_URL)).thenReturn(null);

        Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        when(configFileReaderMock.getProperties(new File("/etc/motech/config/bootstrap.properties"))).thenReturn(properties);

        assertThat(bootstrapConfigLoader.getBootstrapConfig(), IsEqual.equalTo(new BootstrapConfig(new DBConfig(dbUrl, null, null), "DEFAULT", ConfigSource.UI)));
    }

    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {
        when(environmentMock.getValue(Environment.MOTECH_CONFIG_DIR)).thenReturn(null);
        when(environmentMock.getValue(Environment.MOTECH_DB_URL)).thenReturn(null);
        final Properties properties = new Properties();
        properties.setProperty("db.url", dbUrl);
        when(configFileReaderMock.getProperties(new File("/etc/motech/config/bootstrap.properties"))).thenReturn(properties);

        bootstrapConfigLoader.getBootstrapConfig();

        InOrder inOrder = inOrder(environmentMock, configFileReaderMock);

        inOrder.verify(environmentMock).getValue(Environment.MOTECH_CONFIG_DIR);
        inOrder.verify(environmentMock).getValue(Environment.MOTECH_DB_URL);
        inOrder.verify(configFileReaderMock).getProperties(new File("/etc/motech/config/bootstrap.properties"));
    }
}

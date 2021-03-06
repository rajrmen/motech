package org.motechproject.config.core.bootstrap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.impl.BootstrapManagerImpl;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.SQLDBConfig;
import org.motechproject.config.core.environment.Environment;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;
import org.motechproject.config.core.filestore.ConfigPropertiesUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.motechproject.config.core.domain.BootstrapConfig.SQL_URL;
import static org.motechproject.config.core.domain.BootstrapConfig.TENANT_ID;
import static org.motechproject.config.core.domain.ConfigLocation.FileAccessType.READABLE;

@RunWith(PowerMockRunner.class)
public class BootstrapManagerTest {

    private final String bootstrapFileLocation = "file_location";
    private final String bootstrapFile = bootstrapFileLocation + "/bootstrap.properties";
    private final String sqlUrl = "jdbc:mysql://localhost:3306/";
    private final String sqlUsername = "root";
    private final String sqlPassword = "password";
    private final String tenantId = "test_tenant_id";
    private final String felixPath = "./felix";
    private static final String sqlDriver = "com.mysql.jdbc.Driver";
    private final String configSource = ConfigSource.FILE.getName();
    private final String queueUrl = "tcp://localhost:61616";

    private BootstrapManager bootstrapManager;

    @Mock
    private Environment environment;

    @Mock
    private ConfigLocationFileStore configLocationFileStore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bootstrapManager = new BootstrapManagerImpl(configLocationFileStore, environment);
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldReturnBootstrapConfigFromFileSpecifiedInTheEnvironmentVariable() throws IOException {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        when(environment.getConfigDir()).thenReturn(bootstrapFileLocation);

        Properties properties = createProperties();

        when(ConfigPropertiesUtils.getPropertiesFromFile(new File(bootstrapFile))).thenReturn(properties);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), tenantId, ConfigSource.FILE, null, queueUrl);

        assertThat(bootstrapManager.loadBootstrapConfig(), equalTo(expectedBootstrapConfig));
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfConfigFileReaderCanNotReadFileSpecifiedInEnvironmentVariable() throws IOException {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        when(environment.getConfigDir()).thenReturn(bootstrapFileLocation);
        when(ConfigPropertiesUtils.getPropertiesFromFile(new File(bootstrapFile))).thenThrow(new IOException());

        bootstrapManager.loadBootstrapConfig();
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldReturnBootStrapConfigValuesFromEnvironmentVariableWhenMotechConfigDirIsNotSpecified() throws IOException {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        when(environment.getConfigDir()).thenReturn(null);
        when(environment.getBootstrapPropperties()).thenReturn(createProperties());

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), tenantId, ConfigSource.FILE, null, queueUrl);

        assertThat(bootstrapManager.loadBootstrapConfig(), equalTo(expectedBootstrapConfig));
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldReturnDefaultValueIfTenantIdIsNotSpecified() {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        Properties properties = createProperties();
        properties.put(BootstrapConfig.TENANT_ID, "DEFAULT");
        properties.put(BootstrapConfig.CONFIG_SOURCE, "FILE");
        when(environment.getBootstrapPropperties()).thenReturn(properties);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), "DEFAULT", ConfigSource.FILE, null, queueUrl);

        BootstrapConfig actualBootStrapConfig = bootstrapManager.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, equalTo(expectedBootstrapConfig));
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldReturnDefaultValueIfConfigSourceIsNotSpecified() {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        Properties properties = createProperties();
        properties.put(BootstrapConfig.TENANT_ID, "DEFAULT");
        properties.put(BootstrapConfig.CONFIG_SOURCE, "");
        when(environment.getBootstrapPropperties()).thenReturn(properties);

        BootstrapConfig expectedBootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, sqlUsername, sqlPassword), "DEFAULT", ConfigSource.UI, null, queueUrl);

        BootstrapConfig actualBootStrapConfig = bootstrapManager.loadBootstrapConfig();

        assertThat(actualBootStrapConfig, equalTo(expectedBootstrapConfig));
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldReturnBootStrapConfigFromFileAtDefaultLocation_If_NoEnvironmentVariablesAreSpecified() throws IOException {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        Properties environmentProperties = createProperties();
        environmentProperties.put(BootstrapConfig.SQL_URL, "");
        environmentProperties.put(BootstrapConfig.SQL_DRIVER, "");
        environmentProperties.put(BootstrapConfig.QUEUE_URL, "");
        when(environment.getBootstrapPropperties()).thenReturn(new Properties());

        File bootstrapConfigFile = mockDefaultBootstrapFile();

        Properties properties = new Properties();
        properties.setProperty(BootstrapConfig.SQL_URL, sqlUrl);
        properties.setProperty(BootstrapConfig.SQL_DRIVER, sqlDriver);
        properties.setProperty(BootstrapConfig.QUEUE_URL, queueUrl);

        when(ConfigPropertiesUtils.getPropertiesFromFile(bootstrapConfigFile)).thenReturn(properties);


        assertThat(bootstrapManager.loadBootstrapConfig(), equalTo(new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, null, null), "DEFAULT", ConfigSource.UI, null, queueUrl)));
    }

    private File mockDefaultBootstrapFile() throws IOException {
        File bootstrapConfigFile = new File(System.getProperty("user.home") + "/" + "/bootstrap.properties");
        List<ConfigLocation> configLocationList = new ArrayList<>();
        ConfigLocation configLocationMock = Mockito.mock(ConfigLocation.class);
        configLocationList.add(configLocationMock);
        when(configLocationFileStore.getAll()).thenReturn(configLocationList);
        when(configLocationMock.getFile(BootstrapManager.BOOTSTRAP_PROPERTIES, READABLE)).thenReturn(bootstrapConfigFile);
        when(ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.READABLE, configLocationList, BootstrapManager.BOOTSTRAP_PROPERTIES))
                .thenReturn(bootstrapConfigFile);

        return bootstrapConfigFile;
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test
    public void shouldLoadPropertiesInTheCorrectOrder() throws IOException {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        Iterable<ConfigLocation> configLocations = new ArrayList<ConfigLocation>();
        Properties properties = createProperties();
        properties.put(SQL_URL, "");

        when(environment.getConfigDir()).thenReturn(null);
        when(environment.getBootstrapPropperties()).thenReturn(properties);
        when(configLocationFileStore.getAll()).thenReturn(configLocations);
        when(ConfigPropertiesUtils.getDefaultPropertiesFile(ConfigLocation.FileAccessType.READABLE, configLocations, BootstrapManager.BOOTSTRAP_PROPERTIES))
                .thenThrow(new MotechConfigurationException("Error loading file from config locations"));

        try {
            bootstrapManager.loadBootstrapConfig();
        } catch (MotechConfigurationException e) {
            // Ignore error because invocation order is to be verified.
        }

        InOrder inOrder = Mockito.inOrder(environment, configLocationFileStore);

        inOrder.verify(environment).getConfigDir();
        inOrder.verify(environment).getBootstrapPropperties();
        inOrder.verify(configLocationFileStore).getAll();
    }

    @PrepareForTest(ConfigPropertiesUtils.class)
    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionIfReadingTheBootstrapFileFails() throws Exception {
        PowerMockito.mockStatic(ConfigPropertiesUtils.class);
        when(environment.getConfigDir()).thenReturn(null);
        when(environment.getBootstrapPropperties()).thenReturn(new Properties());
        mockDefaultBootstrapFile();

        when(ConfigPropertiesUtils.getPropertiesFromFile(any(File.class))).thenThrow(new IOException());

        bootstrapManager.loadBootstrapConfig();
    }

    @Test
    public void shouldSaveBootstrapConfigToPropertiesFileInDefaultLocation() throws IOException {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new SQLDBConfig(sqlUrl, sqlDriver, "some_username", "some_password"), "tenantId", ConfigSource.FILE, felixPath, queueUrl);

        String tempDir = new File(System.getProperty("java.io.tmpdir"), "config").getAbsolutePath();
        List<ConfigLocation> configLocationList = new ArrayList<>();
        configLocationList.add(new ConfigLocation(tempDir));
        File file = new File(tempDir, BootstrapManager.BOOTSTRAP_PROPERTIES);

        when(configLocationFileStore.getAll()).thenReturn(configLocationList);

        bootstrapManager.saveBootstrapConfig(bootstrapConfig);

        Properties savedBootstrapProperties = new Properties();
        savedBootstrapProperties.load(new FileInputStream(new File(tempDir, "bootstrap.properties")));
        assertNotNull(savedBootstrapProperties);
        assertThat(savedBootstrapProperties.getProperty(SQL_URL), equalTo(sqlUrl));
        assertThat(savedBootstrapProperties.getProperty(TENANT_ID), equalTo("tenantId"));
    }

    private Properties createProperties() {
        Properties properties = new Properties();
        properties.put(BootstrapConfig.SQL_URL, sqlUrl);
        properties.put(BootstrapConfig.SQL_USER, sqlUsername);
        properties.put(BootstrapConfig.SQL_PASSWORD, sqlPassword);
        properties.put(BootstrapConfig.TENANT_ID, tenantId);
        properties.put(BootstrapConfig.CONFIG_SOURCE, configSource);
        properties.put(BootstrapConfig.SQL_DRIVER, sqlDriver);
        properties.put(BootstrapConfig.QUEUE_URL, queueUrl);
        return properties;
    }
}

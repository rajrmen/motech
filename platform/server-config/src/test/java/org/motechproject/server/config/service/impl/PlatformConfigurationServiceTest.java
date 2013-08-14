package org.motechproject.server.config.service.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.config.domain.BootstrapConfig;
import org.motechproject.server.config.service.BootstrapConfigLoader;
import org.motechproject.server.config.service.PlatformConfigurationService;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlatformConfigurationServiceTest {
    @Mock
    private BootstrapConfigLoader bootstrapConfigLoader;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldLoadBootstrapDBConfiguration() {
        PlatformConfigurationService platformConfigService = new PlatformConfigurationServiceImpl(bootstrapConfigLoader);
        BootstrapConfig expectedConfig = new BootstrapConfig();
        when(bootstrapConfigLoader.getBootstrapConfig()).thenReturn(expectedConfig);

        BootstrapConfig bootstrapConfig = platformConfigService.getBootstrapConfig();
        assertNotNull(bootstrapConfig);

        assertThat(bootstrapConfig, IsEqual.equalTo(bootstrapConfig));
    }
}

package org.motechproject.config.bootstrap.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.PropertyPermission;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.motechproject.config.bootstrap.impl.BootstrapConfigManagerImpl.DEFAULT_BOOTSTRAP_CONFIG_DIR;
import static org.motechproject.config.domain.BootstrapConfig.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/configContext.xml"})
public class BootstrapconfigManagerIT {

    @Autowired
    private BootstrapConfigManagerImpl bootstrapConfigManager;

    @Test
    public void shouldLoadBootstrapConfigLocationFromPropertiesFile() {
        assertThat(bootstrapConfigManager.getDefaultBootstrapConfigDir(), IsEqual.equalTo("/tmp"));
    }
}

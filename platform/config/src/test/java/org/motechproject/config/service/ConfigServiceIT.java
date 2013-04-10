package org.motechproject.config.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.domain.MotechConfig;
import org.motechproject.config.repository.AllMotechConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class ConfigServiceIT {

    private static final String HOST = "1.2.3.4";
    private static final String PORT = "8080";
    private static final String MODULE = "test-module";

    @Autowired
    private ConfigService configService;
    @Autowired
    private AllMotechConfigs allMotechConfigs;

    @Before
    public void setUp() {
        Properties configs = new Properties();
        configs.put("host", HOST);
        configs.put("port", PORT);
        MotechConfig config = new MotechConfig(MODULE, configs);
        configService.createConfig(config);
    }

    @Test
    public void testFindConfig() {
        String host = configService.findConfig("host", MODULE);
        assertNotNull(host);
        assertEquals(host, HOST);
    }

    @After
    public void tearDown() {
        allMotechConfigs.removeAll();
    }
}

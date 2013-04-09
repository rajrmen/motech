package org.motechproject.server.conf.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class ConfigServiceIT {

    @Autowired
    private ConfigService configService;
    @Autowired
    private AllMotechConfigs allMotechConfigs;

    @Before
    public void setUp() {
        Map<String, String> configs = new HashMap<>();
        configs.put("host", "1.2.3.4");
        configs.put("port", "8080");
        MotechConfig config = new MotechConfig("test-module", configs);
        configService.createConfig(config);
    }

    @Test
    public void testFindConfig() {
        String host = configService.findConfig("host", "test-module");
        assertNotNull(host);
        assertEquals(host, "1.2.3.4");
    }

    @After
    public void tearDown() {
        allMotechConfigs.removeAll();
    }
}

package com.blackduck.integration.blackduck.nexus3.task.common;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.blackduck.integration.blackduck.configuration.BlackDuckServerConfig;
import com.blackduck.integration.blackduck.nexus3.mock.MockBlackDuckConnection;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.test.TestProperties;
import com.blackduck.integration.test.TestPropertyKey;

public class CommonRepositoryTaskHelperTestIT {

    @Test
    public void getHubServerConfigTest() throws IntegrationException {
        final MockBlackDuckConnection mockBlackDuckConnection = new MockBlackDuckConnection();
        final CommonRepositoryTaskHelper commonRepositoryTaskHelper = new CommonRepositoryTaskHelper(null, null, mockBlackDuckConnection);

        final BlackDuckServerConfig blackDuckServerConfig = commonRepositoryTaskHelper.getBlackDuckServerConfig();
        Assert.assertNotNull(blackDuckServerConfig);

        final URL blackDuckUrl = blackDuckServerConfig.getBlackDuckUrl();
        Assert.assertNotNull(blackDuckUrl);

        final TestProperties testProperties = new TestProperties();
        final String storedUrl = testProperties.getProperty(TestPropertyKey.TEST_HUB_SERVER_URL);
        Assert.assertEquals(blackDuckUrl.toExternalForm(), storedUrl);
    }

    @Test
    public void getHubServicesFactoryTest() throws IntegrationException {
        final MockBlackDuckConnection mockBlackDuckConnection = new MockBlackDuckConnection();
        final CommonRepositoryTaskHelper commonRepositoryTaskHelper = new CommonRepositoryTaskHelper(null, null, mockBlackDuckConnection);

        final BlackDuckServicesFactory blackDuckServicesFactory = commonRepositoryTaskHelper.getBlackDuckServicesFactory();
        Assert.assertNotNull(blackDuckServicesFactory);
    }
}

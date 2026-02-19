package com.synopsys.integration.blackduck.nexus3.task.inspector.wait;

import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.wait.WaitJobTask;

public class ComponentLinkWaitJob implements WaitJobTask {
    private final String projectVersionViewHref;
    private final BlackDuckApiClient blackDuckApiClient;

    public ComponentLinkWaitJob(String projectVersionViewHref, BlackDuckApiClient blackDuckApiClient) {
        this.projectVersionViewHref = projectVersionViewHref;
        this.blackDuckApiClient = blackDuckApiClient;
    }

    @Override
    public boolean isComplete() throws IntegrationException {
        ProjectVersionView projectVersionView = getProjectVersionView(projectVersionViewHref);
        return projectVersionView.hasLink(ProjectVersionView.COMPONENTS_LINK);
    }

    private ProjectVersionView getProjectVersionView(String projectVersionViewHref) throws IntegrationException {
        return blackDuckApiClient.getResponse(new com.synopsys.integration.rest.HttpUrl(projectVersionViewHref), ProjectVersionView.class);
    }

}

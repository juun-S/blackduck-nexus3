package com.synopsys.integration.blackduck.nexus3.task.inspector;

import org.apache.commons.lang3.StringUtils;
import org.sonatype.nexus.repository.Repository;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.bdioupload.BdioUploadService;
import com.synopsys.integration.blackduck.nexus3.task.inspector.dependency.DependencyType;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.dataservice.ComponentService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;

public class InspectorConfiguration {
    private final String exceptionMessage;

    private final DependencyType dependencyType;
    private final BlackDuckApiClient blackDuckApiClient;
    private final ComponentService componentService;
    private final ProjectService projectService;
    private final CodeLocationCreationService codeLocationCreationService;
    private final BdioUploadService bdioUploadService;
    private final ProjectBomService projectBomService;

    public static InspectorConfiguration createConfigurationWithError(String exceptionMessage, Repository repository, DependencyType dependencyType) {
        return new InspectorConfiguration(exceptionMessage, repository, dependencyType, null, null, null, null, null, null);
    }

    public static InspectorConfiguration createConfiguration(Repository repository, DependencyType dependencyType, BlackDuckApiClient blackDuckApiClient, ComponentService componentService,
        ProjectService projectService, CodeLocationCreationService codeLocationCreationService, BdioUploadService bdioUploadService, ProjectBomService projectBomService) {
        return new InspectorConfiguration(null, repository, dependencyType, blackDuckApiClient, componentService, projectService, codeLocationCreationService, bdioUploadService, projectBomService);
    }

    public InspectorConfiguration(String exceptionMessage, Repository repository, DependencyType dependencyType, BlackDuckApiClient blackDuckApiClient, ComponentService componentService,
        ProjectService projectService, CodeLocationCreationService codeLocationCreationService, BdioUploadService bdioUploadService, ProjectBomService projectBomService) {
        this.exceptionMessage = exceptionMessage;
        this.repository = repository;
        this.dependencyType = dependencyType;
        this.blackDuckApiClient = blackDuckApiClient;
        this.componentService = componentService;
        this.projectService = projectService;
        this.codeLocationCreationService = codeLocationCreationService;
        this.bdioUploadService = bdioUploadService;
        this.projectBomService = projectBomService;
    }

    public boolean hasErrors() {
        return StringUtils.isNotBlank(exceptionMessage) || null == blackDuckApiClient || null == projectService || null == codeLocationCreationService || null == bdioUploadService || null == projectBomService;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public Repository getRepository() {
        return repository;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    public BlackDuckApiClient getBlackDuckApiClient() {
        return blackDuckApiClient;
    }

    public ComponentService getComponentService() {
        return componentService;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public CodeLocationCreationService getCodeLocationCreationService() {
        return codeLocationCreationService;
    }

    public BdioUploadService getBdioUploadService() {
        return bdioUploadService;
    }

    public ProjectBomService getProjectBomService() {
        return projectBomService;
    }
}

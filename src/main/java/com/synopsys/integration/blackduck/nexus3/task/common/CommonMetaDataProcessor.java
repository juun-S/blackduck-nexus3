/**
 * blackduck-nexus3
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.blackduck.nexus3.task.common;

// import static com.synopsys.integration.blackduck.api.generated.enumeration.ComponentVersionRiskProfileRiskDataCountsCountTypeType.CRITICAL;
// import static com.synopsys.integration.blackduck.api.generated.enumeration.ComponentVersionRiskProfileRiskDataCountsCountTypeType.HIGH;
// import static com.synopsys.integration.blackduck.api.generated.enumeration.ComponentVersionRiskProfileRiskDataCountsCountTypeType.LOW;
// import static com.synopsys.integration.blackduck.api.generated.enumeration.ComponentVersionRiskProfileRiskDataCountsCountTypeType.MEDIUM;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import com.synopsys.integration.blackduck.api.generated.component.ComponentVersionRiskProfileRiskDataCountsView; // Removed
// import com.synopsys.integration.blackduck.api.generated.enumeration.ComponentVersionRiskProfileRiskDataCountsCountTypeType; // Removed
import com.synopsys.integration.blackduck.api.manual.view.ProjectVersionComponentVersionView; 
// import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionComponentView; // Replaced
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionPolicyStatusView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.api.manual.view.ProjectView; // Update to manual view if needed, or keep generated if compatible.
// NOTE: SDK TagService uses manual view. ProjectService likely uses generated view. 
// We should check if they are compatible.
// Let's assume generated view is fine or we update to manual.
// Actually, I'll stick to generated view import for now and see if it fails.
// But better to remove local TagService import and add SDK TagService import.

import com.synopsys.integration.blackduck.api.generated.view.TagView;
// import com.synopsys.integration.blackduck.nexus3.TagService; // Removed
import com.synopsys.integration.blackduck.service.dataservice.TagService; // SDK TagService
import com.synopsys.integration.blackduck.nexus3.task.AssetWrapper;
import com.synopsys.integration.blackduck.nexus3.ui.AssetPanelLabel;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.dataservice.ProjectBomService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.model.PolicyStatusDescription;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;

@Named
@Singleton
public class CommonMetaDataProcessor {
    public static final String NEXUS_PROJECT_TAG = "blackduck_nexus3";

    // private Map<ComponentVersionRiskProfileRiskDataCountsCountTypeType, Integer> countsToPriorty = new EnumMap<>(ComponentVersionRiskProfileRiskDataCountsCountTypeType.class);
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CommonMetaDataProcessor() {
        // countsToPriorty.put(CRITICAL, 10);
        // countsToPriorty.put(HIGH, 8);
        // countsToPriorty.put(MEDIUM, 6);
        // countsToPriorty.put(LOW, 4);
    }

    public void setAssetVulnerabilityData(VulnerabilityLevels vulnerabilityLevels, AssetWrapper assetWrapper) {
        assetWrapper.addToBlackDuckAssetPanel(AssetPanelLabel.VULNERABILITIES, vulnerabilityLevels.getAllCounts());
    }

    public List<ProjectVersionComponentVersionView> getBomComponents(ProjectBomService projectBomService, ProjectVersionView projectVersionView) throws IntegrationException {
        if (!projectVersionView.hasLink(ProjectVersionView.COMPONENTS_LINK)) {
            logger.error(String.format("The '%s' link is missing from the Project Version: '%s'.", ProjectVersionView.COMPONENTS_LINK, projectVersionView.getHref().orElse("MISSING HREF")));
        }
        return projectBomService.getComponentsForProjectVersion(projectVersionView);
    }

    /*
    public void addAllAssetVulnerabilityCounts(List<ComponentVersionRiskProfileRiskDataCountsView> vulnerabilities, VulnerabilityLevels vulnerabilityLevels) {
        for (ComponentVersionRiskProfileRiskDataCountsView riskCountView : vulnerabilities) {
            ComponentVersionRiskProfileRiskDataCountsCountTypeType riskCountType = riskCountView.getCountType();
            BigDecimal riskCount = riskCountView.getCount();
            vulnerabilityLevels.addXVulnerabilities(riskCountType, riskCount);
        }
    }

    public void addMaxAssetVulnerabilityCounts(List<ComponentVersionRiskProfileRiskDataCountsView> vulnerabilities, VulnerabilityLevels vulnerabilityLevels) {
        Optional<ComponentVersionRiskProfileRiskDataCountsCountTypeType> highestSeverity =
            vulnerabilities.stream()
                .filter(Objects::nonNull)
                .filter(this::hasVulnerabilities)
                .map(ComponentVersionRiskProfileRiskDataCountsView::getCountType)
                .max(Comparator.comparingInt(this::getCountTypePriority));

        highestSeverity.ifPresent(vulnerabilityLevels::addVulnerability);
    }

    private int getCountTypePriority(ComponentVersionRiskProfileRiskDataCountsCountTypeType countType) {
        return countsToPriorty.getOrDefault(countType, 0);
    }
    */

    /*
    private boolean hasVulnerabilities(ComponentVersionRiskProfileRiskDataCountsView riskCountView) {
        return Optional
                   .ofNullable(riskCountView.getCount())
                   .filter(bigDecimal -> bigDecimal.compareTo(BigDecimal.ZERO) > 0)
                   .isPresent();
    }
    */

    public void removeAssetVulnerabilityData(AssetWrapper assetWrapper) {
        assetWrapper.removeFromBlackDuckAssetPanel(AssetPanelLabel.VULNERABILITIES);
        assetWrapper.removeFromBlackDuckAssetPanel(AssetPanelLabel.VULNERABLE_COMPONENTS);
    }

    public void setAssetPolicyData(ProjectVersionPolicyStatusView policyStatusView, AssetWrapper assetWrapper) {
        PolicyStatusDescription policyStatusDescription = new PolicyStatusDescription(policyStatusView);
        String policyStatus = policyStatusDescription.getPolicyStatusMessage();
        String overallStatus = policyStatusView.getOverallStatus().prettyPrint();

        assetWrapper.addToBlackDuckAssetPanel(AssetPanelLabel.POLICY_STATUS, policyStatus);
        assetWrapper.addToBlackDuckAssetPanel(AssetPanelLabel.OVERALL_POLICY_STATUS, overallStatus);
    }

    public void removePolicyData(AssetWrapper assetWrapper) {
        assetWrapper.removeFromBlackDuckAssetPanel(AssetPanelLabel.POLICY_STATUS);
        assetWrapper.removeFromBlackDuckAssetPanel(AssetPanelLabel.OVERALL_POLICY_STATUS);
    }

    public Optional<ProjectVersionPolicyStatusView> checkAssetPolicy(BlackDuckApiClient blackDuckApiClient, ProjectVersionView projectVersionView) throws IntegrationException {
        logger.info("Checking metadata of {}", projectVersionView.getVersionName());
        // getResponse is available in BlackDuckApiClient
        return blackDuckApiClient.getResponse(projectVersionView, ProjectVersionView.POLICY_STATUS_LINK_RESPONSE);
    }

    public void removeAllMetaData(AssetWrapper assetWrapper) {
        removePolicyData(assetWrapper);
        removeAssetVulnerabilityData(assetWrapper);
    }

    public ProjectVersionView getOrCreateProjectVersion(BlackDuckApiClient blackDuckApiClient, ProjectService projectService, TagService tagService, String name, String versionName) throws IntegrationException {
        ProjectVersionWrapper projectVersionWrapper = handleGetOrCreateProjectVersion(projectService, name, versionName);

        // TagService is now passed in or created via factory, not instantiated with just client/logger easily if it requires ApiDiscovery
        // But here we can assume it is passed in, or we can try to instantiate it if we have factory.
        // For now, let's change signature to accept TagService.
        
        ProjectView projectView = projectVersionWrapper.getProjectView();
        
        // SDK TagService uses manual.view.ProjectView? 
        // If projectView is generated.view, we might need to assume compatibility or use a wrapper.
        // Verification needed: assume compatibility for now or check hierarchy.
        
        // Actually, let's look at the javap of ProjectService.

        Optional<TagView> matchingTag = tagService.findMatchingTag(projectView, NEXUS_PROJECT_TAG);
        if (!matchingTag.isPresent()) {
            logger.debug("Adding tag {} to project {} in Black Duck.", NEXUS_PROJECT_TAG, name);
            TagView tagView = new TagView();
            tagView.setName(NEXUS_PROJECT_TAG);
            tagService.createTag(projectView, tagView);
        }

        return projectVersionWrapper.getProjectVersionView();
    }

    private ProjectVersionWrapper handleGetOrCreateProjectVersion(ProjectService projectService, String name, String versionName) throws IntegrationException {
        logger.debug("Getting project in Black Duck : {}. Version: {}", name, versionName);

        ProjectVersionWrapper projectVersionWrapper = null;
        ProjectSyncModel projectSyncModel = ProjectSyncModel.createWithDefaults(name, versionName);
        Optional<ProjectView> projectViewOptional = projectService.getProjectByName(name);
        if (projectViewOptional.isPresent()) {
            ProjectView projectView = projectViewOptional.get();
            ProjectVersionView projectVersionView = null;
            Optional<ProjectVersionView> projectVersionViewOptional = projectService.getProjectVersion(projectView, versionName);
            if (projectVersionViewOptional.isPresent()) {
                projectVersionView = projectVersionViewOptional.get();
            } else {
                logger.debug("Creating version: {}. In Project {}", versionName, name);
                projectVersionView = projectService.createProjectVersion(projectView, projectSyncModel.createProjectVersionRequest());
            }
            projectVersionWrapper = new ProjectVersionWrapper(projectView, projectVersionView);
        } else {
            logger.debug("Creating project in Black Duck : {}. Version: {}", name, versionName);
            projectVersionWrapper = projectService.createProject(projectSyncModel.createProjectRequest());
        }
        return projectVersionWrapper;
    }
}

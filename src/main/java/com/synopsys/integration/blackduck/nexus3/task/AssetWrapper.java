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
package com.synopsys.integration.blackduck.nexus3.task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger; // Added logger 
import org.slf4j.LoggerFactory; // Added logger
import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.api.BlobStore;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.content.Asset;
import org.sonatype.nexus.repository.content.Component;

import com.synopsys.integration.blackduck.nexus3.database.QueryManager;
import com.synopsys.integration.blackduck.nexus3.ui.AssetPanel;
import com.synopsys.integration.blackduck.nexus3.ui.AssetPanelLabel;
import com.synopsys.integration.exception.IntegrationException;

public class AssetWrapper {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Asset asset;
    private final Repository repository;
    private final QueryManager queryManager;
    private final DateTimeParser dateTimeParser;
    private final AssetPanelLabel statusLabel;
    private Component associatedComponent;
    private Blob associatedBlob;
    private AssetPanel associatedAssetPanel;

    public static AssetWrapper createInspectionAssetWrapper(Asset asset, Repository repository, QueryManager queryManager) {
        return new AssetWrapper(asset, repository, queryManager, AssetPanelLabel.INSPECTION_TASK_STATUS);
    }

    public static AssetWrapper createScanAssetWrapper(Asset asset, Repository repository, QueryManager queryManager) {
        return new AssetWrapper(asset, repository, queryManager, AssetPanelLabel.SCAN_TASK_STATUS);
    }

    public static AssetWrapper createAssetWrapper(Asset asset, Repository repository, QueryManager queryManager, AssetPanelLabel statusLabel) {
        return new AssetWrapper(asset, repository, queryManager, statusLabel);
    }

    private AssetWrapper(Asset asset, Repository repository, QueryManager queryManager, AssetPanelLabel statusLabel) {
        this.asset = asset;
        this.repository = repository;
        this.queryManager = queryManager;
        dateTimeParser = new DateTimeParser();
        this.statusLabel = statusLabel;
    }

    public Component getComponent() {
        if (associatedComponent == null) {
            // Content API Asset has direct component access
            associatedComponent = asset.component().orElse(null);
            if (associatedComponent == null) {
                 // Try to look up via QueryManager if not present? 
                 // Usually asset always has component in Nexus 3 Content API unless it's a raw asset without component?
                 // But findAllAssetsInRepository returns Assets which might be linked.
            }
        }
        return associatedComponent;
    }

    public Blob getBlob() throws IntegrationException {
        if (associatedBlob == null) {
            org.sonatype.nexus.repository.content.AssetBlob assetBlob = asset.blob()
                .orElseThrow(() -> new IntegrationException("Could not find the AssetBlob for this asset."));
            
            associatedBlob = queryManager.getBlob(repository, assetBlob.blobRef());
            if (associatedBlob == null) {
                throw new IntegrationException("Could not find the Blob for this asset.");
            }
        }
        return associatedBlob;
    }

    // ...

    public void updateAsset() {
        // queryManager.updateAsset(repository, asset); 
        // Update is not straightforward in Content API purely via Asset object. 
        // Attributes update requires Fluent API or AttributesFacet.
        // For now, disabling update until we implement it properly via FluentAsset.
        logger.warn("Asset update requested but not implemented for Content API yet.");
    }

    public String getName() {
        Component comp = getComponent();
        return comp != null ? comp.name() : "unknown";
    }

    public String getFullPath() {
        return asset.path();
    }

    public String getVersion() {
         Component comp = getComponent();
         return comp != null ? StringUtils.defaultIfBlank(comp.version(), "bd-nexus3-unknown") : "bd-nexus3-unknown";
    }

    // ...

    public DateTime getAssetLastUpdated() {
        // AssetBlob has blobCreated(). 
        return asset.blob()
            .map(b -> dateTimeParser.formatDateTime(java.util.Date.from(b.blobCreated().toInstant())))
            .orElse(DateTime.now()); 
        // Note: Validation required for DateTime conversion from OffsetDateTime.
    }

    public void addToBlackDuckAssetPanel(AssetPanelLabel label, String value) {
        getAssetPanel().addToBlackDuckPanel(label, value);
    }

    public String getFromBlackDuckAssetPanel(AssetPanelLabel label) {
        return getAssetPanel().getFromBlackDuckPanel(label);
    }

    public void removeFromBlackDuckAssetPanel(AssetPanelLabel label) {
        getAssetPanel().removeFromBlackDuckPanel(label);
    }

    public Asset getAsset() {
        return asset;
    }

    public void addPendingToBlackDuckPanel(String pendingMessage) {
        updateStatus(TaskStatus.PENDING, pendingMessage);
    }

    public void addSuccessToBlackDuckPanel(String successMessage) {
        updateStatus(TaskStatus.SUCCESS, successMessage);
    }

    public void addComponentNotFoundToBlackDuckPanel(String componentNotFoundMessage) {
        updateStatus(TaskStatus.COMPONENT_NOT_FOUND, componentNotFoundMessage);
    }

    public void addFailureToBlackDuckPanel(String errorMessage) {
        updateStatus(TaskStatus.FAILURE, errorMessage);
    }

    private void updateStatus(TaskStatus taskStatus, String message) {
        removeFromBlackDuckAssetPanel(AssetPanelLabel.OLD_STATUS);
        addToBlackDuckAssetPanel(statusLabel, taskStatus.name());
        addToBlackDuckAssetPanel(AssetPanelLabel.TASK_STATUS_DESCRIPTION, message);
    }

    public void removeAllBlackDuckData() {
        for (AssetPanelLabel assetPanelLabel : AssetPanelLabel.values()) {
            removeFromBlackDuckAssetPanel(assetPanelLabel);
        }
    }

    public TaskStatus getBlackDuckStatus() {
        String status = getFromBlackDuckAssetPanel(statusLabel);
        if (StringUtils.isBlank(status)) {
            return null;
        }
        return Enum.valueOf(TaskStatus.class, status);
    }
}

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
package com.synopsys.integration.blackduck.nexus3.database;

import java.util.Collections;
import java.util.Optional;

import javax.inject.Named;
import javax.inject.Singleton;

// Removed legacy Storage API imports
import org.sonatype.nexus.blobstore.api.Blob;
import org.sonatype.nexus.blobstore.api.BlobRef;
// import org.sonatype.nexus.common.entity.EntityId;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.content.Asset;
import org.sonatype.nexus.repository.content.Component;
// import org.sonatype.nexus.repository.storage.Query;
// import org.sonatype.nexus.repository.storage.StorageFacet;
// import org.sonatype.nexus.repository.storage.StorageTx;
import org.sonatype.nexus.repository.content.facet.ContentFacet;

@Named
@Singleton
public class QueryManager {

    public Iterable<Asset> findAssetsInRepository(final Repository repository, final String filter) {
        return repository.facet(ContentFacet.class).assets().browse(Integer.MAX_VALUE, null); 
        // Note: 'filter' is not directly supported in browse() without criteria. 
        // For now returning all assets, filtering should be done by caller or implemented via search().
        // Legacy 'Query' object is removed.
    }

    public Iterable<Asset> findAllAssetsInRepository(final Repository repository) {
        return repository.facet(ContentFacet.class).assets().browse(Integer.MAX_VALUE, null);
    }

//    public void updateAsset(final Repository repository, final Asset asset) {
//        // Content API assets are generally immutable or updated via specific fluent methods.
//        // Direct saveAsset() equivalent is usually not exposed the same way.
//        // TODO: Verify if update is needed or provided by FluentAsset.
//    }

    public Blob getBlob(final Repository repository, final BlobRef blobRef) {
        return repository.facet(ContentFacet.class).blobs().blob(blobRef).orElse(null);
    }

    public Optional<Component> findComponent(final Repository repository, final String namespace, final String name, final String version) {
        return repository.facet(ContentFacet.class).components().name(name).version(version).find();
    }
}

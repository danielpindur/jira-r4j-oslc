// Start of user code Copyright
/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Simple
 */
// End of user code

package cz.vutbr.fit.danielpindur.oslc.r4j;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Folder;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Person;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.RequirementCollection;

// Start of user code imports
import cz.vutbr.fit.danielpindur.oslc.r4j.clients.JiraAdaptorClient;
// End of user code

// Start of user code pre_class_code
// End of user code

public class ResourcesFactory {

    private String basePath;

    // Start of user code class_attributes
    // End of user code

    public ResourcesFactory(String basePath) {
        this.basePath = basePath;
    }

    // Start of user code class_methods
    private final String jiraBasePath = JiraAdaptorClient.getServiceProviderURI();

    public URI constructURIForRequirement(final String id) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("id", id);
        String instanceURI = "requirement/Requirement/{id}";

        final UriBuilder builder = UriBuilder.fromUri(this.jiraBasePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }

    public Link constructLinkForRequirement(final String id) {
        return new Link(constructURIForRequirement(id));
    }

    public URI constructURIForRequirementCollection(final String id) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("id", id);
        String instanceURI = "requirementCollection/RequirementCollection/{id}";

        final UriBuilder builder = UriBuilder.fromUri(this.jiraBasePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }

    public Link constructLinkForRequirementCollection(final String id) {
        return new Link(constructURIForRequirementCollection(id));
    }
    // End of user code

    //methods for Folder resource
    
    public Folder createFolder(final String id) {
        return new Folder(constructURIForFolder(id));
    }
    
    public URI constructURIForFolder(final String id) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("id", id);
        String instanceURI = "folder/Folder/{id}";
    
        final UriBuilder builder = UriBuilder.fromUri(this.basePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }
    
    public Link constructLinkForFolder(final String id , final String label) {
        return new Link(constructURIForFolder(id), label);
    }
    
    public Link constructLinkForFolder(final String id) {
        return new Link(constructURIForFolder(id));
    }
    

}

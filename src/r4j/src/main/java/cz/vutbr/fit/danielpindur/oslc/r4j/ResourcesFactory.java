// Start of user code Copyright
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

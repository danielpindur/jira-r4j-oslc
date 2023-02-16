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

package cz.vutbr.fit.danielpindur.oslc.jira;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Person;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.RequirementCollection;

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

    //methods for Person resource
    
    public Person createPerson(final String email) {
        return new Person(constructURIForPerson(email));
    }
    
    public URI constructURIForPerson(final String email) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("email", email);
        String instanceURI = "person/Person/{email}";
    
        final UriBuilder builder = UriBuilder.fromUri(this.basePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }
    
    public Link constructLinkForPerson(final String email , final String label) {
        return new Link(constructURIForPerson(email), label);
    }
    
    public Link constructLinkForPerson(final String email) {
        return new Link(constructURIForPerson(email));
    }
    

    //methods for Project resource
    
    public Project createProject(final String id) {
        return new Project(constructURIForProject(id));
    }
    
    public URI constructURIForProject(final String id) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("id", id);
        String instanceURI = "project/Project/{id}";
    
        final UriBuilder builder = UriBuilder.fromUri(this.basePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }
    
    public Link constructLinkForProject(final String id , final String label) {
        return new Link(constructURIForProject(id), label);
    }
    
    public Link constructLinkForProject(final String id) {
        return new Link(constructURIForProject(id));
    }
    

    //methods for Requirement resource
    
    public Requirement createRequirement(final String id) {
        return new Requirement(constructURIForRequirement(id));
    }
    
    public URI constructURIForRequirement(final String id) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("id", id);
        String instanceURI = "requirement/Requirement/{id}";
    
        final UriBuilder builder = UriBuilder.fromUri(this.basePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }
    
    public Link constructLinkForRequirement(final String id , final String label) {
        return new Link(constructURIForRequirement(id), label);
    }
    
    public Link constructLinkForRequirement(final String id) {
        return new Link(constructURIForRequirement(id));
    }
    

    //methods for RequirementCollection resource
    
    public RequirementCollection createRequirementCollection(final String id) {
        return new RequirementCollection(constructURIForRequirementCollection(id));
    }
    
    public URI constructURIForRequirementCollection(final String id) {
        Map<String, Object> pathParameters = new HashMap<String, Object>();
        pathParameters.put("id", id);
        String instanceURI = "requirementCollection/RequirementCollection/{id}";
    
        final UriBuilder builder = UriBuilder.fromUri(this.basePath);
        return builder.path(instanceURI).buildFromMap(pathParameters);
    }
    
    public Link constructLinkForRequirementCollection(final String id , final String label) {
        return new Link(constructURIForRequirementCollection(id), label);
    }
    
    public Link constructLinkForRequirementCollection(final String id) {
        return new Link(constructURIForRequirementCollection(id));
    }
    

}

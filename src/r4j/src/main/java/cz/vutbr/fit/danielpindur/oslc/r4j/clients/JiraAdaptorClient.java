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
 *
 * This file is generated by Lyo Designer (https://www.eclipse.org/lyo/)
 */
// End of user code

package cz.vutbr.fit.danielpindur.oslc.r4j.clients;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.session.SessionProvider;
import org.eclipse.lyo.client.OSLCConstants;
import org.eclipse.lyo.client.OslcClient;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.RequirementCollection;

// Start of user code imports
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import java.util.HashMap;
import java.util.Map;
// End of user code


// Start of user code pre_class_code
// End of user code

public class JiraAdaptorClient
{

    // Start of user code class_attributes
    static Map<String, String> requestHeaders = new HashMap<>();
    // End of user code
    
    // Start of user code class_methods
    public static String getServiceProviderURI() {
        return ConfigurationProvider.GetConfiguration().JiraAdaptorUrl + "/jira/services";
    }
    static String serviceProviderCatalogURI = getServiceProviderURI() + "/catalog/singleton";

    private static HttpAuthenticationFeature getAuthenticationHandler() {
        requestHeaders.clear();
        var session = SessionProvider.GetSession();

        if (session == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        var token = session.getAttribute(SessionProvider.OAUTH_TOKEN);
        if (token != null) {
            requestHeaders.put("Authorization", "Bearer " + token);
            return null;
        }
        else {
            var username = session.getAttribute(SessionProvider.BASIC_USERNAME).toString();
            var password = session.getAttribute(SessionProvider.BASIC_PASSWORD).toString();

            if (username == null || password == null) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            return HttpAuthenticationFeature.basic(username, password.getBytes());
        }
    }

    // End of user code

    public static ServiceProviderCatalog getServiceProviderCatalog() throws Exception {
        OslcClient client = new OslcClient();
        Response response = null;
        ServiceProviderCatalog catalog = null;

        // Start of user code getServiceProviderCatalog_init
        ClientBuilder builder = ClientBuilder.newBuilder();
        var authHandler = getAuthenticationHandler();
        if (authHandler != null) {
            builder.register(authHandler);
        }
        client = new OslcClient(builder);
        // End of user code

        response = client.getResource(serviceProviderCatalogURI, requestHeaders, OSLCConstants.CT_RDF);
        if (response != null) {
            catalog = response.readEntity(ServiceProviderCatalog.class);
        }
        // Start of user code getServiceProviderCatalog_final
        // End of user code
        return catalog;
    }

    public static Requirement getRequirement(String resourceURI) throws Exception {
        OslcClient client = new OslcClient();
        Response response = null;
        Requirement resource = null;

        // Start of user code getRequirement_init
        ClientBuilder builder = ClientBuilder.newBuilder();
        var authHandler = getAuthenticationHandler();
        if (authHandler != null) {
            builder.register(authHandler);
        }
        client = new OslcClient(builder);
        // End of user code

        response = client.getResource(resourceURI, requestHeaders, OSLCConstants.CT_RDF);
        if (response != null) {
            resource = response.readEntity(Requirement.class);
        }
        // Start of user code getRequirement_final
        // End of user code
        return resource;
    }

    public static RequirementCollection getRequirementCollection(String resourceURI) throws Exception {
        OslcClient client = new OslcClient();
        Response response = null;
        RequirementCollection resource = null;

        // Start of user code getRequirementCollection_init
        ClientBuilder builder = ClientBuilder.newBuilder();
        var authHandler = getAuthenticationHandler();
        if (authHandler != null) {
            builder.register(authHandler);
        }
        client = new OslcClient(builder);
        // End of user code

        response = client.getResource(resourceURI, requestHeaders, OSLCConstants.CT_RDF);
        if (response != null) {
            resource = response.readEntity(RequirementCollection.class);
        }
        // Start of user code getRequirementCollection_final
        // End of user code
        return resource;
    }
}

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
 *
 * This file is generated by Lyo Designer (https://www.eclipse.org/lyo/)
 */
// End of user code

package cz.vutbr.fit.danielpindur.oslc.r4j.services;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;

import cz.vutbr.fit.danielpindur.oslc.r4j.RestDelegate;
import cz.vutbr.fit.danielpindur.oslc.r4j.servlet.ServiceProviderCatalogSingleton;

// Start of user code imports
// End of user code

@OslcService(OslcConstants.OSLC_CORE_DOMAIN)
@Path("serviceProviders")
public class ServiceProviderService
{
    @Context private HttpServletRequest httpServletRequest;
    @Context private HttpServletResponse httpServletResponse;

    /**
     * RDF/XML, XML and JSON representations of an OSLC Service Provider collection
     * @return
     */
    @OslcDialog
    (
         title = "Service Provider Selection Dialog",
         label = "Service Provider Selection Dialog",
         uri = "",
         hintWidth = "1000px",
         hintHeight = "600px",
         resourceTypes = {OslcConstants.TYPE_SERVICE_PROVIDER},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @OslcQueryCapability
    (
         title = "Service Provider Query Capability",
         label = "Service Provider Query",
         resourceShape = OslcConstants.PATH_RESOURCE_SHAPES + "/" + OslcConstants.PATH_SERVICE_PROVIDER,
         resourceTypes = {OslcConstants.TYPE_SERVICE_PROVIDER},
         usages = {OslcConstants.OSLC_USAGE_DEFAULT}
    )
    @GET
    @Path("")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_JSON_LD, OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public ServiceProvider[] getServiceProviders()
    {
        httpServletResponse.addHeader("Oslc-Core-Version","2.0");
        return ServiceProviderCatalogSingleton.getServiceProviders(httpServletRequest);
    }

    /**
     * RDF/XML, XML and JSON representations of a single OSLC Service Provider
     *
     * @param serviceProviderId
     * @return
     */
    @GET
    @Path("{serviceProviderId}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_JSON_LD, OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    public ServiceProvider getServiceProvider(@PathParam("serviceProviderId") final String serviceProviderId)
    {
        httpServletResponse.addHeader("Oslc-Core-Version","2.0");
        return ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, serviceProviderId);
    }

    /**
     * HTML representation of a single OSLC Service Provider
     *
     * Forwards to serviceprovider_html.jsp to create the html document
     *
     * @param serviceProviderId
     */
    @GET
    @Path("{serviceProviderId}")
    @Produces(MediaType.TEXT_HTML)
    public void getHtmlServiceProvider(@PathParam("serviceProviderId") final String serviceProviderId) throws ServletException, IOException
    {
        ServiceProvider serviceProvider = ServiceProviderCatalogSingleton.getServiceProvider(httpServletRequest, serviceProviderId);
        Service [] services = serviceProvider.getServices();

        httpServletRequest.setAttribute("serviceProvider", serviceProvider);
        httpServletRequest.setAttribute("services", services);
        // Start of user code getHtmlServiceProvider_setAttributes
        // End of user code

        RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cz/vutbr/fit/danielpindur/oslc/r4j/serviceprovider.jsp");
        rd.forward(httpServletRequest, httpServletResponse);
        return;
    }
}


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

package cz.vutbr.fit.danielpindur.oslc.jira.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.UriBuilder;

import cz.vutbr.fit.danielpindur.oslc.shared.errors.ErrorHandler;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.eclipse.lyo.oslc4j.provider.json4j.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.annotation.OslcCreationFactory;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialog;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDialogs;
import org.eclipse.lyo.oslc4j.core.annotation.OslcQueryCapability;
import org.eclipse.lyo.oslc4j.core.annotation.OslcService;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.eclipse.lyo.oslc4j.core.model.Preview;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;

import cz.vutbr.fit.danielpindur.oslc.jira.RestDelegate;
import cz.vutbr.fit.danielpindur.oslc.jira.ServerConstants;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Oslc_rmDomainConstants;
import cz.vutbr.fit.danielpindur.oslc.jira.servlet.ServiceProviderCatalogSingleton;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

// Start of user code imports
// End of user code

// Start of user code pre_class_code
// End of user code
@Path("requirement")
public class RequirementService
{
    @Context private HttpServletRequest httpServletRequest;
    @Context private HttpServletResponse httpServletResponse;
    @Context private UriInfo uriInfo;
    @Inject  private RestDelegate delegate;

    private static final Logger log = LoggerFactory.getLogger(RequirementService.class);

    // Start of user code class_attributes
    private static final ErrorHandler errorHandler = new ErrorHandler(log);
    // End of user code

    // Start of user code class_methods
    // End of user code

    public RequirementService()
    {
        super();
    }

    private void addCORSHeaders (final HttpServletResponse httpServletResponse) {
        //UI preview can be blocked by CORS policy.
        //add select CORS headers to every response that is embedded in an iframe.
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD");
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
    }

    @GET
    @Path("Requirement/{id}")
    @Produces({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_JSON_LD, OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON})
    @Operation(
        summary = "GET for resources of type {'" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "'}",
        description = "GET for resources of type {'" + "<a href=\"" + Oslc_rmDomainConstants.REQUIREMENT_TYPE + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}" +
            ", with respective resource shapes {'" + "<a href=\"" + "../services/" + OslcConstants.PATH_RESOURCE_SHAPES + "/" + Oslc_rmDomainConstants.REQUIREMENT_PATH + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}",
        responses = {@ApiResponse(description = "default response",
            content = {@Content(mediaType = OslcMediaType.APPLICATION_RDF_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_JSON), @Content(
                mediaType = OslcMediaType.TEXT_TURTLE), @Content(
                mediaType = MediaType.TEXT_HTML), @Content(
                mediaType = OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML)})
        }
    )
    public Requirement getRequirement(
                @PathParam("id") final String id
        ) throws IOException, ServletException, URISyntaxException
    {
        // Start of user code getResource_init
        // End of user code

        final Requirement aRequirement = delegate.getRequirement(httpServletRequest, id);

        if (aRequirement != null) {
            // Start of user code getRequirement
            // End of user code
            httpServletResponse.setHeader("ETag", delegate.getETagFromRequirement(aRequirement));
            httpServletResponse.addHeader(ServerConstants.HDR_OSLC_VERSION, ServerConstants.OSLC_VERSION_V2);
            return aRequirement;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("Requirement/{id}")
    @Produces({ MediaType.TEXT_HTML })
    @Operation(
        summary = "GET for resources of type {'" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "'}",
        description = "GET for resources of type {'" + "<a href=\"" + Oslc_rmDomainConstants.REQUIREMENT_TYPE + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}" +
            ", with respective resource shapes {'" + "<a href=\"" + "../services/" + OslcConstants.PATH_RESOURCE_SHAPES + "/" + Oslc_rmDomainConstants.REQUIREMENT_PATH + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}",
        responses = {@ApiResponse(description = "default response",
            content = {@Content(mediaType = OslcMediaType.APPLICATION_RDF_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_JSON), @Content(
                mediaType = OslcMediaType.TEXT_TURTLE), @Content(
                mediaType = MediaType.TEXT_HTML), @Content(
                mediaType = OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML)})
        }
    )
    public void getRequirementAsHtml(
        @PathParam("id") final String id
        ) throws ServletException, IOException, URISyntaxException
    {
        // Start of user code getRequirementAsHtml_init
        // End of user code

        final Requirement aRequirement = delegate.getRequirement(httpServletRequest, id);

        if (aRequirement != null) {
            httpServletRequest.setAttribute("aRequirement", aRequirement);
            // Start of user code getRequirementAsHtml_setAttributes
            // End of user code

            RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cz/vutbr/fit/danielpindur/oslc/jira/requirement.jsp");
            rd.forward(httpServletRequest,httpServletResponse);
            return;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("Requirement/{id}")
    @Produces({OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML})
    @Operation(
        summary = "GET for resources of type {'" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "'}",
        description = "GET for resources of type {'" + "<a href=\"" + Oslc_rmDomainConstants.REQUIREMENT_TYPE + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}" +
            ", with respective resource shapes {'" + "<a href=\"" + "../services/" + OslcConstants.PATH_RESOURCE_SHAPES + "/" + Oslc_rmDomainConstants.REQUIREMENT_PATH + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}",
        responses = {@ApiResponse(description = "default response",
            content = {@Content(mediaType = OslcMediaType.APPLICATION_RDF_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_JSON), @Content(
                mediaType = OslcMediaType.TEXT_TURTLE), @Content(
                mediaType = MediaType.TEXT_HTML), @Content(
                mediaType = OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML)})
        }
    )
    public Compact getRequirementCompact(
        @PathParam("id") final String id
        ) throws ServletException, IOException, URISyntaxException
    {
        String iconUri = OSLC4JUtils.getPublicURI() + "/images/ui_preview_icon.gif";
        String smallPreviewHintHeight = "200px";
        String smallPreviewHintWidth = "300px";
        String largePreviewHintHeight = "400px";
        String largePreviewHintWidth = "600px";

        // Start of user code getRequirementCompact_init
        //TODO: adjust the preview height & width values from the default values provided above.
        // End of user code

        final Requirement aRequirement = delegate.getRequirement(httpServletRequest, id);

        if (aRequirement != null) {
            final Compact compact = new Compact();

            compact.setAbout(aRequirement.getAbout());
            compact.setTitle(aRequirement.toString());

            compact.setIcon(new URI(iconUri));

            //Create and set attributes for OSLC preview resource
            final Preview smallPreview = new Preview();
            smallPreview.setHintHeight(smallPreviewHintHeight);
            smallPreview.setHintWidth(smallPreviewHintWidth);
            smallPreview.setDocument(UriBuilder.fromUri(aRequirement.getAbout()).path("smallPreview").build());
            compact.setSmallPreview(smallPreview);

            final Preview largePreview = new Preview();
            largePreview.setHintHeight(largePreviewHintHeight);
            largePreview.setHintWidth(largePreviewHintWidth);
            largePreview.setDocument(UriBuilder.fromUri(aRequirement.getAbout()).path("largePreview").build());
            compact.setLargePreview(largePreview);

            httpServletResponse.addHeader(ServerConstants.HDR_OSLC_VERSION, ServerConstants.OSLC_VERSION_V2);
            addCORSHeaders(httpServletResponse);
            return compact;
        }
        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("Requirement/{id}/smallPreview")
    @Produces({ MediaType.TEXT_HTML })
    public void getRequirementAsHtmlSmallPreview(
        @PathParam("id") final String id
        ) throws ServletException, IOException, URISyntaxException
    {
        // Start of user code getRequirementAsHtmlSmallPreview_init
        // End of user code

        final Requirement aRequirement = delegate.getRequirement(httpServletRequest, id);

        if (aRequirement != null) {
            httpServletRequest.setAttribute("aRequirement", aRequirement);
            // Start of user code getRequirementAsHtmlSmallPreview_setAttributes
            // End of user code

            RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cz/vutbr/fit/danielpindur/oslc/jira/requirementsmallpreview.jsp");
            httpServletResponse.addHeader(ServerConstants.HDR_OSLC_VERSION, ServerConstants.OSLC_VERSION_V2);
            addCORSHeaders(httpServletResponse);
            rd.forward(httpServletRequest, httpServletResponse);
            return;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }

    @GET
    @Path("Requirement/{id}/largePreview")
    @Produces({ MediaType.TEXT_HTML })
    public void getRequirementAsHtmlLargePreview(
        @PathParam("id") final String id
        ) throws ServletException, IOException, URISyntaxException
    {
        // Start of user code getRequirementAsHtmlLargePreview_init
        // End of user code

        final Requirement aRequirement = delegate.getRequirement(httpServletRequest, id);

        if (aRequirement != null) {
            httpServletRequest.setAttribute("aRequirement", aRequirement);
            // Start of user code getRequirementAsHtmlLargePreview_setAttributes
            // End of user code

            RequestDispatcher rd = httpServletRequest.getRequestDispatcher("/cz/vutbr/fit/danielpindur/oslc/jira/requirementlargepreview.jsp");
            httpServletResponse.addHeader(ServerConstants.HDR_OSLC_VERSION, ServerConstants.OSLC_VERSION_V2);
            addCORSHeaders(httpServletResponse);
            rd.forward(httpServletRequest, httpServletResponse);
            return;
        }

        throw new WebApplicationException(Status.NOT_FOUND);
    }
    @DELETE
    @Path("Requirement/{id}")
    @Operation(
        summary = "DELETE for resources of type {'" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "'}",
        description = "DELETE for resources of type {'" + "<a href=\"" + Oslc_rmDomainConstants.REQUIREMENT_TYPE + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}" +
            ", with respective resource shapes {'" + "<a href=\"" + "../services/" + OslcConstants.PATH_RESOURCE_SHAPES + "/" + Oslc_rmDomainConstants.REQUIREMENT_PATH + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}",
        responses = {@ApiResponse(description = "default response",
            content = {@Content(mediaType = OslcMediaType.APPLICATION_RDF_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_JSON), @Content(
                mediaType = OslcMediaType.TEXT_TURTLE), @Content(
                mediaType = MediaType.TEXT_HTML), @Content(
                mediaType = OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML)})
        }
    )
    public Response deleteRequirement(
                @PathParam("id") final String id
        ) throws IOException, ServletException, URISyntaxException
    {
        // Start of user code deleteRequirement_init
        // End of user code
        return errorHandler.Execute("Delete Requirement", () -> {
            final Requirement aResource = delegate.getRequirement(httpServletRequest, id);

            if (aResource != null) {
                // Start of user code deleteRequirement
                // End of user code
                boolean deleted = delegate.deleteRequirement(httpServletRequest, id);
                if (deleted)
                    return Response.ok().header(ServerConstants.HDR_OSLC_VERSION, ServerConstants.OSLC_VERSION_V2).build();
                else
                    throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
            }
            throw new WebApplicationException(Status.NOT_FOUND);
        });
    }

    @PUT
    @Path("Requirement/{id}")
    @Consumes({OslcMediaType.APPLICATION_RDF_XML, OslcMediaType.APPLICATION_JSON_LD, OslcMediaType.TEXT_TURTLE, OslcMediaType.APPLICATION_XML, OslcMediaType.APPLICATION_JSON })
    @Operation(
        summary = "PUT for resources of type {'" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "'}",
        description = "PUT for resources of type {'" + "<a href=\"" + Oslc_rmDomainConstants.REQUIREMENT_TYPE + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}" +
            ", with respective resource shapes {'" + "<a href=\"" + "../services/" + OslcConstants.PATH_RESOURCE_SHAPES + "/" + Oslc_rmDomainConstants.REQUIREMENT_PATH + "\">" + Oslc_rmDomainConstants.REQUIREMENT_LOCALNAME + "</a>" + "'}",
        responses = {@ApiResponse(description = "default response",
            content = {@Content(mediaType = OslcMediaType.APPLICATION_RDF_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_XML), @Content(
                mediaType = OslcMediaType.APPLICATION_JSON), @Content(
                mediaType = OslcMediaType.TEXT_TURTLE), @Content(
                mediaType = MediaType.TEXT_HTML), @Content(
                mediaType = OslcMediaType.APPLICATION_X_OSLC_COMPACT_XML)})
        }
    )
    public Response updateRequirement(
            @HeaderParam("If-Match") final String eTagHeader,
            @PathParam("id") final String id ,
            final Requirement aResource
        ) throws IOException, ServletException
    {
        // Start of user code updateRequirement_init
        // End of user code
        return errorHandler.Execute("Update Requirement", () -> {
            final Requirement originalResource = delegate.getRequirement(httpServletRequest, id);

            if (originalResource != null) {
                final String originalETag = delegate.getETagFromRequirement(originalResource);

                if ((eTagHeader == null) || (originalETag.equals(eTagHeader))) {
                    // Start of user code updateRequirement
                    // End of user code
                    final Requirement updatedResource = delegate.updateRequirement(httpServletRequest, aResource, id);
                    httpServletResponse.setHeader("ETag", delegate.getETagFromRequirement(updatedResource));
                    return Response.ok().header(ServerConstants.HDR_OSLC_VERSION, ServerConstants.OSLC_VERSION_V2).build();
                }
                else {
                    throw new WebApplicationException(Status.PRECONDITION_FAILED);
                }
            }
            else {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        });
    }

}

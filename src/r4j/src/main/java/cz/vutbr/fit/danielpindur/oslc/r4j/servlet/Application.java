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

package cz.vutbr.fit.danielpindur.oslc.r4j.servlet;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.AllowedValues;
import org.eclipse.lyo.oslc4j.core.model.Compact;
import org.eclipse.lyo.oslc4j.core.model.CreationFactory;
import org.eclipse.lyo.oslc4j.core.model.Dialog;
import org.eclipse.lyo.oslc4j.core.model.Error;
import org.eclipse.lyo.oslc4j.core.model.ExtendedError;
import org.eclipse.lyo.oslc4j.core.model.OAuthConfiguration;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;
import org.eclipse.lyo.oslc4j.core.model.PrefixDefinition;
import org.eclipse.lyo.oslc4j.core.model.Preview;
import org.eclipse.lyo.oslc4j.core.model.Property;
import org.eclipse.lyo.oslc4j.core.model.Publisher;
import org.eclipse.lyo.oslc4j.core.model.QueryCapability;
import org.eclipse.lyo.oslc4j.core.model.ResourceShape;
import org.eclipse.lyo.oslc4j.core.model.ResourceShapeFactory;
import org.eclipse.lyo.oslc4j.core.model.Service;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.model.ServiceProviderCatalog;
import org.eclipse.lyo.oslc4j.provider.jena.JenaProvidersRegistry;
import org.eclipse.lyo.oslc4j.provider.json4j.Json4JProvidersRegistry;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import cz.vutbr.fit.danielpindur.oslc.r4j.services.RootServicesService;
import cz.vutbr.fit.danielpindur.oslc.r4j.services.ServiceProviderCatalogService;
import cz.vutbr.fit.danielpindur.oslc.r4j.services.ServiceProviderService;
import cz.vutbr.fit.danielpindur.oslc.r4j.services.ResourceShapeService;

import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Folder;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Person;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.RequirementCollection;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.DctermsDomainConstants;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.FoafDomainConstants;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.JiraDomainConstants;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Jira_r4jDomainConstants;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.OslcDomainConstants;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Oslc_rmDomainConstants;
import cz.vutbr.fit.danielpindur.oslc.r4j.services.ServiceProviderService1;
import cz.vutbr.fit.danielpindur.oslc.r4j.services.FolderService;

// Start of user code imports
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import java.io.FileNotFoundException;
// End of user code

// Start of user code pre_class_code
// End of user code

/**
 * Generated by Lyo Designer 5.0.0.Final
 */

@OpenAPIDefinition(info = @Info(title = "R4J", version = "1.0.0"), servers = @Server(url = "/r4j/services/"))
public class Application extends javax.ws.rs.core.Application {

    private static final Set<Class<?>>         RESOURCE_CLASSES                          = new HashSet<Class<?>>();
    private static final Map<String, Class<?>> RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP = new HashMap<String, Class<?>>();

    // Start of user code class_attributes
    // End of user code

    // Start of user code class_methods
    // End of user code

    @Override
    public Set<Object> getSingletons() {
        return Collections.singleton(new ApplicationBinder());
    }
    static
    {
        RESOURCE_CLASSES.addAll(JenaProvidersRegistry.getProviders());
        RESOURCE_CLASSES.addAll(Json4JProvidersRegistry.getProviders());
        RESOURCE_CLASSES.add(ServiceProviderService1.class);
        RESOURCE_CLASSES.add(FolderService.class);

        // Catalog resources
        RESOURCE_CLASSES.add(ServiceProviderCatalogService.class);
        RESOURCE_CLASSES.add(ServiceProviderService.class);
        RESOURCE_CLASSES.add(ResourceShapeService.class);

        // Swagger resources
        RESOURCE_CLASSES.add(OpenApiResource.class);
        RESOURCE_CLASSES.add(AcceptHeaderOpenApiResource.class);

        // OAuth resources
        RESOURCE_CLASSES.add(RootServicesService.class);

        // Start of user code Custom Resource Classes
        try {
            ConfigurationProvider.Initialize();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // End of user code

        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_ALLOWED_VALUES,           AllowedValues.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_COMPACT,                  Compact.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_CREATION_FACTORY,         CreationFactory.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_DIALOG,                   Dialog.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_ERROR,                    Error.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_EXTENDED_ERROR,           ExtendedError.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_OAUTH_CONFIGURATION,      OAuthConfiguration.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PREFIX_DEFINITION,        PrefixDefinition.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PREVIEW,                  Preview.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PROPERTY,                 Property.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_PUBLISHER,                Publisher.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_QUERY_CAPABILITY,         QueryCapability.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_RESOURCE_SHAPE,           ResourceShape.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_SERVICE,                  Service.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_SERVICE_PROVIDER,         ServiceProvider.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(OslcConstants.PATH_SERVICE_PROVIDER_CATALOG, ServiceProviderCatalog.class);

        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(Jira_r4jDomainConstants.FOLDER_PATH, Folder.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(FoafDomainConstants.PERSON_PATH, Person.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(JiraDomainConstants.PROJECT_PATH, Project.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(Oslc_rmDomainConstants.REQUIREMENT_PATH, Requirement.class);
        RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.put(Oslc_rmDomainConstants.REQUIREMENTCOLLECTION_PATH, RequirementCollection.class);
    }

    @Inject
    public Application(ServiceLocator locator)
           throws OslcCoreApplicationException,
                  URISyntaxException
    {
        ServiceLocatorUtilities.enableImmediateScope(locator);
        final String BASE_URI = "http://localhost/validatingResourceShapes";
        for (final Map.Entry<String, Class<?>> entry : RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP.entrySet()) {
            ResourceShapeFactory.createResourceShape(BASE_URI, OslcConstants.PATH_RESOURCE_SHAPES, entry.getKey(), entry.getValue());
        }
    }

    @Override 
    public Set<Class<?>> getClasses() { 
        return RESOURCE_CLASSES; 
    }

    public static Map<String, Class<?>> getResourceShapePathToResourceClassMap() {
        return RESOURCE_SHAPE_PATH_TO_RESOURCE_CLASS_MAP;
    }
}

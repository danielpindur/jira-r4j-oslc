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


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContextEvent;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import cz.vutbr.fit.danielpindur.oslc.jira.servlet.ServiceProviderCatalogSingleton;
import cz.vutbr.fit.danielpindur.oslc.jira.ServiceProviderInfo;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Person;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.RequirementCollection;



// Start of user code imports
import cz.vutbr.fit.danielpindur.oslc.jira.facades.PersonFacade;
import cz.vutbr.fit.danielpindur.oslc.jira.facades.ProjectFacade;
import cz.vutbr.fit.danielpindur.oslc.jira.facades.RequirementFacade;
import cz.vutbr.fit.danielpindur.oslc.jira.facades.RequirementCollectionFacade;
// End of user code

// Start of user code pre_class_code
// End of user code

public class RestDelegate {

    private static final Logger log = LoggerFactory.getLogger(RestDelegate.class);

    
    
    @Inject ResourcesFactory resourcesFactory;
    // Start of user code class_attributes
    @Inject PersonFacade personFacade;
    @Inject ProjectFacade projectFacade;
    @Inject RequirementFacade requirementFacade;
    @Inject RequirementCollectionFacade requirementCollectionFacade;
    // End of user code
    
    public RestDelegate() {
        log.trace("Delegate is initialized");
    }
    
    
    // Start of user code class_methods
    // End of user code

    //The methods contextInitializeServletListener() and contextDestroyServletListener() no longer exits
    //Migrate any user-specific code blocks to the class cz.vutbr.fit.danielpindur.oslc.jira.servlet.ServletListener
    //Any user-specific code should be found in *.lost files.

    public static ServiceProviderInfo[] getServiceProviderInfos(HttpServletRequest httpServletRequest)
    {
        ServiceProviderInfo[] serviceProviderInfos = {};
        
        // Start of user code "ServiceProviderInfo[] getServiceProviderInfos(...)"

        ServiceProviderInfo r1 = new ServiceProviderInfo();
        r1.name = "JIRA Provider";
        r1.serviceProviderId = "1"; // TODO: Is this id okay?

        serviceProviderInfos = new ServiceProviderInfo[1];
        serviceProviderInfos[0] = r1;

        // End of user code
        return serviceProviderInfos;
    }

    public List<Requirement> queryRequirements(HttpServletRequest httpServletRequest, String where, String prefix, boolean paging, int page, int limit)
    {
        List<Requirement> resources = null;
        
        
        // Start of user code queryRequirements
        // TODO Implement code to return a set of resources.
        // An empty List should imply that no resources where found.
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return resources;
    }
    public List<RequirementCollection> queryRequirementCollections(HttpServletRequest httpServletRequest, String where, String prefix, boolean paging, int page, int limit)
    {
        List<RequirementCollection> resources = null;
        
        
        // Start of user code queryRequirementCollections
        // TODO Implement code to return a set of resources.
        // An empty List should imply that no resources where found.
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return resources;
    }
    public List<Requirement> RequirementSelector(HttpServletRequest httpServletRequest, String terms)
    {
        List<Requirement> resources = null;
        
        
        // Start of user code RequirementSelector
        // TODO Implement code to return a set of resources, based on search criteria 
        // An empty List should imply that no resources where found.
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return resources;
    }
    public List<RequirementCollection> RequirementCollectionSelector(HttpServletRequest httpServletRequest, String terms)
    {
        List<RequirementCollection> resources = null;
        
        
        // Start of user code RequirementCollectionSelector
        // TODO Implement code to return a set of resources, based on search criteria 
        // An empty List should imply that no resources where found.
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return resources;
    }
    public Requirement createRequirement(HttpServletRequest httpServletRequest, final Requirement aResource)
    {
        Requirement newResource = null;
        
        
        // Start of user code createRequirement
        // TODO Implement code to create a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return newResource;
    }
    public RequirementCollection createRequirementCollection(HttpServletRequest httpServletRequest, final RequirementCollection aResource)
    {
        RequirementCollection newResource = null;
        
        
        // Start of user code createRequirementCollection
        // TODO Implement code to create a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return newResource;
    }

    public Requirement createRequirementFromDialog(HttpServletRequest httpServletRequest, final Requirement aResource)
    {
        Requirement newResource = null;
        
        
        // Start of user code createRequirementFromDialog
        // TODO Implement code to create a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return newResource;
    }
    public RequirementCollection createRequirementCollectionFromDialog(HttpServletRequest httpServletRequest, final RequirementCollection aResource)
    {
        RequirementCollection newResource = null;
        
        
        // Start of user code createRequirementCollectionFromDialog
        // TODO Implement code to create a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return newResource;
    }


    public List<Person> queryPersons(HttpServletRequest httpServletRequest, String where, String prefix, boolean paging, int page, int limit)
    {
        List<Person> resources = null;
        
        
        // Start of user code queryPersons
        // TODO Implement code to return a set of resources.
        // An empty List should imply that no resources where found.
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return resources;
    }
    public List<Person> PersonSelector(HttpServletRequest httpServletRequest, String terms)
    {
        List<Person> resources = null;
        
        
        // Start of user code PersonSelector
        resources = personFacade.search(terms);
        // End of user code
        return resources;
    }



    public List<Project> queryProjects(HttpServletRequest httpServletRequest, String where, String prefix, boolean paging, int page, int limit)
    {
        List<Project> resources = null;
        
        
        // Start of user code queryProjects
        // TODO Implement code to return a set of resources.
        // An empty List should imply that no resources where found.
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return resources;
    }
    public List<Project> ProjectSelector(HttpServletRequest httpServletRequest, String terms)
    {
        List<Project> resources = null;
        
        
        // Start of user code ProjectSelector
        resources = projectFacade.search(terms);
        // End of user code
        return resources;
    }




    public Requirement getRequirement(HttpServletRequest httpServletRequest, final String id)
    {
        Requirement aResource = null;
        
        
        // Start of user code getRequirement
        aResource = requirementFacade.get(id);
        // End of user code
        return aResource;
    }

    public Boolean deleteRequirement(HttpServletRequest httpServletRequest, final String id)
    {
        Boolean deleted = false;
        
        // Start of user code deleteRequirement
        // TODO Implement code to delete a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return deleted;
    }

    public Requirement updateRequirement(HttpServletRequest httpServletRequest, final Requirement aResource, final String id) {
        Requirement updatedResource = null;
        
        // Start of user code updateRequirement
        // TODO Implement code to update and return a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return updatedResource;
    }
    public RequirementCollection getRequirementCollection(HttpServletRequest httpServletRequest, final String id)
    {
        RequirementCollection aResource = null;
        
        
        // Start of user code getRequirementCollection
        aResource = requirementCollectionFacade.get(id);
        // End of user code
        return aResource;
    }

    public Boolean deleteRequirementCollection(HttpServletRequest httpServletRequest, final String id)
    {
        Boolean deleted = false;
        
        // Start of user code deleteRequirementCollection
        // TODO Implement code to delete a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return deleted;
    }

    public RequirementCollection updateRequirementCollection(HttpServletRequest httpServletRequest, final RequirementCollection aResource, final String id) {
        RequirementCollection updatedResource = null;
        
        // Start of user code updateRequirementCollection
        // TODO Implement code to update and return a resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return updatedResource;
    }
    public Person getPerson(HttpServletRequest httpServletRequest, final String id)
    {
        Person aResource = null;
        
        
        // Start of user code getPerson
        aResource = personFacade.get(id);
        // End of user code
        return aResource;
    }


    public Project getProject(HttpServletRequest httpServletRequest, final String id)
    {
        Project aResource = null;
        
        
        // Start of user code getProject
        aResource = projectFacade.get(id);
        // End of user code
        return aResource;
    }



    public String getETagFromPerson(final Person aResource)
    {
        String eTag = null;
        // Start of user code getETagFromPerson
        // TODO Implement code to return an ETag for a particular resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return eTag;
    }
    public String getETagFromProject(final Project aResource)
    {
        String eTag = null;
        // Start of user code getETagFromProject
        // TODO Implement code to return an ETag for a particular resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return eTag;
    }
    public String getETagFromRequirement(final Requirement aResource)
    {
        String eTag = null;
        // Start of user code getETagFromRequirement
        // TODO Implement code to return an ETag for a particular resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return eTag;
    }
    public String getETagFromRequirementCollection(final RequirementCollection aResource)
    {
        String eTag = null;
        // Start of user code getETagFromRequirementCollection
        // TODO Implement code to return an ETag for a particular resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return eTag;
    }

}

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


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContextEvent;
import java.util.List;
import java.util.ArrayList;

import cz.vutbr.fit.danielpindur.oslc.shared.session.SessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.eclipse.lyo.oslc4j.core.OSLC4JUtils;
import org.eclipse.lyo.oslc4j.core.model.AbstractResource;
import cz.vutbr.fit.danielpindur.oslc.r4j.servlet.ServiceProviderCatalogSingleton;
import cz.vutbr.fit.danielpindur.oslc.r4j.ServiceProviderInfo;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Folder;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Person;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.RequirementCollection;



// Start of user code imports
import cz.vutbr.fit.danielpindur.oslc.r4j.facades.FolderFacade;
// End of user code

// Start of user code pre_class_code
// End of user code

public class RestDelegate {

    private static final Logger log = LoggerFactory.getLogger(RestDelegate.class);

    
    
    @Inject ResourcesFactory resourcesFactory;
    // Start of user code class_attributes
    @Inject FolderFacade folderFacade;
    // End of user code
    
    public RestDelegate() {
        log.trace("Delegate is initialized");
    }
    
    
    // Start of user code class_methods
    // End of user code

    //The methods contextInitializeServletListener() and contextDestroyServletListener() no longer exits
    //Migrate any user-specific code blocks to the class cz.vutbr.fit.danielpindur.oslc.r4j.servlet.ServletListener
    //Any user-specific code should be found in *.lost files.

    public static ServiceProviderInfo[] getServiceProviderInfos(HttpServletRequest httpServletRequest)
    {
        ServiceProviderInfo[] serviceProviderInfos = {};
        
        // Start of user code "ServiceProviderInfo[] getServiceProviderInfos(...)"
        ServiceProviderInfo r1 = new ServiceProviderInfo();
        r1.name = "R4J Provider";
        r1.serviceProviderId = "1";

        serviceProviderInfos = new ServiceProviderInfo[1];
        serviceProviderInfos[0] = r1;
        // End of user code
        return serviceProviderInfos;
    }

    public List<Folder> queryFolders(HttpServletRequest httpServletRequest, String where, final String terms, String prefix, boolean paging, int page, int limit)
    {
        List<Folder> resources = null;
        
        
        // Start of user code queryFolders
        try {
            SessionProvider.SetSession(httpServletRequest.getSession());
            resources = folderFacade.queryFolder(where, terms, prefix, paging, page, limit);
        } finally {
            SessionProvider.ClearSession();
        }
        // End of user code
        return resources;
    }
    public List<Folder> FolderSelector(HttpServletRequest httpServletRequest, String terms)
    {
        List<Folder> resources = null;
        
        
        // Start of user code FolderSelector
        try {
            SessionProvider.SetSession(httpServletRequest.getSession());
            resources = folderFacade.selectFolders(terms);
        } finally {
            SessionProvider.ClearSession();
        }
        // End of user code
        return resources;
    }
    public Folder createFolder(HttpServletRequest httpServletRequest, final Folder aResource)
    {
        Folder newResource = null;
        
        
        // Start of user code createFolder
        try {
            SessionProvider.SetSession(httpServletRequest.getSession());
            newResource = folderFacade.create(aResource);
        } finally {
            SessionProvider.ClearSession();
        }
        // End of user code
        return newResource;
    }

    public Folder createFolderFromDialog(HttpServletRequest httpServletRequest, final Folder aResource)
    {
        Folder newResource = null;
        
        
        // Start of user code createFolderFromDialog
        try {
            SessionProvider.SetSession(httpServletRequest.getSession());
            newResource = folderFacade.create(aResource);
        } finally {
            SessionProvider.ClearSession();
        }
        // End of user code
        return newResource;
    }



    public Folder getFolder(HttpServletRequest httpServletRequest, final String id)
    {
        Folder aResource = null;
        
        
        // Start of user code getFolder
        try {
            SessionProvider.SetSession(httpServletRequest.getSession());
            aResource = folderFacade.get(id);
        } finally {
            SessionProvider.ClearSession();
        }
        // End of user code
        return aResource;
    }

    public Boolean deleteFolder(HttpServletRequest httpServletRequest, final String id)
    {
        Boolean deleted = false;
        
        // Start of user code deleteFolder
        try {
            SessionProvider.SetSession(httpServletRequest.getSession());
            deleted = folderFacade.delete(id);
        } finally {
            SessionProvider.ClearSession();
        }
        // End of user code
        return deleted;
    }

    public Folder updateFolder(HttpServletRequest httpServletRequest, final Folder aResource, final String id) {
        Folder updatedResource = null;
        
        // Start of user code updateFolder
        try {
            SessionProvider.SetSession(httpServletRequest.getSession());
            updatedResource = folderFacade.updateFolder(id, aResource);
        } finally {
            SessionProvider.ClearSession();
        }
        // End of user code
        return updatedResource;
    }

    public String getETagFromFolder(final Folder aResource)
    {
        String eTag = null;
        // Start of user code getETagFromFolder
        // TODO Implement code to return an ETag for a particular resource
        // If you encounter problems, consider throwing the runtime exception WebApplicationException(message, cause, final httpStatus)
        // End of user code
        return eTag;
    }

}

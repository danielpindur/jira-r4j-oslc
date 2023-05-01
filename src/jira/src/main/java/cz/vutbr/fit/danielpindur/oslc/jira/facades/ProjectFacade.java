/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Facade for Project resource
 */
public class ProjectFacade extends BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    // TODO: validate returned HTTP codes - align with OSLC standard

    /**
     * Maps API project resource to OSLC project resource
     * 
     * @param resource API project resource
     * 
     * @return OSLC project resource
     */
    private Project MapResourceToResult(final com.atlassian.jira.rest.client.api.domain.Project resource) {
        var result = new Project();
        var projectIdString = Objects.requireNonNull(resource.getId()).toString();

        result.setShortTitle(resource.getKey());
        result.setDescription(resource.getDescription());
        result.setTitle(resource.getName());
        result.setIdentifier(projectIdString);
        result.setAbout(resourcesFactory.constructURIForProject(projectIdString));

        return result;
    }

    /**
     * Gets project by ID
     * 
     * @param id project ID
     * 
     * @return Project resource
     */
    public Project get(final String id) {
        com.atlassian.jira.rest.client.api.domain.Project projectResource = null;

        try {
            projectResource = getProjectClient().getProject(id).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        return projectResource != null ? MapResourceToResult(projectResource) : null;
    }

    /** 
     * Gets project by ID
     * 
     * @param id project ID
     * 
     * @return Project resource
     */
    public Project get(final Long id) {
        var idString = Objects.requireNonNull(id).toString();
        return get(idString);
    }

    /**
     * Validate if project should be returned from search by terms
     * 
     * @param project project to be validated
     * @param terms terms to be searched
     * 
     * @return true if project should be returned from search, false otherwise
     */
    private boolean shouldBeReturnedFromSearch(final BasicProject project, final String terms) {
        return  containsTerms(project.getName(), terms) || containsTerms(project.getKey(), terms);
    }

    /**
     * Searches for projects by terms
     * 
     * @param terms terms to be searched
     * 
     * @return list of projects
     */
    public List<Project> search(final String terms) {
        var projectResources = getProjectClient().getAllProjects().claim();
        var results = new LinkedList<Project>();

        for (BasicProject projectResource : projectResources) {
            if (shouldBeReturnedFromSearch(projectResource, terms)) {
                var result = get(projectResource.getId());
                results.add(result);
            }
        }

        return results;
    }
}

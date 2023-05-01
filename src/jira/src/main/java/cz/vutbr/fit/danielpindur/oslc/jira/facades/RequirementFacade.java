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

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Facade for Requirement resource
 */
public class RequirementFacade extends IssueFacade {
    /**
     * Maps API issue resource to OSLC Requirement resource
     * 
     * @param resource API issue resource
     * 
     * @return OSLC Requirement resource
     */
    private Requirement MapResourceToResult(final Issue resource) {
        var result = new Requirement();
        var jiraIssueId = resource.getId().intValue();
        var projectIdString = SafeConvert(resource.getProject().getId());

        var identifier = getIssueClient().getIssueGUID(resource, true);

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey());
        result.setJiraId(jiraIssueId);
        result.setIdentifier(identifier);
        result.setAbout(resourcesFactory.constructURIForRequirement(identifier));
        result.setSubject(getIssueClient().getFieldStringSetValueWithoutIdentifier(configuration.LabelsFieldName, resource));
        result.setCreated(resource.getCreationDate().toDateTimeISO().toDate());
        result.setModified(resource.getCreationDate().toDateTimeISO().toDate());
        result.setProject(resourcesFactory.constructLinkForProject(projectIdString));
        result.setCreator(
                new HashSet<Link>(){{
                    add(GetCreatorLink(resource));
                }}
        );

        result.setDecomposedBy(GetDecomposedBy(resource));
        result.setDecomposes(GetDecomposes(resource));
        result.setContributor(GetContributorsLinks(resource));

        return result;
    }

    /**
     * Creates new Requirement resource
     * 
     * @param requirement Requirement resource to create
     * 
     * @return Created Requirement resource
     */
    public Requirement create(final Requirement requirement) {
        var identifier = requirement.getIdentifier() != null
                        ? requirement.getIdentifier()
                        : IssueHelper.CreateIssueGUID();

        var projectUri = requirement.getProject().getValue();
        ValidateLinks(requirement.getDecomposedBy());
        ValidateLinks(requirement.getDecomposes());

        createIssue(requirement.getDescription(),
                configuration.RequirementIssueTypeName,
                UriHelper.GetIdFromUri(projectUri),
                requirement.getTitle(),
                identifier,
                requirement.getSubject());

        CreateDecomposedByLinks(requirement.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirement.getDecomposes(), identifier);

        return get(identifier);
    }

    /**
     * Gets Requirement resource by identifier
     * 
     * @param id Requirement identifier
     * 
     * @return Requirement resource
     */
    public Requirement get(final String id) {
        var issue = getIssueByIdentifier(id);

        if (!IssueHelper.IsRequirement(issue)) {
            return null;
        }

        return MapResourceToResult(issue);
    }

    /**
     * Deletes Requirement resource by identifier
     * 
     * @param id Requirement identifier
     * 
     * @return True if deleted, false otherwise
     */
    public boolean delete(final String id) {
        return deleteIssue(id);
    }

    /**
     * Updates Requirement resource
     * 
     * @param requirement Requirement resource to update
     * @param identifier Requirement identifier
     * 
     * @return Updated Requirement resource
     */
    public Requirement update(final Requirement requirement, final String identifier) {
        ValidateLinks(requirement.getDecomposedBy());
        ValidateLinks(requirement.getDecomposes());

        updateIssue(identifier, requirement.getDescription(), requirement.getTitle(), requirement.getSubject());

        RemoveAdaptorIssueLinks(identifier);
        CreateDecomposedByLinks(requirement.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirement.getDecomposes(), identifier);

        return get(identifier);
    }

    /**
     * Maps API issue resources to OSLC Requirement resources
     * 
     * @param issues API issue resources
     * 
     * @return OSLC Requirement resources
     */
    private List<Requirement> mapIssuesToRequirements(final Iterable<Issue> issues) {
        var requirements = new LinkedList<Requirement>();

        if (issues == null) {
            return requirements;
        }

        for (Issue issue : issues) {
            var requirement = MapResourceToResult(issue);
            requirements.add(requirement);
        }

        return requirements;
    }

    /**
     * Selects Requirement resources by terms
     * 
     * @param terms Terms to search
     * 
     * @return Requirement resources
     */
    public List<Requirement> selectRequirements(final String terms) {
        var issues = selectIssues(terms, configuration.RequirementIssueTypeName);
        return mapIssuesToRequirements(issues);
    }

    /**
     * Queries Requirement resources
     * 
     * @param where Where clause
     * @param terms Terms for full-text search.
     * @param prefix Prefix of the used oslc properties.
     * @param paging Enable paging.
     * @param page Selected page.
     * @param limit Amount of issues per page.
     * 
     * @return Requirement resources
     */
    public List<Requirement> queryRequirements(final String where, final String terms, final String prefix, final boolean paging, final int page, final int limit) {
        var issues = queryIssues(configuration.RequirementIssueTypeName, where, terms, prefix, paging, page, limit);
        return mapIssuesToRequirements(issues);
    }
}

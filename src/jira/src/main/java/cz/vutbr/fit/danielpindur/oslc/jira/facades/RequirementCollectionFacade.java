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
import cz.vutbr.fit.danielpindur.oslc.jira.resources.RequirementCollection;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Facade for RequirementCollection resource
 */
public class RequirementCollectionFacade extends IssueFacade {
    /**
     * Maps API issue resource to OSLC RequirementCollection resource
     * 
     * @param resource API issue resource
     * 
     * @return OSLC RequirementCollection resource
     */
    private RequirementCollection MapResourceToResult(final Issue resource) {
        var result = new RequirementCollection();
        var jiraIssueId = resource.getId().intValue();
        var projectIdString = SafeConvert(resource.getProject().getId());

        var identifier = getIssueClient().getIssueGUID(resource, true);

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey());
        result.setJiraId(jiraIssueId);
        result.setIdentifier(identifier);
        result.setAbout(resourcesFactory.constructURIForRequirementCollection(identifier));
        result.setCreated(resource.getCreationDate().toDateTimeISO().toDate());
        result.setSubject(getIssueClient().getFieldStringSetValueWithoutIdentifier(configuration.LabelsFieldName, resource));
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
     * Gets RequirementCollection resource by identifier
     * 
     * @param id Identifier of RequirementCollection resource
     * 
     * @return RequirementCollection resource
     */
    public RequirementCollection get(final String id) {
        var issue = getIssueByIdentifier(id);

        if (!IssueHelper.IsRequirementCollection(issue)) {
            return null;
        }

        return MapResourceToResult(issue);
    }

    /**
     * Creates RequirementCollection resource
     * 
     * @param requirementCollection RequirementCollection resource to create
     * 
     * @return Created RequirementCollection resource
     */
    public RequirementCollection create(final RequirementCollection requirementCollection) {
        var identifier = requirementCollection.getIdentifier() != null
                ? requirementCollection.getIdentifier()
                : IssueHelper.CreateIssueGUID();

        var projectUri = requirementCollection.getProject().getValue();

        ValidateLinks(requirementCollection.getDecomposedBy());
        ValidateLinks(requirementCollection.getDecomposes());

        createIssue(requirementCollection.getDescription(),
                configuration.RequirementCollectionIssueTypeName,
                UriHelper.GetIdFromUri(projectUri),
                requirementCollection.getTitle(),
                identifier,
                requirementCollection.getSubject());

        CreateDecomposedByLinks(requirementCollection.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirementCollection.getDecomposes(), identifier);

        return get(identifier);
    }

    /**
     * Deletes RequirementCollection resource
     * 
     * @param id Identifier of RequirementCollection resource
     * 
     * @return True if deleted, false otherwise
     */
    public boolean delete(final String id) {
        return deleteIssue(id);
    }

    /**
     * Updates RequirementCollection resource
     * 
     * @param requirementCollection RequirementCollection resource to update
     * @param identifier Identifier of RequirementCollection resource
     * 
     * @return Updated RequirementCollection resource
     */
    public RequirementCollection update(final RequirementCollection requirementCollection, final String identifier) {
        ValidateLinks(requirementCollection.getDecomposedBy());
        ValidateLinks(requirementCollection.getDecomposes());

        updateIssue(identifier, requirementCollection.getDescription(), requirementCollection.getTitle(), requirementCollection.getSubject());

        RemoveAdaptorIssueLinks(identifier);
        CreateDecomposedByLinks(requirementCollection.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirementCollection.getDecomposes(), identifier);

        return get(identifier);
    }

    /**
     * Maps API issues to OSLC RequirementCollection resources
     */
    private List<RequirementCollection> mapIssuesToRequirementCollections(Iterable<Issue> issues) {
        var requirementCollections = new LinkedList<RequirementCollection>();

        if (issues == null) {
            return requirementCollections;
        }

        for (Issue issue : issues) {
            var requirementCollection = MapResourceToResult(issue);
            requirementCollections.add(requirementCollection);
        }

        return requirementCollections;
    }

    /**
     * Selects RequirementCollection resources by terms
     * 
     * @param terms Terms to search for
     * 
     * @return List of RequirementCollection resources
     */
    public List<RequirementCollection> selectRequirementCollections(final String terms) {
        var issues = selectIssues(terms, configuration.RequirementCollectionIssueTypeName);
        return mapIssuesToRequirementCollections(issues);
    }

    /**
     * Queries RequirementCollection resources
     * 
     * @param where Where clause
     * @param terms Terms for full-text search.
     * @param prefix Prefix of the used oslc properties.
     * @param paging Enable paging.
     * @param page Selected page.
     * @param limit Amount of issues per page.
     * 
     * @return List of RequirementCollection resources
     */
    public List<RequirementCollection> queryRequirementCollections(final String where, final String terms, final String prefix, final boolean paging, final int page, final int limit) {
        var issues = queryIssues(configuration.RequirementCollectionIssueTypeName, where, terms, prefix, paging, page, limit);
        return mapIssuesToRequirementCollections(issues);
    }
}

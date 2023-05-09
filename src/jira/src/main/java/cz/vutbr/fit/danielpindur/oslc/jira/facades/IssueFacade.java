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

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.api.RestClientException;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.jira.translators.IssueTranslator;
import cz.vutbr.fit.danielpindur.oslc.shared.builders.JiraQueryBuilder;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;
import org.eclipse.lyo.core.query.QueryUtils;
import org.eclipse.lyo.core.query.SimpleTerm;
import org.eclipse.lyo.core.query.WhereClause;
import org.eclipse.lyo.oslc4j.core.model.Link;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Issue facade.
 */
public class IssueFacade extends BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    // TODO: verify all endpoints responds correctly with 401
    // TODO: validate return codes

    /**
     * Get issue by identifier.
     * 
     * @param identifier Issue identifier
     * 
     * @return Issue
     */
    protected Issue getIssueByIdentifier(final String identifier) {
        var issue = getIssueClient().searchIssueByIdentifier(identifier);

        // Get issue with expandos
        return issue != null ? getIssue(issue.getId()) : null;
    }

    /**
     * Get issue contributors from Issue.
     * 
     * @param issue Issue
     * 
     * @return Set of contributors
     */
    private Set<String> GetContributors(final Issue issue) {
        var contributorUsernames = new HashSet<String>();
        var changelog = issue.getChangelog();

        if (changelog == null) {
            return contributorUsernames;
        }

        for (var change : changelog) {
            var user = change.getAuthor();
            contributorUsernames.add(user.getName());
        }

        return contributorUsernames;
    }

    /**
     * Get creator link from Issue.
     * 
     * @param issue Issue
     * 
     * @return Creator link
     */
    protected Link GetCreatorLink(final Issue issue) {
        var creator = issue.getReporter();
        if (creator == null) {
            throw new WebApplicationException("Creator for issue with identifier (" + issue.getKey() + ") not found!", Response.Status.CONFLICT);
        }

        return resourcesFactory.constructLinkForPerson(creator.getName());
    }

    /**
     * Get contributors links from Issue.
     * 
     * @param issue Issue
     * 
     * @return Set of contributors links
     */
    protected Set<Link> GetContributorsLinks(final Issue issue) {
        var contributors = GetContributors(issue);
        var contributorsLinks = new HashSet<Link>();

        for (var contributor : contributors) {
            contributorsLinks.add(resourcesFactory.constructLinkForPerson(contributor));
        }

        contributorsLinks.add(GetCreatorLink(issue));

        return contributorsLinks;
    }

    /**
     * Get issue by id
     * 
     * @param id Issue id
     * 
     * @return Issue
     */
    protected Issue getIssue(final String id) {
        var expandos = new LinkedList<IssueRestClient.Expandos>();
        expandos.add(IssueRestClient.Expandos.CHANGELOG);
        return getIssueClient().getIssue(id, expandos).claim();
    }

    /**
     * Get issue by id
     * 
     * @param id Issue id
     * 
     * @return Issue
     */
    protected Issue getIssue(final Long id) {
        var stringId = Objects.requireNonNull(id).toString();
        return getIssue(stringId);
    }

    /**
     * Validate if link is decomposedBy link.
     * 
     * @param link Link
     * 
     * @return True if link is decomposedBy link, false otherwise
     */
    private boolean IsDecomposedByLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase(configuration.IssueLinkTypeName)
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.INBOUND);
    }

    /**
     * Validate if link is decomposes link.
     * 
     * @param link Link
     * 
     * @return True if link is decomposes link, false otherwise
     */
    private boolean IsDecomposesLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase(configuration.IssueLinkTypeName)
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.OUTBOUND);
    }

    /**
     * Construct link for decomposes link.
     * 
     * @param id Target issue id
     * 
     * @return Link
     */
    private Link ConstructLinkForDecomposeLink(final String id) {
        var issue = getIssue(id);

        if (IssueHelper.IsRequirement(issue)) {
            return resourcesFactory.constructLinkForRequirement(getIssueClient().getIssueGUID(issue, true));
        }
        else if (IssueHelper.IsRequirementCollection(issue)) {
            return resourcesFactory.constructLinkForRequirementCollection(getIssueClient().getIssueGUID(issue, true));
        }

        // Issue is not Requirement or Requirement Collection, omit from link
        return null;
    }

    /**
     * Get decomposedBy links from Issue.
     * 
     * @param resource Issue
     * 
     * @return Set of decomposedBy links
     */
    protected Set<Link> GetDecomposedBy(final Issue resource) {
        var issueLinks = resource.getIssueLinks();
        var decomposedBy = new HashSet<Link>();

        if (issueLinks != null) {
            for (IssueLink link : issueLinks) {
                if (IsDecomposedByLink(link)) {
                    var constructedLink = ConstructLinkForDecomposeLink(link.getTargetIssueKey());
                    if (constructedLink != null) {
                        decomposedBy.add(constructedLink);
                    }
                }
            }
        }

        return decomposedBy;
    }

    /**
     * Get decomposes links from Issue.
     * 
     * @param resource Issue
     * 
     * @return Set of decomposes links
     */
    protected Set<Link> GetDecomposes(final Issue resource) {
        var issueLinks = resource.getIssueLinks();
        var decomposes = new HashSet<Link>();

        if (issueLinks != null) {
            for (IssueLink link : issueLinks) {
                if (IsDecomposesLink(link)) {
                    var constructedLink = ConstructLinkForDecomposeLink(link.getTargetIssueKey());
                    if (constructedLink != null) {
                        decomposes.add(constructedLink);
                    }
                }
            }
        }

        return decomposes;
    }

    /**
     * Validate if links target issue exists.
     * 
     * @param links Links
     */
    protected void ValidateLinks(final Set<Link> links) {
        if (links == null) return;

        for (Link link : links) {
            var identifier = UriHelper.GetIdFromUri(link.getValue());
            var issue = getIssueByIdentifier(identifier);
            if (issue == null) {
                throw new WebApplicationException("Issue with identifier " + identifier + ", not found!", Response.Status.BAD_REQUEST);
            }
        }
    }

    /**
     * Create link between two issues.
     * 
     * @param identifierFrom Identifier of issue from
     * @param identifierTo Identifier of issue to
     * @param linkTypeName Link type name
     */
    private void CreateLink(final String identifierFrom, final String identifierTo, final String linkTypeName) {
        var issueFrom = getIssueByIdentifier(identifierFrom);
        if (issueFrom == null) {
            throw new WebApplicationException("Issue with identifier (" + identifierFrom + ") not found!", Response.Status.BAD_REQUEST);
        }

        var issueTo = getIssueByIdentifier(identifierTo);
        if (issueTo == null) {
            throw new WebApplicationException("Issue with identifier (" + identifierTo + ") not found!", Response.Status.BAD_REQUEST);
        }

        ValidateIssueLinkType(linkTypeName);

        var issueLink = new LinkIssuesInput(issueFrom.getKey(), issueTo.getKey(), linkTypeName);
        getIssueClient().linkIssue(issueLink);
    }

    /**
     * Create issue.
     * 
     * @param description Issue description
     * @param issueTypeName Issue type name
     * @param projectId Project id
     * @param title Issue title
     * @param identifier Issue identifier
     * @param subject Issue subject
     * 
     * @return Created issue
     */
    protected BasicIssue createIssue(final String description, final String issueTypeName, final String projectId, final String title, final String identifier, final Set<String> subject) {
        com.atlassian.jira.rest.client.api.domain.Project project = null;
        try {
            project = getProjectClient().getProject(projectId).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        if (project == null) {
            throw new WebApplicationException("Project with " + projectId + " not found!", Response.Status.NOT_FOUND);
        }

        var issueTypes = project.getIssueTypes();
        IssueType issueType = null;

        for (IssueType type : issueTypes) {
            var typeName = type.getName();
            if (typeName != null && typeName.equalsIgnoreCase(issueTypeName)) {
                issueType = type;
                break;
            }
        }

        if (issueType == null) {
            throw new WebApplicationException("Selected project doesn't contain issueType " + issueTypeName + "!", Response.Status.BAD_REQUEST);
        }

        var issueWithSameIdentifier = getIssueByIdentifier(identifier);
        if (issueWithSameIdentifier != null) {
            throw new WebApplicationException("Issue with same identifier (" + identifier +") already exists!", Response.Status.CONFLICT);
        }

        var fields = getMetadataClient().getFields().claim();

        var labelsFieldId = getIssueClient().GetFieldId(configuration.LabelsFieldName, fields);
        var identifierFieldId = getIssueClient().GetFieldId(configuration.IdentifierFieldName, fields);

        if (labelsFieldId == null) {
            throw new WebApplicationException("Field Labels not found, failed to create issue!", Response.Status.CONFLICT);
        }

        if (identifierFieldId == null) {
            throw new WebApplicationException("Field Identifier not found, failed to create issue!", Response.Status.CONFLICT);
        }

        if (title == null || title.length() == 0) {
            throw new WebApplicationException("Title has to be specified, failed to create issue!", Response.Status.BAD_REQUEST);
        }

        var issueInputBuilder = new IssueInputBuilder(project.getKey(), issueType.getId(), title)
                .setDescription(description);

        if (configuration.SaveIdentifierInLabelsField) {
            var subjectsWithIdentifier = subject;
            subjectsWithIdentifier.add(IssueHelper.GetFormattedLabelsIdentifier(identifier));

            issueInputBuilder
                    .setFieldInput(new FieldInput(labelsFieldId, subjectsWithIdentifier));
        } else {
            issueInputBuilder
                    .setFieldInput(new FieldInput(labelsFieldId, subject))
                    .setFieldInput(new FieldInput(identifierFieldId, identifier));
        }

        var issue = issueInputBuilder.build();

        try {
            return getIssueClient().createIssue(issue).claim();
        } catch (Exception e) {
            throw new WebApplicationException("Failed to create " + issueTypeName + "!", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete issue.
     * 
     * @param identifier Issue identifier
     * 
     * @return True if issue was deleted, otherwise false
     */
    protected boolean deleteIssue(final String identifier) {
        var issue = getIssueByIdentifier(identifier);
        if (issue == null) {
            throw new WebApplicationException("Issue with identifier (" + identifier +") not found!", Response.Status.NOT_FOUND);
        }
        getIssueClient().deleteIssue(issue.getKey(), false).claim();

        var deletedIssue = getIssueByIdentifier(identifier);

        return deletedIssue == null;
    }

    /**
     * Update issue.
     * 
     * @param identifier Issue identifier
     * @param description Issue description
     * @param title Issue title
     * @param subject Issue subject
     */
    protected void updateIssue(final String identifier, final String description, final String title, final Set<String> subject) {
        var issue = getIssueByIdentifier(identifier);
        if (issue == null) {
            throw new WebApplicationException("Issue with identifier (" + identifier +") not found!", Response.Status.NOT_FOUND);
        }

        var labelsFieldId = getIssueClient().GetFieldId(configuration.LabelsFieldName);

        if (labelsFieldId == null) {
            throw new WebApplicationException("Field Labels not found, failed to create issue!", Response.Status.CONFLICT);
        }

        if (title == null || title.length() == 0) {
            throw new WebApplicationException("Title has to be specified, failed to create issue!", Response.Status.BAD_REQUEST);
        }

        var issueInputBuilder = new IssueInputBuilder(issue.getProject().getKey(), issue.getIssueType().getId(), title)
                .setDescription(description);

        if (configuration.SaveIdentifierInLabelsField) {
            var subjectsWithIdentifier = subject;
            subjectsWithIdentifier.add(IssueHelper.GetFormattedLabelsIdentifier(identifier));

            issueInputBuilder
                    .setFieldInput(new FieldInput(labelsFieldId, subjectsWithIdentifier));
        } else {
            issueInputBuilder.setFieldInput(new FieldInput(labelsFieldId, subject));
        }

        var updatedIssue = issueInputBuilder.build();

        getIssueClient().updateIssue(issue.getKey(), updatedIssue).claim();
    }

    /**
     * Remove all issue links created by adaptor from the specified issue.
     * 
     * @param identifier Issue identifier
     */
    protected void RemoveAdaptorIssueLinks(final String identifier) {
        var issue = getIssueByIdentifier(identifier);
        var issueLinkIds = getIssueLinkRestClient().getAdaptorIssueLinkIdsForIssue(issue.getKey(), configuration.IssueLinkTypeName).claim();

        for (var issueLinkId : issueLinkIds) {
            getIssueLinkRestClient().deleteLink(issueLinkId).claim();
        }
    }

    /**
     * Create decomposedBy links for the specified issue.
     * 
     * @param links Links to create
     * @param identifier Issue identifier
     */
    protected void CreateDecomposedByLinks(final Set<Link> links, final String identifier) {
        for (Link link : links) {
            CreateLink(UriHelper.GetIdFromUri(link.getValue()), identifier, configuration.IssueLinkTypeName);
        }
    }

    /**
     * Create decomposes links for the specified issue.
     * 
     * @param links Links to create
     * @param identifier Issue identifier
     */
    protected void CreateDecomposesLinks(final Set<Link> links, final String identifier) {
        for (Link link : links) {
            CreateLink(identifier, UriHelper.GetIdFromUri(link.getValue()), configuration.IssueLinkTypeName);
        }
    }

    /**
     * Get issue link type by name.
     * 
     * @param issueLinkTypeName Issue link type name
     * 
     * @return Issue link type
     */
    protected IssuelinksType GetIssueLinksTypeByName(final String issueLinkTypeName) {
        var issueLinkTypes = getMetadataClient().getIssueLinkTypes().claim();

        for (IssuelinksType issueLinkType : issueLinkTypes) {
            if (issueLinkType.getName().equalsIgnoreCase(issueLinkTypeName)) {
                return issueLinkType;
            }
        }

        return null;
    }

    /**
     * Validate if issue link type exists.
     * 
     * @param issueLinkTypeName Issue link type name
     */
    protected void ValidateIssueLinkType(final String issueLinkTypeName) {
        if (GetIssueLinksTypeByName(issueLinkTypeName) == null) {
            throw new WebApplicationException("IssueLinkType with identifier (" + issueLinkTypeName +") not found!", Response.Status.CONFLICT);
        }
    }

    /**
     * Select issues by terms and issue type name.
     * 
     * @param terms Search terms
     * @param issueTypeName Issue type name
     * 
     * @return Issues
     */
    protected Iterable<Issue> selectIssues(final String terms, final String issueTypeName) {
        var searchString = new JiraQueryBuilder().Terms(terms).IssueType(issueTypeName).build();

        // TODO: add unauthorized catch
        // TODO: verify all endpoints responds correctly with 401
        var search = getSearchClient().searchJql(searchString).claim();
        if (search.getTotal() == 0) {
            return null;
        }

        return search.getIssues();
    }

    /** 
     * Query issues.
     * 
     * @param issueTypeName Issue type name
     * @param where Where clause
     * @param terms Terms for full-text search.
     * @param prefix Prefix of the used oslc properties.
     * @param paging Enable paging.
     * @param page Selected page.
     * @param limit Amount of issues per page.
     * 
     * @return Issues
     */
    protected Iterable<Issue> queryIssues(final String issueTypeName, final String where, final String terms, final String prefix, final boolean paging, final int page, final int limit) {
        Map<String, String> parsedPrefix = null;
        WhereClause parsedWhere = null;

        try {
            if (prefix != null) {
                parsedPrefix = QueryUtils.parsePrefixes(prefix);
            }
        } catch (Exception e) {
            throw new WebApplicationException("Failed to parse query prefixes!", Response.Status.BAD_REQUEST);
        }

        if (parsedPrefix == null && where != null) {
            throw new WebApplicationException("oslc.where used without oslc.prefix", Response.Status.BAD_REQUEST);
        }

        try {
            if (where != null) {
                parsedWhere = QueryUtils.parseWhere(where, parsedPrefix);
            }
        } catch (Exception e) {
            throw new WebApplicationException("Failed to parse where!", Response.Status.BAD_REQUEST);
        }

        var issueLinks = GetIssueLinksTypeByName(configuration.IssueLinkTypeName);
        var queryBuilder = new JiraQueryBuilder(new IssueTranslator(issueLinks.getInward(), issueLinks.getOutward()))
                .IssueType(issueTypeName)
                .Terms(terms);

        if (parsedWhere != null) {
            for (SimpleTerm term : parsedWhere.children()) {
                queryBuilder.addTerm(term);
            }
        }

        // TODO: add unauthorized catch
        // TODO: verify all endpoints responds correctly with 401
        SearchResult search = null;
        if (paging && page >= 0 && limit > 0) {
            search = getSearchClient().searchJql(queryBuilder.build(), limit, page * limit).claim();
        }
        else {
            search = getSearchClient().searchJql(queryBuilder.build()).claim();
        }

        if (search == null || search.getTotal() == 0) {
            return null;
        }

        return search.getIssues();
    }
}

package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.api.RestClientException;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;
import org.eclipse.lyo.oslc4j.core.model.Link;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

import static java.util.UUID.randomUUID;

public class IssueFacade extends BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    protected String getIdentifierSearchQuery(final String identifier) {
        if (configuration.SaveIdentifierInLabelsField) {
            return configuration.LabelsFieldName + " = " + getIssueClient().getFormattedLabelsIdentifier(identifier);
        } else {
            return configuration.IdentifierFieldName + " ~ " + identifier;
        }
    }

    protected Issue getIssueByIdentifier(final String identifier) {

        var searchString = getIdentifierSearchQuery(identifier);

        // TODO: add unauthorized catch
        // TODO: verify all endpoints responds correctly with 401
        var search = getSearchClient().searchJql(searchString).claim();
        if (search.getTotal() == 0) {
            return null;
        }

        if (search.getTotal() > 1) {
            throw new WebApplicationException("Multiple issues with same identifier (" + identifier + ") exist!", Response.Status.CONFLICT);
        }

        var issues = search.getIssues();
        Issue result = null;

        for (Issue issue : issues) {
            // Only one issue should make it here, resulting in single iteration
            result = issue;
        }

        return getIssue(result.getId());
    }

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

    protected Link GetCreatorLink(final Issue issue) {
        var creator = issue.getReporter();
        if (creator == null) {
            throw new WebApplicationException("Creator for issue with identifier (" + issue.getKey() + ") not found!", Response.Status.CONFLICT);
        }

        return resourcesFactory.constructLinkForPerson(creator.getName());
    }

    protected Set<Link> GetContributorsLinks(final Issue issue) {
        var contributors = GetContributors(issue);
        var contributorsLinks = new HashSet<Link>();

        for (var contributor : contributors) {
            contributorsLinks.add(resourcesFactory.constructLinkForPerson(contributor));
        }

        contributorsLinks.add(GetCreatorLink(issue));

        return contributorsLinks;
    }

    protected Issue getIssue(final String id) {
        var expandos = new LinkedList<IssueRestClient.Expandos>();
        expandos.add(IssueRestClient.Expandos.CHANGELOG);
        return getIssueClient().getIssue(id, expandos).claim();
    }

    protected Issue getIssue(final Long id) {
        var stringId = Objects.requireNonNull(id).toString();
        return getIssue(stringId);
    }

    private boolean IsDecomposedByLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase(configuration.IssueLinkTypeName)
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.INBOUND);
    }

    private boolean IsDecomposesLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase(configuration.IssueLinkTypeName)
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.OUTBOUND);
    }

    private Link ConstructLinkForDecomposeLink(final String id) {
        var issue = getIssue(id);

        if (getIssueClient().IsRequirement(issue)) {
            return resourcesFactory.constructLinkForRequirement(getIssueClient().getIssueGUID(issue, true));
        }
        else if (getIssueClient().IsRequirementCollection(issue)) {
            return resourcesFactory.constructLinkForRequirementCollection(getIssueClient().getIssueGUID(issue, true));
        }

        // Issue is not Requirement or Requirement Collection, omit from link
        return null;
    }

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

        // TODO: check model for title length
        if (title == null || title.length() == 0) {
            throw new WebApplicationException("Title has to be specified, failed to create issue!", Response.Status.BAD_REQUEST);
        }

        var issueInputBuilder = new IssueInputBuilder(project.getKey(), issueType.getId(), title)
                .setDescription(description);

        if (configuration.SaveIdentifierInLabelsField) {
            var subjectsWithIdentifier = subject;
            subjectsWithIdentifier.add(getIssueClient().getFormattedLabelsIdentifier(identifier));

            issueInputBuilder
                    .setFieldInput(new FieldInput(labelsFieldId, subjectsWithIdentifier));
        } else {
            issueInputBuilder
                    .setFieldInput(new FieldInput(labelsFieldId, subject))
                    .setFieldInput(new FieldInput(identifierFieldId, identifier));
        }

        var issue = issueInputBuilder.build();

        try {
            // TODO: problem with Epic as it requires EPIC name field as well
            return getIssueClient().createIssue(issue).claim();
        } catch (Exception e) {
            throw new WebApplicationException("Failed to create " + issueTypeName + "!", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    protected boolean deleteIssue(final String identifier) {
        var issue = getIssueByIdentifier(identifier);
        if (issue == null) {
            throw new WebApplicationException("Issue with identifier (" + identifier +") not found!", Response.Status.NOT_FOUND);
        }
        getIssueClient().deleteIssue(issue.getKey(), false).claim();

        var deletedIssue = getIssueByIdentifier(identifier);

        return deletedIssue == null;
    }

    protected void updateIssue(final String identifier, final String description, final String title, final Set<String> subject) {
        var issue = getIssueByIdentifier(identifier);
        if (issue == null) {
            throw new WebApplicationException("Issue with identifier (" + identifier +") not found!", Response.Status.NOT_FOUND);
        }

        var labelsFieldId = getIssueClient().GetFieldId(configuration.LabelsFieldName);

        if (labelsFieldId == null) {
            throw new WebApplicationException("Field Labels not found, failed to create issue!", Response.Status.CONFLICT);
        }

        // TODO: check model for title length
        if (title == null || title.length() == 0) {
            throw new WebApplicationException("Title has to be specified, failed to create issue!", Response.Status.BAD_REQUEST);
        }

        var issueInputBuilder = new IssueInputBuilder(issue.getProject().getKey(), issue.getIssueType().getId(), title)
                .setDescription(description);

        if (configuration.SaveIdentifierInLabelsField) {
            var subjectsWithIdentifier = subject;
            subjectsWithIdentifier.add(getIssueClient().getFormattedLabelsIdentifier(identifier));

            issueInputBuilder
                    .setFieldInput(new FieldInput(labelsFieldId, subjectsWithIdentifier));
        } else {
            issueInputBuilder.setFieldInput(new FieldInput(labelsFieldId, subject));
        }

        var updatedIssue = issueInputBuilder.build();

        getIssueClient().updateIssue(issue.getKey(), updatedIssue).claim();
    }

    protected void RemoveAdaptorIssueLinks(final String identifier) {
        var issue = getIssueByIdentifier(identifier);
        var issueLinkIds = getIssueLinkRestClient().getAdaptorIssueLinkIdsForIssue(issue.getKey(), configuration.IssueLinkTypeName).claim();

        for (var issueLinkId : issueLinkIds) {
            getIssueLinkRestClient().deleteLink(issueLinkId).claim();
        }
    }

    protected void CreateDecomposedByLinks(final Set<Link> links, final String identifier) {
        for (Link link : links) {
            CreateLink(GetIdFromUri(link.getValue()), identifier, configuration.IssueLinkTypeName);
        }
    }

    protected void CreateDecomposesLinks(final Set<Link> links, final String identifier) {
        for (Link link : links) {
            CreateLink(identifier, GetIdFromUri(link.getValue()), configuration.IssueLinkTypeName);
        }
    }

    protected void ValidateIssueLinkType(final String issueLinkTypeName) {
        var issueLinkTypes = getMetadataClient().getIssueLinkTypes().claim();

        for (IssuelinksType issueLinkType : issueLinkTypes) {
            if (issueLinkType.getName().equalsIgnoreCase(issueLinkTypeName)) {
                return;
            }
        }

        throw new WebApplicationException("IssueLinkType with identifier (" + issueLinkTypeName +") not found!", Response.Status.CONFLICT);
    }
}

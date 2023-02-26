package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import org.eclipse.lyo.oslc4j.core.model.Link;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.UUID.randomUUID;

public class IssueFacade extends BaseFacade {

    protected Issue getIssueByIdentifier(final String identifier) {
        // TODO: move to config
        var searchString = "Identifier ~ " + identifier;
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

        return result;
    }

    protected Issue getIssue(final String id) {
        return getIssueClient().getIssue(id).claim();
    }

    protected Issue getIssue(final Long id) {
        var stringId = Objects.requireNonNull(id).toString();
        return getIssue(stringId);
    }

    // TODO: move names to config file
    protected boolean IsRequirement(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase("Requirement");
    }

    // TODO: move names to config file
    protected boolean IsRequirementCollection(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase("Requirement Collection");
    }

    // TODO: move names to config file
    private boolean IsDecomposedByLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase("Decompose")
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.INBOUND);
    }

    // TODO: move names to config file
    private boolean IsDecomposesLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase("Decompose")
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.OUTBOUND);
    }

    private Link ConstructLinkForDecomposeLink(final String id) {
        var issue = getIssue(id);

        if (IsRequirement(issue)) {
            return resourcesFactory.constructLinkForRequirement(GetIssueGUID(issue, true));
        }
        else if (IsRequirementCollection(issue)) {
            return resourcesFactory.constructLinkForRequirementCollection(GetIssueGUID(issue, true));
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

    protected void CreateLink(final String identifierFrom, final String identifierTo, final String linkTypeName) {
        var issueFrom = getIssueByIdentifier(identifierFrom);
        if (issueFrom == null) {
            throw new WebApplicationException("Issue with identifier (" + identifierFrom + ") not found!", Response.Status.BAD_REQUEST);
        }

        var issueTo = getIssueByIdentifier(identifierTo);
        if (issueTo == null) {
            throw new WebApplicationException("Issue with identifier (" + identifierTo + ") not found!", Response.Status.BAD_REQUEST);
        }

        var issueLink = new LinkIssuesInput(issueFrom.getKey(), issueTo.getKey(), linkTypeName);
        getIssueClient().linkIssue(issueLink);
    }

    protected BasicIssue createIssue(final String description, final String issueTypeName, final String projectId, final String title, final String identifier, final Set<String> subject) {
        var project = getProjectClient().getProject(projectId).claim();

        if (project == null) {
            throw new WebApplicationException("Project with " + projectId + " not found!", Response.Status.BAD_REQUEST);
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

        // TODO: move names to config file
        var searchQuery = "Identifier ~ " + identifier;
        var identifierSearch = getSearchClient().searchJql(searchQuery).claim();
        if (identifierSearch.getTotal() > 0) {
            throw new WebApplicationException("Issue with same identifier (" + identifier +") already exists!", Response.Status.CONFLICT);
        }

        var fields = getMetadataClient().getFields().claim();

        // TODO: move names to config file
        var labelsFieldId = GetFieldId("Labels", fields);
        var identifierFieldId = GetFieldId("Identifier", fields);

        if (labelsFieldId == null) {
            throw new WebApplicationException("Field Labels not found, failed to create issue!", Response.Status.CONFLICT);
        }

        if (identifierFieldId == null) {
            throw new WebApplicationException("Field Identifier not found, failed to create issue!", Response.Status.CONFLICT);
        }

        var issue = new IssueInputBuilder(project.getKey(), issueType.getId(), title)
                .setFieldInput(new FieldInput(labelsFieldId, subject))
                .setFieldInput(new FieldInput(identifierFieldId, identifier))
                .setDescription(description)
                .build();

        try {
            return getIssueClient().createIssue(issue).claim();
        } catch (Exception e) {
            throw new WebApplicationException("Failed to create " + issueTypeName + "!", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    protected String GetFieldId(final String fieldName, Iterable<Field> fields) {
        for (var field : fields ) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                 return field.getId();
            }
        }

        return null;
    }

    protected String GetFieldId(final String fieldName) {
        var fields = getMetadataClient().getFields().claim();
        return GetFieldId(fieldName, fields);
    }

    protected String GetFieldStringValue(final String fieldName, final Issue issue) {
        var fieldId = GetFieldId(fieldName);
        if (fieldId == null) {
            throw new WebApplicationException("Failed to find fieldId for " + fieldName + "!", Response.Status.CONFLICT);
        }

        var field = issue.getField(fieldId);
        if (field == null) {
            throw new WebApplicationException("Failed to find field for fieldId " + fieldId + "!", Response.Status.CONFLICT);
        }


        return field.getValue() != null ? field.getValue().toString() : null;
    }

    protected String GetIssueGUID(final Issue issue, final boolean first) {
        // TODO: Move to config file
        var identifierFieldName = "Identifier";
        var identifier = GetFieldStringValue(identifierFieldName, issue);

        if (identifier != null) {
            return identifier;
        }

        if (!first) {
            throw new WebApplicationException("Failed to generate GUID for issue " + issue.getKey() + "!", Response.Status.INTERNAL_SERVER_ERROR);
        }

        identifier = CreateIssueGUID();
        getIssueClient().updateIssue(
                issue.getKey(),
                new IssueInputBuilder(issue.getProject().getKey(), issue.getIssueType().getId())
                        .setFieldInput(new FieldInput(GetFieldId(identifierFieldName), identifier))
                        .build())
                .claim();

        var updated = getIssue(issue.getId());

        return GetIssueGUID(updated, false);
    }

    protected String CreateIssueGUID() {
        return randomUUID().toString();
    }

    protected String GetIdFromUri(final URI uri) {
        var exploded = uri.toString().split("/");
        return exploded[exploded.length - 1];
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
}

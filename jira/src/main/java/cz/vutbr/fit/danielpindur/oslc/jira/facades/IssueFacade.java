package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import org.eclipse.lyo.oslc4j.core.model.Link;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class IssueFacade extends BaseFacade {

    protected Issue getIssue(final String id) {
        return getIssueClient().getIssue(id).claim();
    }

    protected Issue getIssue(final Long id) {
        var stringId = Objects.requireNonNull(id).toString();
        return getIssue(stringId);
    }

    protected boolean IsRequirement(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase("Requirement");
    }

    protected boolean IsRequirementCollection(final Issue issue) {
        return issue != null && issue.getIssueType().getName().equalsIgnoreCase("Requirement Collection");
    }

    private boolean IsDecomposedByLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase("Decompose")
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.INBOUND);
    }

    private boolean IsDecomposesLink(final IssueLink link) {
        return link.getIssueLinkType().getName().equalsIgnoreCase("Decompose")
                && link.getIssueLinkType().getDirection().equals(IssueLinkType.Direction.OUTBOUND);
    }

    private Link ConstructLinkForDecomposeLink(final String id) {
        var issue = getIssue(id);

        if (IsRequirement(issue)) {
            return resourcesFactory.constructLinkForRequirement(SafeConvert(issue.getId()));
        }
        else if (IsRequirementCollection(issue)) {
            return resourcesFactory.constructLinkForRequirementCollection(SafeConvert(issue.getId()));
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

    protected BasicIssue createIssue(final String description, final String issueTypeName, final String projectId, final String title) {
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

        var issue = new IssueInputBuilder(projectId, issueType.getId(), title)
                .setDescription(description)
                .build();

        try {
            return getIssueClient().createIssue(issue).claim();
        } catch (Exception e) {
            throw new WebApplicationException("Failed to create " + issueTypeName + "!", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

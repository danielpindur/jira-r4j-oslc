package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;

public class RequirementFacade extends IssueFacade {
    private Requirement MapResourceToResult(final Issue resource) {
        var result = new Requirement();
        var jiraIssueId = resource.getId().intValue();
        var projectIdString = SafeConvert(resource.getProject().getId());

        var identifier = getIssueClient().getIssueGUID(resource, true);

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey()); // TODO: Check if set RO
        result.setJiraId(jiraIssueId);
        result.setIdentifier(identifier);  // TODO: Check if readonly
        result.setAbout(resourcesFactory.constructURIForRequirement(identifier));
        result.setSubject(GetFieldStringSetValue(configuration.LabelsFieldName, resource));
        result.setCreated(resource.getCreationDate().toDate());
        result.setModified(resource.getUpdateDate().toDate());
        result.setProject(resourcesFactory.constructLinkForProject(projectIdString));
        // TODO: Creator should be probably always one instead of array
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

    // TODO: check and verify creation and resource shapes
    public Requirement create(final Requirement requirement) {
        var identifier = requirement.getIdentifier() != null
                        ? requirement.getIdentifier()
                        : getIssueClient().CreateIssueGUID();

        var projectUri = requirement.getProject().getValue();

        createIssue(requirement.getDescription(),
                configuration.RequirementIssueTypeName,
                GetIdFromUri(projectUri),
                requirement.getTitle(),
                identifier,
                requirement.getSubject());

        CreateDecomposedByLinks(requirement.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirement.getDecomposes(), identifier);

        return get(identifier);
    }

    public Requirement get(final String id) {
        var issue = getIssueByIdentifier(id);

        if (issue == null || !getIssueClient().IsRequirement(issue)) {
            return null;
        }

        return MapResourceToResult(issue);
    }

    public boolean delete(final String id) {
        return deleteIssue(id);
    }

    public Requirement update(final Requirement requirement, final String identifier) {
        updateIssue(identifier, requirement.getDescription(), requirement.getTitle(), requirement.getSubject());

        RemoveAdaptorIssueLinks(identifier);
        CreateDecomposedByLinks(requirement.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirement.getDecomposes(), identifier);

        return get(identifier);
    }
}

package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.RequirementCollection;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;

public class RequirementCollectionFacade extends IssueFacade {
    private RequirementCollection MapResourceToResult(final Issue resource) {
        var result = new RequirementCollection();
        var jiraIssueId = resource.getId().intValue();
        var projectIdString = SafeConvert(resource.getProject().getId());

        var identifier = GetIssueGUID(resource, true);

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey()); // TODO: Check if set RO
        result.setJiraId(jiraIssueId);
        result.setIdentifier(identifier);  // TODO: Check if readonly
        result.setAbout(resourcesFactory.constructURIForRequirementCollection(identifier));
        result.setCreated(resource.getCreationDate().toDate());
        result.setSubject(GetFieldStringSetValue(configuration.LabelsFieldName, resource));
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

    public RequirementCollection get(final String id) {
        var issue = getIssueByIdentifier(id);

        if (issue == null) {
            return null;
        }

        return MapResourceToResult(issue);
    }

    public RequirementCollection create(final RequirementCollection requirementCollection) {
        var identifier = requirementCollection.getIdentifier() != null
                ? requirementCollection.getIdentifier()
                : CreateIssueGUID();

        var projectUri = requirementCollection.getProject().getValue();

        createIssue(requirementCollection.getDescription(),
                configuration.RequirementCollectionIssueTypeName,
                GetIdFromUri(projectUri),
                requirementCollection.getTitle(),
                identifier,
                requirementCollection.getSubject());

        CreateDecomposedByLinks(requirementCollection.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirementCollection.getDecomposes(), identifier);

        return get(identifier);
    }

    public boolean delete(final String id) {
        return deleteIssue(id);
    }

    public RequirementCollection update(final RequirementCollection requirementCollection, final String identifier) {
        updateIssue(identifier, requirementCollection.getDescription(), requirementCollection.getTitle(), requirementCollection.getSubject());

        RemoveAdaptorIssueLinks(identifier);
        CreateDecomposedByLinks(requirementCollection.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirementCollection.getDecomposes(), identifier);

        return get(identifier);
    }
}

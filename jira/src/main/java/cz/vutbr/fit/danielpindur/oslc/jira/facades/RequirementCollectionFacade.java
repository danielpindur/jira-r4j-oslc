package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
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
        result.setSubject(resource.getLabels());
        result.setModified(resource.getUpdateDate().toDate());
        result.setProject(resourcesFactory.constructLinkForProject(projectIdString));
        // TODO: Creator should be probably always one instead of array
        result.setCreator(
                new HashSet<Link>(){{
                    add(resourcesFactory.constructLinkForPerson(resource.getReporter().getName()));
                }}
        );

        result.setDecomposedBy(GetDecomposedBy(resource));
        result.setDecomposes(GetDecomposes(resource));

        // TODO: Contributors

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

        // TODO: move to config
        createIssue(requirementCollection.getDescription(),
                "Requirement Collection",
                GetIdFromUri(projectUri),
                requirementCollection.getTitle(),
                identifier,
                requirementCollection.getSubject());

        for (Link link : requirementCollection.getDecomposedBy()) {
            // TODO: move to config
            CreateLink(GetIdFromUri(link.getValue()), identifier, "Decompose");
        }

        for (Link link : requirementCollection.getDecomposes()) {
            // TODO: move to config
            CreateLink(identifier, GetIdFromUri(link.getValue()), "Decompose");
        }

        return get(identifier);
    }
}

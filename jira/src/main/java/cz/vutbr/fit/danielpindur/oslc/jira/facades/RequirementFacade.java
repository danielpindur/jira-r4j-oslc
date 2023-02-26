package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;
import java.util.Objects;

public class RequirementFacade extends IssueFacade {
    private Requirement MapResourceToResult(final Issue resource) {
        var result = new Requirement();
        var jiraIssueId = resource.getId().intValue();
        var projectIdString = SafeConvert(resource.getProject().getId());

        var identifier = GetIssueGUID(resource, true);

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey()); // TODO: Check if set RO
        result.setJiraId(jiraIssueId);
        result.setIdentifier(identifier);  // TODO: Check if readonly
        result.setAbout(resourcesFactory.constructURIForRequirement(identifier));
        result.setSubject(resource.getLabels());
        result.setCreated(resource.getCreationDate().toDate());
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

    public Requirement create(final Requirement requirement) {
        var identifier = requirement.getIdentifier() != null
                        ? requirement.getIdentifier()
                        : CreateIssueGUID();

        var projectUri = requirement.getProject().getValue();

        // TODO: move to config
        createIssue(requirement.getDescription(),
                "Requirement",
                GetIdFromUri(projectUri),
                requirement.getTitle(),
                identifier,
                requirement.getSubject());

        for (Link link : requirement.getDecomposedBy()) {
            // TODO: move to config
            CreateLink(GetIdFromUri(link.getValue()), identifier, "Decompose");
        }

        for (Link link : requirement.getDecomposes()) {
            // TODO: move to config
            CreateLink(identifier, GetIdFromUri(link.getValue()), "Decompose");
        }

        return get(identifier);
    }

    public Requirement get(final String id) {
        var issue = getIssueByIdentifier(id);
        if (issue == null || !IsRequirement(issue)) {
            return null;
        }

        return MapResourceToResult(issue);
    }
}

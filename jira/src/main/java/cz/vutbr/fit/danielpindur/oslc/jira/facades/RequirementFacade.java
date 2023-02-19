package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;
import java.util.Objects;

public class RequirementFacade extends IssueFacade {
    private Requirement MapResourceToResult(final Issue resource) {
        var result = new Requirement();
        var issueIdString = SafeConvert(resource.getId());
        var projectIdString = SafeConvert(resource.getProject().getId());

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey()); // TODO: Check if set RO
        result.setIdentifier(issueIdString);  // TODO: Check if readonly, can be only number - okay or not?
        result.setAbout(resourcesFactory.constructURIForRequirement(issueIdString));
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
        // Create issue

        // Add decomposed by

        // Add decomposes

        return null;
    }

    public Requirement get(final String id) {
        var issue = getIssue(id);
        if (issue == null || !IsRequirement(issue)) {
            return null;
        }

        return MapResourceToResult(issue);
    }
}

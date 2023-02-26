package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.RequirementCollection;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;

public class RequirementCollectionFacade extends IssueFacade {
    private RequirementCollection MapResourceToResult(final Issue resource) {
        var result = new RequirementCollection();
        var issueIdString = SafeConvert(resource.getId());
        var projectIdString = SafeConvert(resource.getProject().getId());

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey()); // TODO: Check if set RO
        result.setIdentifier(issueIdString);  // TODO: Check if readonly, can be only number - okay or not?
        result.setAbout(resourcesFactory.constructURIForRequirementCollection(issueIdString));
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

    public RequirementCollection get(final String id) {
        var issue = getIssueByIdentifier(id);

        if (issue == null) {
            return null;
        }

        return MapResourceToResult(issue);
    }
}

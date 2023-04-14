package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class RequirementFacade extends IssueFacade {
    // TODO: same thing for folder
    private Requirement MapResourceToResult(final Issue resource) {
        var result = new Requirement();
        var jiraIssueId = resource.getId().intValue();
        var projectIdString = SafeConvert(resource.getProject().getId());

        var identifier = getIssueClient().getIssueGUID(resource, true);

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey());
        result.setJiraId(jiraIssueId);
        result.setIdentifier(identifier);
        result.setAbout(resourcesFactory.constructURIForRequirement(identifier));
        result.setSubject(getIssueClient().getFieldStringSetValueWithoutIdentifier(configuration.LabelsFieldName, resource));
        result.setCreated(resource.getCreationDate().toDateTimeISO().toDate());
        result.setModified(resource.getCreationDate().toDateTimeISO().toDate());
        result.setProject(resourcesFactory.constructLinkForProject(projectIdString));
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

    public Requirement create(final Requirement requirement) {
        var identifier = requirement.getIdentifier() != null
                        ? requirement.getIdentifier()
                        : IssueHelper.CreateIssueGUID();

        var projectUri = requirement.getProject().getValue();
        ValidateLinks(requirement.getDecomposedBy());
        ValidateLinks(requirement.getDecomposes());

        createIssue(requirement.getDescription(),
                configuration.RequirementIssueTypeName,
                UriHelper.GetIdFromUri(projectUri),
                requirement.getTitle(),
                identifier,
                requirement.getSubject());

        CreateDecomposedByLinks(requirement.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirement.getDecomposes(), identifier);

        return get(identifier);
    }

    public Requirement get(final String id) {
        var issue = getIssueByIdentifier(id);

        if (!IssueHelper.IsRequirement(issue)) {
            return null;
        }

        return MapResourceToResult(issue);
    }

    public boolean delete(final String id) {
        return deleteIssue(id);
    }

    public Requirement update(final Requirement requirement, final String identifier) {
        ValidateLinks(requirement.getDecomposedBy());
        ValidateLinks(requirement.getDecomposes());

        updateIssue(identifier, requirement.getDescription(), requirement.getTitle(), requirement.getSubject());

        RemoveAdaptorIssueLinks(identifier);
        CreateDecomposedByLinks(requirement.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirement.getDecomposes(), identifier);

        return get(identifier);
    }

    private List<Requirement> mapIssuesToRequirements(final Iterable<Issue> issues) {
        var requirements = new LinkedList<Requirement>();

        if (issues == null) {
            return requirements;
        }

        for (Issue issue : issues) {
            var requirement = MapResourceToResult(issue);
            requirements.add(requirement);
        }

        return requirements;
    }

    public List<Requirement> selectRequirements(final String terms) {
        var issues = selectIssues(terms, configuration.RequirementIssueTypeName);
        return mapIssuesToRequirements(issues);
    }

    public List<Requirement> queryRequirements(final String where, final String terms, final String prefix, final boolean paging, final int page, final int limit) {
        var issues = queryIssues(configuration.RequirementIssueTypeName, where, terms, prefix, paging, page, limit);
        return mapIssuesToRequirements(issues);
    }
}

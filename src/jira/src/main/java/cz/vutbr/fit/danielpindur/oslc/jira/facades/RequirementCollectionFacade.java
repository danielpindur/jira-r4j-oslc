package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.Issue;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.RequirementCollection;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import org.eclipse.lyo.oslc4j.core.model.Link;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class RequirementCollectionFacade extends IssueFacade {
    private RequirementCollection MapResourceToResult(final Issue resource) {
        var result = new RequirementCollection();
        var jiraIssueId = resource.getId().intValue();
        var projectIdString = SafeConvert(resource.getProject().getId());

        var identifier = getIssueClient().getIssueGUID(resource, true);

        result.setTitle(resource.getSummary());
        result.setDescription(resource.getDescription());
        result.setShortTitle(resource.getKey());
        result.setJiraId(jiraIssueId);
        result.setIdentifier(identifier);
        result.setAbout(resourcesFactory.constructURIForRequirementCollection(identifier));
        result.setCreated(resource.getCreationDate().toDate());
        result.setSubject(getIssueClient().getFieldStringSetValueWithoutIdentifier(configuration.LabelsFieldName, resource));
        result.setModified(resource.getUpdateDate().toDate());
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

    public RequirementCollection get(final String id) {
        var issue = getIssueByIdentifier(id);

        if (!IssueHelper.IsRequirementCollection(issue)) {
            return null;
        }

        return MapResourceToResult(issue);
    }

    public RequirementCollection create(final RequirementCollection requirementCollection) {
        var identifier = requirementCollection.getIdentifier() != null
                ? requirementCollection.getIdentifier()
                : IssueHelper.CreateIssueGUID();

        var projectUri = requirementCollection.getProject().getValue();

        ValidateLinks(requirementCollection.getDecomposedBy());
        ValidateLinks(requirementCollection.getDecomposes());

        createIssue(requirementCollection.getDescription(),
                configuration.RequirementCollectionIssueTypeName,
                UriHelper.GetIdFromUri(projectUri),
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
        ValidateLinks(requirementCollection.getDecomposedBy());
        ValidateLinks(requirementCollection.getDecomposes());

        updateIssue(identifier, requirementCollection.getDescription(), requirementCollection.getTitle(), requirementCollection.getSubject());

        RemoveAdaptorIssueLinks(identifier);
        CreateDecomposedByLinks(requirementCollection.getDecomposedBy(), identifier);
        CreateDecomposesLinks(requirementCollection.getDecomposes(), identifier);

        return get(identifier);
    }

    private List<RequirementCollection> mapIssuesToRequirementCollections(Iterable<Issue> issues) {
        var requirementCollections = new LinkedList<RequirementCollection>();

        if (issues == null) {
            return requirementCollections;
        }

        for (Issue issue : issues) {
            var identifier = getIssueClient().getIssueGUID(issue, true);
            var requirementCollection = get(identifier);
            requirementCollections.add(requirementCollection);
        }

        return requirementCollections;
    }

    public List<RequirementCollection> selectRequirementCollections(final String terms) {
        var issues = selectIssues(terms, configuration.RequirementCollectionIssueTypeName);
        return mapIssuesToRequirementCollections(issues);
    }

    public List<RequirementCollection> queryRequirementCollections(final String where, final String terms, final String prefix, final boolean paging, final int page, final int limit) {
        var issues = queryIssues(configuration.RequirementCollectionIssueTypeName, where, terms, prefix, paging, page, limit);
        return mapIssuesToRequirementCollections(issues);
    }
}

package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Project;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProjectFacade extends BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    // TODO: validate returned HTTP codes - align with OSLC standard
    private Project MapResourceToResult(final com.atlassian.jira.rest.client.api.domain.Project resource) {
        var result = new Project();
        var projectIdString = Objects.requireNonNull(resource.getId()).toString();

        result.setShortTitle(resource.getKey());
        result.setDescription(resource.getDescription());
        result.setTitle(resource.getName());
        result.setIdentifier(projectIdString);
        result.setAbout(resourcesFactory.constructURIForProject(projectIdString));

        return result;
    }

    public Project get(final String id) {
        com.atlassian.jira.rest.client.api.domain.Project projectResource = null;

        try {
            projectResource = getProjectClient().getProject(id).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        return projectResource != null ? MapResourceToResult(projectResource) : null;
    }

    public Project get(final Long id) {
        var idString = Objects.requireNonNull(id).toString();
        return get(idString);
    }

    private boolean shouldBeReturnedFromSearch(final BasicProject project, final String terms) {
        return  containsTerms(project.getName(), terms) || containsTerms(project.getKey(), terms);
    }

    public List<Project> search(final String terms) {
        var projectResources = getProjectClient().getAllProjects().claim();
        var results = new LinkedList<Project>();

        for (BasicProject projectResource : projectResources) {
            if (shouldBeReturnedFromSearch(projectResource, terms)) {
                var result = get(projectResource.getId());
                results.add(result);
            }
        }

        return results;
    }
}

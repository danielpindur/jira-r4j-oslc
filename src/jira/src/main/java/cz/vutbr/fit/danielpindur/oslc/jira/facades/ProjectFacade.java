package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.domain.BasicProject;
import cz.vutbr.fit.danielpindur.oslc.jira.resources.Project;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProjectFacade extends BaseFacade {
    private Project MapResourceToResult(final com.atlassian.jira.rest.client.api.domain.Project resource) {
        var result = new Project();
        var projectIdString = Objects.requireNonNull(resource.getId()).toString();

        result.setShortTitle(resource.getKey()); // TODO: Check if I can change shortitle independently of title, else change to RO
        result.setDescription(resource.getDescription());
        result.setTitle(resource.getName());
        result.setIdentifier(projectIdString); // TODO: Check if readonly, can be only number - okay or not?
        result.setAbout(resourcesFactory.constructURIForProject(projectIdString));

        return result;
    }

    public Project get(final String id) {
        var projectResource = getProjectClient().getProject(id).claim();

        if (projectResource == null) {
            return null;
        }

        return MapResourceToResult(projectResource);
    }

    public Project get(final Long id) {
        var idString = Objects.requireNonNull(id).toString();
        return get(idString);
    }

    private boolean shouldBeReturnedFromSearch(final BasicProject project, final String terms) {
        return  containsTerms(project.getName(), terms) || containsTerms(project.getKey(), terms);
    }

    // TODO: What exactly are terms? Can it be multiple words?
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
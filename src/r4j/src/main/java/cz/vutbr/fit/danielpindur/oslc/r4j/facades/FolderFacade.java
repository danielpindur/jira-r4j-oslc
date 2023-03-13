package cz.vutbr.fit.danielpindur.oslc.r4j.facades;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import cz.vutbr.fit.danielpindur.oslc.r4j.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.r4j.clients.JiraAdaptorClient;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Folder;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Requirement;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.RequirementCollection;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import org.eclipse.lyo.oslc4j.core.model.Link;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class FolderFacade extends BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    private Folder MapResourceToResult(final String folderIdentifier, final FolderModel resource, final String projectKey) {
        var result = new Folder();
        result.setTitle(resource.Title);
        result.setDescription(resource.Description);
        result.setIdentifier(folderIdentifier);
        result.setAbout(resourcesFactory.constructURIForFolder(folderIdentifier));

        var parentIdentifier = GetFolderIdentifier(projectKey, resource.ParentId);
        result.setParent(parentIdentifier != null ? resourcesFactory.constructLinkForFolder(parentIdentifier) : null);

        result.setSubfolder(GetSubfolderLinks(resource.SubfolderIds, projectKey));
        result.setContains(GetContainsLinks(resource.ContainsIssueKeys));

        return result;
    }

    private Set<Link> GetSubfolderLinks(final Set<Integer> subfolderIds, final String projectKey) {
        var subfolderLinks = new HashSet<Link>();
        for (var subfolderId : subfolderIds) {
            var subfolderIdentifier = GetFolderIdentifier(projectKey, subfolderId);
            if (subfolderIdentifier != null) {
                subfolderLinks.add(resourcesFactory.constructLinkForFolder(subfolderIdentifier));
            }
        }
        return subfolderLinks;
    }

    private Set<Link> GetContainsLinks(final Set<String> issueKeys) {
        var containsLinks = new HashSet<Link>();

        for (var issueKey : issueKeys) {
            Issue issue = null;
            try {
                issue = getIssueClient().getIssue(issueKey).claim();
            } catch (RestClientException e) {
                if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                    throw e;
                }
            }
            if (issue == null) {
                continue;
            }

            var identifier = getIssueClient().getIssueGUID(issue, true);
            if (identifier == null) {
                throw new WebApplicationException("Failed to get GUID for issue " + issue.getKey() + "!", Response.Status.INTERNAL_SERVER_ERROR);
            }

            if (getIssueClient().IsRequirement(issue)){
                containsLinks.add(resourcesFactory.constructLinkForRequirement(identifier));
            } else if (getIssueClient().IsRequirementCollection(issue)) {
                containsLinks.add(resourcesFactory.constructLinkForRequirementCollection(identifier));
            }
        }

        return containsLinks;
    }

    private String GetProjectKey(final String folderId) {
        var exploded = folderId.split("-");
        if (exploded.length != 2) {
            throw new WebApplicationException("Invalid folderId " + folderId, Response.Status.BAD_REQUEST);
        }

        return exploded[0];
    }

    private Integer GetFolderId(final String folderId) {
        var exploded = folderId.split("-");
        if (exploded.length != 2) {
            throw new WebApplicationException("Invalid folderId " + folderId, Response.Status.BAD_REQUEST);
        }

        if (exploded[1].equalsIgnoreCase(configuration.RootFolderId)) {
            return -1;
        }

        try {
            return Integer.parseInt(exploded[1]);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid folderId " + folderId, Response.Status.BAD_REQUEST);
        }
    }

    private String GetFolderIdentifier(final String projectKey, final Integer folderId) {
        String secondPart = null;
        if (folderId == 0) {
            return null;
        }
        if (folderId == -1) {
            secondPart = configuration.RootFolderId;
        }
        else {
            secondPart = folderId.toString();
        }

        return projectKey + "-" + secondPart;
    }

    // TODO: make root folder non-editable
    // TODO: remove path from folder
    // TODO: folderId = 0 should be converted to null
    public Folder get(final String id) {
        Project project = null;

        try {
            project = getProjectClient().getProject(GetProjectKey(id)).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        if (project == null) {
            throw new WebApplicationException("Project with key=" + GetProjectKey(id) + " not found", Response.Status.NOT_FOUND);
        }

        FolderModel result = null;
        try {
            result = getFolderClient().get(project.getKey(), GetFolderId(id)).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        return result != null ? MapResourceToResult(id, result, project.getKey()) : null;
    }

    // TODO: probably don't want root node to be removable
    public boolean delete(final String id) {
        var folder = get(id);
        if (folder == null) {
            throw new WebApplicationException("Folder with identifier (" + id +") not found!", Response.Status.NOT_FOUND);
        }

        getFolderClient().deleteFolder(GetProjectKey(id), GetFolderId(id)).claim();

        var deletedFolder = get(id);

        return deletedFolder == null;
    }

    public Folder create
}

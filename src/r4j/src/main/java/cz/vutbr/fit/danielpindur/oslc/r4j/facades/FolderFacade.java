package cz.vutbr.fit.danielpindur.oslc.r4j.facades;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import cz.vutbr.fit.danielpindur.oslc.r4j.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.r4j.filters.FolderFilter;
import cz.vutbr.fit.danielpindur.oslc.r4j.filters.FolderFilterInput;
import cz.vutbr.fit.danielpindur.oslc.r4j.resources.Folder;
import cz.vutbr.fit.danielpindur.oslc.r4j.translators.FolderTranslator;
import cz.vutbr.fit.danielpindur.oslc.r4j.helpers.FolderHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.IssueHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.helpers.UriHelper;
import cz.vutbr.fit.danielpindur.oslc.shared.services.facades.BaseFacade;
import cz.vutbr.fit.danielpindur.oslc.shared.services.inputs.FolderInput;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import org.eclipse.lyo.core.query.QueryUtils;
import org.eclipse.lyo.core.query.SimpleTerm;
import org.eclipse.lyo.core.query.WhereClause;
import org.eclipse.lyo.oslc4j.core.model.Link;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

public class FolderFacade extends BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    private Folder MapResourceToResult(final String folderIdentifier, final FolderModel resource, final String projectKey) {
        var result = new Folder();
        result.setTitle(resource.Title);
        result.setDescription(resource.Description);
        result.setIdentifier(folderIdentifier);
        result.setAbout(resourcesFactory.constructURIForFolder(folderIdentifier));

        var parentIdentifier = FolderHelper.ConstructFolderIdentifier(projectKey, resource.ParentId);
        result.setParent(parentIdentifier != null ? resourcesFactory.constructLinkForFolder(parentIdentifier) : null);

        var folderPath = getFolderClient().getFolderPath(GetFolderId(folderIdentifier), projectKey);
        result.setPath(folderPath);

        result.setSubfolder(GetSubfolderLinks(resource.SubfolderIds, projectKey));
        result.setContains(GetContainsLinks(resource.ContainsIssueKeys));

        return result;
    }

    private Set<Link> GetSubfolderLinks(final Set<Integer> subfolderIds, final String projectKey) {
        var subfolderLinks = new HashSet<Link>();
        for (var subfolderId : subfolderIds) {
            var subfolderIdentifier = FolderHelper.ConstructFolderIdentifier(projectKey, subfolderId);
            if (subfolderIdentifier != null) {
                subfolderLinks.add(resourcesFactory.constructLinkForFolder(subfolderIdentifier));
            }
        }
        return subfolderLinks;
    }

    private boolean IsRootFolder(final String identifier) {
        var folderId = GetFolderId(identifier);
        return folderId == -1;
    }

    private boolean ParentChanged(final Folder original, final Folder update) {
        return !original.getParent().getValue().toString().equalsIgnoreCase(update.getParent().getValue().toString());
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

            if (IssueHelper.IsRequirement(issue)){
                containsLinks.add(resourcesFactory.constructLinkForRequirement(identifier));
            } else if (IssueHelper.IsRequirementCollection(issue)) {
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

    private void ValidateProject(final String projectKey) {
        Project project = null;

        try {
            project = getProjectClient().getProject(projectKey).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        if (project == null) {
            throw new WebApplicationException("Project with key=" + projectKey + " not found", Response.Status.NOT_FOUND);
        }
    }

    public Folder get(final String id) {
        var projectKey = GetProjectKey(id);
        ValidateProject(projectKey);

        FolderModel result = null;
        try {
            result = getFolderClient().get(projectKey, GetFolderId(id)).claim();
        } catch (RestClientException e) {
            if (!e.getStatusCode().isPresent() || e.getStatusCode().get() != 404) {
                throw e;
            }
        }

        return result != null ? MapResourceToResult(id, result, projectKey) : null;
    }

    private boolean exists(final String id) {
        return get(id) != null;
    }

    public boolean delete(final String id) {
        if (IsRootFolder(id)) {
            throw new WebApplicationException("Root folder of project cannot be edited", Response.Status.BAD_REQUEST);
        }

        var folder = get(id);
        if (folder == null) {
            throw new WebApplicationException("Folder with identifier (" + id +") not found!", Response.Status.NOT_FOUND);
        }

        getFolderClient().deleteFolder(GetProjectKey(id), GetFolderId(id)).claim();

        var deletedFolder = get(id);

        return deletedFolder == null;
    }

    private void ValidateFolder(final Folder resource) {
        if (resource.getTitle() == null || resource.getTitle().isEmpty()) {
            throw new WebApplicationException("Title cannot be null", Response.Status.BAD_REQUEST);
        }

        if (resource.getParent() == null) {
            throw new WebApplicationException("Parent cannot be null", Response.Status.BAD_REQUEST);
        }
    }

    private void ValidateParentFolder(final String parentFolderIdentifier, final String newTitle) {
        if (!exists(parentFolderIdentifier)) {
            throw new WebApplicationException("Root folder of project cannot be edited", Response.Status.NOT_FOUND);
        }

        var subfolderNames = getFolderClient()
                .getSubfolderNames(
                        GetFolderId(parentFolderIdentifier),
                        GetProjectKey(parentFolderIdentifier)).claim();
        if (subfolderNames.contains(newTitle)) {
            throw new WebApplicationException("Folder " + parentFolderIdentifier + " already contains folder with name=" + newTitle, Response.Status.BAD_REQUEST);
        }
    }

    private void ValidateContains(final Set<Link> contains) {
        for (Link link : contains) {
            var identifier = UriHelper.GetIdFromUri(link.getValue());
            var issue = getIssueClient().searchIssueByIdentifier(identifier);

            if (issue == null) {
                throw new WebApplicationException("Issue with identifier " + identifier + " not found!", Response.Status.BAD_REQUEST);
            }

            if (UriHelper.IsRequirementUri(link.getValue()) && !IssueHelper.IsRequirement(issue)) {
                throw new WebApplicationException("Requirement with identifier " + identifier + " not found!", Response.Status.BAD_REQUEST);
            }

            if (UriHelper.IsRequirementCollectionUri(link.getValue()) && !IssueHelper.IsRequirementCollection(issue)) {
                throw new WebApplicationException("Requirement Collection with identifier " + identifier + " not found!", Response.Status.BAD_REQUEST);
            }
        }
    }

    private void CreateContainsLinks(final Set<Link> links, final Integer folderId, final String projectKey) {
        for (var link : links) {
            var issueKey = FolderHelper.GetIssueKeyFromLink(link);
            getFolderClient().createContainsLink(folderId, projectKey, issueKey).claim();
        }
    }

    private void RemoveAllContainsLinks(final Set<String> links, final Integer folderId, final String projectKey) {
        for (var link : links) {
            getFolderClient().removeContainsLink(folderId, projectKey, link).claim();
        }
    }

    public Folder create(final Folder resource) {
        ValidateFolder(resource);

        var parentFolderIdentifier = UriHelper.GetIdFromUri(resource.getParent().getValue());
        var projectKey = GetProjectKey(parentFolderIdentifier);
        ValidateProject(projectKey);

        ValidateParentFolder(parentFolderIdentifier, resource.getTitle());
        ValidateContains(resource.getContains());

        var folderInput = new FolderInput(resource.getTitle(), resource.getDescription(), GetFolderId(parentFolderIdentifier));

        FolderModel createdFolder = null;
        try {
            createdFolder = getFolderClient().createFolder(folderInput, projectKey).claim();
        } catch (Exception e) {
            throw new WebApplicationException("Failed to create folder!", Response.Status.INTERNAL_SERVER_ERROR);
        }

        CreateContainsLinks(resource.getContains(), createdFolder.Id, projectKey);

        return get(FolderHelper.ConstructFolderIdentifier(projectKey, createdFolder.Id));
    }

    public Folder updateFolder(final String id, final Folder resource) {
        if (IsRootFolder(id)) {
            throw new WebApplicationException("Root folder of project cannot be edited", Response.Status.BAD_REQUEST);
        }

        ValidateFolder(resource);

        var beforeUpdate = get(id);
        var parentFolderIdentifier = UriHelper.GetIdFromUri(resource.getParent().getValue());

        if (ParentChanged(beforeUpdate, resource)) {
            ValidateParentFolder(parentFolderIdentifier, resource.getTitle());
        }

        ValidateContains(resource.getContains());

        var folderInput = new FolderInput(resource.getTitle(), resource.getDescription(), GetFolderId(parentFolderIdentifier));
        var updated = getFolderClient().updateFolder(folderInput, GetProjectKey(id), GetFolderId(id)).claim();

        RemoveAllContainsLinks(updated.ContainsIssueKeys, GetFolderId(id), GetProjectKey(id));
        CreateContainsLinks(resource.getContains(), GetFolderId(id), GetProjectKey(id));

        return get(id);
    }

    public List<Folder> selectFolders(final String terms) {
        var filterInput = new FolderFilterInput();
        var enabledProjectKeys = getFolderClient().getEnabledProjectKeys().claim();
        var result = new LinkedList<Folder>();

        for (var enabledProjectKey : enabledProjectKeys) {
            var folderTree = getFolderClient().getTree(enabledProjectKey).claim();
            var filteredFolderModels = new FolderFilter(filterInput, terms, enabledProjectKey).filter(folderTree);

            for (var folderModel : filteredFolderModels) {
                result.add(MapResourceToResult(FolderHelper.ConstructFolderIdentifier(enabledProjectKey, folderModel.Id), folderModel, enabledProjectKey));
            }
        }

        return result;
    }

    public List<Folder> queryFolder(final String where, final String terms, final String prefix, final boolean paging, final int page, final int limit) {
        Map<String, String> parsedPrefix = null;
        WhereClause parsedWhere = null;

        try {
            if (prefix != null) {
                parsedPrefix = QueryUtils.parsePrefixes(prefix);
            }
        } catch (Exception e) {
            throw new WebApplicationException("Failed to parse query prefixes!", Response.Status.BAD_REQUEST);
        }

        if (parsedPrefix == null && where != null) {
            throw new WebApplicationException("oslc.where used without oslc.prefix", Response.Status.BAD_REQUEST);
        }

        try {
            if (where != null) {
                parsedWhere = QueryUtils.parseWhere(where, parsedPrefix);
            }
        } catch (Exception e) {
            throw new WebApplicationException("Failed to parse where!", Response.Status.BAD_REQUEST);
        }

        var translator = new FolderTranslator();
        if (parsedWhere != null) {
            for (SimpleTerm term : parsedWhere.children()) {
                translator.translate(term);
            }
        }

        var filterInput = translator.filterInput;
        var enabledProjectKeys = getFolderClient().getEnabledProjectKeys().claim();
        var result = new LinkedList<Folder>();

        for (var enabledProjectKey : enabledProjectKeys) {
            var folderTree = getFolderClient().getTree(enabledProjectKey).claim();
            var filteredFolderModels = new FolderFilter(filterInput, terms, enabledProjectKey).filter(folderTree);

            for (var folderModel : filteredFolderModels) {
                result.add(MapResourceToResult(FolderHelper.ConstructFolderIdentifier(enabledProjectKey, folderModel.Id), folderModel, enabledProjectKey));
            }
        }

        if (paging && limit > 0 && page >= 0) {
            if (limit < result.size()) {
                var currentPage = page;

                while(!result.isEmpty() && currentPage > 0) {
                    var toRemove = Math.min(result.size(), limit);
                    for (int i = 0; i < toRemove; i++) {
                        result.removeFirst();
                    }
                    currentPage--;
                }

                if (limit < result.size()) {
                    var pageList = new LinkedList<Folder>();
                    for (int i = 0; i < limit; i++) {
                        pageList.add(result.removeFirst());
                    }
                    return pageList;
                }
            }
        }

        return result;
    }
}

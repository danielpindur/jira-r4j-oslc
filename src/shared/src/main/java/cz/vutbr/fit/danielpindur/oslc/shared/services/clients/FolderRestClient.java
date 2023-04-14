package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.FolderInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.MoveFolderInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.UpdateContainsLinkInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.UpdateFolderInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderSubfolderNamesParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderWatchersParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.inputs.FolderInput;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderParser;
import io.atlassian.util.concurrent.Promise;
import org.codehaus.jettison.json.JSONArray;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class FolderRestClient extends AbstractAsynchronousRestClient implements Closeable {
    private final URI baseUri;
    private final DisposableHttpClient client;
    private final OldFolderRestClient oldFolderRestClient;

    public FolderRestClient(final URI baseUri, final DisposableHttpClient client, final OldFolderRestClient oldFolderRestClient) {
        super(client);
        this.baseUri = baseUri;
        this.client = client;
        this.oldFolderRestClient = oldFolderRestClient;
    }

    public void close() {
        try {
            this.client.destroy();
            this.oldFolderRestClient.close();
        } catch (Exception ignored) { }
    }

    public Promise<FolderModel> get(final String projectKey, final Integer folderId) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        return getAndParse(uri, new FolderParser());
    }

    public Promise<Void> deleteFolder(final String projectKey, final Integer folderId) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        return this.delete(uri);
    }

    public Promise<FolderModel> createFolder(final FolderInput input, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").build();
        return this.postAndParse(uri, input, new FolderInputJsonGenerator(), new FolderParser());
    }

    private Promise<JSONArray> getWatchers(final Integer folderId, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        return getAndParse(uri, new FolderWatchersParser());
    }

    public Promise<Set<String>> getSubfolderNames(final Integer folderId, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        return getAndParse(uri, new FolderSubfolderNamesParser());
    }

    public String getFolderPath(final Integer folderId, final String projectKey) {
        if (folderId == -1) {
            return projectKey;
        }

        var folder = get(projectKey, folderId).claim();
        return getFolderPath(folder.ParentId, projectKey) + "/" + folder.Title;
    }

    public Promise<Void> removeContainsLink(final Integer folderId, final String projectKey, final String issueKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("issues").path(issueKey).path("remove").path("folder").build();
        return put(uri, folderId, new UpdateContainsLinkInputJsonGenerator());
    }

    public Promise<Void> moveIssue(final Integer folderId, final String projectKey, final String issueKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("issues").path(issueKey).path("move").path("folder").build();
        return put(uri, folderId, new UpdateContainsLinkInputJsonGenerator());
    }

    public Promise<Void> createContainsLink(final Integer folderId, final String projectKey, final String issueKey) {
        if (oldFolderRestClient.existsContainsLink(issueKey, projectKey).claim()) {
            // Already exists, move
            return moveIssue(folderId, projectKey, issueKey);
        }
        else {
            // Doesn't exist, create
            final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("issues").path(issueKey).path("add").path("folder").build();
            return put(uri, folderId, new UpdateContainsLinkInputJsonGenerator());
        }
    }

    private boolean folderWasUpdated(final FolderInput update, final FolderModel updated) {
        return updated.Title.equalsIgnoreCase(update.Title) && updated.Description.equalsIgnoreCase(update.Description);
    }

    public Promise<FolderModel> updateFolder(final FolderInput input, final String projectKey, final Integer folderId) {
        // Get current watchers to not override them in the update
        var watchers = getWatchers(folderId, projectKey).claim();
        var beforeUpdate = get(projectKey, folderId).claim();

        // Update folder
        final URI updateUri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        if (beforeUpdate.ParentId == -1) {
            // R4J Api will always return 500 on edit to folders with parentId=ROOT even though the update succeeds
            try {
                put(updateUri, input, new UpdateFolderInputJsonGenerator(watchers)).claim();
            } catch (Exception ignored) {}

            var updatedFolder = get(projectKey, folderId).claim();
            if (!folderWasUpdated(input, updatedFolder)) {
                throw new WebApplicationException("Failed to update folder!", Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        else {
            put(updateUri, input, new UpdateFolderInputJsonGenerator(watchers)).claim();
        }

        // Move folder to new parentId
        final URI moveUri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).path("move").build();
        put(moveUri, input, new MoveFolderInputJsonGenerator()).claim();

        return get(projectKey, folderId);
    }
}

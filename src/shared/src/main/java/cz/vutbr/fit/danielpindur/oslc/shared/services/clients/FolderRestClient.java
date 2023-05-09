/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.FolderInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.MoveFolderInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.UpdateContainsLinkInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.UpdateFolderInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderSubfolderNamesParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderTreeParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderWatchersParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.inputs.FolderInput;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderParser;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderTreeModel;
import io.atlassian.util.concurrent.Promise;
import org.codehaus.jettison.json.JSONArray;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Asynchronous REST client for the R4J API for Folder v2.
 */
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

    /**
     * Get folder by id.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * 
     * @return promise of the folder
     */
    public Promise<FolderModel> get(final String projectKey, final Integer folderId) {
        return get(projectKey, folderId, true);
    }

    /**
     * Get folder by id.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * @param includeIssues whether to include issues in the folder
     * 
     * @return promise of the folder
     */
    public Promise<FolderModel> get(final String projectKey, final Integer folderId, final Boolean includeIssues) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).queryParam("includeIssues", includeIssues).build();
        return getAndParse(uri, new FolderParser());
    }

    /**
     * Get folder tree for the project.
     * 
     * @param projectKey key of the project
     * 
     * @return promise of the folder tree
     */
    public Promise<FolderTreeModel> getTree(final String projectKey) {
        return getTree(projectKey, -1, true);
    }

    /**
     * Get folder tree for the project starting from the given folder.
     * 
     * @param projectKey key of the project
     * @param folderId id of the folder
     * 
     * @return promise of the folder tree
     */
    public Promise<FolderTreeModel> getTree(final String projectKey, final Integer folderId) {
        return getTree(projectKey, folderId, true);
    }

    /**
     * Get folder tree for the project starting from the given folder with the issues contained in the folders.
     * 
     * @param projectKey key of the project
     * @param folderId id of the folder
     * @param includeIssues whether to include issues in the folder
     * 
     * @return promise of the folder tree
     */
    public Promise<FolderTreeModel> getTree(final String projectKey, final Integer folderId, final Boolean includeIssues) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).queryParam("includeIssues", includeIssues).build();
        return getAndParse(uri, new FolderTreeParser());
    }

    /**
     * Delete folder by id.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * 
     * @return promise of the deletion
     */
    public Promise<Void> deleteFolder(final String projectKey, final Integer folderId) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        return this.delete(uri);
    }

    /**
     * Create folder.
     * 
     * @param input input for the folder
     * @param projectKey key of the project
     * 
     * @return promise of the folder creation
     */
    public Promise<FolderModel> createFolder(final FolderInput input, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").build();
        return this.postAndParse(uri, input, new FolderInputJsonGenerator(), new FolderParser());
    }

    /**
     * Get watchers of the given folder
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * 
     * @return promise of the watchers
     */
    private Promise<JSONArray> getWatchers(final Integer folderId, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        return getAndParse(uri, new FolderWatchersParser());
    }

    /**
     * Get list of names of the subfolders for a given folder.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * 
     * @return promise of the list of names of the subfolders
     */
    public Promise<Set<String>> getSubfolderNames(final Integer folderId, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("folders").path(folderId.toString()).build();
        return getAndParse(uri, new FolderSubfolderNamesParser());
    }

    /**
     * Get the folder path for the given folder.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * 
     * @return Folder path
     */
    public String getFolderPath(final Integer folderId, final String projectKey) {
        if (folderId == -1) {
            return projectKey;
        }

        var folder = get(projectKey, folderId).claim();
        return getFolderPath(folder.ParentId, projectKey) + "/" + folder.Title;
    }

    /**
     * Remove issue from the given folder.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * @param issueKey key of the issue
     * 
     * @return promise of the removal
     */
    public Promise<Void> removeContainsLink(final Integer folderId, final String projectKey, final String issueKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("issues").path(issueKey).path("remove").path("folder").build();
        return put(uri, folderId, new UpdateContainsLinkInputJsonGenerator());
    }

    /**
     * Move issue to the given folder.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * @param issueKey key of the issue
     * 
     * @return promise of the move
     */
    public Promise<Void> moveIssue(final Integer folderId, final String projectKey, final String issueKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("projects").path(projectKey).path("issues").path(issueKey).path("move").path("folder").build();
        return put(uri, folderId, new UpdateContainsLinkInputJsonGenerator());
    }

    /**
     * Create contains link between the given folder and issue.
     * 
     * @param folderId id of the folder
     * @param projectKey key of the project
     * @param issueKey key of the issue
     * 
     * @return promise of the creation
     */
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

    /**
     * Validate if the folder has been updated.
     * 
     * @param update input for the update
     * @param updated updated folder
     * 
     * @return true if the folder has been updated, false otherwise
     */
    private boolean folderWasUpdated(final FolderInput update, final FolderModel updated) {
        return updated.Title.equalsIgnoreCase(update.Title) && updated.Description.equalsIgnoreCase(update.Description);
    }

    /**
     * Update folder.
     * 
     * @param input input for the update
     * @param projectKey key of the project
     * @param folderId id of the folder
     * 
     * @return promise of the folder update
     */
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

    /**
     * Get list of enabled project keys.
     * 
     * @return promise of the list of enabled project keys
     */
    public Promise<List<String>> getEnabledProjectKeys() {
        return oldFolderRestClient.getEnabledProjectKeys();
    }
}

package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.json.gen.IssueInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.generators.FolderInputJsonGenerator;
import cz.vutbr.fit.danielpindur.oslc.shared.services.inputs.FolderInput;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.FolderParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class FolderRestClient extends AbstractAsynchronousRestClient {
    private final URI baseUri;

    public FolderRestClient(final URI baseUri, final HttpClient client) {
        super(client);
        this.baseUri = baseUri;
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
}

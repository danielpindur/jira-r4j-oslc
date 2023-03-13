package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.models.FolderModel;
import cz.vutbr.fit.danielpindur.oslc.shared.services.parsers.FolderParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Set;

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
}

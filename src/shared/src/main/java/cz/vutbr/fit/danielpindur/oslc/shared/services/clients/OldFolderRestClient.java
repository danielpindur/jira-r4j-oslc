package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.ContainsLinkExistsParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

public class OldFolderRestClient extends AbstractAsynchronousRestClient implements Closeable {
    private final URI baseUri;
    private final DisposableHttpClient client;

    public OldFolderRestClient(final URI baseUri, final DisposableHttpClient client) {
        super(client);
        this.baseUri = baseUri;
        this.client = client;
    }

    public void close() {
        try {
            this.client.destroy();
        } catch (Exception ignored) { }
    }

    public Promise<Boolean> existsContainsLink(final String issueKey, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("issue").path("req-path").path("key="+issueKey).build();
        return getAndParse(uri, new ContainsLinkExistsParser(projectKey));
    }
}

package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.ContainsLinkExistsParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class OldFolderRestClient extends AbstractAsynchronousRestClient {
    private final URI baseUri;

    public OldFolderRestClient(final URI baseUri, final HttpClient client) {
        super(client);
        this.baseUri = baseUri;
    }

    public Promise<Boolean> existsContainsLink(final String issueKey, final String projectKey) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("issue").path("req-path").path("key="+issueKey).build();
        return getAndParse(uri, new ContainsLinkExistsParser(projectKey));
    }
}

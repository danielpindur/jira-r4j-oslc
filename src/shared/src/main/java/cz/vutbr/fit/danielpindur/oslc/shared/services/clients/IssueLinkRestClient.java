package cz.vutbr.fit.danielpindur.oslc.shared.services.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.json.parsers.IssueLinkIdsForIssueParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

public class IssueLinkRestClient extends AbstractAsynchronousRestClient implements Closeable {
    private final URI baseUri;
    private final DisposableHttpClient client;

    public IssueLinkRestClient(final URI baseUri, final DisposableHttpClient client) {
        super(client);
        this.baseUri = baseUri;
        this.client = client;
    }

    public void close() {
        try {
            this.client.destroy();
        } catch (Exception ignored) { }
    }

    public Promise<Void> deleteLink(final String linkId) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("issueLink").path(linkId).build();
        return delete(uri);
    }

    public Promise<Set<String>> getAdaptorIssueLinkIdsForIssue(final String issueId, final String issueLinkName) {
        final URI uri = UriBuilder.fromUri(this.baseUri).path("issue").path(issueId).build();
        return getAndParse(uri, new IssueLinkIdsForIssueParser(issueLinkName));
    }

    public Promise<Set<String>> getIssueLinkIdsForIssue(final String issueId) {
        return getAdaptorIssueLinkIdsForIssue(issueId, null);
    }
}

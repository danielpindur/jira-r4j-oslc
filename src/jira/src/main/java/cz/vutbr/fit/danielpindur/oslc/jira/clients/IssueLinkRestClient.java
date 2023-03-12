package cz.vutbr.fit.danielpindur.oslc.jira.clients;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import cz.vutbr.fit.danielpindur.oslc.jira.parsers.IssueLinkIdsForIssueParser;
import io.atlassian.util.concurrent.Promise;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Set;

public class IssueLinkRestClient extends AbstractAsynchronousRestClient {
    private final URI baseUri;


    public IssueLinkRestClient(final URI baseUri, final HttpClient client) {
        super(client);
        this.baseUri = baseUri;
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

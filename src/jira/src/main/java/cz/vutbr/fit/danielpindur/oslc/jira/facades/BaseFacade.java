package cz.vutbr.fit.danielpindur.oslc.jira.facades;

import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.jira.ResourcesFactory;
import cz.vutbr.fit.danielpindur.oslc.jira.clients.IssueLinkRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Objects;

public class BaseFacade {
    @Inject ResourcesFactory resourcesFactory;

    private final JiraRestClient restClient;
    protected static final Logger log = LoggerFactory.getLogger(BaseFacade.class);
    protected Configuration configuration = ConfigurationProvider.getInstance().GetConfiguration();

    public BaseFacade() {
        // TODO: Link Basic auth to Adaptor basic auth
        // TODO: Add OAuth
        restClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(configuration.JiraServer.Url), "xpindu01", "testpassword");
    }

    protected boolean containsTerms(final String target, final String terms) {
        if (target == null || terms == null) {
            return false;
        }

        var capitalizedTarget = target.toUpperCase();
        var capitalizedTerms = terms.toUpperCase();

        return capitalizedTarget.contains(capitalizedTerms);
    }

    protected String SafeConvert(final Long number) {
        return Objects.requireNonNull(number).toString();
    }

    protected UserRestClient getUserClient() {
        return restClient.getUserClient();
    }

    protected ProjectRestClient getProjectClient() {
        return restClient.getProjectClient();
    }

    protected IssueRestClient getIssueClient() { return restClient.getIssueClient(); }

    protected MetadataRestClient getMetadataClient() { return restClient.getMetadataClient(); }

    protected SearchRestClient getSearchClient() { return restClient.getSearchClient(); }

    protected IssueLinkRestClient getIssueLinkRestClient() {
        // TODO: Link Basic auth to Adaptor basic auth
        // TODO: Add OAuth
        // TODO: Probably unify with the base
        var authenticationHandler = new BasicHttpAuthenticationHandler("xpindu01", "testpassword");
        final DisposableHttpClient httpClient = new AsynchronousHttpClientFactory()
                .createClient(URI.create(configuration.JiraServer.Url), authenticationHandler);

        return new IssueLinkRestClient(URI.create(configuration.JiraServer.Url + "/rest/api/latest"), httpClient);
    }
}

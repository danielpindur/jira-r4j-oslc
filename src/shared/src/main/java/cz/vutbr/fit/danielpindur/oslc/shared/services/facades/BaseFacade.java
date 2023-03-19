package cz.vutbr.fit.danielpindur.oslc.shared.services.facades;

import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.*;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Objects;

public class BaseFacade {
    private final JiraRestClient restClient;
    protected static final Logger log = LoggerFactory.getLogger(BaseFacade.class);
    protected Configuration configuration = ConfigurationProvider.getInstance().GetConfiguration();

    public BaseFacade() {
        // TODO: Link Basic auth to Adaptor basic auth
        // TODO: Add OAuth
        restClient = new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(configuration.JiraServer.Url), "test_user", "testpassword");
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

    protected ProjectRestClient getProjectClient() { return restClient.getProjectClient(); }

    protected MetadataRestClient getMetadataClient() { return restClient.getMetadataClient(); }

    protected SearchRestClient getSearchClient() { return restClient.getSearchClient(); }

    // TODO: Link Basic auth to Adaptor basic auth
    // TODO: Add OAuth
    // TODO: Probably unify with the base
    private DisposableHttpClient getHttpClient() {
        var authenticationHandler = new BasicHttpAuthenticationHandler("test_user", "testpassword");
        return new AsynchronousHttpClientFactory()
                .createClient(URI.create(configuration.JiraServer.Url), authenticationHandler);
    }

    protected IssueLinkRestClient getIssueLinkRestClient() {
        return new IssueLinkRestClient(URI.create(configuration.JiraServer.Url + "/rest/api/latest"), getHttpClient());
    }

    protected UserRestClientExtended getUserClient() {
        return new UserRestClientExtended(URI.create(configuration.JiraServer.Url + "/rest/api/latest"), getHttpClient());
    }

    protected IssueRestClientExtended getIssueClient() {
        return new IssueRestClientExtended(URI.create(configuration.JiraServer.Url + "/rest/api/latest"),getHttpClient(), restClient.getSessionClient(), getMetadataClient());
    }


    protected FolderRestClient getFolderClient() {
        var oldFolderRestClient = new OldFolderRestClient(URI.create(configuration.JiraServer.Url + "/rest/com.easesolutions.jira.plugins.requirements/1.0"), getHttpClient());
        return new FolderRestClient(URI.create(configuration.JiraServer.Url + "/rest/com.easesolutions.jira.plugins.requirements/2.0"), getHttpClient(), oldFolderRestClient);
    }

    protected String GetIdFromUri(final URI uri) {
        var exploded = uri.toString().split("/");
        return exploded[exploded.length - 1];
    }
}

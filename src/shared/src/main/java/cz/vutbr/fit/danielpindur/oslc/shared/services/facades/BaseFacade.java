package cz.vutbr.fit.danielpindur.oslc.shared.services.facades;

import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.*;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.shared.session.SessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Objects;

public class BaseFacade {
    protected static final Logger log = LoggerFactory.getLogger(BaseFacade.class);
    protected Configuration configuration = ConfigurationProvider.getInstance().GetConfiguration();

    private DisposableHttpClient cachedHttpClient;
    private JiraRestClient cachedJiraClient;
    private AuthenticationHandler cachedAuthenticationHandler;

    public BaseFacade() { }

    private AuthenticationHandler getAuthenticationHandler() {
        if (cachedAuthenticationHandler == null) {
            var session = SessionProvider.GetSession();

            if (session == null) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            var username = session.getAttribute(SessionProvider.BASIC_USERNAME).toString();
            var password = session.getAttribute(SessionProvider.BASIC_PASSWORD).toString();

            if (username == null || password == null) {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }

            cachedAuthenticationHandler = new BasicHttpAuthenticationHandler(username, password);
        }

        return cachedAuthenticationHandler;
    }

    // TODO: Add OAuth
    private DisposableHttpClient getHttpClient() {
        if (cachedHttpClient == null) {
            cachedHttpClient = new AsynchronousHttpClientFactory()
                    .createClient(URI.create(configuration.JiraServer.Url), getAuthenticationHandler());
        }

        return cachedHttpClient;
    }

    private JiraRestClient getRestClient() {
        if (cachedJiraClient == null) {
            cachedJiraClient = new AsynchronousJiraRestClientFactory()
                    .createWithAuthenticationHandler(URI.create(configuration.JiraServer.Url), getAuthenticationHandler());
        }

        return cachedJiraClient;
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

    protected ProjectRestClient getProjectClient() { return getRestClient().getProjectClient(); }

    protected MetadataRestClient getMetadataClient() { return getRestClient().getMetadataClient(); }

    protected SearchRestClient getSearchClient() { return getRestClient().getSearchClient(); }

    protected IssueLinkRestClient getIssueLinkRestClient() {
        return new IssueLinkRestClient(URI.create(configuration.JiraServer.Url + "/rest/api/latest"), getHttpClient());
    }

    protected UserRestClientExtended getUserClient() {
        return new UserRestClientExtended(URI.create(configuration.JiraServer.Url + "/rest/api/latest"), getHttpClient());
    }

    protected IssueRestClientExtended getIssueClient() {
        return new IssueRestClientExtended(URI.create(configuration.JiraServer.Url + "/rest/api/latest"),getHttpClient(), getRestClient().getSessionClient(), getMetadataClient());
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

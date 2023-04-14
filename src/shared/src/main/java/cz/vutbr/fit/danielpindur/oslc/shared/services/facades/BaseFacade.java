package cz.vutbr.fit.danielpindur.oslc.shared.services.facades;

import com.atlassian.jira.rest.client.api.*;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;
import cz.vutbr.fit.danielpindur.oslc.shared.authentication.OAuthHttpAuthenticationHandler;
import cz.vutbr.fit.danielpindur.oslc.shared.services.clients.*;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.ConfigurationProvider;
import cz.vutbr.fit.danielpindur.oslc.shared.configuration.models.Configuration;
import cz.vutbr.fit.danielpindur.oslc.shared.session.SessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Objects;

public class BaseFacade {
    protected static final Logger log = LoggerFactory.getLogger(BaseFacade.class);
    private URI baseUri;
    protected Configuration configuration = ConfigurationProvider.GetConfiguration();

    public BaseFacade() { }

    private AuthenticationHandler getAuthenticationHandler() {
        var session = SessionProvider.GetSession();

        if (session == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        var token = session.getAttribute(SessionProvider.OAUTH_TOKEN);
        if (token != null) {
            return getOAuthAuthenticationHandler(token.toString());
        }
        else {
            return getBasicAuthenticationHandler(session);
        }

    }

    private URI getJiraBaseUri() {
        if (baseUri == null) {
            baseUri = URI.create(configuration.JiraServer.Url + "/rest/api/latest");
        }

        return baseUri;
    }

    private AuthenticationHandler getBasicAuthenticationHandler(final HttpSession session) {
        var username = session.getAttribute(SessionProvider.BASIC_USERNAME);
        var password = session.getAttribute(SessionProvider.BASIC_PASSWORD);

        if (username == null || password == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return new BasicHttpAuthenticationHandler(username.toString(), password.toString());
    }

    private AuthenticationHandler getOAuthAuthenticationHandler(final String token) {
        return new OAuthHttpAuthenticationHandler(token);
    }

    private DisposableHttpClient getHttpClient() {
        var client = new AsynchronousHttpClientFactory()
                .createClient(URI.create(configuration.JiraServer.Url), getAuthenticationHandler());
        SessionProvider.AddClient(client);
        return client;
    }

    private JiraRestClient getRestClient() {
        var jiraClient = new AsynchronousJiraRestClientFactory()
                .createWithAuthenticationHandler(URI.create(configuration.JiraServer.Url), getAuthenticationHandler());
        SessionProvider.AddJiraClient(jiraClient);
        return jiraClient;
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

    protected SearchRestClient getSearchClient() { return new SearchRestClientExtended(getJiraBaseUri(), getHttpClient()); }

    protected IssueLinkRestClient getIssueLinkRestClient() {
        return new IssueLinkRestClient(getJiraBaseUri(), getHttpClient());
    }

    protected UserRestClientExtended getUserClient() {
        return new UserRestClientExtended(getJiraBaseUri(), getHttpClient());
    }

    protected IssueRestClientExtended getIssueClient() {
        return new IssueRestClientExtended(getJiraBaseUri(),getHttpClient(), getRestClient().getSessionClient(), getMetadataClient(), getSearchClient());
    }


    protected FolderRestClient getFolderClient() {
        var oldFolderRestClient = new OldFolderRestClient(URI.create(configuration.JiraServer.Url + "/rest/com.easesolutions.jira.plugins.requirements/1.0"), getHttpClient());
        return new FolderRestClient(URI.create(configuration.JiraServer.Url + "/rest/com.easesolutions.jira.plugins.requirements/2.0"), getHttpClient(), oldFolderRestClient);
    }
}

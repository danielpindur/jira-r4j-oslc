/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

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

/**
 * Base facade class for all facades. Provides setup and basic functionality shared by all facades.
 */
public class BaseFacade {
    protected static final Logger log = LoggerFactory.getLogger(BaseFacade.class);
    protected static Configuration configuration = ConfigurationProvider.GetConfiguration();

    public BaseFacade() { }

    private static AuthenticationHandler getAuthenticationHandler() {
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

    /**
     * Get the URI of the Jira server.
     */
    private static URI getJiraBaseUri() {
        return URI.create(configuration.JiraServer.Url + "/rest/api/latest");
    }

    /**
     * Get authentication handler for BASIC authentication.
     * 
     * @param session Session to get username and password from.
     */
    private static AuthenticationHandler getBasicAuthenticationHandler(final HttpSession session) {
        var username = session.getAttribute(SessionProvider.BASIC_USERNAME);
        var password = session.getAttribute(SessionProvider.BASIC_PASSWORD);

        if (username == null || password == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return new BasicHttpAuthenticationHandler(username.toString(), password.toString());
    }

    /**
     * Get authentication handler for OAuth authentication.
     * 
     * @param token Token to use for authentication.
     */
    private static AuthenticationHandler getOAuthAuthenticationHandler(final String token) {
        return new OAuthHttpAuthenticationHandler(token);
    }

    /**
     * Get a disposable HTTP client for the Jira server.
     */
    private static DisposableHttpClient getHttpClient() {
        var client = new AsynchronousHttpClientFactory()
                .createClient(URI.create(configuration.JiraServer.Url), getAuthenticationHandler());
        SessionProvider.AddClient(client);
        return client;
    }

    /**
     * Get a Jira REST client from Jira API SDK.
     */
    private static JiraRestClient getRestClient() {
        var jiraClient = new AsynchronousJiraRestClientFactory()
                .createWithAuthenticationHandler(URI.create(configuration.JiraServer.Url), getAuthenticationHandler());
        SessionProvider.AddJiraClient(jiraClient);
        return jiraClient;
    }

    /**
     * Check if a string contains a substring, ignoring case.
     */
    protected static boolean containsTerms(final String target, final String terms) {
        if (target == null || terms == null) {
            return false;
        }

        var capitalizedTarget = target.toUpperCase();
        var capitalizedTerms = terms.toUpperCase();

        return capitalizedTarget.contains(capitalizedTerms);
    }

    /**
     * Safely convert a long to a string with null check.
     */
    protected static String SafeConvert(final Long number) {
        return Objects.requireNonNull(number).toString();
    }

    /**
     * Get REST API client for Jira projects.
     */
    protected static ProjectRestClient getProjectClient() { return getRestClient().getProjectClient(); }

    /**
     * Get REST API client for Jira issues metadata.
     */
    protected static MetadataRestClient getMetadataClient() { return getRestClient().getMetadataClient(); }

    /**
     * Get REST API client for Jira issue search.
     */
    protected static SearchRestClientExtended getSearchClient() { return new SearchRestClientExtended(getJiraBaseUri(), getHttpClient()); }

    /**
     * Get REST API client for Jira issue links.
     */
    protected static IssueLinkRestClient getIssueLinkRestClient() {
        return new IssueLinkRestClient(getJiraBaseUri(), getHttpClient());
    }

    /**
     * Get REST API client for Jira users.
     */
    protected static UserRestClientExtended getUserClient() {
        return new UserRestClientExtended(getJiraBaseUri(), getHttpClient());
    }

    /**
     * Get REST API client for Jira issues.
     */
    public static IssueRestClientExtended getIssueClient() {
        return new IssueRestClientExtended(getJiraBaseUri(),getHttpClient(), getRestClient().getSessionClient(), getMetadataClient(), getSearchClient());
    }

    /**
     * Get REST API client for R4J folders.
     */
    protected static FolderRestClient getFolderClient() {
        var oldFolderRestClient = new OldFolderRestClient(URI.create(configuration.JiraServer.Url + "/rest/com.easesolutions.jira.plugins.requirements/1.0"), getHttpClient());
        return new FolderRestClient(URI.create(configuration.JiraServer.Url + "/rest/com.easesolutions.jira.plugins.requirements/2.0"), getHttpClient(), oldFolderRestClient);
    }
}

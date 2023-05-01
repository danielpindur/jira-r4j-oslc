/*
 * Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package cz.vutbr.fit.danielpindur.oslc.shared.session;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton class for providing session to other classes.
 */
public final class SessionProvider {
    public static String BASIC_USERNAME = "Username";
    public static String BASIC_PASSWORD = "Password";
    public static String OAUTH_TOKEN = "OAuth_Token";

    private static SessionProvider INSTANCE;

    private HttpSession session;
    private List<DisposableHttpClient> disposableHttpClients;
    private List<JiraRestClient> jiraRestClients;

    /**
     * Get singleton instance of this class.
     */
    private static SessionProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionProvider();
        }

        return INSTANCE;
    }

    /**
     * Set session for the singleton instance of this class.
     * 
     * @param session Session to be set.
     */
    public static void SetSession(final HttpSession session) {
        getInstance().session = session;
        SetupClients();
    }

    /**
     * Clear session for the singleton instance of this class.
     */
    public static void ClearSession() {
        getInstance().session = null;
        ClearClients();
    }

    /**
     * Get session of the singleton instance of this class.
     * 
     * @return Session.
     */
    public static HttpSession GetSession() {
        return getInstance().session;
    }

    /**
     * Closes all of the cached open HTTP connections.
     */
    public static void ClearClients() {
        if (getInstance().disposableHttpClients != null && !getInstance().disposableHttpClients.isEmpty()) {
            for (var client : getInstance().disposableHttpClients) {
                try {
                    client.destroy();
                } catch (Exception ignored) { }
            }
        }

        if (getInstance().jiraRestClients != null && !getInstance().jiraRestClients.isEmpty()) {
            for (var client : getInstance().jiraRestClients) {
                try {
                    client.close();
                } catch (Exception ignored) { }
            }
        }
    }

    /**
     * Initializes the lists for caching HTTP clients.
     */
    private static void SetupClients() {
        ClearClients();
        getInstance().disposableHttpClients = new LinkedList<>();
        getInstance().jiraRestClients = new LinkedList<>();
    }

    /**
     * Adds a HTTP client to the cache.
     * 
     * @param client Client to be added.
     */
    public static void AddClient(final DisposableHttpClient client) {
        getInstance().disposableHttpClients.add(client);
    }

    /**
     * Adds a Jira client to the cache.
     * 
     * @param client Client to be added.
     */
    public static void AddJiraClient(final JiraRestClient client) {
        getInstance().jiraRestClients.add(client);
    }
}
